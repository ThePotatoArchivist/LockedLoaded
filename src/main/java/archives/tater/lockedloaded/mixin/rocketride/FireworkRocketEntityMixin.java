package archives.tater.lockedloaded.mixin.rocketride;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.objectweb.asm.Opcodes;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends Entity {
    @Shadow
    private int life;

    public FireworkRocketEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @WrapWithCondition(
            method = "tick",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/projectile/FireworkRocketEntity;life:I", opcode = Opcodes.PUTFIELD)
    )
    private boolean increaseLifeMounted(FireworkRocketEntity instance, int value) {
        return life <= 0 || getPassengers().isEmpty() || tickCount % 2 == 0;
    }

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FireworkRocketEntity;isShotAtAngle()Z")
    )
    private void controlRotation(CallbackInfo ci) {
        var controller = getControllingPassenger();
        if (controller == null) return;
        if (!canSimulateMovement()) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }

        float strafe = controller.xxa;
        float forward = controller.zza;

        var movement = getDeltaMovement();
        var rotation = movement.rotation();
        setDeltaMovement(Vec3.directionFromRotation(rotation.x + -5 * forward, rotation.y + -5 * strafe).scale(movement.length()));
    }
}
