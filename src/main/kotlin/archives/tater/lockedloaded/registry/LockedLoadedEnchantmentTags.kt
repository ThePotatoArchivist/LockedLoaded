package archives.tater.lockedloaded.registry

import archives.tater.lockedloaded.LockedLoaded
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.enchantment.Enchantment

object LockedLoadedEnchantmentTags {
    private fun create(path: String): TagKey<Enchantment> =
        TagKey.create(Registries.ENCHANTMENT, LockedLoaded.id(path))

    @JvmField val SWARM_EXCLUSIVE: TagKey<Enchantment> = create("exclusive_set/swarm")
    @JvmField val MULTICHAMBERED_EXCLUSIVE: TagKey<Enchantment> = create("exclusive_set/multichambered")
    @JvmField val RAID_PILLAGER_POST_WAVE_5_RARE: TagKey<Enchantment> = create("raid_pillager_post_wave_5_rare")
}