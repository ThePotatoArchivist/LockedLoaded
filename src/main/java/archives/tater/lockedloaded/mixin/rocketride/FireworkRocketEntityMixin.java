package archives.tater.lockedloaded.mixin.rocketride;

import archives.tater.lockedloaded.network.ServerboundRideFireworkPayload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

    @Unique
    private boolean rideInitialized = false;
    @Unique
    private double riddenSpeed = 0;
    @Unique
    private float riddenXRot = 0;
    @Unique
    private float riddenYRot = 0;

    public FireworkRocketEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @WrapWithCondition(
            method = "tick",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/projectile/FireworkRocketEntity;life:I", opcode = Opcodes.PUTFIELD)
    )
    private boolean increaseLifeMounted(FireworkRocketEntity instance, int value) {
        return life <= 0 || getPassengers().isEmpty() || tickCount % 4 == 0;
    }

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FireworkRocketEntity;isShotAtAngle()Z")
    )
    private void controlRotation(CallbackInfo ci) {
        var controller = getControllingPassenger();
        if (controller == null || !canSimulateMovement()) return;

        var strafe = controller.xxa;
        var forward = controller.zza;

        if (strafe == 0 && forward == 0 && rideInitialized) return;

        if (!rideInitialized) {
            riddenSpeed = getDeltaMovement().length();
            riddenYRot = getDeltaMovement().rotation().y;
            rideInitialized = true;
        }

        riddenXRot += -5 * forward;
        riddenYRot += -5 * strafe;
        var movement = Vec3.directionFromRotation(riddenXRot, riddenYRot).scale(riddenSpeed);
        setDeltaMovement(movement);
        if (level().isClientSide()) ClientPlayNetworking.send(new ServerboundRideFireworkPayload(movement));
    }
}
