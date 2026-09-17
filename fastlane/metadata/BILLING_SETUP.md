# Kindling — In-App Purchase setup (checklist)

Use this when creating products in **App Store Connect** and **Google Play Console**.  
Code already expects these exact product IDs.

---

## Product IDs (must match code)

| Product ID | Type | Target price (USD) | Period |
|------------|------|--------------------|--------|
| `premium_monthly` | Auto-renewable subscription | **$4.99** | 1 month |
| `premium_yearly` | Auto-renewable subscription | **$39.99** | 1 year |

Both unlock the same **Kindling Premium** entitlement in-app:

- Unlimited personal insights (free tier: **25**)
- Cloud backup / multi-device sync (**requires sign-in**)
- Export personal insights as JSON

Reference in code: `ProductIds` in `composeApp/.../billing/BillingProduct.kt`.

Local iOS sandbox catalog: `iosApp/iosApp/Kindling.storekit`  
Shared scheme references it for simulator purchases.

---

## A. Google Play Console

Package: **`com.markduenas.insights`**

### Prerequisites
- [ ] App exists; at least one **internal testing** release uploaded (AAB)
- [ ] Play Console account is merchant-enabled (payments profile)
- [ ] License testers added: Setup → License testing (Gmail accounts)

### Create subscriptions
1. **Monetize with Play → Products → Subscriptions → Create subscription**
2. **Monthly**
   - Product ID: `premium_monthly` (immutable — type carefully)
   - Name: Kindling Premium Monthly  
   - Description: Unlimited personal insights, cloud backup, and export.
   - Add **base plan** (e.g. `monthly` or `p1m`):
     - Billing period: 1 month  
     - Price: $4.99 (or auto-convert)  
     - Renewal: auto  
   - Activate base plan + subscription
3. **Yearly**
   - Product ID: `premium_yearly`
   - Name: Kindling Premium Yearly  
   - Base plan (e.g. `yearly` / `p1y`): 1 year, $39.99  
   - Activate
4. [ ] Optional: free trial / intro offer (not required for v1)
5. [ ] Grace period / account hold: use Play defaults or 7–16 days grace if offered

### Testing
- [ ] Device/emulator signed in with a **license tester** account  
- [ ] App installed from **internal testing** track (not side-load only, if products don’t resolve)  
- [ ] Settings → Upgrade → prices load from Play  
- [ ] Complete test purchase (test card / license response)  
- [ ] Restore purchases after reinstall  

### Notes
- Product IDs cannot be reused if you delete them — double-check spelling.  
- Billing Library queries **SUBS** only for these two IDs.  
- Feature graphic / listing uploads are separate from IAP setup.

---

## B. App Store Connect

Bundle ID: **`com.markduenas.insights.insights`** (confirm in Xcode / store listing)

### Prerequisites
- [ ] Paid Apps Agreement + banking/tax active  
- [ ] App record created for Kindling  
- [ ] Sandbox testers: Users and Access → Sandbox → Testers  

### Create subscription group
1. App → **Subscriptions** → **Create** group  
2. Reference name: **Kindling Premium**  
3. Localization (en-US): display name “Kindling Premium”

### Add subscriptions
1. **premium_monthly**
   - Product ID: `premium_monthly`  
   - Duration: 1 month  
   - Price: $4.99 tier  
   - Localization: “Kindling Premium Monthly” / short description of benefits  
2. **premium_yearly**
   - Product ID: `premium_yearly`  
   - Duration: 1 year  
   - Price: $39.99 tier  
   - Localization: “Kindling Premium Yearly”  
3. [ ] Set subscription group ranking (yearly often higher value / lower rank number)  
4. [ ] Review screenshot / review notes for IAP if prompted  
5. [ ] Submit products with the app version (or clear “Ready to Submit”)

### Local / TestFlight testing
- [ ] Xcode scheme uses **StoreKit Configuration**: `Kindling.storekit` (shared scheme included)  
- [ ] Simulator: Settings → Upgrade → buy monthly/yearly without real charge  
- [ ] Device: Sandbox Apple ID under Settings → App Store → Sandbox Account  
- [ ] TestFlight: real sandbox transactions against App Store Connect products  

### App Review
Paste IAP section from root `store-notes.md` (product IDs + sandbox path).  
Demo account can stay free-tier; reviewer can use sandbox to buy Premium.

---

## C. QA without stores (debug builds only)

Debug/debuggable builds show **Settings → Developer → Grant Premium**:

- Forces Premium entitlement without Play/App Store  
- **Not shown in release builds** (`BuildConfig.DEBUG` / Native `isDebugBinary`)  
- Survives relaunch until toggled off; store verify does not clear the debug override  

Use this to test: 25-cap bypass, cloud sync when signed in, export, paywall dismiss path.

---

## D. Privacy / legal (both consoles)

- [ ] Privacy policy mentions in-app purchases processed by Apple/Google  
- [ ] Subscription disclosure in store description (auto-renew, cancel anytime)  
- [ ] Play Data Safety / Apple App Privacy already cover account + analytics; IAP is via platform  

Suggested store description blurb:

> Optional Kindling Premium (auto-renewable): unlimited personal insights, cloud backup, and export. Monthly or yearly. Cancel anytime in your store account settings.

---

## E. Code map

| Layer | Location |
|-------|----------|
| Product IDs | `composeApp/.../billing/BillingProduct.kt` |
| Repository | `composeApp/.../data/BillingRepository.kt`, `PremiumRepository.kt` |
| Android | `BillingService.android.kt` + Play Billing 7.x |
| iOS | `StoreKitBridge.swift`, `BillingService.ios.kt`, `Kindling.storekit` |
| Paywall UI | `presentation/paywall/` |
| Gates | `AddInsightScreenModel` (25), `PersonalInsightRepositoryImpl` (cloud) |

---

## F. Done when

- [ ] Both product IDs active on Play + App Store Connect  
- [ ] Debug Grant Premium works on debug install  
- [ ] Real sandbox purchase + restore works on each platform  
- [ ] `store-notes.md` demo email filled; App Review notes pasted  
- [ ] Release build has **no** Developer toggle  
