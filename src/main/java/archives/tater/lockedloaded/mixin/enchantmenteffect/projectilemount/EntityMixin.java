package archives.tater.lockedloaded.mixin.enchantmenteffect.projectilemount;

import archives.tater.lockedloaded.registry.LockedLoadedAttachments;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@SuppressWarnings("NonExtendableApiUsage")
@Mixin(Entity.class)
public abstract class EntityMixin implements AttachmentTarget {

    @Shadow
    public abstract boolean isVehicle();

    @Shadow
    public abstract Level level();

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
