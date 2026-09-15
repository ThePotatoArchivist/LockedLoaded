package archives.tater.lockedloaded.mixin.client.enchantmenteffect.spin;

import archives.tater.lockedloaded.LockedLoadedClient;
import archives.tater.lockedloaded.client.render.CrossbowSpinRendering;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.objectweb.asm.Opcodes;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class FirstPersonHandsAndItemsRendererMixin {

    @ModifyExpressionValue(
            method = "submitArmWithItem",
            at = {
                    @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CrossbowItem;isCharged(Lnet/minecraft/world/item/ItemStack;)Z"),
                    @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;isUsingItem:Z", opcode = Opcodes.GETFIELD)
            }
    )
    private static boolean normalPoseSpin(boolean original, PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state, float partialTicks, float xRot, InteractionHand hand) {
        return original && state.getDataOrDefault(LockedLoadedClient.spinProgress(hand), 0f) == 0f;
    }

    @Inject(
            method = "submitArmWithItem",
            at = @At(value = "INVOKE:FIRST", target = "Lnet/minecraft/client/renderer/FirstPersonHandsAndItemsRenderer;swingArm(FLcom/mojang/blaze3d/vertex/PoseStack;ILnet/minecraft/world/entity/HumanoidArm;)V")
    )
    private void spin(PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state, float partialTicks, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci) {
        float spinProgress = state.getDataOrDefault(LockedLoadedClient.spinProgress(hand), 0f);
        if (spinProgress == 0f) return;

        CrossbowSpinRendering.transformCrossbowSpinInHand(poseStack, spinProgress, (playerState.avatarRenderState != null && playerState.avatarRenderState.mainArm == HumanoidArm.LEFT) ^ hand == InteractionHand.OFF_HAND);
    }
}
