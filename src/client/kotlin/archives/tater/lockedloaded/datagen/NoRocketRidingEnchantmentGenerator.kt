package archives.tater.lockedloaded.datagen

import archives.tater.lockedloaded.registry.LockedLoadedEnchantments
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.tags.EnchantmentTags
import net.minecraft.tags.ItemTags
import java.util.concurrent.CompletableFuture

class NoRocketRidingEnchantmentGenerator(
    output: FabricPackOutput,
    registriesFuture: CompletableFuture<HolderLookup.Provider>
) : FabricDynamicRegistryProvider(output, registriesFuture) {
    override fun configure(
        registries: HolderLookup.Provider,
        entries: Entries
    ) {
        entries.add(LockedLoadedEnchantments.ROCKETRY, EnchantmentGenerator.rocketry(
            registries.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
            registries.getOrThrow(EnchantmentTags.CROSSBOW_EXCLUSIVE),
            registries.lookupOrThrow(Registries.ITEM),
            registries.lookupOrThrow(Registries.ENTITY_TYPE),
            false,
        ).build(LockedLoadedEnchantments.ROCKETRY.identifier()))
    }

    override fun getName(): String = "Enchantments"
}