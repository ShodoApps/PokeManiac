import SwiftUI
import PokeManiacPresentation

struct WelcomeScreen: View {
    @StateObject private var adapter: KotlinViewAdapter<WelcomeScreenModel, WelcomeUiState>
    @State private var alertText = ""
    @State private var showAlert = false

    let onNavigateToDashboard: () -> Void

    init(onNavigateToDashboard: @escaping () -> Void) {
        self.onNavigateToDashboard = onNavigateToDashboard
        _adapter = StateObject(
            wrappedValue: KotlinViewAdapter(
                viewModel: { scope in
                    WelcomeScreenModel(coroutineScope: scope)
                },
                state: { $0.uiState }
            )
        )
    }

    var body: some View {
        WelcomeView(
            onSignUp: { adapter.viewModel.onSignUpClicked() },
            onSignIn: { adapter.viewModel.onSignInClicked() }
        )
        .task {
            for await event in collectAsAsyncStream(adapter.viewModel.uiEvent) {
                switch onEnum(of: event) {
                case .showMessage(let data):
                    alertText = data.message.message
                    showAlert = true
                case .navigateToDashboard:
                    onNavigateToDashboard()
                }
            }
        }
        .alert(alertText, isPresented: $showAlert) {
            Button("OK", role: .cancel) {}
        }
    }
}

#Preview {
    WelcomeScreen(onNavigateToDashboard: {})
}
