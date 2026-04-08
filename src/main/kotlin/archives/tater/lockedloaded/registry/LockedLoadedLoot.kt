package archives.tater.lockedloaded.registry

import archives.tater.lockedloaded.LockedLoaded
import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.storage.loot.BuiltInLootTables
import net.minecraft.world.level.storage.loot.LootPool.lootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.NestedLootTable.lootTableReference

object LockedLoadedLoot {
    private fun of(path: String) = ResourceKey.create(Registries.LOOT_TABLE, LockedLoaded.id(path))

    private fun injectOf(table: ResourceKey<LootTable>) = of("inject/${table.identifier().path}")

    val PILLAGER_OUTPOST = injectOf(BuiltInLootTables.PILLAGER_OUTPOST)
    val TRIAL_CHAMBERS_REWARD_RARE = injectOf(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_RARE)

    fun init() {
        LootTableEvents.MODIFY.register { key, tableBuilder, _, _ ->
            when (key) {
                BuiltInLootTables.PILLAGER_OUTPOST -> tableBuilder.withPool(lootPool().add(lootTableReference(PILLAGER_OUTPOST)))
                BuiltInLootTables.TRIAL_CHAMBERS_REWARD_RARE -> tableBuilder.withPool(lootPool().add(lootTableReference(TRIAL_CHAMBERS_REWARD_RARE)))
            }
        }
    }
}