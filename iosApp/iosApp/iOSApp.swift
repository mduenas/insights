import SwiftUI
import ComposeApp
import FirebaseCore

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
        if #available(iOS 15.0, *) {
            StoreKitBridgeSetup.listenForTransactions()
            StoreKitBridgeSetup.configure()
        }
        MainViewControllerKt.startKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
