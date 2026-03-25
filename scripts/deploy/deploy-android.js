#!/usr/bin/env node
/**
 * deploy-android.js
 *
 * Builds the Android release AAB and uploads it to the Play Store internal track.
 *
 * Prerequisites:
 *   1. Google Play service account JSON at ~/.config/kindling-play-service-account.json
 *      (or set PLAY_SERVICE_ACCOUNT env var to the path)
 *   2. Release signing configured in local.properties
 *
 * Usage:
 *   node deploy-android.js [--track internal|alpha|beta|production]
 *
 * Tracks: internal (default), alpha, beta, production
 */

const { google } = require('googleapis');
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

// ── Config ────────────────────────────────────────────────────────────────────

const PACKAGE_NAME = 'com.markduenas.insights';
const REPO_ROOT = path.resolve(__dirname, '../..');
const AAB_PATH = path.join(REPO_ROOT, 'composeApp/build/outputs/bundle/release/composeApp-release.aab');
const SERVICE_ACCOUNT_PATH = process.env.PLAY_SERVICE_ACCOUNT
  || path.join(REPO_ROOT, '../play-store-key.json');

const trackArg = process.argv.indexOf('--track');
const TRACK = trackArg !== -1 ? process.argv[trackArg + 1] : 'internal';

// ── Preflight checks ──────────────────────────────────────────────────────────

function preflight() {
  if (!fs.existsSync(SERVICE_ACCOUNT_PATH)) {
    console.error(`\n❌ Service account not found at: ${SERVICE_ACCOUNT_PATH}`);
    console.error('   Set PLAY_SERVICE_ACCOUNT env var or place the JSON at the path above.');
    console.error('   To create one: Play Console → Setup → API access → Create service account');
    process.exit(1);
  }

  const validTracks = ['internal', 'alpha', 'beta', 'production'];
  if (!validTracks.includes(TRACK)) {
    console.error(`❌ Invalid track "${TRACK}". Must be one of: ${validTracks.join(', ')}`);
    process.exit(1);
  }
}

// ── Build ─────────────────────────────────────────────────────────────────────

function buildAAB() {
  console.log('🔨 Building release AAB...');
  try {
    execSync('./gradlew :composeApp:bundleRelease', {
      cwd: REPO_ROOT,
      stdio: 'inherit',
    });
  } catch {
    console.error('❌ Gradle build failed.');
    process.exit(1);
  }

  if (!fs.existsSync(AAB_PATH)) {
    console.error(`❌ AAB not found at expected path: ${AAB_PATH}`);
    process.exit(1);
  }

  console.log(`✅ AAB built: ${path.relative(REPO_ROOT, AAB_PATH)}\n`);
}

// ── Upload ────────────────────────────────────────────────────────────────────

async function upload() {
  const auth = new google.auth.GoogleAuth({
    keyFile: SERVICE_ACCOUNT_PATH,
    scopes: ['https://www.googleapis.com/auth/androidpublisher'],
  });

  const publisher = google.androidpublisher({ version: 'v3', auth });

  // 1. Open edit
  console.log('📝 Opening Play Store edit...');
  const { data: edit } = await publisher.edits.insert({ packageName: PACKAGE_NAME });
  const editId = edit.id;

  try {
    // 2. Upload AAB
    console.log(`📦 Uploading AAB to track: ${TRACK}...`);
    const { data: bundle } = await publisher.edits.bundles.upload({
      packageName: PACKAGE_NAME,
      editId,
      media: {
        mimeType: 'application/octet-stream',
        body: fs.createReadStream(AAB_PATH),
      },
    });
    console.log(`   Version code: ${bundle.versionCode}`);

    // 3. Assign to track
    await publisher.edits.tracks.update({
      packageName: PACKAGE_NAME,
      editId,
      track: TRACK,
      requestBody: {
        track: TRACK,
        releases: [{
          status: 'completed',
          versionCodes: [bundle.versionCode],
        }],
      },
    });

    // 4. Commit edit
    console.log('✅ Committing edit...');
    await publisher.edits.commit({ packageName: PACKAGE_NAME, editId });

    console.log(`\n🎉 Done — uploaded to Play Store (${TRACK} track)`);
    console.log(`   https://play.google.com/console/u/0/developers/app/${PACKAGE_NAME}`);
  } catch (err) {
    // Abort edit on failure
    await publisher.edits.delete({ packageName: PACKAGE_NAME, editId }).catch(() => {});
    throw err;
  }
}

// ── Entry point ───────────────────────────────────────────────────────────────

preflight();
buildAAB();
upload().catch(err => {
  console.error('❌ Upload failed:', err.message);
  process.exit(1);
});
