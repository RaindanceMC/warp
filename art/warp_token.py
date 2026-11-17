"""Draws the warp token for the warps pack: an eight-pointed gold compass star around a night sky with a turning
portal swirl, a crystal in the middle and four small gems on the points. Run from the warp repo root:
python3 art/warp_token.py — writes into warp-module/src/main/resources/de/raindancer/modules/warp/pack/."""
import json
import math
import os
import random

from PIL import Image

OUT = "warp-module/src/main/resources/de/raindancer/modules/warp/pack/assets/rainswarps"
FRAMES = 16


def lerp(a, b, t):
    return tuple(round(a[i] + (b[i] - a[i]) * t) for i in range(len(a)))


def clamp(v):
    return max(0, min(255, int(v)))


def gold():
    """Brushed gold with an engraved border; a bright sheen crosses it now and then."""
    sheet = Image.new("RGBA", (16, 16 * FRAMES))
    dark, mid, light = (122, 74, 18), (214, 158, 46), (255, 231, 150)
    for f in range(FRAMES):
        band = -8 + f * 2.2
        for y in range(16):
            for x in range(16):
                t = (x + y) / 30
                c = lerp(light, mid, t) if t < 0.5 else lerp(mid, dark, (t - 0.5) * 2)
                if x in (0, 15) or y in (0, 15):
                    c = lerp(c, dark, 0.55)
                elif x in (1, 14) or y in (1, 14):
                    c = lerp(c, light, 0.35)
                if (x * 7 + y * 3) % 11 == 0:
                    c = lerp(c, dark, 0.18)
                d = abs((x - y) - band)
                if d < 2.2:
                    c = lerp(c, (255, 252, 230), 0.75 * (1 - d / 2.2))
                sheet.putpixel((x, y + 16 * f), c + (255,))
    return sheet


def sky():
    """Indigo night with a slowly turning two-armed swirl and stars that blink."""
    rnd = random.Random(7)
    stars = [(rnd.randrange(16), rnd.randrange(16), rnd.random() * math.tau) for _ in range(9)]
    sheet = Image.new("RGBA", (16, 16 * FRAMES))
    deep, violet, cyan = (12, 8, 40), (88, 36, 150), (110, 220, 255)
    for f in range(FRAMES):
        turn = f / FRAMES * math.tau
        for y in range(16):
            for x in range(16):
                dx, dy = x - 7.5, y - 7.5
                r = math.hypot(dx, dy) / 10.6
                a = math.atan2(dy, dx)
                arm = (math.cos(2 * (a - turn) + r * 7.0) + 1) / 2
                c = lerp(violet, deep, min(1, r * 1.2))
                c = lerp(c, cyan, arm ** 6 * max(0, 1 - r) * 0.85)
                sheet.putpixel((x, y + 16 * f), c + (255,))
        for sx, sy, phase in stars:
            glow = (math.sin(turn * 2 + phase) + 1) / 2
            if glow > 0.35:
                sheet.putpixel((sx, sy + 16 * f), lerp((150, 140, 220), (255, 255, 255), glow) + (255,))
    return sheet


def crystal():
    """Faceted crystal running from ice blue through violet to rose and back."""
    sheet = Image.new("RGBA", (16, 16 * FRAMES))
    hues = [(140, 240, 255), (170, 150, 255), (255, 150, 220), (170, 150, 255)]
    for f in range(FRAMES):
        for y in range(16):
            for x in range(16):
                p = ((x + y) / 30 + f / FRAMES) % 1 * len(hues)
                i = int(p)
                c = lerp(hues[i % len(hues)], hues[(i + 1) % len(hues)], p - i)
                facet = abs(x - 7.5) + abs(y - 7.5)
                c = lerp(c, (255, 255, 255), max(0, 0.6 - facet / 12))
                if facet > 13:
                    c = lerp(c, (40, 30, 90), 0.4)
                sheet.putpixel((x, y + 16 * f), c + (255,))
    return sheet


def gem():
    img = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            facet = abs(x - 7.5) + abs(y - 7.5)
            c = lerp((255, 240, 250), (232, 70, 160), min(1, facet / 11))
            img.putpixel((x, y), c + (255,))
    return img


