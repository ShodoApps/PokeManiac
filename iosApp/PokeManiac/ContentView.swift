import SwiftUI

private enum AppRoute: Hashable {
    case dashboard
}

struct ContentView: View {
    @State private var path = NavigationPath()

    var body: some View {
        NavigationStack(path: $path) {
            WelcomeScreen(
                onNavigateToDashboard: {
                    path.append(AppRoute.dashboard)
                }
            )
            .navigationDestination(for: AppRoute.self) { route in
                switch route {
                case .dashboard:
                    Text("Dashboard (stub)")
                        .navigationTitle("Home")
                }
            }
        }
    }
}

#Preview {
    ContentView()
}
