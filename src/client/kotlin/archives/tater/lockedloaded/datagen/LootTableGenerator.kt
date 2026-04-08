package archives.tater.lockedloaded.datagen

import archives.tater.lockedloaded.registry.LockedLoadedEnchantments
import archives.tater.lockedloaded.registry.LockedLoadedLoot
import archives.tater.lockedloaded.util.empty
import archives.tater.lockedloaded.util.item
import archives.tater.lockedloaded.util.lootTable
import archives.tater.lockedloaded.util.pool
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.HolderSet
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class LootTableGenerator(
    output: FabricPackOutput,
    val registryLookupFuture: CompletableFuture<HolderLookup.Provider>,
) : SimpleFabricLootTableSubProvider(output, registryLookupFuture, LootContextParamSets.CHEST) {
    override fun generate(output: BiConsumer<ResourceKey<LootTable>, LootTable.Builder>) {
        val registries = registryLookupFuture.join()

        fun enchantWith(vararg enchantments: ResourceKey<Enchantment>) = EnchantRandomlyFunction.randomEnchantment().apply {
            withOneOf(HolderSet.direct(registries::getOrThrow, *enchantments))
        }

        output.accept(LockedLoadedLoot.PILLAGER_OUTPOST, lootTable {
            pool {
                item(Items.BOOK) {
                    apply(enchantWith(
                        LockedLoadedEnchantments.PUMP_CHARGE,
                        LockedLoadedEnchantments.SHARPSHOOTING,
                        LockedLoadedEnchantments.ROCKETRY,
                    ))
                }
                empty(weight = 4)
            }
        })
        output.accept(LockedLoadedLoot.TRIAL_CHAMBERS_REWARD_RARE, lootTable {
            pool {
                item(Items.BOOK) {
                    apply(enchantWith(
                        LockedLoadedEnchantments.PUMP_CHARGE,
                        LockedLoadedEnchantments.SHARPSHOOTING,
                        LockedLoadedEnchantments.ROCKETRY,
                    ))
                }
                empty(weight = 4)
            }
        })
    }
}