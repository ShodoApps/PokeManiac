import Foundation
import PokeManiacPresentation

/// Wraps SKIE `SharedFlow` for `for await` in `.task` (avoids `any AsyncSequence` / deployment edge cases).
public func collectAsAsyncStream<T>(
    _ flow: SkieSwiftSharedFlow<T>
) -> AsyncStream<T> {
    AsyncStream { continuation in
        let task = Task {
            for await value in flow {
                continuation.yield(value)
            }
            continuation.finish()
        }
        continuation.onTermination = { _ in
            task.cancel()
        }
    }
}
