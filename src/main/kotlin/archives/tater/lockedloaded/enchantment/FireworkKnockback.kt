package archives.tater.lockedloaded.enchantment

import archives.tater.lockedloaded.registry.LockedLoadedAttachments
import archives.tater.lockedloaded.util.centerPos
import archives.tater.lockedloaded.util.minus
import archives.tater.lockedloaded.util.times
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.FireworkRocketEntity

fun applyFireworkKnockback(firework: FireworkRocketEntity, entity: LivingEntity, radius: Double) {
    val knockback = firework.getAttachedOrElse(LockedLoadedAttachments.FIREWORK_KNOCKBACK, 0f);
    if (knockback <= 0) return

    entity.addDeltaMovement((entity.centerPos - firework.position()) * (knockback / radius))
    entity.needsSync = true
}
