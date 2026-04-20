import SwiftUI
import PokeManiacPresentation

struct SearchFriendView: View {
    let uiState: SearchFriendUiState
    @Binding var query: String
    let onSearch: () -> Void
    let onSubscribe: (String) -> Void
    let onUnsubscribe: (String) -> Void

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                TextField("Search by name", text: $query)
                    .textFieldStyle(.roundedBorder)
                    .submitLabel(.search)
                    .onSubmit { onSearch() }
                Button("Search", action: onSearch)
            }
            .padding()

            switch onEnum(of: uiState) {
            case .emptySearch:
                ContentUnavailableView(
                    "Search friends",
                    systemImage: "person.2",
                    description: Text("Type a name and tap Search.")
                )
            case .loading:
                ProgressView()
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            case .emptyResult(let r):
                ContentUnavailableView(
                    "No results",
                    systemImage: "magnifyingglass",
                    description: Text("No one matches “\(r.query)”.")
                )
            case .data(let d):
                let people = d.peopleList()
                List {
                    ForEach(people, id: \.id) { person in
                        searchFriendRow(person)
                    }
                }
                .listStyle(.plain)
            }
        }
        .background(Color(.systemGroupedBackground))
    }

    @ViewBuilder
    private func searchFriendRow(_ person: SearchFriendUiModel) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 4) {
                    Text(person.name)
                        .font(.headline)
                    Text(person.description_)
                        .font(.caption)
                        .foregroundStyle(.secondary)
                }
                Spacer()
                subscriptionControl(person)
            }
            let cardCount = person.pokemonCards.count
            if cardCount > 0 {
                Text("Cards: \(cardCount)")
                    .font(.caption2)
                    .foregroundStyle(.tertiary)
            }
        }
        .padding(.vertical, 4)
    }

    @ViewBuilder
    private func subscriptionControl(_ person: SearchFriendUiModel) -> some View {
        switch person.subscriptionState {
        case SubscriptionState.subscribed:
            Button("Unsubscribe") { onUnsubscribe(person.id) }
                .buttonStyle(.bordered)
        case SubscriptionState.notSubscribed:
            Button("Subscribe") { onSubscribe(person.id) }
                .buttonStyle(.borderedProminent)
        case SubscriptionState.updatingSubscribe:
            ProgressView()
                .scaleEffect(0.8)
        }
    }
}
