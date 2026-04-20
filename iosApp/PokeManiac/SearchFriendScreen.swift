import SwiftUI
import PokeManiacPresentation

struct SearchFriendScreen: View {
    @StateObject private var adapter: KotlinViewAdapter<SearchFriendScreenModel, SearchFriendUiState>
    @State private var query = ""
    @State private var errorMessage: String?
    @State private var showError = false

    let onBack: () -> Void

    init(onBack: @escaping () -> Void) {
        self.onBack = onBack
        _adapter = StateObject(
            wrappedValue: KotlinViewAdapter(
                viewModel: { scope in
                    SearchFriendIosCompositionKt.createSearchFriendScreenModelForIos(coroutineScope: scope)
                },
                state: { $0.uiState }
            )
        )
    }

    var body: some View {
        SearchFriendView(
            uiState: adapter.state,
            query: $query,
            onSearch: { adapter.viewModel.searchFriend(friendName: query) },
            onSubscribe: { adapter.viewModel.subscribeFriend(friendId: $0) },
            onUnsubscribe: { adapter.viewModel.unsubscribeFriend(friendId: $0) }
        )
        .navigationTitle("Search friends")
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .toolbar {
            ToolbarItem(placement: .topBarLeading) {
                Button {
                    onBack()
                } label: {
                    Image(systemName: "chevron.backward")
                }
                .accessibilityLabel("Back")
            }
        }
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
