/**
 * set-admin-claim.js
 *
 * One-time script to grant admin privileges to a Firebase user.
 *
 * Usage (local):
 *   node set-admin-claim.js --key-file ../../composeApp/insights-b91e8-3190503df694.json <USER_UID>
 *
 * Usage (CI):
 *   FIREBASE_SERVICE_ACCOUNT=<base64-json> node set-admin-claim.js <USER_UID>
 *
 * To find your UID: Firebase Console → Authentication → Users → copy the User UID column.
 */

const admin = require('firebase-admin');
const fs = require('fs');
const path = require('path');

// Strip --key-file arg before reading UID positional arg
const args = process.argv.slice(2);
const keyFileIdx = args.indexOf('--key-file');
let keyFilePath = null;
if (keyFileIdx !== -1) {
  keyFilePath = args.splice(keyFileIdx, 2)[1]; // remove --key-file <path> from args
}

const uid = args[0];
if (!uid) {
  console.error('Usage: node set-admin-claim.js [--key-file <path>] <USER_UID>');
  process.exit(1);
}

function resolveServiceAccount() {
  if (keyFilePath) {
    const keyPath = path.resolve(keyFilePath);
    console.log(`Using service account file: ${keyPath}`);
    return JSON.parse(fs.readFileSync(keyPath, 'utf8'));
  }
  if (process.env.FIREBASE_SERVICE_ACCOUNT) {
    return JSON.parse(
      Buffer.from(process.env.FIREBASE_SERVICE_ACCOUNT, 'base64').toString('utf8')
    );
  }
  throw new Error(
    'Provide --key-file <path> or set FIREBASE_SERVICE_ACCOUNT env variable.'
  );
}

const serviceAccount = resolveServiceAccount();
admin.initializeApp({ credential: admin.credential.cert(serviceAccount) });

admin.auth()
  .setCustomUserClaims(uid, { admin: true })
  .then(() => {
    console.log(`✅ Admin claim set for user: ${uid}`);
    console.log('The user must sign out and sign back in for the claim to take effect.');
    process.exit(0);
  })
  .catch(err => {
    console.error('Failed to set admin claim:', err.message);
    process.exit(1);
  });
