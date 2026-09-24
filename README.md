# Grocify KMP

Kotlin Multiplatform port of Grocify using **Jetpack Compose Multiplatform**, **Koin**, **SQLDelight**, and **Clerk** (Android SDK + iOS Swift host).

## Modules

- `shared` — domain, SQLite repository, ViewModels, Compose UI (Android + iOS)
- `androidApp` — Android launcher + Clerk Android SDK
- `iosApp` — SwiftUI shell: ClerkKit auth + Compose (`MainViewController`)

## Stack

- Kotlin Multiplatform + Compose Multiplatform + Material 3
- Koin 4
- SQLDelight (local SQLite per Clerk `user_id`)
- Clerk: Android (`clerk-android-ui`), iOS (`ClerkKit` / `ClerkKitUI` in Swift)
- `expect/actual` for auth UI, DB driver, platform DI

## Auth overview

| Platform | Auth UI | Session → Kotlin |
|----------|---------|------------------|
| Android | Compose `AuthView` | `AndroidAuthTokenProvider` / Clerk SDK |
| iOS | Swift `AuthView` in `ContentView` | `IosClerkBridge` → `IosAuthTokenProvider` |

**Required:** enable Clerk **Native API** and register apps — see [docs/CLERK_NATIVE_SETUP.md](docs/CLERK_NATIVE_SETUP.md).

Data is **local SQLite** (no Expo backend required).

## Run Android

1. Set `clerk.publishableKey` in `local.properties`
2. Enable Native API + register `com.grocify.android` (see docs above)
3. Android Studio → open this repo → Run **`androidApp`**

```bash
./gradlew :androidApp:installDebug
```

## Run iOS (macOS + Xcode)

1. Set `CLERK_PUBLISHABLE_KEY` in `iosApp/Configuration/Config.xcconfig`
2. Enable Native API + register Team ID + Bundle ID `com.grocify.ios`
3. Add SPM packages `ClerkKit` + `ClerkKitUI` from `https://github.com/clerk/clerk-ios`
4. Embed shared framework (Xcode build phase usually runs):

```bash
./gradlew :shared:embedAndSignAppleFrameworkForXcode
```

5. Open/create `iosApp` Xcode project, select a simulator, Run

If Compose mounts while signed out, you can use **«Продолжить без входа (debug)»** on the iOS Compose sign-in placeholder.

## Project layout

```
shared/src/commonMain/   # UI, ViewModels, SQLDelight, Koin common
shared/src/androidMain/  # Clerk Android, SQLite driver, SignInScreen
shared/src/iosMain/      # IosClerkBridge, SQLite driver, MainViewController
androidApp/              # Application + MainActivity
iosApp/                  # Swift Clerk host + ComposeView
docs/CLERK_NATIVE_SETUP.md
```
