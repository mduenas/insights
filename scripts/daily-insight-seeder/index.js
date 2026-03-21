/**
 * Daily Insight Seeder
 *
 * Writes a batch of curated insights to Firestore `pending_insights` collection.
 * An admin approves them via the in-app admin screen before they appear publicly.
 *
 * Usage (local):
 *   node index.js --key-file ../../composeApp/insights-b91e8-3190503df694.json
 *
 * Usage (CI/GitHub Actions):
 *   FIREBASE_SERVICE_ACCOUNT=<base64-encoded-service-account-json> node index.js
 *
 * Environment variables:
 *   FIREBASE_SERVICE_ACCOUNT  — Base64-encoded Firebase service account JSON
 *   INSIGHTS_PER_RUN          — Number of insights to add per run (default: 3)
 *   SEED_FILE                 — Path to a JSON seed file (default: seeds/insights.json)
 */

const admin = require('firebase-admin');
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

// ── Firebase initialization ──────────────────────────────────────────────────

function resolveServiceAccount() {
  // --key-file <path> argument takes precedence (for local runs)
  const keyFileIdx = process.argv.indexOf('--key-file');
  if (keyFileIdx !== -1 && process.argv[keyFileIdx + 1]) {
    const keyPath = path.resolve(process.argv[keyFileIdx + 1]);
    console.log(`Using service account file: ${keyPath}`);
    return JSON.parse(fs.readFileSync(keyPath, 'utf8'));
  }
  // Fall back to base64 env var (GitHub Actions)
  if (process.env.FIREBASE_SERVICE_ACCOUNT) {
    return JSON.parse(
      Buffer.from(process.env.FIREBASE_SERVICE_ACCOUNT, 'base64').toString('utf8')
    );
  }
  throw new Error(
    'Provide --key-file <path> or set FIREBASE_SERVICE_ACCOUNT env variable.'
  );
}

function initFirebase() {
  const serviceAccount = resolveServiceAccount();
  admin.initializeApp({ credential: admin.credential.cert(serviceAccount) });
}

// ── Seed data loading ────────────────────────────────────────────────────────

function loadSeedInsights() {
  const seedFile = process.env.SEED_FILE
    || path.join(__dirname, 'seeds', 'insights.json');

  if (!fs.existsSync(seedFile)) {
    // Return built-in fallback insights when no seed file is present
    return FALLBACK_INSIGHTS;
  }
  return JSON.parse(fs.readFileSync(seedFile, 'utf8'));
}

/** Strips the 'new' tag from any approved insights that currently carry it. */
async function clearNewTag(db) {
  const newSnap = await db.collection('insights')
    .where('tags', 'array-contains', 'new')
    .get();

  if (newSnap.empty) return;

  const BATCH_SIZE = 500;
  const docs = newSnap.docs;
  let cleared = 0;

  for (let i = 0; i < docs.length; i += BATCH_SIZE) {
    const batch = db.batch();
    const chunk = docs.slice(i, i + BATCH_SIZE);
    for (const doc of chunk) {
      batch.update(doc.ref, {
        tags: admin.firestore.FieldValue.arrayRemove('new'),
        updatedAt: Date.now(),
      });
    }
    await batch.commit();
    cleared += chunk.length;
  }

  console.log(`Cleared 'new' tag from ${cleared} existing insight(s).`);
}

/** Returns insights that haven't been seeded yet by checking existing IDs. */
async function getUnseededInsights(db, allInsights) {
  const existing = new Set();
  const snapshot = await db.collection('pending_insights').get();
  snapshot.forEach(doc => existing.add(doc.id));
  const approvedSnapshot = await db.collection('insights').get();
  approvedSnapshot.forEach(doc => existing.add(doc.id));
  return allInsights.filter(i => !existing.has(i.id));
}

// ── Topic request report ─────────────────────────────────────────────────────

