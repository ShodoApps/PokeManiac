import SwiftUI

/// Stateless layout mirroring Android `WelcomeView` (coreui strings: welcome / signup / signin).
struct WelcomeView: View {
    let onSignUp: () -> Void
    let onSignIn: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("Welcome !")
                .font(.largeTitle.weight(.bold))
                .padding(.horizontal, 16)
                .padding(.top, 16)
                .frame(maxWidth: .infinity, alignment: .leading)

            Spacer(minLength: 0)

            Image(systemName: "leaf.circle.fill")
                .symbolRenderingMode(.hierarchical)
                .font(.system(size: 120))
                .foregroundStyle(.green, .primary)
                .frame(maxWidth: .infinity)

            Spacer(minLength: 0)

            Button(action: onSignUp) {
                Text("Sign Up")
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            .padding(.horizontal, 16)
            .padding(.vertical, 8)

            Button(action: onSignIn) {
                Text("Sign In")
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.bordered)
            .padding(.horizontal, 16)
            .padding(.bottom, 32)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(.systemGroupedBackground))
    }
}

#Preview {
    WelcomeView(onSignUp: {}, onSignIn: {})
}
