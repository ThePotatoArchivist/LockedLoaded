package archives.tater.lockedloaded.datagen

import archives.tater.lockedloaded.registry.LockedLoadedEnchantments
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.tags.TagAppender
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.EnchantmentTags
import net.minecraft.tags.TagKey
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantments
import java.util.concurrent.CompletableFuture

class EnchantmentTagGenerator(
    output: FabricPackOutput,
    registryLookupFuture: CompletableFuture<HolderLookup.Provider>
) : FabricTagsProvider<Enchantment>(output, Registries.ENCHANTMENT, registryLookupFuture) {

    private fun penchantTag(path: String) =
        TagKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath("penchant", path))

    private fun buildTag(tag: TagKey<Enchantment>, block: TagAppender<ResourceKey<Enchantment>, Enchantment>.() -> Unit) {
        builder(tag).block()
    }

    override fun addTags(registries: HolderLookup.Provider) {
        buildTag(EnchantmentTags.NON_TREASURE) {
            +LockedLoadedEnchantments.MULTICHAMBERED
            +LockedLoadedEnchantments.PUMP_CHARGE
            +LockedLoadedEnchantments.SHARPSHOOTING
            +LockedLoadedEnchantments.ROCKETRY
            +LockedLoadedEnchantments.RECOVERY
        }
        buildTag(EnchantmentTags.TREASURE) {
            +LockedLoadedEnchantments.TWIRLING_CURSE
        }
        buildTag(EnchantmentTags.CURSE) {
            +LockedLoadedEnchantments.TWIRLING_CURSE
        }
        buildTag(EnchantmentTags.CROSSBOW_EXCLUSIVE) {
            +LockedLoadedEnchantments.MAGAZINE
            +LockedLoadedEnchantments.SHARPSHOOTING
            +LockedLoadedEnchantments.PUMP_CHARGE
            +LockedLoadedEnchantments.ROCKETRY
        }
        buildTag(LockedLoadedEnchantments.MAGAZINE_EXCLUSIVE) {
            +EnchantmentTags.CROSSBOW_EXCLUSIVE
            +Enchantments.QUICK_CHARGE
            +LockedLoadedEnchantments.MULTICHAMBERED
        }
        buildTag(LockedLoadedEnchantments.MULTICHAMBERED_EXCLUSIVE) {
            +Enchantments.QUICK_CHARGE
            +Enchantments.MULTISHOT
            +LockedLoadedEnchantments.PUMP_CHARGE
        }

        buildTag(penchantTag("common")) {
            +LockedLoadedEnchantments.MULTICHAMBERED
        }
        buildTag(penchantTag("uncommon")) {
            +LockedLoadedEnchantments.RECOVERY
        }
        buildTag(penchantTag("rare")) {
            +LockedLoadedEnchantments.PUMP_CHARGE
            +LockedLoadedEnchantments.SHARPSHOOTING
            +LockedLoadedEnchantments.ROCKETRY
        }
    }

}