def animated(name, sheet, frametime, interpolate=True):
    sheet.save(f"{OUT}/textures/item/{name}.png", format="PNG")
    with open(f"{OUT}/textures/item/{name}.png.mcmeta", "w") as out:
        json.dump({"animation": {"frametime": frametime, "interpolate": interpolate}}, out, indent=2)
        out.write("\n")


def cube(frm, to, tex, faces=("north", "south", "east", "west", "up", "down"), angle=0, origin=None, glow=0,
         side=None):
    element = {"from": frm, "to": to, "faces": {}}
    for face in faces:
        element["faces"][face] = {"uv": [0, 0, 16, 16], "texture": "#" + (side if side and face not in ("north", "south") else tex)}
    if angle:
        element["rotation"] = {"angle": angle, "axis": "z", "origin": origin or [8, 8, 8]}
    if glow:
        element["light_emission"] = glow
    return element


def model():
    e = []
    # The star: two gold squares, one turned, behind everything else.
    e.append(cube([2.5, 2.5, 7.25], [13.5, 13.5, 8.75], "gold"))
    # Faces in one plane flicker against each other in the game: the turned ones stand a hair proud.
    e.append(cube([2.5, 2.5, 7.2], [13.5, 13.5, 8.8], "gold", angle=45))
    # The sky, an octagon a little proud of the star on both sides, its rim gold.
    e.append(cube([4.25, 4.25, 6.9], [11.75, 11.75, 9.1], "sky", side="gold", glow=6))
    e.append(cube([4.25, 4.25, 6.85], [11.75, 11.75, 9.15], "sky", side="gold", angle=45, glow=6))
    # The crystal, a diamond through the middle, with a brighter heart standing out further.
    e.append(cube([6.6, 6.6, 6.2], [9.4, 9.4, 9.8], "crystal", angle=45, glow=12))
    e.append(cube([7.35, 7.35, 5.6], [8.65, 8.65, 10.4], "crystal", angle=45, glow=15))
    # A gem on each point of the turned square.
    for cx, cy in ((8, 15.2), (15.2, 8), (8, 0.8), (0.8, 8)):
        e.append(cube([cx - 0.8, cy - 0.8, 7.4], [cx + 0.8, cy + 0.8, 8.6], "gem", angle=45, origin=[cx, cy, 8],
                      glow=10))
    return {
        "textures": {"particle": "rainswarps:item/token_gold", "gold": "rainswarps:item/token_gold",
                     "sky": "rainswarps:item/token_sky", "crystal": "rainswarps:item/token_crystal",
                     "gem": "rainswarps:item/token_gem"},
        "elements": e,
        "display": {
            "gui": {"rotation": [12, -24, 0], "scale": [0.95, 0.95, 0.95]},
            "ground": {"translation": [0, 2, 0], "scale": [0.5, 0.5, 0.5]},
            "fixed": {"rotation": [0, 180, 0]},
            "head": {"rotation": [0, 180, 0], "translation": [0, 13, 7]},
            "thirdperson_righthand": {"rotation": [0, 0, 0], "translation": [0, 3, 1], "scale": [0.6, 0.6, 0.6]},
            "thirdperson_lefthand": {"rotation": [0, 0, 0], "translation": [0, 3, 1], "scale": [0.6, 0.6, 0.6]},
            "firstperson_righthand": {"rotation": [0, -90, 25], "translation": [1.13, 3.2, 1.13],
                                      "scale": [0.7, 0.7, 0.7]},
            "firstperson_lefthand": {"rotation": [0, 90, -25], "translation": [1.13, 3.2, 1.13],
                                     "scale": [0.7, 0.7, 0.7]},
        },
    }


def main():
    for folder in ("items", "models/item", "textures/item"):
        os.makedirs(f"{OUT}/{folder}", exist_ok=True)
    animated("token_gold", gold(), 4)
    animated("token_sky", sky(), 3)
    animated("token_crystal", crystal(), 3)
    gem().save(f"{OUT}/textures/item/token_gem.png", format="PNG")
    with open(f"{OUT}/models/item/token.json", "w") as out:
        json.dump(model(), out, indent=2)
        out.write("\n")
    with open(f"{OUT}/items/token.json", "w") as out:
        json.dump({"model": {"type": "minecraft:model", "model": "rainswarps:item/token"}}, out, indent=2)
        out.write("\n")


if __name__ == "__main__":
    main()
