# Kindling — Store Listing Info (for release)

Compiled for dual-platform submit (Android + iOS), modeled after LocalMind’s release pack.
Display name: **Kindling**. Package / IDs below.

Current builds (repo): Android **1.0.7** (versionCode **8**), iOS **1.0.5** (build **6**).
Align both before the next public upload (see `version.properties`).

---

## 0. Blockers — resolve before production submit

| # | Item | Status | Notes |
|---|------|--------|--------|
| 1 | **Sign out + Delete account** | ✅ In app | Settings → Sign out / Delete account (password re-auth) |
| 2 | **Privacy policy URL** | ✅ Shared | `https://www.markduenas.com/privacy` — also linked in Settings. Confirm page covers Firebase Auth, Firestore, Analytics, Crashlytics. |
| 3 | **Support URL / email** | ✅ Shared | `https://www.markduenas.com` — Play Console still needs a monitored contact email. |
| 4 | **Store screenshots** | ❌ Capture | Phone screens for both stores (see §4). Scorekeeper pattern: in-app `StoreScreenshotScreen` + `scripts/capture-store-screenshots.sh` (not ported yet). |
| 5 | **Play feature graphic** | ✅ Generated | `fastlane/metadata/android/en-US/images/featureGraphic.png` (1024×500). Regenerate: `python3 scripts/generate_feature_graphic.py`. **Upload blocked by SA 403** — manual Play Console upload or grant service account store-presence/Admin, then `python3 scripts/deploy/upload-play-listing.py`. |
| 6 | **Play hi-res icon** | ✅ Generated | `fastlane/metadata/android/en-US/images/icon.png` (512×512). Same upload path as feature graphic. |
| 7 | **Content rating (Play)** | ❌ Console | Store presence → App content questionnaire. Expect Everyone / PEGI 3. |
| 8 | **Age rating (App Store)** | ❌ Console | Expect 4+. |
| 9 | **Data Safety (Play) + App Privacy (Apple)** | ❌ Console | See §6 — account + analytics must be declared. |
| 10 | **Demo account for review** | ❌ Create | Seed a Firebase email/password user; put credentials in App Review notes. |
| 11 | **App Review notes** | ✅ Draft | Paste from `store-notes.md` (includes IAP). |
| 12 | **Version alignment** | ⬜ Before submit | Bump Android + iOS to the same marketing version. |
| 13 | **IAP products in stores** | ❌ Create | `premium_monthly` + `premium_yearly` — see `fastlane/metadata/BILLING_SETUP.md`. Code is implemented. |

---

## 1. App identity

| Field | Value |
|---|---|
| Store listing title | **Kindling — Wisdom & Insights** (≤30 chars on Play title if needed: **Kindling**) |
| Android launcher label | `Kindling` (`@string/app_name`) |
| iOS display name | `Kindling` (`PRODUCT_NAME`) |
| Android applicationId | `com.markduenas.insights` |
| iOS bundle identifier | `com.markduenas.insights.insights` |
| Category (Play) | Education or Lifestyle |
| Category (App Store) | Primary: Education — Secondary: Lifestyle / Health & Fitness |
| Min OS | Android 7.0 (API 24+) · iOS as in Xcode project |
| Backend | Firebase Auth (email/password), Firestore, Analytics, Crashlytics |

---

## 2. Google Play Store listing

Copy into Play Console or `fastlane/metadata/android/en-US/` (scaffolded).

**Title** (30 char max):
> Kindling — Wisdom Insights

**Short description** (80 char max):
> Curated wisdom + personal insights. Source-linked, local-first, private.

**Full description:**
> Kindling is a mobile companion for collecting and revisiting the insights that shape how you think and live.
>
> DISCOVER
>
> Browse an admin-curated library of insights drawn from books, philosophy, science, and lived experience. Every entry includes source attribution so you can go deeper.
>
> CAPTURE
>
> Save personal insights with title, body, tags, and optional source. Personal entries stay on your device first and sync to the cloud when you’re signed in.
>
> CONNECT
>
> Smart matching links your personal captures to related curated insights so your private notes sit next to shared wisdom.
>
> PRIVACY & ACCOUNT
>
> Sign-in is optional for browsing. Accounts use email and password (Firebase). You can sign out or permanently delete your account and personal data from Settings.
>
> Free to download. No ads.

