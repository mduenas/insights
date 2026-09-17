#!/usr/bin/env python3
"""Generate Play Store feature graphic (1024×500) and hi-res icon (512×512).

Uses the iOS AppIcon and Kindling brand palette. Exact text is drawn with
Pillow (not an image model) so store copy stays crisp.

Usage:
  python3 scripts/generate_feature_graphic.py
"""

from __future__ import annotations

from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter, ImageFont

ROOT = Path(__file__).resolve().parents[1]
ICON_SRC = ROOT / "iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/app-icon-1024.png"
OUT_DIR = ROOT / "fastlane/metadata/android/en-US/images"

DARK_BG = (28, 30, 42)
CYAN = (51, 195, 255)
ORANGE = (255, 140, 50)
WHITE = (255, 255, 255)
SOFT = (226, 228, 244)


def load_font(size: int, bold: bool = False) -> ImageFont.ImageFont:
    candidates = [
        "/System/Library/Fonts/Supplemental/Arial Bold.ttf"
        if bold
        else "/System/Library/Fonts/Supplemental/Arial.ttf",
        "/Library/Fonts/Arial.ttf",
        "/System/Library/Fonts/Helvetica.ttc",
    ]
    for path in candidates:
        try:
            return ImageFont.truetype(path, size)
        except OSError:
            continue
    return ImageFont.load_default()


def generate() -> None:
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    icon_src = Image.open(ICON_SRC).convert("RGBA")

    # Hi-res Play icon
    icon_512 = icon_src.resize((512, 512), Image.Resampling.LANCZOS)
    icon_path = OUT_DIR / "icon.png"
    icon_512.save(icon_path, "PNG", optimize=True)
    print(f"Wrote {icon_path.relative_to(ROOT)} {icon_512.size}")

    # Feature graphic
    w, h = 1024, 500
    fg = Image.new("RGBA", (w, h), (*DARK_BG, 255))
    draw = ImageDraw.Draw(fg)

    for x in range(w):
        t = x / (w - 1)
        r = min(255, int(28 + t * 40 + (t**2) * 60))
        g = min(255, int(30 + t * 20))
        b = min(255, int(42 + t * 80 - (t**2) * 20))
        draw.line([(x, 0), (x, h)], fill=(r, g, b, 255))

    glow = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    gdraw = ImageDraw.Draw(glow)
    cx, cy = 220, h // 2
    for radius, alpha in [(220, 40), (160, 70), (110, 100)]:
        gdraw.ellipse(
            [cx - radius, cy - radius, cx + radius, cy + radius],
            fill=(255, 150, 60, alpha),
        )
    glow = glow.filter(ImageFilter.GaussianBlur(radius=28))
    fg = Image.alpha_composite(fg, glow)

    icon_h = 300
    icon_img = icon_src.resize((icon_h, icon_h), Image.Resampling.LANCZOS)
    shadow = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    sdraw = ImageDraw.Draw(shadow)
    ix, iy = 70, (h - icon_h) // 2
    sdraw.rounded_rectangle(
        [ix + 8, iy + 12, ix + icon_h + 8, iy + icon_h + 12],
        radius=60,
        fill=(0, 0, 0, 90),
    )
    shadow = shadow.filter(ImageFilter.GaussianBlur(10))
    fg = Image.alpha_composite(fg, shadow)
    fg.paste(icon_img, (ix, iy), icon_img)
    draw = ImageDraw.Draw(fg)

    title_font = load_font(72, bold=True)
    tag_font = load_font(28)
    small_font = load_font(22)

    text_x = 420
    title = "Kindling"
    tb = draw.textbbox((0, 0), title, font=title_font)
    tw, th = tb[2] - tb[0], tb[3] - tb[1]
    block_h = th + 18 + 34 + 8 + 28
    ty = (h - block_h) // 2 - 8

    draw.text((text_x + 2, ty + 2), title, font=title_font, fill=(0, 0, 0, 80))
    draw.text((text_x, ty), title, font=title_font, fill=WHITE)

    underline_y = ty + th + 12
    draw.rounded_rectangle(
        [text_x, underline_y, text_x + min(tw, 280), underline_y + 5],
        radius=3,
        fill=(*ORANGE, 255),
    )

    ty2 = underline_y + 22
    draw.text(
        (text_x, ty2),
        "Curated wisdom. Personal insights.",
        font=tag_font,
        fill=SOFT,
    )
    draw.text(
        (text_x, ty2 + 38),
        "Source-linked. Local-first.",
        font=small_font,
        fill=(*CYAN, 230),
    )

    fg_rgb = Image.new("RGB", (w, h), DARK_BG)
    fg_rgb.paste(fg, mask=fg.split()[-1])
    feature_path = OUT_DIR / "featureGraphic.png"
    fg_rgb.save(feature_path, "PNG", optimize=True)
    print(f"Wrote {feature_path.relative_to(ROOT)} {fg_rgb.size}")


if __name__ == "__main__":
    generate()
