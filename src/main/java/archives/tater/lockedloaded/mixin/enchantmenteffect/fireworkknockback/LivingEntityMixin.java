package archives.tater.lockedloaded.mixin.enchantmenteffect.fireworkknockback;

import archives.tater.lockedloaded.registry.LockedLoadedAttachments;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    private int currentImpulseContextResetGraceTime;

    @Shadow
    @Final
    private static int CURRENT_IMPULSE_CONTEXT_RESET_GRACE_TIME_TICKS;

    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @ModifyReturnValue(
            method = "shouldDiscardFriction",
            at = @At("RETURN")
    )
    private boolean discardFriction(boolean original) {
        return original || hasAttached(LockedLoadedAttachments.DISCARD_FRICTION_CURRENT_IMPULSE);
    }

    @Inject(
            method = "resetCurrentImpulseContext",
            at = @At("TAIL")
    )
    private void resetDiscardFriction(CallbackInfo ci) {
        removeAttached(LockedLoadedAttachments.DISCARD_FRICTION_CURRENT_IMPULSE);
    }

    @Inject(
            method = "tryResetCurrentImpulseContext",
            at = @At("TAIL")
    )
    private void resetDiscardFriction2(CallbackInfo ci) {
        if (currentImpulseContextResetGraceTime < CURRENT_IMPULSE_CONTEXT_RESET_GRACE_TIME_TICKS - 10) // keep rocketslide
            removeAttached(LockedLoadedAttachments.DISCARD_FRICTION_CURRENT_IMPULSE);
    }
}
