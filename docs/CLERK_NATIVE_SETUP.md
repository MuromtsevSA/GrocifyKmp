# Clerk Native setup (Android + iOS)

Clerk Android/iOS SDKs talk to Clerk’s **Native API**. Without it you get blank screens / `Failed to load client`.

## 1. Enable Native API

1. Open [Clerk Dashboard](https://dashboard.clerk.com)
2. Go to **Configure → Native applications**  
   Direct link (replace instance if needed): https://dashboard.clerk.com/~/native-applications
3. Turn **Native API** **ON**

## 2. Register Android app

On the same page → **Add application** → Android:

| Field | Value |
|--------|--------|
| Package name | `com.grocify.android` |

Publishable key already in `local.properties`:

```properties
clerk.publishableKey=pk_test_...
```

Rebuild / reinstall the app after enabling Native API.

## 3. Register iOS app

**Add application** → iOS:

| Field | Where to get it |
|--------|------------------|
| **App ID Prefix (Team ID)** | Apple Developer → Membership → Team ID, or Xcode → Signing & Capabilities |
| **Bundle ID** | `com.grocify.ios` (see `iosApp/Configuration/Config.xcconfig`) |

Publishable key for iOS: `iosApp/Configuration/Config.xcconfig` → `CLERK_PUBLISHABLE_KEY`  
(same `pk_test_…` as Android).

## 4. iOS: add Clerk SPM packages (Mac + Xcode)

Clerk iOS is **Swift-only** (`ClerkKit` / `ClerkKitUI`). Auth UI runs in `iosApp`; Kotlin gets the session via `IosClerkBridge`.

1. On a Mac, open the iOS project in Xcode (create/generate `iosApp.xcodeproj` if missing — see README).
2. **File → Add Package Dependencies…**
3. URL: `https://github.com/clerk/clerk-ios`
4. Add products to the **iosApp** target:
   - `ClerkKit`
   - `ClerkKitUI`
5. Ensure `Config.xcconfig` is applied to the target (so `CLERK_PUBLISHABLE_KEY` reaches `Info.plist`).
6. Build & run on simulator.

Flow:

```
Swift AuthView (Clerk) → setSession(userId, jwt) on IosClerkBridge
       → Compose App (shared) uses IosAuthTokenProvider / IosCurrentUserProvider
Sign out in Kotlin → IosClerkBridge.requestHostSignOut() → Clerk.auth.signOut()
```

## 5. Optional: social login (Google / Apple)

Only if you enable them in Clerk:

- **Google**: OAuth clients in Google Cloud + credentials in Clerk; register Android package / iOS Bundle ID
- **Apple**: enable Apple in Clerk; register Team ID + Bundle ID; add Sign in with Apple capability in Xcode

Email/password / email code work without extra OAuth setup once Native API is on.

## Checklist

- [ ] Native API enabled
- [ ] Android package `com.grocify.android` registered
- [ ] iOS Team ID + Bundle ID `com.grocify.ios` registered
- [ ] Same publishable key on Android (`local.properties`) and iOS (`Config.xcconfig`)
- [ ] iOS SPM: ClerkKit + ClerkKitUI
- [ ] Cold restart app after Dashboard changes
