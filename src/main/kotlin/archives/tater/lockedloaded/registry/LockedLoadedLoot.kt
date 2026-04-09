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

    private data class Inject(val table: ResourceKey<LootTable>, val poolWeight: Int? = null)

    private val INJECTS = mutableMapOf<ResourceKey<LootTable>, Inject>()

    private fun injectOf(table: ResourceKey<LootTable>, poolWeight: Int? = null) = of("inject/${table.identifier().path}").also {
        INJECTS[table] = Inject(it, poolWeight)
    }

    val PILLAGER_OUTPOST = injectOf(BuiltInLootTables.PILLAGER_OUTPOST)
    val TRIAL_CHAMBERS_REWARD_RARE = injectOf(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_RARE, poolWeight = 2)

    fun init() {
        LootTableEvents.MODIFY.register { key, tableBuilder, _, _ ->
            val (table, poolWeight) = INJECTS[key] ?: return@register
            if (poolWeight == null)
                tableBuilder.withPool(lootPool()
                    .add(lootTableReference(table)))
            else
                tableBuilder.modifyPools {
                    it.add(lootTableReference(table)
                        .setWeight(poolWeight))
                }
        }
    }
}