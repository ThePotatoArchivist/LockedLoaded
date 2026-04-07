package archives.tater.lockedloaded.mixin.rocketride;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.phys.Vec3;

import org.jspecify.annotations.Nullable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract @Nullable Entity getFirstPassenger();

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
}
