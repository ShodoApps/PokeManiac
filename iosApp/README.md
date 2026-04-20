# PokeManiac iOS app (SwiftUI)

Swift sources live under `PokeManiac/`. The shared Kotlin UI logic is in **`:shared:presentation`**, exported as **`PokeManiacPresentation.framework`**.

## 1. Build the framework (simulator, debug)

From repo root:

```bash
./gradlew :shared:presentation:linkDebugFrameworkIosSimulatorArm64
```

Framework output:

`shared/presentation/build/bin/iosSimulatorArm64/debugFramework/PokeManiacPresentation.framework`

For **device** (arm64):

```bash
./gradlew :shared:presentation:linkDebugFrameworkIosArm64
```

Output:

`shared/presentation/build/bin/iosArm64/debugFramework/PokeManiacPresentation.framework`

## 2. Xcode project

The repo includes **`iosApp/PokeManiac.xcodeproj`** (target **PokeManiac**, `Info.plist`, shared scheme). There is **no `Package.swift` for the Kotlin framework**: `PokeManiacPresentation` is produced by Gradle (`linkDebugFramework…`), not Swift Package Manager. You can still add SPM later for pure Swift dependencies only.

1. Run the Gradle task in §1 for the destination you need (simulator vs device).
2. Open **`iosApp/PokeManiac.xcodeproj`** in Xcode.
3. **Signing & Capabilities** → select your **Team** (required to run on a device or some simulators).
4. **Build** (**⌘B**). Debug already sets **Framework Search Paths** to the simulator framework output; Release points at the **iosArm64** path for device. `ContentView` calls `IosKmpSmoke.shared.greeting()` from Kotlin.

Re-run the Gradle `linkDebugFramework…` task after Kotlin changes before rebuilding in Xcode.

**CLI check** (from repo root, after building the framework):

```bash
xcodebuild -project iosApp/PokeManiac.xcodeproj -scheme PokeManiac -destination 'platform=iOS Simulator,name=iPhone 16' build
```

## 3. Next steps (Phase G)

- Wire **SwiftUI** to **`WelcomeScreenModel`** (coroutine scope + `StateFlow` interop: e.g. SKIE, KMP-NativeCoroutines, or manual collectors).
- Add Apple targets to **`:shared:api`**, **`:shared:data`**, **`:shared:database`** and iOS **`startKoin`** per `docs/kmp-migration-plan.md` §7 Phase G.
