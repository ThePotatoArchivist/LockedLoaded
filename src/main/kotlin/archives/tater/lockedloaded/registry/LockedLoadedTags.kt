package archives.tater.lockedloaded.registry

import archives.tater.lockedloaded.LockedLoaded
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey

object LockedLoadedTags {
    private fun <T: Any> of(registry: ResourceKey<Registry<T>>, path: String): TagKey<T> = TagKey.create(registry, LockedLoaded.id(path))
    private fun ofItem(path: String) = of(Registries.ITEM, path)
    private fun ofBlock(path: String) = of(Registries.BLOCK, path)
    private fun ofEntity(path: String) = of(Registries.ENTITY_TYPE, path)

    @JvmField val BYPASSES_COOLDOWN_SAME_ATTACKER = of(Registries.DAMAGE_TYPE, "bypasses_cooldown_same_attacker")

}