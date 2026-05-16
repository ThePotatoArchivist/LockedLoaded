package archives.tater.lockedloaded.mixin.rocketride;

import archives.tater.lockedloaded.registry.LockedLoadedAttachments;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.jspecify.annotations.Nullable;

@SuppressWarnings("NonExtendableApiUsage")
@Mixin(Entity.class)
public abstract class EntityMixin implements AttachmentTarget {
    @Shadow
    public abstract @Nullable Entity getFirstPassenger();

    @Shadow
    public abstract boolean isVehicle();

    @Shadow
    public abstract Level level();

    @SuppressWarnings("ConstantValue")
    @ModifyReturnValue(
            method = "getControllingPassenger",
            at = @At("RETURN")
    )
    private LivingEntity fireworkController(LivingEntity original) {
        if (original != null) return original;
        if (!((Object) this instanceof FireworkRocketEntity)) return null;
        return getFirstPassenger() instanceof Player player ? player : null;
    }
    @SuppressWarnings("ConstantValue")
    @ModifyReturnValue(
            method = "getVehicleAttachmentPoint",
            at = @At("RETURN")
    )
    private Vec3 playerSitHeight(Vec3 original, Entity vehicle) {
        if (!((Object) this instanceof Player) || !(vehicle instanceof FireworkRocketEntity)) return original;
        return original.add(0.0, -0.6, 0.0);
    }

    @ModifyReturnValue(
            method = "interact",
            at = @At("TAIL")
    )
    private InteractionResult mountProjectile(InteractionResult original, Player player) {
        if (!hasAttached(LockedLoadedAttachments.MOUNTABLE_PROJECTILE) || isVehicle() || player.isSecondaryUseActive())
            return original;

        if (!level().isClientSide())
            player.startRiding((Entity) (Object) this);

        return InteractionResult.SUCCESS;
    }
}
