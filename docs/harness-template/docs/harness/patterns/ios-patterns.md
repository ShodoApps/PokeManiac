# iOS Patterns

> Always-loaded rules are in `iosApp/CLAUDE.md`. This file has full KotlinViewAdapter + SKIE code examples.
> Read it when working in `iosApp/`.

---

## Architecture

The iOS app consumes shared `ScreenModel` + `UiState` from `:shared:presentation` via SKIE (Kotlin/Swift interop). It mirrors the Android Screen/View split in SwiftUI.

```
SwiftUI Screen (struct)
  ├── @StateObject KotlinViewAdapter<ScreenModel, UiState>
  ├── .task { for await event in collectAsAsyncStream(adapter.viewModel.uiEvent) { ... } }
  └── delegates to →
        SwiftUI View (struct)
        ├── pure rendering — no state ownership
        └── all data via parameters
```

---

## KotlinViewAdapter

`KotlinViewAdapter` bridges a shared `ScreenModel` (with `StateFlow`) to SwiftUI's `ObservableObject` / `@Published`.

```swift
@StateObject private var adapter: KotlinViewAdapter<MyScreenModel, MyUiState>

init(...) {
    _adapter = StateObject(
        wrappedValue: KotlinViewAdapter(
            viewModel: { scope in
                // Simple ScreenModel: construct directly
                MyScreenModel(coroutineScope: scope)
                // ScreenModel needing Koin deps: use iOS composition helper
                // MyIosCompositionKt.createMyScreenModelForIos(coroutineScope: scope)
            },
            state: { $0.uiState }
        )
    )
}
```

`KotlinViewAdapter` creates a `CoroutineScope` via `createPresentationCoroutineScope()`, passes it to the ScreenModel, and cancels it on `deinit`. Do not manage scope manually.

---

## Collecting SharedFlow Events (SKIE)

Use `collectAsAsyncStream` from `SkieFlowCollect.swift` to consume `SharedFlow` in `.task`:

```swift
.task {
    for await event in collectAsAsyncStream(adapter.viewModel.uiEvent) {
        switch onEnum(of: event) {
        case .navigateToDashboard:
            onNavigateToDashboard()
        case .showMessage(let data):
            alertText = data.message.message
            showAlert = true
        }
    }
}
```

For error flows (`SharedFlow<UiError>`):
```swift
.task {
    for await err in collectAsAsyncStream(adapter.viewModel.error) {
        errorMessage = err.message
        showError = true
    }
}
```

`.task` is cancelled automatically when the view disappears — no manual cancellation needed.

---

## Screen Pattern (reference: WelcomeScreen, MyFeatureScreen)

```swift
struct MyScreen: View {
    @StateObject private var adapter: KotlinViewAdapter<MyScreenModel, MyUiState>
    @State private var errorMessage: String?
    @State private var showError = false

    let onBack: () -> Void

    init(onBack: @escaping () -> Void) {
        self.onBack = onBack
        _adapter = StateObject(
            wrappedValue: KotlinViewAdapter(
                viewModel: { scope in MyScreenModel(coroutineScope: scope) },
                state: { $0.uiState }
            )
        )
    }

    var body: some View {
        MyView(
            uiState: adapter.state,
            onAction: { adapter.viewModel.onAction() }
        )
        .task {
            for await err in collectAsAsyncStream(adapter.viewModel.error) {
                errorMessage = err.message
                showError = true
            }
        }
        .alert("Error", isPresented: $showError, presenting: errorMessage) { _ in
            Button("OK", role: .cancel) {}
        } message: { msg in Text(msg) }
    }
}
```

---

## View Pattern

SwiftUI Views are stateless structs — no `@StateObject`, no flow collection:

```swift
struct MyView: View {
    let uiState: MyUiState
    let onAction: () -> Void

    var body: some View {
        // render uiState
    }
}

#Preview {
    MyView(uiState: ..., onAction: {})
}
```

---

## ScreenModels Needing Koin Dependencies

For ScreenModels that require repositories injected via Koin (e.g. `MyFeatureScreenModel`), create an iOS composition helper in `:shared:presentation` `iosMain`:

```kotlin
// iosMain
fun createMyFeatureScreenModelForIos(coroutineScope: CoroutineScope): MyFeatureScreenModel {
    return MyFeatureScreenModel(
        repository = KoinComponent().get(),
        coroutineScope = coroutineScope
    )
}
```

Then call from Swift: `MyFeatureIosCompositionKt.createMyFeatureScreenModelForIos(coroutineScope: scope)`.

---

## Existing Reference Screens

| Screen | Files | Pattern used |
|---|---|---|
| Welcome | `WelcomeScreen.swift` + `WelcomeView.swift` | KotlinViewAdapter + `uiEvent` SharedFlow |
| MyFeature | `MyFeatureScreen.swift` + `MyFeatureView.swift` | KotlinViewAdapter + iOS composition helper + error SharedFlow |
| Dashboard | `DashboardScreen.swift` + `DashboardView.swift` | KotlinViewAdapter + StateFlow only |

---

## Photo Selection — PHPicker (Preferred)

Use `PHPickerViewController` for photo selection. It requires **no permission on iOS 14+** — the user sees a picker without a permission prompt, and your app receives only the photos the user explicitly selected.

```swift
import PhotosUI

struct PhotoPickerView: UIViewControllerRepresentable {
    @Binding var selectedImage: UIImage?

    func makeUIViewController(context: Context) -> PHPickerViewController {
        var config = PHPickerConfiguration()
        config.selectionLimit = 1
        config.filter = .images
        let picker = PHPickerViewController(configuration: config)
        picker.delegate = context.coordinator
        return picker
    }

    func makeCoordinator() -> Coordinator { Coordinator(self) }

    class Coordinator: NSObject, PHPickerViewControllerDelegate {
        let parent: PhotoPickerView
        init(_ parent: PhotoPickerView) { self.parent = parent }

        func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
            picker.dismiss(animated: true)
            guard let provider = results.first?.itemProvider, provider.canLoadObject(ofClass: UIImage.self) else { return }
            provider.loadObject(ofClass: UIImage.self) { image, _ in
                DispatchQueue.main.async { self.parent.selectedImage = image as? UIImage }
            }
        }
    }
}
```

❌ Do NOT use `UIImagePickerController` with `sourceType: .photoLibrary` — it requires `NSPhotoLibraryUsageDescription` and shows a full-album permission dialog.

---

## Runtime Permission Flow (Camera, Location, Contacts, etc.)

For permissions other than the photo library, iOS uses a **one-shot model**: the system shows the dialog once. If the user denies, it will never show again — your app must direct them to Settings.

### Step 1 — Declare in `Info.plist` (REQUIRED before first request)
Missing `NS*UsageDescription` = crash on device (not simulator):

| Permission | Info.plist key |
|---|---|
| Camera | `NSCameraUsageDescription` |
| Location | `NSLocationWhenInUseUsageDescription` |
| Microphone | `NSMicrophoneUsageDescription` |
| Contacts | `NSContactsUsageDescription` |

### Step 2 — Request at the right moment (not at app launch)

```swift
AVCaptureDevice.requestAccess(for: .video) { granted in
    DispatchQueue.main.async {
        if granted {
            // proceed
        } else {
            // show Settings redirect
        }
    }
}
```

### Step 3 — Handle denial (direct to Settings)

```swift
if let url = URL(string: UIApplication.openSettingsURLString) {
    UIApplication.shared.open(url)
}
```

Never re-request after denial — iOS ignores repeated `requestAccess` calls once denied. Always provide a "Go to Settings" path so users can reverse their decision.
