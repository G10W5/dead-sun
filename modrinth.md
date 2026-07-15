# 🧟 Dead Sun

> **The sun no longer protects you. Build your apocalypse. Configure your nightmare.**

Dead Sun is a **fully modular** zombie apocalypse mod that fundamentally changes how zombies behave in Minecraft. Every mechanic is designed to make you feel hunted — and every mechanic can be individually toggled, tuned, or disabled.

> 💀 **100% configurable** — Every feature, every value, every behavior. Make it as brutal or as mild as you want.

---

## 🌍 What Changes

> Only zombies spawn in the overworld, the Nether, and the End.

- **☀️ Sunlight is no longer safe** — Zombies do not burn in daylight. They chase you at high noon just as readily as at midnight.
- **🔦 Torches create safety zones** — Place torches to keep zombies from spawning nearby. The protection radius is fully configurable.
- **🧟 Zombies spawn in groups** — In the overworld, zombies spawn in groups of 2-3 or 8-12, creating natural-looking patrols.
- **🎒 Loot bags replace missing mobs** — Since only zombies spawn, loot bags drop from zombie kills as an alternative source for gunpowder, bones, string, and more.

---

## 🧠 Smart Zombies

> They're not just mindless — they're tactical.

### Coordinated Flanking
When multiple zombies target you, they **spread out and approach from different angles** instead of all rushing the same path. No more funneling them into a chokepoint.

### Pile-Up Climbing
Zombies **climb on top of each other** to scale walls. They require actual support (ground or another zombie) to climb, creating an organic flesh ramp. Each zombie climbs one step at a time, pressing into the wall with wobbling, desperate movement.

### Sound Tracking
> Opening doors, breaking blocks, eating food — zombies hear it all.

Zombies pathfind toward sounds made by players. **Sneaking silences you.**

### Light Attraction
At night, zombies seek out light sources. Torches attract them from afar while keeping you safe up close — a constant tension between danger and safety.

### Noisy Zombies
When a zombie spots you, it **calls out to nearby zombies**, pulling them toward you. Chain reactions can occur — one zombie calling more.

### Wandering Hordes
Zombies periodically group up and wander toward random waypoints, creating **organic patrols** across the landscape.

---

## 🧬 Zombie Variants

> Not all zombies are created equal.

| Variant | Chance | Behavior |
|---------|--------|----------|
| 🏃 **Runner** | 5% | **100% faster** movement speed |
| 🦘 **Jumper** | 3% | Leaps toward players with enhanced jump range |
| 💥 **Exploder** | 2% | Holds TNT, **2-second fuse**, explodes like a creeper |
| 🪓 **Brute** | 3% | Slower but hits harder with **Strength I** |
| 👑 **Alpha** | 2% | **60 HP**, 1.5x scale, better loot drops |

> ⚠️ All variants **stack** — encounter an Alpha Runner or a Brute Exploder.

---

## 🎒 Loot System

Since only zombies spawn, **loot bags** provide an alternative source for resources you'd normally get from other mobs.

### Overworld Loot
- Gunpowder (1-3)
- Bone (1-3)
- String (2-4)
- Spider Eye (1-2)
- XP Bottle (3-7)

### Nether Loot
- Quartz (6-13)
- Bone (3-5)
- Gold Nuggets (5-13)
- Ghast Tear (1-2)
- XP Bottle (3-7)

### End Loot
- Ender Pearl (1-3)
- Ender Eye
- XP Bottle (5-12)

> 🥚 **Ender pearls also drop from zombies** (2% chance) — solving the End progression softlock.

---

## ⚙️ Fully Modular & Configurable

> Every single feature can be toggled on or off. Every value is configurable.

### Spawn Settings
- `spawnDensity` — Max zombies per player
- `spawnRadius` / `minSpawnDistance` — Spawn range
- `groupSpawning` — Group spawn behavior
- `allowBabyZombies` — Toggle baby zombies
- Dimension caps for Overworld, Nether, and End

### Zombie Behavior
- `zombieLeap` / `leapStrength` / `leapHeight` / `leapCooldown`
- `zombiePileUp` / `pileUpMinGroupSize` / `climbSpeed` / `climbStep` / `maxWallScan`

### Torch Safety
- `torchRadius` — Protection radius around torches

### Sound & Light Tracking
- `soundTracking` / `soundHearRange` / `soundDecayTime`
- `lightTracking` / `lightSearchRange` / `lightMinBrightness`

### Wandering Hordes
- `wanderingHordes` / `hordeFrequency` / `hordeRange`

### Noisy Zombies
- `noisyZombies` / `noisyZombieRange` / `noisyZombieCooldown`

### Zombie Variants
- Individual toggle + spawn chance for Runner, Jumper, Exploder, Brute
- `runnerSpeedBoost` / `jumperLeapBoost` / `exploderFuseTime` / `exploderExplosionRadius` / `bruteStrengthLevel`

### Alpha Zombies
- `alphaZombies` / `alphaSpawnChance` / `alphaHealth` / `alphaScale` / `alphaAttackDamage`

### Coordinated Flanking
- `coordinatedFlanking` / `flankingUpdateInterval` / `flankingRange` / `flankingMinGroupSize` / `flankingRadius` / `flankingEngageDistance` / `flankingNavSpeed`

### Days Before Activation
- `daysBeforeActivation` — Grace period before features activate (0 = immediate)

---

## 📋 Technical Details

| Detail | Value |
|--------|-------|
| **Platform** | Fabric + NeoForge (universal JAR) |
| **Minecraft** | 26.2 |
| **Mod Loader** | Architectury |
| **Config** | JSON + in-game screen (Cloth Config / Mod Menu) |
| **Performance** | Lightweight tick-based systems, minimal overhead |

---

## ⚠️ Compatibility

> This mod replaces the natural spawning system via mixins.

- ✅ Works with most gameplay mods
- ✅ Alex's Mobs confirmed compatible
- ⚠️ May conflict with other zombie mods that modify spawning or zombie AI
- ⚠️ May conflict with mods that add custom mob categories

---

## 📦 Installation

1. Install **Fabric** or **NeoForge** for Minecraft 26.2
2. Place the universal JAR into your `mods` folder
3. Optionally install **Mod Menu** + **Cloth Config** for the in-game settings screen

> The same JAR works on both Fabric and NeoForge.

---

## 🏆 Why Dead Sun?

Most zombie mods add new zombies or new weapons. Dead Sun **fundamentally changes how zombies behave**. They're smarter, more coordinated, and more terrifying. Every mechanic is designed to make you feel hunted — and every mechanic can be turned off if it's too much.

> **Build your apocalypse. Configure your nightmare.**
