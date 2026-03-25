#!/bin/bash
# deploy-ios.sh
#
# Builds the iOS archive and uploads it to App Store Connect (TestFlight).
#
# Prerequisites:
#   1. AuthKey_TB52W6Z8MK.p8 at ~/.config/AuthKey_TB52W6Z8MK.p8
#      (copied from ../Recipes/AuthKey_TB52W6Z8MK.p8)
#   2. DEVELOPMENT_TEAM set in iosApp/Configuration/Config.xcconfig
#   3. GoogleService-Info.plist present at iosApp/iosApp/GoogleService-Info.plist
#
# Usage:
#   bash deploy-ios.sh

set -e

# ── Config ────────────────────────────────────────────────────────────────────

REPO_ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
WORKSPACE="$REPO_ROOT/iosApp/iosApp.xcworkspace"
SCHEME="iosApp"
CONFIGURATION="Release"
ARCHIVE_PATH="/tmp/Kindling.xcarchive"
EXPORT_PATH="/tmp/KindlingExport"
EXPORT_OPTIONS_PLIST="$REPO_ROOT/scripts/deploy/ExportOptions.plist"

ASC_KEY_PATH="${ASC_KEY_PATH:-$HOME/.config/AuthKey_TB52W6Z8MK.p8}"
ASC_KEY_ID="TB52W6Z8MK"
ASC_ISSUER_ID="69a6de8a-a43a-47e3-e053-5b8c7c11a4d1"

# ── Preflight ─────────────────────────────────────────────────────────────────

if [ ! -f "$ASC_KEY_PATH" ]; then
  echo "❌ App Store Connect key not found at: $ASC_KEY_PATH"
  echo "   Copy it from: ../Recipes/AuthKey_TB52W6Z8MK.p8"
  echo "   Or set ASC_KEY_PATH env var to its location."
  exit 1
fi

if [ ! -f "$REPO_ROOT/iosApp/iosApp/GoogleService-Info.plist" ]; then
  echo "❌ GoogleService-Info.plist missing — required for Firebase."
  echo "   Download it from Firebase Console and place it at iosApp/iosApp/GoogleService-Info.plist"
  exit 1
fi

# ── Gradle: build KMP framework first ────────────────────────────────────────

echo "🔨 Building KMP framework..."
cd "$REPO_ROOT"
./gradlew :composeApp:linkReleaseFrameworkIosArm64

# ── Xcode: archive ────────────────────────────────────────────────────────────

echo "📦 Archiving..."
xcodebuild archive \
  -workspace "$WORKSPACE" \
  -scheme "$SCHEME" \
  -configuration "$CONFIGURATION" \
  -destination "generic/platform=iOS" \
  -archivePath "$ARCHIVE_PATH" \
  CODE_SIGN_STYLE=Automatic \
  | xcpretty 2>/dev/null || true

if [ ! -d "$ARCHIVE_PATH" ]; then
  echo "❌ Archive failed — check xcodebuild output above."
  exit 1
fi

# ── Xcode: export IPA ────────────────────────────────────────────────────────

echo "📤 Exporting IPA..."
rm -rf "$EXPORT_PATH"
xcodebuild -exportArchive \
  -archivePath "$ARCHIVE_PATH" \
  -exportPath "$EXPORT_PATH" \
  -exportOptionsPlist "$EXPORT_OPTIONS_PLIST" \
  | xcpretty 2>/dev/null || true

IPA_PATH=$(find "$EXPORT_PATH" -name "*.ipa" | head -1)
if [ -z "$IPA_PATH" ]; then
  echo "❌ IPA export failed — check xcodebuild output above."
  exit 1
fi

echo "✅ IPA: $IPA_PATH"

# ── Upload to App Store Connect ───────────────────────────────────────────────

echo "🚀 Uploading to TestFlight..."
xcrun altool --upload-app \
  -f "$IPA_PATH" \
  -t ios \
  --apiKey "$ASC_KEY_ID" \
  --apiIssuer "$ASC_ISSUER_ID" \
  --apiKeyPath "$ASC_KEY_PATH" \
  --verbose

echo ""
echo "🎉 Done — build submitted to TestFlight."
echo "   https://appstoreconnect.apple.com/apps"
