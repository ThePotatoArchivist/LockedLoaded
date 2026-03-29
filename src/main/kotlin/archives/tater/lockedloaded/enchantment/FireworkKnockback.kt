package archives.tater.lockedloaded.enchantment

import archives.tater.lockedloaded.registry.LockedLoadedAttachments
import archives.tater.lockedloaded.util.*
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.FireworkRocketEntity

fun applyFireworkKnockback(firework: FireworkRocketEntity, entity: LivingEntity, radius: Double) {
    if (entity != firework.owner || entity.onGround()) return
    val knockback = firework.getAttachedOrElse(LockedLoadedAttachments.FIREWORK_OWNER_KNOCKBACK, 0f);
    if (knockback <= 0) return

    entity.deltaMovement = (entity.centerPos - firework.position()) * (knockback / radius)
    entity.setIgnoreFallDamageFromCurrentImpulse(true, entity.position())
    entity.hurtMarked = true
    entity[LockedLoadedAttachments.DISCARD_FRICTION_CURRENT_IMPULSE] = McUnit.INSTANCE
}
