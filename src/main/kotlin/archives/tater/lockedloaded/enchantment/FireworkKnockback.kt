package archives.tater.lockedloaded.enchantment

import archives.tater.lockedloaded.registry.LockedLoadedAttachments
import archives.tater.lockedloaded.util.*
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.FireworkRocketEntity
import net.minecraft.world.item.enchantment.LevelBasedValue
import net.minecraft.world.phys.Vec3


@JvmRecord
data class FireworkKnockback(val base: LevelBasedValue, val perExtraExplosion: LevelBasedValue = ZERO) {
    fun get(enchantmentLevel: Int, explosions: Int) = base.calculate(enchantmentLevel) + (explosions - 1) * perExtraExplosion.calculate(enchantmentLevel)

    companion object {
        val ZERO = LevelBasedValue.constant(0f)

        val CODEC: Codec<FireworkKnockback> = RecordCodecBuilder.create { it.group(
            LevelBasedValue.CODEC.fieldOf("base").forGetter(FireworkKnockback::base),
            LevelBasedValue.CODEC.optionalFieldOf("per_extra_explosion", ZERO).forGetter(FireworkKnockback::perExtraExplosion)
        ).apply(it, ::FireworkKnockback) }

        @JvmStatic
        fun apply(firework: FireworkRocketEntity, entity: LivingEntity, radius: Double, oldMovement: Vec3) {
            if (entity != firework.owner || entity.onGround() || entity.isPassenger) return
            val knockback = firework.getAttachedOrElse(LockedLoadedAttachments.FIREWORK_OWNER_KNOCKBACK, 0f);
            if (knockback <= 0) return

            entity.deltaMovement = oldMovement + (entity.centerPos - firework.position()) * (knockback / radius)
            entity.setIgnoreFallDamageFromCurrentImpulse(true, entity.position())
            entity.hurtMarked = true
            entity[LockedLoadedAttachments.DISCARD_FRICTION_CURRENT_IMPULSE] = McUnit.INSTANCE
        }
    }
}