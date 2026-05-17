package archives.tater.lockedloaded.datagen

import archives.tater.lockedloaded.registry.LockedLoadedEnchantmentTags
import archives.tater.lockedloaded.registry.LockedLoadedEnchantments
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider
import net.minecraft.core.HolderLookup
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.util.valueproviders.UniformInt
import net.minecraft.world.item.enchantment.providers.EnchantmentsByCost
import net.minecraft.world.item.enchantment.providers.SingleEnchantment
import java.util.concurrent.CompletableFuture

class EnchantmentProviderGenerator(
    output: FabricPackOutput,
    registriesFuture: CompletableFuture<HolderLookup.Provider>
) : FabricDynamicRegistryProvider(output, registriesFuture) {
    override fun configure(
        registries: HolderLookup.Provider,
        entries: Entries
    ) {
        entries.add(LockedLoadedEnchantments.RAID_PILLAGER_POST_WAVE_5_RARE, EnchantmentsByCost(
            registries.getOrThrow(LockedLoadedEnchantmentTags.RAID_PILLAGER_POST_WAVE_5_RARE),
            ConstantInt(50)
        ))
        entries.add(LockedLoadedEnchantments.RAID_PILLAGER_POST_WAVE_5_COMMON, SingleEnchantment(
            registries.getOrThrow(LockedLoadedEnchantments.MULTICHAMBERED),
            UniformInt.of(2, 3)
        ))
    }

    override fun getName(): String = "Enchantment Providers"
}