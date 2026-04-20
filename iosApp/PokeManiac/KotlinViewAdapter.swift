import Foundation
import PokeManiacPresentation

/// Bridges KMM `ScreenModel` with `StateFlow` to SwiftUI (`@Published` + `for await` on `SkieSwiftStateFlow`).
public final class KotlinViewAdapter<ViewModel, State: AnyObject>: ObservableObject {
    public let viewModel: ViewModel
    @Published public var state: State

    private var observingTask: Task<Void, Never>?
    private let scope: Kotlinx_coroutines_coreCoroutineScope

    public init(
        viewModel: (Kotlinx_coroutines_coreCoroutineScope) -> ViewModel,
        state: (ViewModel) -> SkieSwiftStateFlow<State>
    ) {
        let scope = PresentationCoroutineScopeKt.createPresentationCoroutineScope()
        let vm = viewModel(scope)
        let stateFlow = state(vm)
        self.scope = scope
        self.viewModel = vm
        self.state = stateFlow.value
        self.observingTask = Task { @MainActor [weak self] in
            for await value in stateFlow {
                self?.state = value
            }
        }
    }

    deinit {
        observingTask?.cancel()
        PresentationCoroutineScopeKt.cancelPresentationCoroutineScope(scope: scope)
    }
}
