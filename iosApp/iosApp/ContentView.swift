import SwiftUI
import Shared
import ClerkKit
import ClerkKitUI

struct ContentView: View {
    @Environment(Clerk.self) private var clerk

    var body: some View {
        Group {
            if clerk.user != nil {
                ComposeView()
                    .ignoresSafeArea(.keyboard)
                    .task(id: clerk.user?.id) {
                        await syncSessionToKotlin()
                    }
            } else {
                AuthView(isDismissible: false)
                    .prefetchClerkImages()
            }
        }
        .onAppear {
            registerKotlinSignOutHandler()
        }
        .onChange(of: clerk.user?.id) { _, newId in
            if newId == nil {
                IosClerkBridge.shared.clearSession()
            }
        }
    }

    private func syncSessionToKotlin() async {
        guard let user = clerk.user else {
            IosClerkBridge.shared.clearSession()
            return
        }

        // Local SQLite only needs userId; JWT is optional (kept for future API use).
        var jwt: String? = nil
        do {
            jwt = try await clerk.auth.getToken()
        } catch {
            print("Clerk getToken failed: \(error)")
        }

        IosClerkBridge.shared.setSession(userId: user.id, token: jwt)
    }

    private func registerKotlinSignOutHandler() {
        IosClerkBridge.shared.setOnHostSignOut {
            Task { @MainActor in
                do {
                    try await clerk.auth.signOut()
                } catch {
                    print("Clerk signOut failed: \(error)")
                }
                IosClerkBridge.shared.clearSession()
            }
        }
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
