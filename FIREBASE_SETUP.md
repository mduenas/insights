# Firebase Setup Guide

This project requires a Firebase project with Firestore and Authentication enabled.

## 1. Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Click **Add project** → name it `insights`
3. Disable Google Analytics (optional) → **Create project**

## 2. Add Android App

1. In the Firebase project, click **Add app** → Android
2. Package name: `com.markduenas.insights`
3. App nickname: `Insights Android`
4. Download `google-services.json`
5. Place it at: `composeApp/google-services.json`
6. In `composeApp/build.gradle.kts`, uncomment the line:
   ```kotlin
   alias(libs.plugins.googleServices)
   ```

## 3. Add iOS App

1. Click **Add app** → iOS
2. Bundle ID: (find in `iosApp/iosApp.xcodeproj` → target → General → Bundle Identifier)
3. App nickname: `Insights iOS`
4. Download `GoogleService-Info.plist`
5. Add it to the Xcode project under `iosApp/iosApp/` (drag into Xcode navigator)

## 4. Enable Firestore

1. In Firebase Console → **Firestore Database** → **Create database**
2. Start in **test mode** (change rules before production)
3. Choose a region close to your users

## 5. Enable Authentication

1. In Firebase Console → **Authentication** → **Get started**
2. Enable **Email/Password** provider
3. Enable **Google** provider (requires SHA-1 fingerprint for Android)

## 6. Set Admin Custom Claim

To grant admin privileges to your account, run this Node.js snippet once
(requires `firebase-admin` and a service account key):

```javascript
const admin = require('firebase-admin');
admin.initializeApp({ credential: admin.credential.cert('./service-account.json') });
admin.auth().setCustomUserClaims('<YOUR_UID>', { admin: true });
```

## 7. Firestore Collections

The app uses these top-level collections:
- `insights` — approved common insights
- `pending_insights` — awaiting admin review
- `users/{uid}/personal_insights` — synced personal insights

## 8. Firestore Security Rules (starter)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Common insights: readable by all, writable only by admin
    match /insights/{id} {
      allow read: if true;
      allow write: if request.auth.token.admin == true;
    }
    // Pending insights: writable by admin or seeder service account
    match /pending_insights/{id} {
      allow read, write: if request.auth.token.admin == true;
    }
    // Personal insights: owned by the user
    match /users/{uid}/personal_insights/{id} {
      allow read, write: if request.auth != null && request.auth.uid == uid;
    }
  }
}
```
