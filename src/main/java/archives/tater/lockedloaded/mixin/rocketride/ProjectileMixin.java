package archives.tater.lockedloaded.mixin.rocketride;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

@Mixin(Projectile.class)
public abstract class ProjectileMixin extends Entity {
    public ProjectileMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @SuppressWarnings("ConstantValue")
    @ModifyReturnValue(
            method = "canHitEntity",
            at = @At("RETURN")
    )
    private boolean fireworkNoHitPassenger(boolean original, Entity entity) {
        return original && (!((Object) this instanceof FireworkRocketEntity) || !isPassengerOfSameVehicle(entity));
    }
}
