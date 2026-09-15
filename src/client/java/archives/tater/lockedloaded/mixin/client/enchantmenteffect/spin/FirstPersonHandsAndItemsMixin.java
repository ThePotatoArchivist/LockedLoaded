package archives.tater.lockedloaded.mixin.client.enchantmenteffect.spin;

import archives.tater.lockedloaded.LockedLoadedClient;
import archives.tater.lockedloaded.client.render.CrossbowSpinRendering;
import archives.tater.lockedloaded.registry.LockedLoadedAttachments;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.world.item.ItemStack;

@Mixin(FirstPersonHandsAndItems.class)
public class FirstPersonHandsAndItemsMixin {

    @Definition(id = "usedItemStack", local = @Local(type = ItemStack.class, name = "usedItemStack"))
    @Definition(id = "is", method = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z")
    @Expression("usedItemStack.is(?)")
    @ModifyExpressionValue(
            method = "evaluateWhichHandsToRender",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private static boolean normalPoseSpin(boolean original, @Local(argsOnly = true, name = "player") LocalPlayer player) {
        return original && !player.hasAttached(LockedLoadedAttachments.SPINNING_ITEM);
    }

    @Definition(id = "isChargedCrossbow", method = "Lnet/minecraft/client/player/FirstPersonHandsAndItems;isChargedCrossbow(Lnet/minecraft/world/item/ItemStack;)Z")
    @Definition(id = "offhandItem", local = @Local(type = ItemStack.class, name = "offhandItem"))
    @Expression("isChargedCrossbow(offhandItem)")
    @WrapOperation(
            method = "evaluateWhichHandsToRender",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private static boolean showBothCrossbows(ItemStack item, Operation<Boolean> original, @Local(argsOnly = true, name = "player") LocalPlayer player) {
        return original.call(item) && !(CrossbowSpinRendering.shouldSpin(item) && CrossbowSpinRendering.shouldSpin(player.getMainHandItem()));
    }

    @Inject(
            method = "extractRenderState",
            at = @At("TAIL")
    )
    private void setSpinning(LocalPlayer player, float partialTicks, FirstPersonHandsAndItemsRenderState state, CallbackInfo ci) {
        var spinning = player.hasAttached(LockedLoadedAttachments.SPINNING_ITEM);
        var spinDuration = player.getTicksUsingItem(partialTicks);
        state.setData(LockedLoadedClient.MAINHAND_SPIN_PROGRESS, spinning && CrossbowSpinRendering.shouldSpin(state.mainHandItem) ? spinDuration : 0);
        state.setData(LockedLoadedClient.OFFHAND_SPIN_PROGRESS, spinning && CrossbowSpinRendering.shouldSpin(state.offHandItem) ? spinDuration : 0);
    }
}
