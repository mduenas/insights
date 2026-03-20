/**
 * Bulk Approve Pending Insights
 *
 * Copies all documents from `pending_insights` → `insights` with status APPROVED,
 * then deletes them from `pending_insights`.
 *
 * Usage:
 *   node approve-all-pending.js --key-file ../../composeApp/insights-b91e8-3190503df694.json
 */

const admin = require('firebase-admin');
const path = require('path');

// ── Firebase initialization ──────────────────────────────────────────────────
function resolveServiceAccount() {
  const keyFileIdx = process.argv.indexOf('--key-file');
  if (keyFileIdx !== -1 && process.argv[keyFileIdx + 1]) {
    const keyPath = path.resolve(process.argv[keyFileIdx + 1]);
    return require(keyPath);
  }
  if (process.env.FIREBASE_SERVICE_ACCOUNT) {
    return JSON.parse(Buffer.from(process.env.FIREBASE_SERVICE_ACCOUNT, 'base64').toString('utf8'));
  }
  throw new Error('Provide --key-file <path> or FIREBASE_SERVICE_ACCOUNT env var');
}

const serviceAccount = resolveServiceAccount();
admin.initializeApp({ credential: admin.credential.cert(serviceAccount) });
const db = admin.firestore();

// ── Main ─────────────────────────────────────────────────────────────────────
async function approveAll() {
  const pendingSnap = await db.collection('pending_insights').get();

  if (pendingSnap.empty) {
    console.log('No pending insights found.');
    return;
  }

  console.log(`Found ${pendingSnap.size} pending insight(s). Approving...`);

  // Firestore batches are limited to 500 ops; 2 ops per doc (set + delete)
  const BATCH_SIZE = 200;
  const docs = pendingSnap.docs;
  let approved = 0;

  for (let i = 0; i < docs.length; i += BATCH_SIZE) {
    const batch = db.batch();
    const chunk = docs.slice(i, i + BATCH_SIZE);

    for (const doc of chunk) {
      const data = doc.data();
      const insightRef = db.collection('insights').doc(doc.id);
      batch.set(insightRef, { ...data, status: 'APPROVED', updatedAt: Date.now() });
      batch.delete(doc.ref);
    }

    await batch.commit();
    approved += chunk.length;
    console.log(`  Approved ${approved}/${docs.length}...`);
  }

  console.log(`\n✅ Done — ${approved} insight(s) moved to 'insights' collection.`);
}

approveAll().catch(err => {
  console.error('Error:', err.message);
  process.exit(1);
});