**What's new (first public / this release cycle):**
> Sign out and account deletion, Settings with privacy links, Kindling branding, and release readiness for both stores.

**Graphics:**
- Phone screenshots — capture per §4
- Feature graphic 1024×500 — design
- Hi-res icon 512×512 — export from brand icon

Deploy note: `scripts/deploy/deploy-android.js` uploads the AAB; listing assets are manual unless you extend the script.

---

## 3. Apple App Store listing

**Name** (30 char max):
> Kindling — Wisdom Insights

**Subtitle** (30 char max):
> Curated + personal wisdom

**Promotional text** (170 char, editable anytime):
> Collect insights that stick. Browse curated wisdom, capture your own, and link them together—with sources on every entry.

**Keywords** (100 char, comma-separated):
> insight,wisdom,quotes,stoic,learning,journal,books,philosophy,notes,self

**Description:**
> Kindling helps lifelong learners collect, revisit, and connect insights from books, philosophy, and personal reflection.
>
> - Curated library with source attribution
> - Personal insight capture (local-first + optional cloud sync)
> - Search and tags
> - Smart matching between personal and common insights
> - Optional sign-in; full account deletion in Settings
>
> Privacy policy: https://www.markduenas.com/privacy

**What's New:**
> Account sign-out and deletion, Settings with privacy and support links, Kindling branding polish.

---

## 4. Screenshot plan (capture on device / simulator)

Suggested order (min 2, ideally 5–8):

1. **Home / library** — list of curated insights (seeded content)
2. **Search / tag filter** — active search or tag chip
3. **Insight detail** — full body + source line
4. **Personal insights** — list of user-owned entries
5. **Add personal insight** — form mid-entry
6. **Sign in** — Kindling branding
7. **Settings** — account + privacy links (proves delete account exists)

| Platform | Size |
|----------|------|
| Android phone | 1080×2400 (or 1080×1920) |
| iOS 6.7" | 1290×2796 (iPhone 15 Pro Max) |
| iPad (if listed) | 2048×2732 |

---

## 5. Encryption export compliance

`iosApp/iosApp/Info.plist` sets `ITSAppUsesNonExemptEncryption = false` (standard HTTPS / platform crypto only; no custom non-exempt crypto).

---

## 6. Data Safety / App Privacy — how to answer

Kindling **does** collect data when users sign in or when Analytics/Crashlytics run.

**Google Play Data Safety (high level):**
- **Yes**, app collects/shares data
- **Personal info:** Email address (account) — app functionality, account management
- **App activity / crash logs / diagnostics:** Firebase Analytics + Crashlytics
- **User-generated content:** Personal insights (stored on device + Firestore for signed-in users)
- Not sold; not used for ads
- Data encrypted in transit (HTTPS/TLS)
- Users can request deletion via in-app **Delete account** (Settings)

**Apple App Privacy:**
- Contact Info: Email (linked to user, App Functionality)
- User Content: Other User Content / free-form text (personal insights)
- Identifiers / Usage Data / Diagnostics as required by Firebase Analytics & Crashlytics
- Not used for tracking (no ATT / ad network); answer Tracking = No unless you enable Ad Support later

---

## 7. Deploy commands

```bash
# Android → Play internal track (default)
node scripts/deploy/deploy-android.js --track internal

# iOS → TestFlight
bash scripts/deploy/deploy-ios.sh
```

Prereqs: signing in `local.properties`, Play service account, ASC API key (see script headers).

---

## 8. Pre-submit checklist

- [ ] Bump & align Android + iOS versions
- [ ] Internal/TestFlight smoke: sign up, browse, personal save, match, sign out, delete account
- [ ] Screenshots + feature graphic uploaded
- [ ] Privacy + support URLs set in both consoles
- [ ] Questionnaires complete (rating + data safety / privacy)
- [ ] Demo account in App Review notes
- [ ] Production Firestore rules deployed
- [ ] Seeder healthy / library non-empty on first launch
