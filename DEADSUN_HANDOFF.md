# Dead Sun - Project Handoff

## Project Overview
- **Mod name**: Dead Sun (`deadsun`)
- **MC version**: 26.2
- **Toolchain**: Architectury + Forgix → single universal JAR
- **JAR**: `build/universal/DeadSun-1.0.0-universal.jar`
- **GitHub**: https://github.com/G10W5/dead-sun

## What It Does
| Feature | Status |
|---|---|
| Only zombies spawn in overworld | ✅ Working |
| Zombies don't burn in sunlight | ✅ Working |
| Zombies spawn in daylight | ✅ Fixed — `SpawnPlacementsMixin` targets `SpawnPlacements.checkSpawnRules()` |
| Passive mobs aren't replaced | ✅ Fixed — `NaturalSpawnerMixin` guarded by `MobCategory.MONSTER` |
| Loot bag drops from zombies | ✅ Working |
| Loot bag texture renders | ✅ Fixed — `items/loot_bag.json` added |
| Nether → zombified piglin → zombie | ✅ Working |
| End → 0% enderman, 100% zombie | ✅ Working |
| End loot: ender pearls | ✅ Working |
| Torch safety zones | ✅ Working |
| ModMenu + Cloth Config GUI | ✅ Working |

## Architecture
```
common/src/main/java/com/example/deadsun/
  ├── DeadSunMod.java              # Common init (load config)
  ├── config/ModConfig.java        # JSON config with getters/setters
  ├── item/LootBagItem.java        # Overworld + End loot
  └── mixin/
       ├── NaturalSpawnerMixin.java  # Monster-only entity type filter
       ├── SpawnPlacementsMixin.java # Daylight bypass for zombies
       ├── MobSunMixin.java          # No sun burn
       └── LivingEntityMixin.java    # Loot bag drop on zombie kill

fabric/src/client/java/com/example/deadsun/fabric/
  ├── ConfigScreenBuilder.java     # Cloth Config screen
  └── ModMenuIntegration.java      # ModMenu API
```

## How It Works

### Spawn Control (two mixins)

1. **`NaturalSpawnerMixin`** — `@Inject` at RETURN of `NaturalSpawner.getRandomSpawnMobAt()`:
   - Only runs for `MobCategory.MONSTER` (passive mobs unaffected)
   - Replaces non-zombie monster types with zombies per dimension:
     - Overworld → zombie
     - Nether → zombified_piglin (via biome), then zombie
     - End → all monsters → zombie

2. **`SpawnPlacementsMixin`** — `@Inject` at HEAD of `SpawnPlacements.checkSpawnRules()`:
   - For zombie variants only, bypasses the `isDarkEnoughToSpawn` check
   - Returns `true` → zombie spawns in daylight
   - Returns `false` → zombie blocked near torches (configurable radius)

### Sun Immunity
**`MobSunMixin`** — `@Inject` at HEAD of `Mob.burnUndead()` and `Zombie.aiStep()`:
- Cancels sun damage for all zombie variants

### Loot Drops
**`LivingEntityMixin`** — `@Inject` at HEAD of `LivingEntity.dropAllDeathLoot()`:
- On zombie death → random roll to drop a `LootBagItem`
- Loot bag drop chance: `ModConfig.getLootBagDropChance()` (default 5%)

### Config
`ModConfig.java` — JSON config file at `config/deadsun.json`:
- `torchRadius` (default 8) — how far torches protect from zombie spawns
- `lootBagDropChance` (default 0.05) — percentage chance zombies drop loot bags
- `spawnDensity` (default 5) — max zombies per player
- `enderPearlChance` (default 0.25) — end loot bag ender pearl chance
- ModMenu GUI: Edit all values, Save/Reset/Reload buttons

## Current Version
**1.0.0** — Built and pushed

## How to Build
```bash
./gradlew clean build
# JAR: build/universal/DeadSun-1.0.0-universal.jar
```
