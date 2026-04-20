import SwiftUI

private enum AppRoute: Hashable {
    case dashboard
    case searchFriend
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
                    DashboardScreen(
                        onSearchFriends: { path.append(AppRoute.searchFriend) }
                    )
                case .searchFriend:
                    SearchFriendScreen(
                        onBack: { path.removeLast() }
                    )
                }
            }
        }
    }
}

#Preview {
    ContentView()
}
