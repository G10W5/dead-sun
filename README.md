# Dead Sun

A hardcore zombie apocalypse mod for Minecraft 26.2. The sun no longer protects you — zombies spawn in broad daylight, torches are your only refuge, and every dimension has fallen.

## What Changes

**Zombies are everywhere.** Only zombies spawn in the overworld, the Nether, and the End. Skeletons, creepers, spiders — all gone. Every hostile mob has been replaced with a zombie variant. In the Nether you will not find hoglins or piglins; in the End, no endermen. Just zombies.

**Sunlight is no longer safe.** Zombies do not burn in daylight. They will chase you across open fields at high noon just as readily as at midnight.

**Torches create safety zones.** Place torches, soul torches, or copper torches to keep zombies from spawning nearby. The protection radius is configurable (default 8 blocks). Torches are the difference between life and death.

**Zombies drop loot bags.** When a zombie dies, there is a configurable chance (default 5%) it drops a Loot Bag. Right-click to open it and receive a random reward.

**Loot bags stack.** Loot Bags stack up to 16, so you can hoard them.

**Loot bags have two pools:**

| Overworld | End |
|---|---|
| Gunpowder (1-3) | Ender Pearl (1-3) |
| Bone (1-3) | Ender Eye |
| Bow | XP Bottle (5-12) |
| Spider Eye (1-2) | |
| XP Bottle (3-7) | |

Opening a loot bag plays a sound and shows what you received above the hotbar.

## Configuration

The mod ships with a JSON config at `config/deadsun.json`. All values can be changed in-game through Mod Menu (Fabric) or edited by hand.

| Setting | Default | Description |
|---|---|---|
| `torchRadius` | 8 | Block radius around torches where zombies cannot spawn |
| `lootBagDropChance` | 0.05 | Percentage chance a zombie drops a loot bag on death |
| `spawnDensity` | 1 | Max zombies per player in spawn attempts |
| `enderPearlChance` | 0.30 | Chance of ender pearls in an End loot bag |

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
