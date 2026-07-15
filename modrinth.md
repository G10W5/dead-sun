# Dead Sun

**The zombie apocalypse mod that turns Minecraft into a survival horror experience.**

The sun no longer protects you. Zombies spawn in broad daylight, torches are your only refuge, and every dimension has fallen. Every feature is fully toggleable and configurable — make it as brutal or as mild as you want.

---

## The Apocalypse

**Zombies are everywhere.** Only zombies spawn in the overworld, the Nether, and the End. Skeletons, creepers, spiders — all gone. Every hostile mob has been replaced with a zombie variant. Pillager patrols are suppressed.

**Sunlight is no longer safe.** Zombies do not burn in daylight. They will chase you across open fields at high noon just as readily as at midnight.

**Torches create safety zones.** Place torches, soul torches, or copper torches to keep zombies from spawning nearby. The protection radius is fully configurable.

---

## Loot Bags

Since only zombies spawn, **loot bags** drop from zombie kills as an alternative source for resources you'd normally get from other mobs.

- **Overworld loot bags:** Gunpowder, Bone, String, Spider Eye, XP Bottles
- **Nether loot bags:** Quartz, Bone, Gold Nuggets, Ghast Tear, XP Bottles
- **End loot bags:** Ender Pearl, Ender Eye, XP Bottles

Ender pearls also drop directly from zombies (2% chance) — solving the End progression softlock.

---

## Smart Zombies

Zombies aren't just mindless — they're tactical.

**Coordinated Flanking** — When multiple zombies target you, they spread out and approach from different angles instead of all rushing the same path. No more funneling them into a chokepoint.

**Pile-Up Climbing** — Zombies climb on top of each other to scale walls. They require actual support (ground or another zombie) to climb, creating an organic flesh ramp. Each zombie climbs one step at a time, pressing into the wall with wobbling, desperate movement.

**Sound Tracking** — Opening doors, breaking blocks, eating food — zombies hear it all and pathfind toward the noise. Sneaking silences you.

**Light Attraction** — At night, zombies seek out light sources. Torches attract them from afar while keeping you safe up close.

**Noisy Zombies** — When a zombie spots you, it calls out to nearby zombies, pulling them toward you. Chain reactions can occur.

**Wandering Hordes** — Zombies periodically group up and wander toward random waypoints, creating organic patrols across the landscape.

---

## Zombie Variants

Not all zombies are created equal.

| Variant | Chance | Behavior |
|---------|--------|----------|
| **Runner** | 5% | 100% faster movement speed |
| **Jumper** | 3% | Leaps toward players, enhanced jump range |
| **Exploder** | 2% | Holds TNT, 2-second fuse, explodes like a creeper |
| **Brute** | 3% | Slower but hits harder with Strength I |
| **Alpha** | 2% | 60 HP, 1.5x scale, better loot |

All variants stack — you can encounter an Alpha Runner or a Brute Exploder.

---

## Every Feature is Toggleable

Dead Sun is built for customization. Every single feature can be toggled on or off, and every value is configurable.

**Spawn Settings** — Max zombies, spawn radius, group spawning, baby zombies, dimension caps

**Zombie Behavior** — Leap, pile-up, climb speed, wall scan height, minimum group size for pile-up

**Torch Safety** — Protection radius, block light limits

**Sound & Light Tracking** — Enable/disable, hear range, decay time, light search range, investigate chance

**Wandering Hordes** — Frequency, range, minimum group size

**Noisy Zombies** — Range, cooldown

**Zombie Variants** — Individual toggle and spawn chance for each variant (Runner, Jumper, Exploder, Brute)

**Alpha Zombies** — Spawn chance, HP, scale, attack damage

**Coordinated Flanking** — Toggle, update interval, range, minimum group size, radius, engage distance

**Days Before Activation** — Set a grace period before any features activate (0 = immediate)

---

## Technical Details

- **Platform:** Fabric + NeoForge (single universal JAR)
- **Minecraft Version:** 26.2
- **Mod Loader:** Architectury (cross-loader)
- **Config:** JSON config file + in-game config screen via Cloth Config / Mod Menu
- **Performance:** Lightweight tick-based systems, no entity registration, minimal overhead
- **Compatibility:** Works with most mods. May conflict with other zombie mods that modify spawning or zombie AI.

---

## Installation

1. Install Fabric or NeoForge for Minecraft 26.2
2. Place the universal JAR into your `mods` folder
3. Optionally install Mod Menu + Cloth Config for the in-game settings screen

---

## Why Dead Sun?

Most zombie mods add new zombies or new weapons. Dead Sun fundamentally changes how zombies *behave*. They're smarter, more coordinated, and more terrifying. Every mechanic is designed to make you feel hunted — and every mechanic can be turned off if it's too much.

**Build your apocalypse. Configure your nightmare.**
