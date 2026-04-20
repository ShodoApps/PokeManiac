import SwiftUI
import PokeManiacPresentation

struct DashboardView: View {
    let uiState: DashboardUiState
    let onRefresh: () -> Void

    var body: some View {
        Group {
            switch onEnum(of: uiState) {
            case .loading:
                ProgressView()
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            case .emptyResult:
                ContentUnavailableView(
                    "No activity yet",
                    systemImage: "newspaper",
                    description: Text("Pull to refresh or check back later.")
                )
            case .data(let data):
                let rows = data.newsList()
                List {
                    ForEach(rows, id: \.id) { item in
                        dashboardRow(item)
                    }
                }
                .refreshable { onRefresh() }
            }
        }
    }

    @ViewBuilder
    private func dashboardRow(_ item: NewActivityUiModel) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text(item.friendName)
                    .font(.headline)
                Spacer()
                Text(activityLabel(item.activityType))
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }
            HStack(spacing: 12) {
                cardThumb(item.pokemonCard.imageSource)
                    .frame(width: 56, height: 56)
                    .clipShape(RoundedRectangle(cornerRadius: 8))
                VStack(alignment: .leading, spacing: 4) {
                    Text(item.pokemonCard.name)
                        .font(.subheadline.weight(.semibold))
                    Text(item.date)
                        .font(.caption)
                        .foregroundStyle(.secondary)
                    Text("$\(item.price)")
                        .font(.caption.weight(.medium))
                }
                Spacer(minLength: 0)
            }
        }
        .padding(.vertical, 4)
    }

    @ViewBuilder
    private func cardThumb(_ source: DashboardImageSourceUiModel) -> some View {
        switch onEnum(of: source) {
        case .urlSource(let u):
            if let url = URL(string: u.imageUrl), !u.imageUrl.isEmpty {
                AsyncImage(url: url) { phase in
                    switch phase {
                    case .success(let image):
                        image.resizable().scaledToFill()
                    default:
                        Color.gray.opacity(0.2)
                    }
                }
            } else {
                Color.gray.opacity(0.2)
            }
        case .fileSource:
            Color.gray.opacity(0.2)
        }
    }

    private func activityLabel(_ type: NewActivityTypeUiModel) -> String {
        switch type {
        case NewActivityTypeUiModel.purchase: return "Purchase"
        case NewActivityTypeUiModel.sale: return "Sale"
        }
    }
}
