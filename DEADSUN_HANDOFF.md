# Dead Sun Mod — Debugging Handoff

**Platform:** NeoForge, Minecraft 26.2 (universal jar)
**Context:** Diagnosed by Claude via code review + research against NeoForge 1.21.11/26.1 migration primers and event docs. No live build/test was performed — verify every signature below against the actual decompiled sources before committing.

---

## Bug 1: Zombies don't spawn during day / in lit areas (Overworld, Nether, End)

### Root cause
`MobSpawnMixin` injects into `Mob#checkSpawnRules` (instance method). This is the **wrong hook** — it runs too late in the spawn pipeline. The actual light-level gate for monster spawns is evaluated earlier, via the static per-entity-type predicate registered through `SpawnPlacements` (e.g. `Zombie`'s registered check, which internally calls the monster light-level check). By the time `Mob#checkSpawnRules` runs, a position that fails the light check has already been discarded — so overriding it never gets a chance to matter. This single bug explains all three reported symptoms (day, lit areas, Nether, End) since it's dimension-agnostic.

### Fix
Delete `MobSpawnMixin.java`. Replace it with a NeoForge event handler using `MobSpawnEvent.SpawnPlacementCheck` (package `net.neoforged.neoforge.event.entity.living`), fired on `NeoForge.EVENT_BUS` **before** a mob spawn is attempted — this is the documented, version-stable extension point for exactly this kind of override (light level, slime chunk, etc. overrides). Do NOT mixin into `SpawnPlacements.checkSpawnRules` directly — that method's signature has already changed at least once across recent versions (`MobSpawnType` → `EntitySpawnReason`), so it's a moving target; the event API is maintained by NeoForge to stay stable across that churn.

```java
package com.example.deadsun.event;

import com.example.deadsun.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;

@EventBusSubscriber(modid = "deadsun")
public class DeadSunSpawnEvents {

    @SubscribeEvent
    public static void onSpawnPlacementCheck(MobSpawnEvent.SpawnPlacementCheck event) {
        if (event.getEntityType() == EntityType.ZOMBIE) {
            boolean nearTorch = isNearbyTorch(event.getLevel(), event.getPos());
            event.setResult(nearTorch
                ? MobSpawnEvent.SpawnPlacementCheck.Result.FAIL
                : MobSpawnEvent.SpawnPlacementCheck.Result.SUCCEED);
        }
    }

    private static boolean isNearbyTorch(ServerLevelAccessor level, BlockPos pos) {
        int radius = ModConfig.getTorchRadiusValue();
        for (int x = -radius; x <= radius; x++)
            for (int y = -radius; y <= radius; y++)
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > radius * radius) continue;
                    Block block = level.getBlockState(pos.offset(x, y, z)).getBlock();
                    if (block == Blocks.TORCH || block == Blocks.WALL_TORCH
                            || block == Blocks.SOUL_TORCH || block == Blocks.SOUL_WALL_TORCH
                            || block == Blocks.COPPER_TORCH || block == Blocks.COPPER_WALL_TORCH) {
                        return true;
                    }
                }
        return false;
    }
}
```

### ⚠️ Verify before trusting
- Exact getter names on `MobSpawnEvent.SpawnPlacementCheck` (`getEntityType()`, `getLevel()`, `getPos()`) — confirm via IDE autocomplete on the actual 26.2 class, not this doc. Docs referenced during diagnosis were for NeoForge 1.21.0.
- Exact `Result` enum values — may be `SUCCEED`/`FAIL`/`DEFAULT` or `ALLOW`/`DENY`/`DEFAULT` depending on version. Check the enum in your decompiled sources.
- Confirm the event actually fires for natural (non-spawner) spawns in your version — per NeoForge docs it does NOT fire for spawners using `SpawnData.CustomSpawnRules`, which shouldn't matter here but worth knowing.
- After wiring this up, add a temporary `DeadSunMod.LOGGER.info(...)` inside `onSpawnPlacementCheck` to confirm it's actually being invoked before assuming the fix "didn't work" for some other reason.

---

## Bug 2: NaturalSpawnerMixin — missing MobCategory guard

### Root cause
`deadsun$filterSpawns` in `NaturalSpawnerMixin.java` runs for **every** `MobCategory` (`MONSTER`, `CREATURE`, `AMBIENT`, `WATER_CREATURE`, etc.), not just `MONSTER`. When it swaps a non-zombie-variant candidate to `Zombie`, it doesn't check whether the slot being filled was actually a monster-category slot. Forcing a `MONSTER`-category entity into a non-monster slot can cause downstream category-consistency checks to silently reject the spawn attempt entirely.

**This is the leading suspect for "nothing spawns in the End anymore"** — a regression introduced by this mixin, since it swaps indiscriminately across categories in the End's spawn table too.

### Fix
Add a single guard at the top of the injected method, before touching `cir.getReturnValue()`:

```java
private static void deadsun$filterSpawns(
        ServerLevel level, StructureManager structureManager,
        ChunkGenerator chunkGenerator, MobCategory category,
        RandomSource random, BlockPos pos,
        CallbackInfoReturnable<Optional<MobSpawnSettings.SpawnerData>> cir
) {
    if (category != MobCategory.MONSTER) return; // <-- ADD THIS LINE

    Optional<MobSpawnSettings.SpawnerData> original = cir.getReturnValue();
    if (original.isEmpty()) return;
    // ... rest unchanged
}
```

This fix does not depend on version-specific mappings (`MobCategory` is a stable class) — high confidence, no verification needed beyond a normal test pass.

### Secondary note on Nether
Separately, the Nether branch of this mixin only replaces the candidate when it's *exactly* `ZOMBIFIED_PIGLIN`. Other Nether spawn-table entries (piglins, ghasts, striders, etc.) are left untouched, so zombies will still be relatively rare there even after both fixes — this is expected behavior given the current logic, not a bug, unless the intent was "replace ALL Nether monster-category spawns with zombies," in which case the condition should be loosened to `category == MobCategory.MONSTER` (post-fix-1-guard, this would just mean removing the `ZOMBIFIED_PIGLIN`-only check and replacing any monster-category candidate).

---

## Bug 3: Loot bag texture not visible

### Root cause
Since Minecraft 1.21.4, item rendering info was split into two separate files (still true at 26.2 as far as available docs indicate):

1. `assets/deadsun/models/item/loot_bag.json` — the actual mesh/texture layer definition (this appears to already exist).
2. `assets/deadsun/items/loot_bag.json` — the **new** "Client Item" definition that links the registered item to the model above. **This file is very likely missing** — it's the most common cause of "I have the texture but the item shows as invisible/missing" on 1.21.4+.

### Fix
Create:

```
assets/deadsun/items/loot_bag.json
```
```json
{
  "model": {
    "type": "minecraft:model",
    "model": "deadsun:item/loot_bag"
  }
}
```

### ⚠️ Verify
Confirm `assets/deadsun/models/item/loot_bag.json` itself is well-formed (has `"parent": "item/generated"` and a `layer0` texture pointing at `deadsun:item/loot_bag`, with the actual PNG at `assets/deadsun/textures/item/loot_bag.png`). If the `items/` file above is added and the item still doesn't render, check the model file next.

---

## Suggested order of work

1. **Loot bag texture fix** — isolated, fast to verify visually, no mixin/version risk.
2. **NaturalSpawnerMixin category guard** — one line, stable API, low risk, likely fixes End regression.
3. **Spawn placement check migration** (mixin → event) — highest impact but requires verifying exact event API against 26.2 sources. Budget the most testing time here. Add temporary logging to confirm the handler fires before judging whether the torch logic itself is correct.

## General note for this codebase going forward

Given how fast 26.x is moving and how much internal Minecraft naming has churned recently (`ResourceLocation`→`Identifier`, `MobSpawnType`→`EntitySpawnReason`, item model system split, etc.), prefer NeoForge's documented event bus over mixining into vanilla internals wherever an event exists for the behavior you need. Events are what NeoForge commits to keeping stable across version bumps; mixin targets are not.
