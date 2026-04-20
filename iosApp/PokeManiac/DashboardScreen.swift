import SwiftUI
import PokeManiacPresentation

struct DashboardScreen: View {
    @StateObject private var adapter: KotlinViewAdapter<DashboardScreenModel, DashboardUiState>
    @State private var errorMessage: String?
    @State private var showError = false

    let onSearchFriends: () -> Void

    init(onSearchFriends: @escaping () -> Void) {
        self.onSearchFriends = onSearchFriends
        _adapter = StateObject(
            wrappedValue: KotlinViewAdapter(
                viewModel: { scope in
                    IosPreviewRepositoriesKt.createDashboardScreenModelForIos(coroutineScope: scope)
                },
                state: { $0.uiState }
            )
        )
    }

    var body: some View {
        DashboardView(uiState: adapter.state, onRefresh: { adapter.viewModel.refreshNewsFeed() })
            .navigationTitle("Home")
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        onSearchFriends()
                    } label: {
                        Image(systemName: "magnifyingglass")
                    }
                    .accessibilityLabel("Search friends")
                }
            }
            .onAppear { adapter.viewModel.start() }
            .task {
                for await err in collectAsAsyncStream(adapter.viewModel.error) {
                    errorMessage = err.message
                    showError = true
                }
            }
            .alert("Error", isPresented: $showError, presenting: errorMessage) { _ in
                Button("OK", role: .cancel) {}
            } message: { msg in
                Text(msg)
            }
    }
}
