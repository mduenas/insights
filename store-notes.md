# App Store Connect — App Review Information Notes (Kindling)

Paste the plain-text block in §2 into  
**App Store Connect → Kindling → App Review Information → Notes**.

Create a demo account in Firebase Auth (email/password) before submit and fill the placeholders.

---

## 1. Reviewer path (~2–3 minutes)

1. Launch Kindling (cold start).
2. **Continue without signing in** (optional) → browse curated insights on Home.
3. Or **Sign In** with the demo account below → Home.
4. Open an insight → show source attribution on detail.
5. **My Insights** (person icon) → **Add** a personal insight (title/body) → save.
6. Search or tap a tag on Home.
7. **Settings** (gear) → show Privacy policy link, **Sign out**, and **Delete account** (do not delete the shared demo account unless you recreate it).
8. Optional: Feedback icon → send feedback / request topic.

**N/A by design:**
- No in-app purchases in this build.
- No UGC social feed / reporting / blocking (personal insights are private to the account).
- No microphone, camera, location, contacts, or ATT.

---

## 2. App Review Notes (paste)

```text
APP REVIEW NOTES — Kindling

Bundle ID: com.markduenas.insights.insights
Android package: com.markduenas.insights
Business model: Free to download. Optional Kindling Premium via auto-renewable subscriptions.

IAP product IDs (both unlock the same Premium):
- premium_monthly ($4.99/mo target)
- premium_yearly ($39.99/yr target)

Premium unlocks: unlimited personal insights (free cap 25), cloud backup when signed in, JSON export.
Free: full curated library browse/search, personal capture up to 25, local-only storage.

1) DEMO ACCOUNT
Email: [FILL: demo reviewer email]
Password: [FILL: demo password]
Created specifically for App Review. Full access to browse curated insights and create personal insights on the free tier.

Sandbox IAP: use a Sandbox Apple ID (Users and Access → Sandbox). Settings → Upgrade to Premium → purchase monthly or yearly (or Restore purchases).

2) CORE FLOW
- Launch → Sign In with demo credentials (or Continue without signing in to browse only).
- Home: curated insight library; search and tag filters.
- Tap an insight for detail + source attribution.
- Person icon → personal insights; add/delete personal entries (local-first; cloud sync when Premium + signed in).
- Settings (gear): Premium upgrade/restore, Privacy policy, Support, Sign out, Delete account (password confirmation).

3) ACCOUNT DELETION
Settings → Delete account → confirm with password. Deletes Firebase Auth user, remote personal insights, and local personal data. Please do not permanently delete the shared demo account without notifying us; create a throwaway account if you need to test deletion end-to-end.

4) DEVICES / OS TESTED
- Android emulator/device API 24+
- iOS Simulator / physical device (latest Xcode project target)
Minimum: Android 7.0 · iOS as configured in Xcode.

5) PURPOSE AND AUDIENCE
Kindling is a personal and communal reference for insights—distilled wisdom from books, philosophy, science, and reflection. Users browse an admin-curated library and optionally capture personal insights with source attribution and smart matching.

6) EXTERNAL SERVICES
- Firebase Authentication (email/password)
- Cloud Firestore (curated insights, personal sync, feedback)
- Firebase Analytics + Crashlytics
Privacy policy: https://www.markduenas.com/privacy
Support: https://www.markduenas.com

7) REGIONAL DIFFERENCES
None. Same features and content pipeline in all regions.

8) ENCRYPTION
ITSAppUsesNonExemptEncryption = false (exempt / standard HTTPS only).
```

---

## 3. Play Console notes (internal testing / production)

Use the same demo account for closed testing if reviewers need credentials.  
Data safety answers: see `stores-info.md` §6.
