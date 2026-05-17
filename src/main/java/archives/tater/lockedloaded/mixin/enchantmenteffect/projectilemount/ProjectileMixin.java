package archives.tater.lockedloaded.mixin.enchantmenteffect.projectilemount;

import archives.tater.lockedloaded.registry.LockedLoadedAttachments;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

@Mixin(Projectile.class)
public abstract class ProjectileMixin extends Entity {

    public ProjectileMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @ModifyReturnValue(
            method = "isPickable",
            at = @At("RETURN")
    )
    private boolean allowInteract(boolean original) {
        return original || hasAttached(LockedLoadedAttachments.MOUNTABLE_PROJECTILE);
    }
}
