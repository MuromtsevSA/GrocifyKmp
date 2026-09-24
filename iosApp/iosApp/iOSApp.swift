import SwiftUI
import ClerkKit

@main
struct iOSApp: App {
    init() {
        let key = Bundle.main.object(forInfoDictionaryKey: "CLERK_PUBLISHABLE_KEY") as? String
            ?? ProcessInfo.processInfo.environment["CLERK_PUBLISHABLE_KEY"]
            ?? ""

        if !key.isEmpty {
            Clerk.configure(publishableKey: key)
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environment(Clerk.shared)
        }
    }
}
