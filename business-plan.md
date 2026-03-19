# Insights App — Business, Release & Pricing Plan

---

## TABLE OF CONTENTS

1. [Executive Summary](#1-executive-summary)
2. [Market Analysis](#2-market-analysis)
3. [Competitive Landscape](#3-competitive-landscape)
4. [Unique Value Proposition & Positioning](#4-unique-value-proposition--positioning)
5. [Business Model & Pricing Plan](#5-business-model--pricing-plan)
6. [Release Plan](#6-release-plan)
7. [Marketing & Growth Strategy](#7-marketing--growth-strategy)
8. [Revenue Projections](#8-revenue-projections)
9. [Risk Analysis & Mitigation](#9-risk-analysis--mitigation)
10. [App Name Candidates](#10-app-name-candidates)

---

## 1. EXECUTIVE SUMMARY

### The Idea

A mobile-first application (Android + iOS) that serves as a personal and communal reference for **insights** — distilled wisdom from books, philosophy, science, personal experience, and reflection. The app has two core functions:

1. **Discover** — Browse and search a curated, admin-approved library of common insights, seeded daily and enriched over time.
2. **Capture** — Record personal insights with source attribution, stored locally first with lazy cloud sync for backup and cross-device access.

Key differentiators:
- **Source attribution** on every insight (book, author, year, URL)
- **Smart matching** — personal insights are automatically linked to matching common insights via keyword scoring
- **Admin-curated quality** — every common insight goes through an approval pipeline before publication
- **Local-first architecture** — personal insights live on-device, synced lazily to the cloud for resilience and privacy
- **Cross-platform** — built with Kotlin Multiplatform / Compose Multiplatform for native performance on both platforms

### The Vision

Become the go-to "second brain for wisdom" — a simple, beautiful, trustworthy place where people collect, revisit, and connect the insights that shape how they think and live.

---

## 2. MARKET ANALYSIS

### 2.1 Market Size & Growth

| Metric | Value | Source |
|--------|-------|--------|
| Global self-improvement market (2025) | ~$64.5B | Research and Markets |
| Global self-improvement market (2026) | ~$70.2B (8.9% CAGR) | The Business Research Company |
| Projected by 2030 | ~$93.75B (7.5% CAGR) | MarketResearch.biz |
| Mobile wellness app TAM (2025) | $4.2B – $12.8B | Verified Market Research / Global Growth Insights |
| Mobile wellness app TAM (2027) | $5.9B – $15B+ | Coherent Market Insights |
| Long-term app projection (2035) | ~$21.6B (17.7% CAGR) | Global Growth Insights |

### 2.2 Target Segments

| Segment | Description | Estimated Size |
|---------|-------------|----------------|
| **Lifelong Learners** | Avid readers who highlight books, annotate articles, and want to retain key takeaways | ~50M globally |
| **Stoicism / Philosophy Enthusiasts** | Growing community around practical philosophy, Daily Stoic podcast audience alone is 2M+ | ~15M globally |
| **Self-Improvement Practitioners** | Journal keepers, habit trackers, goal setters who seek actionable wisdom | ~100M+ globally |
| **Students & Academics** | Those studying philosophy, psychology, literature who collect insights | ~30M globally |
| **Mindfulness / Meditation Users** | Overlap with wisdom/reflection apps like Calm, Headspace | ~75M globally |

### 2.3 Key Trends

1. **AI personalization** — users expect context-aware, personalized content recommendations
2. **Local-first / privacy** — increasing demand for on-device data ownership (Obsidian's success proves this)
3. **Spaced repetition for retention** — Readwise popularized the idea that insights need to be revisited, not just saved
4. **Social sharing of wisdom** — shareable quote cards drive organic growth (Stoi, Daily Stoic)
5. **Habit formation integration** — streaks, daily routines, and nudges increase retention
6. **Cross-platform parity** — users expect seamless experience across phone, tablet, and web

---

## 3. COMPETITIVE LANDSCAPE

### 3.1 Direct Competitors

| App | Category | Pricing | Strengths | Weaknesses |
|-----|----------|---------|-----------|------------|
| **Stoic** (getstoic.com) | Journaling + Stoic Wisdom | Free tier / $6.99/mo / $39.99/yr / $199.99 lifetime | Comprehensive journaling, AI prompts, mood tracking, Apple ecosystem | No personal insight capture, no source attribution, iOS-focused |
| **Stoa** | Stoic Philosophy Education | Free trial / $9.99/mo / $89.99/yr | Deep Stoic lectures, guided meditation, text archives | Theory-heavy, no personal content, no matching/linking |
| **StoicZone** | AI Stoic Companion | Free tier / $6.99/mo / $59.99/yr | AI coaching, habit tracking, Memento Mori | Narrow philosophy focus, no user-generated insights |
| **Daily Stoic** | Daily Quotes + Podcast | Free (content + ads) | Massive audience, brand recognition, Ryan Holiday network | Not a PKM tool, no personal capture, no admin curation |
| **Stoi** | Daily Stoic Wisdom | Free tier / Premium (varies) | Modern UI, social sharing, streaks, visual quote cards | Limited to Stoic content, no personal insights |
| **Agora** | Minimalist Stoic Reflection | $2.99/mo | Clean, focused, community features | Very minimal feature set |

### 3.2 Adjacent Competitors (PKM / Knowledge Management)

| App | Category | Pricing | Strengths | Weaknesses |
|-----|----------|---------|-----------|------------|
| **Readwise** | Highlight Retention | $5.59–$9.99/mo | Spaced repetition, multi-source import, Kindle/PDF sync | Not mobile-first, no curated library, no community insights |
| **Obsidian** | Local-first Notes + PKM | Free (sync $4/mo) | Backlinks, graph view, plugin ecosystem, Markdown | Complex for casual users, no curated content, no mobile-first |
| **Notion** | All-in-one Workspace | Free tier / $8–$10/mo | Incredibly flexible, databases, team collab | Overwhelming for simple use cases, not insight-focused |
| **Logseq** | Open-source Outliner + PKM | Free | Open-source, bidirectional links, daily journal | Steep learning curve, sparse mobile experience |
| **Capacities** | Object-based PKM | Free tier / $9.99/mo | Visual, AI-driven, modern | Newer product, limited mobile experience |
| **Matter** | Read-later + Highlights | Free core / Premium | Free highlight review, newsletters, articles | No personal insights, no curated wisdom library |
| **Loxie** | Active Recall from Books | Free / $7.99/mo / $59.99/yr | Spaced repetition quizzes, nonfiction book library | Pre-curated only, no personal content, no source linking |
| **Reflectly** | AI Journal | Free / $9.99/mo / $59.99/yr | Beautiful UI, mood tracking, AI prompts | Journal-only, no insight library, no source attribution |

### 3.3 Competitive Gap Analysis

| Feature | Our App | Stoic | Readwise | Obsidian | Stoi |
|---------|---------|-------|----------|----------|------|
| Curated common insight library | ✅ | Partial (quotes only) | ❌ | ❌ | ✅ |
| Personal insight capture | ✅ | ✅ (journaling) | ✅ (highlights) | ✅ (notes) | ❌ |
| Source attribution (book, author, year) | ✅ | ❌ | Partial (source link) | Manual | ❌ |
| Smart matching (personal → common) | ✅ | ❌ | ❌ | ❌ (manual links) | ❌ |
| Admin curation pipeline | ✅ | ❌ | ❌ | ❌ | ❌ |
| Local-first personal storage | ✅ | ❌ | ❌ | ✅ | ❌ |
| Cross-platform (Android + iOS) | ✅ | ✅ | ✅ (web/mobile) | ✅ | Partial |
| Daily insight seeding | ✅ | ✅ | ❌ (user-driven) | ❌ | ✅ |
| Spaced repetition / revisiting | 🟡 (planned) | ❌ | ✅ | ❌ | ❌ |
| Social sharing / quote cards | 🟡 (planned) | ❌ | ❌ | ❌ | ✅ |

**Our unique position**: We sit at the intersection of **curated wisdom** (like Stoic/Stoi) and **personal knowledge capture** (like Readwise/Obsidian), with source attribution and smart matching that nobody else offers in a single mobile-first package.

---

## 4. UNIQUE VALUE PROPOSITION & POSITIONING

### One-Liner

> "Your personal library of life's most important ideas — discover curated wisdom, capture your own, and see how they connect."

### Positioning Statement

For lifelong learners and self-improvement practitioners who want to **collect, revisit, and connect** the insights that matter most, Kindling is a mobile-first insight companion that combines an **admin-curated wisdom library** with **private, local-first personal capture** and **intelligent linking** — unlike Readwise (which only works with your existing highlights), Stoic apps (which are limited to philosophy quotes), or general PKM tools (which are complex and have no curated content).

### Core Value Pillars

1. **Discover** — A daily-growing, quality-curated library of insights spanning philosophy, psychology, science, business, creativity, and life
2. **Capture** — Record personal insights instantly, with structured source attribution, stored on-device for speed and privacy
3. **Connect** — Automatic matching surfaces when your personal insight echoes a common one, revealing patterns in your thinking
4. **Trust** — Every common insight is admin-reviewed before publication; personal insights stay local-first with optional backup

---

## 5. BUSINESS MODEL & PRICING PLAN

### 5.1 Monetization Strategy: Freemium + Subscription (Hybrid)

Based on market research, the hybrid freemium-to-subscription model is the dominant and most successful approach for self-improvement apps in 2025-2026. This maximizes user acquisition through a generous free tier while driving sustainable revenue through premium subscriptions.

### 5.2 Tier Breakdown

#### 🆓 FREE TIER — "Explorer"

| Feature | Included |
|---------|----------|
| Browse common insights library | ✅ Full access |
| Daily insight feed (3/day) | ✅ |
| Search common insights | ✅ |
| View insight details + source | ✅ |
| Save personal insights | ✅ Up to 25 total |
| Source attribution on personal | ✅ |
| Basic matching (personal → common) | ✅ |
| Cloud backup of personal insights | ❌ |
| Tags and collections | ❌ |
| Spaced repetition / daily review | ❌ |
| Social sharing / quote cards | ✅ (with watermark) |
| Ad-free experience | ❌ (tasteful, non-intrusive) |

**Purpose**: Low-friction entry. Users experience the core value immediately — browsing wisdom, capturing a few personal insights, and seeing the matching magic.

#### ⭐ PREMIUM TIER — "Seeker" ($4.99/mo or $39.99/yr)

| Feature | Included |
|---------|----------|
| Everything in Free | ✅ |
| Unlimited personal insights | ✅ |
| Cloud backup & cross-device sync | ✅ |
| Custom tags and collections | ✅ |
| Advanced search (full-text, by source, by tag) | ✅ |
| Spaced repetition / daily review deck | ✅ |
| Social sharing / quote cards (no watermark) | ✅ |
| Ad-free experience | ✅ |
| Export personal insights (JSON, Markdown) | ✅ |
| Insight statistics & reading patterns | ✅ |

**Purpose**: The "sweet spot" for engaged users. Removes all limits, adds retention tools (spaced repetition), and enables serious personal knowledge management.

#### 💎 PRO TIER — "Sage" ($9.99/mo or $79.99/yr)

| Feature | Included |
|---------|----------|
| Everything in Premium | ✅ |
| AI-powered insight recommendations | ✅ |
| AI-assisted personal insight enrichment | ✅ |
| Priority access to new common insights | ✅ |
| Insight journaling (extended reflection per insight) | ✅ |
| Advanced analytics (insight maps, theme tracking) | ✅ |
| API access (for personal integrations) | ✅ |
| Request insight topics / sources | ✅ |
| Beta access to new features | ✅ |

**Purpose**: Power users and the most engaged community members. AI features and advanced analytics justify the premium.

### 5.3 Alternative Revenue Streams

| Stream | Description | Estimated Contribution |
|--------|-------------|----------------------|
| **One-time purchases** | Themed insight packs (e.g., "50 Insights from Stoic Philosophy", "The Entrepreneur's Wisdom Pack") | 5–10% of revenue |
| **Affiliate partnerships** | Book links (Amazon Associates), course recommendations, event tickets | 3–5% of revenue |
| **Sponsored insights** | Carefully vetted, labeled sponsored wisdom from publishers/authors promoting new books | 2–3% of revenue |
| **Enterprise / Education** | Bulk licensing for schools, book clubs, corporate training programs | Long-term (Year 2+) |

### 5.4 Pricing Rationale

| Data Point | Implication |
|------------|-------------|
| Stoic app: $6.99/mo, $39.99/yr, $199.99 lifetime | Our $4.99/mo undercuts the market leader |
| Readwise: $5.59–$9.99/mo | Our Premium aligns with the low end; Pro with the high end |
| StoicZone: $6.99/mo, $59.99/yr | Our annual pricing is more competitive |
| Market avg conversion rate: 2–5% freemium, 10–25% with free trial | Target: 5% free→Premium, 1% free→Pro |
| Subscription fatigue consideration | Annual discount (~33% off monthly) incentivizes commitment |
| Lifetime option | Consider introducing at $199.99 after Year 1 for early loyalists |

---

## 6. RELEASE PLAN

### Phase 0 — Pre-Launch Foundation (Current → +4 weeks)

| Task | Details |
|------|---------|
| Complete Firebase admin claim + Firestore rules | Production security rules, admin account active |
| Seed initial insight library | 50–100 high-quality insights across 5–6 categories |
| UI/UX polish pass | Typography, spacing, animations, error states, empty states |
| App icon & branding | Commission or design icon, splash screen, color palette |
| Landing page | Single-page site: value prop, email signup, App Store waitlist |
| TestFlight / Play Console setup | Internal testing tracks ready |
| Analytics integration | Firebase Analytics + Crashlytics for both platforms |
| Privacy policy & Terms of Service | Required for both app stores |

### Phase 1 — Closed Alpha (+4 → +8 weeks)

| Task | Details |
|------|---------|
| Invite 20–50 alpha testers | Friends, family, target demographic from landing page signups |
| Core flow testing | Sign up → browse → search → save personal → view match |
| Crash & performance monitoring | Fix top crashers, optimize cold start time |
| Feedback collection | In-app feedback form or dedicated Discord/Slack channel |
| Content seeding | Grow library to 200+ insights; validate seeder pipeline |
| Iterate on matching algorithm | Tune keyword scoring based on real personal insights |

### Phase 2 — Open Beta (+8 → +14 weeks)

| Task | Details |
|------|---------|
| Expand to 200–500 beta testers | ProductHunt "upcoming", Reddit communities, Twitter/X |
| Implement premium features | Cloud sync, tags/collections, spaced repetition, export |
| Implement payment infrastructure | RevenueCat or StoreKit 2 / Google Play Billing for subscriptions |
| A/B test onboarding flow | Optimize first-time user experience for retention |
| Localization (if applicable) | At minimum: English; consider Spanish, Portuguese, German |
| Accessibility audit | VoiceOver/TalkBack, dynamic type, contrast ratios |
| App Store assets | Screenshots, preview video, description copy, keyword optimization |
| Soft launch in secondary market | e.g., Canada, Australia, UK — gather real-world conversion data |

### Phase 3 — Public Launch (+14 → +16 weeks)

| Task | Details |
|------|---------|
| Submit to App Store + Play Store | Both platforms simultaneously |
| ProductHunt launch | Schedule for Tuesday–Thursday for maximum visibility |
| Press outreach | Indie app review sites, philosophy/self-improvement bloggers |
| Social media campaign | Quote card teasers, founder story, "why I built this" thread |
| Email blast to waitlist | Convert pre-launch signups to Day 1 installs |
| Monitor reviews & respond | Reply to every review in first 2 weeks; fix urgent issues within 48h |
| Post-launch hotfix cycle | Daily builds for first week if needed |

### Phase 4 — Growth & Iteration (+16 → +30 weeks)

| Task | Details |
|------|---------|
| Social sharing / quote cards | Branded, beautiful shareable images for Instagram/Twitter |
| AI-powered recommendations | Personal insight analysis → suggest related common insights |
| Insight journaling | Extended reflection space attached to any insight |
| Widget support | iOS WidgetKit + Android Glance — "Insight of the Day" on home screen |
| Apple Watch / Wear OS | Quick capture and daily insight glanceable |
| Web companion app | Read-only web view of your insights library |
| Community features | Explore if users want to share/discover personal insights publicly |
| Content partnerships | Partner with authors, publishers, podcasters for exclusive insight packs |

### Phase 5 — Scale & Monetize Fully (+30 → +52 weeks)

| Task | Details |
|------|---------|
| Pro tier launch | AI features, advanced analytics, API access |
| Enterprise / Education tier | Bulk licensing, classroom features, book club integrations |
| Insight packs marketplace | Curated themed collections as one-time purchases |
| Affiliate revenue | Book recommendation links integrated with insight sources |
| International expansion | Full localization for top 5–10 markets |
| Annual user survey & roadmap | Community-driven feature prioritization |

---

## 7. MARKETING & GROWTH STRATEGY

### 7.1 Pre-Launch (Weeks -8 to 0)

| Channel | Tactic | Goal |
|---------|--------|------|
| Landing page | Email capture, "Get early access" CTA | 1,000+ signups |
| Twitter/X | Daily wisdom threads, behind-the-scenes development | 500+ followers |
| Reddit | r/Stoicism, r/selfimprovement, r/productivity posts | Community awareness |
| ProductHunt "Upcoming" | Pre-launch page with teaser | 200+ subscribers |
| Personal network | Friends & family alpha testing | 50 alpha testers |

### 7.2 Launch (Weeks 0–4)

| Channel | Tactic | Goal |
|---------|--------|------|
| ProductHunt | Full launch with demo video, founder story | Top 5 of the day |
| App Store Optimization | Optimized title, subtitle, keywords, screenshots | Rank for "wisdom", "insights", "quotes" |
| Press / Bloggers | Personalized outreach to 50+ indie app reviewers | 5–10 features |
| Social media | Daily insight quote cards with app branding | Organic sharing |
| Email waitlist | Conversion campaign: "We're live! Here's your exclusive first look" | 30%+ open rate |
| Referral program | "Share with 3 friends, unlock 1 month Premium free" | Viral coefficient > 1.2 |

### 7.3 Ongoing Growth (Months 2–12)

| Channel | Tactic | Goal |
|---------|--------|------|
| Content marketing | Blog: "50 Insights That Changed How I Think", listicles, book roundups | SEO traffic |
| SEO / ASO | Ongoing keyword optimization, A/B test screenshots quarterly | Steady organic growth |
| Social proof | User testimonials, App Store review responses, case studies | Build trust |
| Partnerships | Book authors, podcast hosts (guest appearances), philosophy communities | Audience cross-pollination |
| Paid acquisition (selective) | Apple Search Ads, targeted Instagram/TikTok (if ROI positive) | CAC < $3 per install |
| Newsletter | Weekly "3 Insights" email with app CTA | Retention + re-engagement |

### 7.4 Retention Strategy

| Mechanism | Description |
|-----------|-------------|
| Daily push notification | "Your insight of the day" — drives daily open |
| Streak system | Consecutive-day usage tracking with visual feedback |
| Spaced repetition (Premium) | "Time to revisit this insight" — proven retention driver |
| Weekly digest | "You captured 3 insights this week. Here's what matched." |
| Empty state prompts | When no personal insights yet: "What's the last thing you read that changed your mind?" |
| Widget | Home screen presence keeps the app top-of-mind |

---

## 8. REVENUE PROJECTIONS

### Assumptions

| Variable | Conservative | Moderate | Optimistic |
|----------|-------------|----------|------------|
| Year 1 total installs | 10,000 | 30,000 | 75,000 |
| Free → Premium conversion | 3% | 5% | 8% |
| Free → Pro conversion | 0.5% | 1% | 2% |
| Monthly churn (Premium) | 8% | 6% | 4% |
| Monthly churn (Pro) | 5% | 4% | 3% |
| Average revenue per Premium user/mo | $3.33 (annual) | $3.33 | $3.33 |
| Average revenue per Pro user/mo | $6.67 (annual) | $6.67 | $6.67 |

### Year 1 Monthly Recurring Revenue (Month 12)

| Scenario | Premium Subs | Pro Subs | MRR | ARR |
|----------|-------------|----------|-----|-----|
| Conservative | 300 | 50 | $1,332 | ~$16K |
| Moderate | 1,500 | 300 | $6,996 | ~$84K |
| Optimistic | 6,000 | 1,500 | $29,985 | ~$360K |

### Year 2 Projections (with compounding growth)

| Scenario | Installs (cumulative) | MRR (Month 24) | ARR |
|----------|----------------------|-----------------|-----|
| Conservative | 30,000 | $4,000 | ~$48K |
| Moderate | 100,000 | $20,000 | ~$240K |
| Optimistic | 300,000 | $80,000 | ~$960K |

### Break-Even Analysis

| Cost Category | Monthly Estimate |
|---------------|-----------------|
| Firebase (Blaze plan) | $25–$200 (scales with usage) |
| Apple Developer account | $8.25/mo ($99/yr) |
| Google Play Developer | One-time $25 |
| RevenueCat (if used) | Free up to $2.5K MTR, then 1% |
| Domain + hosting (landing page) | $10–$20/mo |
| **Total monthly fixed costs** | **~$50–$250** |

**Break-even at the Moderate scenario: Month 3–4** (covers infrastructure costs). True profitability (covering opportunity cost of time) depends on founder salary expectations.

---

## 9. RISK ANALYSIS & MITIGATION

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| **Low initial adoption** | Medium | High | Strong pre-launch community, ProductHunt, referral program; iterate fast on feedback |
| **Subscription fatigue / low conversion** | Medium | High | Generous free tier ensures value even without paying; lifetime option for committed users |
| **Content quality / staleness** | Medium | Medium | Daily seeder, admin curation, community contribution pipeline, content partnerships |
| **Competition from established PKM tools** | High | Medium | Differentiate on simplicity + curation + matching; don't try to be Notion/Obsidian |
| **Platform dependency (Firebase)** | Low | High | Abstract repository layer allows migration; local-first architecture as insurance |
| **Copyright issues with insight content** | Medium | Medium | Attribute all sources, focus on paraphrased wisdom not verbatim quotes, legal review |
| **Technical debt from KMP** | Low | Medium | KMP ecosystem maturing rapidly; Compose Multiplatform is production-ready |
| **App Store rejection** | Low | Medium | Follow all guidelines, thorough review of IAP implementation, have backup submission plan |
| **Founder burnout (solo dev)** | Medium | High | Set sustainable pace, automate what's possible (seeder, CI/CD), consider co-founder or contributor |

---

## 10. APP NAME CANDIDATES

### Naming Criteria
- **Memorable**: 1–3 syllables, easy to spell and say
- **Evocative**: Suggests wisdom, discovery, personal growth, or clarity
- **Unique**: Not already a major app in stores
- **Domain-friendly**: .app, .io, or .com available (verify before finalizing)
- **Global-friendly**: Works across languages and cultures

### Category A — "Insight" Rooted

| Name | Notes |
|------|-------|
| **Insightful** | Direct, clear, premium-sounding |
| **InsightVault** | Emphasizes secure personal collection |
| **InsightDrop** | Evokes daily delivery, a "drop" of wisdom |
| **InsightArc** | Journey/growth metaphor (arc of understanding) |
| **Insightly** | Friendly, approachable; check for CRM conflict |
| **Inkling** | "An inkling of insight" — playful, short, memorable |
| **Inklings** | Plural, also nods to C.S. Lewis/Tolkien literary group |

### Category B — Wisdom & Knowledge

| Name | Notes |
|------|-------|
| **Sapienta** | Latin for wisdom; elegant, unique, brandable |
| **Sapien** | "Wise one" (Latin); strong, short; check for conflicts |
| **Lumina** | Latin for "lights" — illumination, clarity |
| **Luminos** | Variation on light/illumination |
| **Gnosis** | Greek for "knowledge" — deep, philosophical |
| **Nous** | Greek for "mind/intellect" — compact, meaningful |
| **Sophora** | Derived from Greek "sophia" (wisdom) |
| **Minerva** | Roman goddess of wisdom; powerful brand imagery |
| **Athenaeum** | Place of learning; literary, distinguished |

### Category C — Discovery & Growth

| Name | Notes |
|------|-------|
| **Eureka** | "I found it!" — the joy of discovery; iconic |
| **Kindling** | Sparking ideas, warmth, beginning of fire |
| **Ember** | Small but powerful; a glowing insight that spreads |
| **Spark** | Instant recognition, energy, beginning of an idea |
| **Seedling** | Growth, nurturing, organic; aligns with daily seeding |
| **Beacon** | Guiding light; suggests direction and clarity |
| **Meridian** | Peak point, alignment, clarity |
| **Zenith** | The highest point; aspiration |
| **Ascent** | Upward journey; personal growth metaphor |
| **Flourish** | Thriving, growing, blooming through wisdom |

### Category D — Metaphorical & Abstract

| Name | Notes |
|------|-------|
| **Mosaic** | Pieces forming a beautiful whole — insights building understanding |
| **Prism** | Refracting light into many perspectives |
| **Lodestar** | Guiding star; timeless navigation metaphor |
| **Compass** | Direction, guidance, finding your way |
| **Wellspring** | Source of continuous wisdom; renewal |
| **Resonance** | When an insight "resonates" — deeply personal |
| **Axiom** | Self-evident truth; clean, intellectual |
| **Distill** | Extracting the essential from the complex |
| **Essence** | The core of something; purity, clarity |
| **Crystallize** | Making thoughts clear and solid |

### Category E — Compound & Brandable

| Name | Notes |
|------|-------|
| **MindMint** | Fresh thinking; "minting" new insights |
| **WiseNest** | A home for your wisdom |
| **ThinkWell** | Thinking clearly and deeply |
| **DeepNote** | Deep thoughts, noted — though check for data tool conflict |
| **ClearMind** | Clarity through collected wisdom |
| **BrightPage** | A page of illumination |
| **IdeaForge** | Where ideas are shaped and strengthened |
| **QuietWisdom** | Calm, reflective, understated |
| **StillWaters** | "Still waters run deep" — contemplation |
| **InnerLens** | Seeing clearly within yourself |

### Category F — Short & Punchy (Best for App Stores)

| Name | Notes |
|------|-------|
| **Gist** | The essential point; 4 letters, memorable |
| **Crux** | The decisive point; sharp, intellectual |
| **Nod** | Acknowledgment of truth; ultra-short |
| **Glean** | To gather bit by bit — perfectly describes the app |
| **Muse** | Source of inspiration; classic, versatile |
| **Tome** | A book of knowledge; literary weight |
| **Core** | The essential center; minimalist |
| **Sage** | A wise person; also our Pro tier name |
| **Lumen** | Unit of light; clarity, illumination |
| **Curio** | An object of curiosity; invites exploration |

### Top 10 Recommendations (Personal Ranking)

| Rank | Name | Why |
|------|------|-----|
| 1 | **Glean** | Perfect verb for the app's purpose; short, memorable, unique in category |
| 2 | **Lumen** | Beautiful, universal meaning (light); works globally; strong brand potential |
| 3 | **Inkling** | Playful yet meaningful; approachable; literary nod |
| 4 | **Wellspring** | Rich metaphor; continuous source of wisdom; premium feel |
| 5 | **Distill** | Exactly what the app does — extract the essential; clean, intellectual |
| 6 | **Ember** | Warm, evocative, small-but-powerful; excellent for brand imagery |
| 7 | **Sapienta** | Elegant Latin root; globally unique; wise + beautiful |
| 8 | **Resonance** | Emotional connection to wisdom; "this resonates with me" |
| 9 | **Mosaic** | Pieces forming a whole; connects personal + common insights visually |
| 10 | **Kindling** | Sparking wisdom; growth metaphor; warm brand feel |

---

## APPENDIX A — Competitor Links & References

### Direct Competitors
- Stoic App: https://www.getstoic.com
- Stoa: https://www.stoa.com
- Stoi: https://www.stoi.app
- StoicZone: https://www.stoiczone.com
- Daily Stoic: https://dailystoic.com

### Adjacent / PKM Competitors
- Readwise: https://readwise.io
- Obsidian: https://obsidian.md
- Notion: https://notion.so
- Logseq: https://logseq.com
- Capacities: https://capacities.io
- Matter: https://hq.getmatter.com
- Loxie: https://loxie.app

### Market Research Sources
- Self-Improvement Market: https://www.researchandmarkets.com/report/global-self-improvement-service-market
- Wellness App Market: https://www.verifiedmarketresearch.com/product/wellness-app-market/
- Self-Improvement Products 2026: https://www.thebusinessresearchcompany.com/report/self-improvement-products-and-services-global-market-report
- ASO Playbook: https://dev.to/iris1031/aso-app-store-optimization-complete-2026-playbook-584j
- App Pricing Models: https://tms-outsource.com/blog/posts/app-pricing-models/

---

*Document generated: March 2026*
*Project: Insights App (KMP / Compose Multiplatform)*
*Status: Pre-launch development*
