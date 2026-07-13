# Dead Sun

A hardcore zombie apocalypse mod for Minecraft 26.2. The sun no longer protects you — zombies spawn in broad daylight, torches are your only refuge, and every dimension has fallen.

## What Changes

**Zombies are everywhere.** Only zombies spawn in the overworld, the Nether, and the End. Skeletons, creepers, spiders — all gone. Every hostile mob has been replaced with a zombie variant. Pillager patrols are suppressed.

**Sunlight is no longer safe.** Zombies do not burn in daylight. They will chase you across open fields at high noon just as readily as at midnight.

**Torches create safety zones.** Place torches, soul torches, or copper torches to keep zombies from spawning nearby. The protection radius is configurable (default 8 blocks).

**Zombies spawn in groups.** In the overworld, zombies spawn in groups of 2-3 or 8-12, creating natural-looking patrols. In the Nether and End, they spawn individually.

**Zombies are smart.** When a zombie is blocked by a wall while chasing you, it will climb on top of other zombies to reach you. When close to a player, zombies leap forward.

**Zombies drop loot bags.** When a zombie dies, there is a configurable chance (default 5%) it drops a Loot Bag. Right-click to open it and receive a random reward. Loot bags stack up to 16.

**Zombies hear you.** Sounds made by players — opening doors, breaking blocks, eating — create audio markers that zombies pathfind toward. Stay quiet or attract the horde.

**Zombies are attracted to light.** At night, zombies seek out light sources (torches, lanterns, glowstone). Torches still keep you safe from spawning, but they act as beacons that draw zombies toward your base from afar.

**Zombies call for reinforcements.** When a zombie spots a player, it calls out to nearby zombies, pulling them toward you. Chain reactions can occur — one zombie calling more.

**Wandering hordes.** Zombies periodically group up and wander toward random waypoints, creating organic patrols across the landscape.

**Safe period.** Configure a number of days before all Dead Sun features activate, giving you time to set up a base before the apocalypse begins.

## Loot Pools

| Overworld | Nether | End |
|---|---|---|
| Gunpowder (1-3) | Quartz (6-13) | Ender Pearl (1-3) |
| Bone (1-3) | Bone (3-5) | Ender Eye |
| String (2-4) | Gold Nuggets (5-13) | XP Bottle (5-12) |
| Spider Eye (1-2) | Ghast Tear (1-2) | |
| XP Bottle (3-7) | XP Bottle (3-7) | |

Opening a loot bag plays a sound and shows what you received above the hotbar.

## Configuration

The mod ships with a JSON config at `config/deadsun.json`. All values can be changed in-game through Mod Menu (Fabric) or edited by hand. The config screen is organized into categories for easy navigation.

### Spawn Settings

| Setting | Default | Description |
|---|---|---|
| `spawnDensity` | 50 | Max zombies per player before spawning stops (10-150) |
| `spawnRadius` | 128 | Max distance from player for zombie spawns (16-128) |
| `minSpawnDistance` | 32 | Min distance from player for zombie spawns (16-64) |
| `ticksBetweenSpawns` | 10 | How often the game tries to spawn zombies (20 = 1 second) |
| `maxBlockLight` | -1 | Max block light for spawning (-1 = ignore light) |
| `groupSpawning` | true | Zombies spawn in groups (overworld only) |
| `minGroupSize` | 2 | Min zombies per group |
| `maxGroupSize` | 8 | Max zombies per group |

### Dimension Caps

| Setting | Default | Description |
|---|---|---|
| `maxZombiesOverworld` | 50 | Max total zombies in the overworld (10-200) |
| `maxZombiesEnd` | 150 | Max total zombies in the End (10-300) |
| `maxZombiesNether` | 100 | Max total zombies in the Nether (10-200) |

### Loot

| Setting | Default | Description |
|---|---|---|
| `lootBagDropChance` | 0.05 | Chance a zombie drops a loot bag on death (0.0 - 1.0) |
| `enderPearlChance` | 0.30 | Chance of ender pearls in an End loot bag |

### Zombie Behavior

| Setting | Default | Description |
|---|---|---|
| `zombieLeap` | true | Zombies leap at players when close |
| `zombiePileUp` | true | Zombies climb on each other when chasing you |
| `leapStrength` | 0.6 | Horizontal speed of the zombie leap |
| `leapHeight` | 0.4 | Vertical speed of the zombie leap |
| `leapCooldown` | 120 | Ticks between leaps (20 = 1 second) |

### Torch Safety

| Setting | Default | Description |
|---|---|---|
| `torchRadius` | 8 | Block radius around torches where zombies cannot spawn |

### Days Before Activation

| Setting | Default | Description |
|---|---|---|
| `daysBeforeActivation` | 0 | In-game days before features activate (0 = always active) |

### Sound Tracking

| Setting | Default | Description |
|---|---|---|
| `soundTracking` | true | Zombies pathfind toward player sounds |
| `soundHearRange` | 64 | How far zombies can hear sounds (16-128) |
| `soundDecayTime` | 200 | How long sounds persist in ticks (60-600) |

### Light Tracking

| Setting | Default | Description |
|---|---|---|
| `lightTracking` | true | Zombies attracted to light at night |
| `lightSearchRange` | 32 | How far zombies search for light (16-128) |
| `lightMinBrightness` | 0.2 | Minimum brightness to attract zombies |

### Wandering Hordes

| Setting | Default | Description |
|---|---|---|
| `wanderingHordes` | true | Zombies group up and wander toward waypoints |
| `hordeFrequency` | 30 | Seconds between horde waypoints (10-120) |
| `hordeRange` | 128 | How far hordes wander from the player (32-256) |

### Noisy Zombies

| Setting | Default | Description |
|---|---|---|
| `noisyZombies` | true | Zombies call out near players, attracting others |
| `noisyZombieRange` | 24 | Range of the noisy zombie call (8-64) |
| `noisyZombieCooldown` | 100 | Ticks between calls (40-400) |

## Installation

1. Install [Fabric](https://fabricmc.net/) or [NeoForge](https://neoforged.net/) for Minecraft 26.2
2. Place the universal JAR (`DeadSun-1.0.0-universal.jar`) into your `mods` folder
3. Optionally install [Mod Menu](https://modrinth.com/mod/modmenu) and [Cloth Config](https://modrinth.com/mod/cloth-config) for the in-game settings screen

The same JAR works on both Fabric and NeoForge.

## Compatibility

This mod replaces the natural spawning system via mixins. Other mods that modify mob spawning may conflict. Tested on Minecraft 26.2 with Fabric and NeoForge.

## Building from Source

```
./gradlew clean build
```

The universal JAR is output to `build/universal/`.
