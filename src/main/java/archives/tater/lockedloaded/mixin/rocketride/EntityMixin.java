package archives.tater.lockedloaded.mixin.rocketride;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;

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
}
