#!/usr/bin/env python3
"""Upload Kindling Play Store listing images (+ optional text).

Requires a Play Console service account with permission to **commit store
listing edits** (Admin / Manage store presence). The shared
`play-deploy@…` key can upload AABs to tracks but currently gets 403 on
image/listing commits for this app.

Usage:
  python3 scripts/deploy/upload-play-listing.py
  python3 scripts/deploy/upload-play-listing.py --images-only
  PLAY_SERVICE_ACCOUNT=/path/key.json python3 scripts/deploy/upload-play-listing.py

Assets:
  fastlane/metadata/android/en-US/images/featureGraphic.png
  fastlane/metadata/android/en-US/images/icon.png
"""

from __future__ import annotations

import argparse
import os
import sys
from pathlib import Path

from google.oauth2 import service_account
from googleapiclient.discovery import build
from googleapiclient.http import MediaFileUpload

REPO = Path(__file__).resolve().parents[2]
PACKAGE = "com.markduenas.insights"
IMG_DIR = REPO / "fastlane/metadata/android/en-US/images"
META = REPO / "fastlane/metadata/android/en-US"


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--images-only",
        action="store_true",
        help="Upload feature graphic + icon only (skip title/description)",
    )
    parser.add_argument(
        "--service-account",
        default=os.environ.get(
            "PLAY_SERVICE_ACCOUNT",
            str(REPO.parent / "play-store-key.json"),
        ),
    )
    args = parser.parse_args()

    feature = IMG_DIR / "featureGraphic.png"
    icon = IMG_DIR / "icon.png"
    if not feature.exists() or not icon.exists():
        print("Missing images. Run: python3 scripts/generate_feature_graphic.py", file=sys.stderr)
        return 1
    if not Path(args.service_account).exists():
        print(f"Service account not found: {args.service_account}", file=sys.stderr)
        return 1

    creds = service_account.Credentials.from_service_account_file(
        args.service_account,
        scopes=["https://www.googleapis.com/auth/androidpublisher"],
    )
    svc = build("androidpublisher", "v3", credentials=creds)
    edit = svc.edits().insert(packageName=PACKAGE).execute()
    eid = edit["id"]
    print(f"Opened edit {eid}")

    try:
        for image_type, path in [("featureGraphic", feature), ("icon", icon)]:
            media = MediaFileUpload(str(path), mimetype="image/png")
            res = (
                svc.edits()
                .images()
                .upload(
                    packageName=PACKAGE,
                    editId=eid,
                    language="en-US",
                    imageType=image_type,
                    media_body=media,
                )
                .execute()
            )
            print(f"Uploaded {image_type}: {res['image']['id']}")

        if not args.images_only:
            title = (META / "title.txt").read_text().strip()
            short = (META / "short_description.txt").read_text().strip()
            full = (META / "full_description.txt").read_text().strip()
            if len(title) > 30 or len(short) > 80:
                print(
                    f"Length check failed: title={len(title)}/30 short={len(short)}/80",
                    file=sys.stderr,
                )
                raise SystemExit(1)
            svc.edits().listings().update(
                packageName=PACKAGE,
                editId=eid,
                language="en-US",
                body={
                    "language": "en-US",
                    "title": title,
                    "shortDescription": short,
                    "fullDescription": full,
                },
            ).execute()
            print(f"Updated listing: {title!r}")

        commit = svc.edits().commit(packageName=PACKAGE, editId=eid).execute()
        print(f"Committed edit {commit.get('id')}")
        print("https://play.google.com/console/u/0/developers/app/com.markduenas.insights")
        return 0
    except Exception as exc:
        print(f"FAILED: {exc}", file=sys.stderr)
        try:
            svc.edits().delete(packageName=PACKAGE, editId=eid).execute()
            print("Edit aborted", file=sys.stderr)
        except Exception:
            pass
        print(
            "\nIf you see 403 on commit: Play Console → Users and permissions →\n"
            f"  grant Admin (or store presence) to the service account,\n"
            f"  OR upload manually:\n"
            f"  {feature}\n  {icon}",
            file=sys.stderr,
        )
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
