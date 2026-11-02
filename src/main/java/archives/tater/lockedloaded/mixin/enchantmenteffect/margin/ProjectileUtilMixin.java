package archives.tater.lockedloaded.mixin.enchantmenteffect.margin;

import archives.tater.lockedloaded.registry.LockedLoadedAttachments;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;

import static java.util.Objects.requireNonNull;

@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin {
    @ModifyReturnValue(
            method = "computeMargin",
            at = @At("RETURN")
    )
    private static float modifyMargin(float original, Entity source) {
        if (!source.hasAttached(LockedLoadedAttachments.PROJECTILE_MARGIN)) return original;
        return original * requireNonNull(source.getAttached(LockedLoadedAttachments.PROJECTILE_MARGIN));
    }
}
