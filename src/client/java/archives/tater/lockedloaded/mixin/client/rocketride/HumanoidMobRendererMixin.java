package archives.tater.lockedloaded.mixin.client.rocketride;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;

@Mixin(HumanoidMobRenderer.class)
public class HumanoidMobRendererMixin {
    @WrapOperation(
            method = "extractHumanoidRenderState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isPassenger()Z")
    )
    private static boolean rocketRidePose(LivingEntity instance, Operation<Boolean> original) {
        return original.call(instance) && !(instance.getVehicle() instanceof FireworkRocketEntity);
    }
}
