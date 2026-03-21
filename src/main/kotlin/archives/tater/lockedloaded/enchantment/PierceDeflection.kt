package archives.tater.lockedloaded.enchantment

import archives.tater.lockedloaded.LockedLoaded
import archives.tater.lockedloaded.registry.LockedLoadedAttachments
import archives.tater.lockedloaded.util.collection
import archives.tater.lockedloaded.util.get
import com.mojang.serialization.Codec
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.entity.projectile.arrow.AbstractArrow

class PierceDeflection {

    fun canArrowPierce(arrow: AbstractArrow) = arrow[LockedLoadedAttachments.PIERCE_DEFLECTION]?.contains(this) == true

    fun canMaybeArrowPierce(arrow: Any?) = arrow is AbstractArrow && canArrowPierce(arrow)

    companion object {
        @JvmField val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, PierceDeflection>()

        @JvmField val CODEC: Codec<PierceDeflection> = ID_MAPPER.codec(Identifier.CODEC)
        @JvmField val SET_CODEC = CODEC.listOf().collection(List<PierceDeflection>::toSet)

        @JvmStatic
        fun register(id: Identifier) = PierceDeflection().also {
            ID_MAPPER.put(id, it)
        }

        private fun register(path: String) = register(LockedLoaded.id(path))

        @JvmField val SHIELD = register("shield")
        @JvmField val SHULKER_SHELL = register("shulker_shell")
        @JvmField val BREEZE = register("breeze")
        @JvmField val WITHER_ARMOR = register("wither_armor")
        @JvmField val ENDER_DRAGON_PERCH = register("ender_dragon_perch")
        @JvmField val ENDERMAN_TELEPORT = register("enderman_teleport")
    }
}