package archives.tater.lockedloaded.mixin.enchantmenteffect.fireworkknockback;


import archives.tater.lockedloaded.enchantment.FireworkKnockback;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends Projectile {
    public FireworkRocketEntityMixin(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    @WrapOperation(
            method = "dealExplosionDamage",
            at = @At(value = "INVOKE:LAST", target = "Lnet/minecraft/world/entity/LivingEntity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z")
    )
    private boolean fireworkKnockback(LivingEntity instance, ServerLevel level, DamageSource source, float amount, Operation<Boolean> original) {
        var oldMovement = instance.getDeltaMovement();

        var result = original.call(instance, level, source, amount);

        FireworkKnockback.apply((FireworkRocketEntity) (Object) this, instance, 5.0, oldMovement);

        return result;
    }
}