/** Fetches topic_requests from Firestore, aggregates by topic, and logs a ranked list. */
async function reportTopicRequests(db) {
  const snapshot = await db.collection('topic_requests').get();
  if (snapshot.empty) return;

  const counts = {};
  snapshot.forEach(doc => {
    const topic = (doc.data().topic || '').toLowerCase().trim();
    if (topic) counts[topic] = (counts[topic] || 0) + 1;
  });

  const ranked = Object.entries(counts)
    .sort(([, a], [, b]) => b - a);

  console.log('\nTop requested topics:');
  ranked.forEach(([topic, count], i) => {
    console.log(`  ${i + 1}. ${topic} (${count} request${count === 1 ? '' : 's'})`);
  });
  console.log('');
}

// ── Seeding ──────────────────────────────────────────────────────────────────

async function seedInsights() {
  initFirebase();
  const db = admin.firestore();
  const perRun = parseInt(process.env.INSIGHTS_PER_RUN || '3', 10);

  const allInsights = loadSeedInsights();
  const unseeded = await getUnseededInsights(db, allInsights);

  if (unseeded.length === 0) {
    console.log('All insights have already been seeded.');
  } else {
    await clearNewTag(db);

    const batch = db.batch();
    const toSeed = unseeded.slice(0, perRun);
    const now = Date.now();

    toSeed.forEach(insight => {
      const id = insight.id || crypto.randomUUID();
      const ref = db.collection('pending_insights').doc(id);
      const tags = Array.isArray(insight.tags) ? insight.tags : [];
      batch.set(ref, {
        ...insight,
        id,
        category: 'COMMON',
        status: 'PENDING',
        tags: tags.includes('new') ? tags : [...tags, 'new'],
        titleLower: insight.title.toLowerCase(),
        createdAt: now,
        updatedAt: now,
      });
    });

    await batch.commit();
    console.log(`Seeded ${toSeed.length} insight(s) to pending_insights:`);
    toSeed.forEach(i => console.log(` - ${i.title}`));
  }

  await reportTopicRequests(db);
}

// ── Fallback insights (used when no seed file is provided) ───────────────────

const FALLBACK_INSIGHTS = [
  {
    id: 'stoic-01',
    title: 'The obstacle is the way',
    body: 'What stands in the way becomes the way. Every adversity contains within it the seed of an equivalent or greater benefit.',
    source: { title: 'Meditations', author: 'Marcus Aurelius', year: 180 },
    tags: ['stoicism', 'resilience', 'mindset'],
  },
  {
    id: 'stoic-02',
    title: 'You have power over your mind, not outside events',
    body: 'Realize this, and you will find strength. It is not events that disturb people, but their judgements about events.',
    source: { title: 'Meditations', author: 'Marcus Aurelius', year: 180 },
    tags: ['stoicism', 'mindset', 'control'],
  },
  {
    id: 'feynman-01',
    title: "If you can't explain it simply, you don't understand it well enough",
    body: 'The Feynman Technique: explain any concept in simple language as if teaching a child. Gaps in your explanation reveal gaps in your understanding.',
    source: { title: 'Surely You\'re Joking, Mr. Feynman!', author: 'Richard Feynman', year: 1985 },
    tags: ['learning', 'understanding', 'teaching'],
  },
  {
    id: 'habits-01',
    title: 'You do not rise to the level of your goals, you fall to the level of your systems',
    body: 'Goals are about the results you want to achieve. Systems are about the processes that lead to those results. Focus on systems.',
    source: { title: 'Atomic Habits', author: 'James Clear', year: 2018 },
    tags: ['habits', 'systems', 'productivity'],
  },
  {
    id: 'mindset-01',
    title: 'The growth mindset believes abilities can be developed',
    body: 'People with a growth mindset believe that their most basic abilities can be developed through dedication and hard work—brains and talent are just the starting point.',
    source: { title: 'Mindset: The New Psychology of Success', author: 'Carol Dweck', year: 2006 },
    tags: ['mindset', 'growth', 'learning'],
  },
];

// ── Entry point ──────────────────────────────────────────────────────────────

seedInsights()
  .then(() => process.exit(0))
  .catch(err => { console.error('Seeding failed:', err); process.exit(1); });
