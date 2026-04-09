package archives.tater.lockedloaded.mixin.mob;

import archives.tater.lockedloaded.enchantment.LoadMultiple;
import archives.tater.lockedloaded.registry.LockedLoadedComponents;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.objectweb.asm.Opcodes;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal.CrossbowState;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;

@Mixin(RangedCrossbowAttackGoal.class)
public class RangedCrossbowAttackGoalMixin<T extends Monster & RangedAttackMob & CrossbowAttackMob> {
    @Shadow
    private CrossbowState crossbowState;

    @Shadow
    private int attackDelay;

    @Shadow
    @Final
    private T mob;

    @ModifyVariable(
            method = "tick",
            name = "needsToMove",
            at = @At("STORE")
    )
    private boolean chargeFirst(boolean original, @Share("originalMove") LocalBooleanRef originalMove) {
        originalMove.set(original);
        return original && (crossbowState == CrossbowState.READY_TO_ATTACK || crossbowState == CrossbowState.CHARGED);
    }

    @Definition(id = "attackDelay", field = "Lnet/minecraft/world/entity/ai/goal/RangedCrossbowAttackGoal;attackDelay:I")
    @Expression("this.attackDelay == 0")
    @ModifyExpressionValue(
            method = "tick",
            at = @At("MIXINEXTRAS:EXPRESSION:FIRST")
    )
    private boolean moveWhileDelay(boolean original) {
        return true;
    }

    @Definition(id = "crossbowState", field = "Lnet/minecraft/world/entity/ai/goal/RangedCrossbowAttackGoal;crossbowState:Lnet/minecraft/world/entity/ai/goal/RangedCrossbowAttackGoal$CrossbowState;")
    @Definition(id = "READY_TO_ATTACK", field = "Lnet/minecraft/world/entity/ai/goal/RangedCrossbowAttackGoal$CrossbowState;READY_TO_ATTACK:Lnet/minecraft/world/entity/ai/goal/RangedCrossbowAttackGoal$CrossbowState;")
    @Expression("this.crossbowState == READY_TO_ATTACK")
    @ModifyExpressionValue(
            method = "tick",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean moveSecond(boolean original, @Share("originalMove") LocalBooleanRef originalMove) {
        return original && !originalMove.get();
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "FIELD:LAST", target = "Lnet/minecraft/world/entity/ai/goal/RangedCrossbowAttackGoal$CrossbowState;UNCHARGED:Lnet/minecraft/world/entity/ai/goal/RangedCrossbowAttackGoal$CrossbowState;", opcode = Opcodes.GETSTATIC)
    )
    private CrossbowState useMultiple(CrossbowState original) {
        if (!CrossbowItem.isCharged(mob.getItemInHand(ProjectileUtil.getWeaponHoldingHand(mob, Items.CROSSBOW)))) return original;
        attackDelay = 10 + mob.getRandom().nextInt(10);
        return RangedCrossbowAttackGoal.CrossbowState.CHARGED;
    }

    @Definition(id = "crossbowState", field = "Lnet/minecraft/world/entity/ai/goal/RangedCrossbowAttackGoal;crossbowState:Lnet/minecraft/world/entity/ai/goal/RangedCrossbowAttackGoal$CrossbowState;")
    @Definition(id = "CHARGED", field = "Lnet/minecraft/world/entity/ai/goal/RangedCrossbowAttackGoal$CrossbowState;CHARGED:Lnet/minecraft/world/entity/ai/goal/RangedCrossbowAttackGoal$CrossbowState;")
    @Expression("this.crossbowState = CHARGED")
    @WrapOperation(
            method = "tick",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private void loadMultiple(RangedCrossbowAttackGoal<T> instance, CrossbowState value, Operation<Void> original, @Share("originalMove") LocalBooleanRef originalMove, @Cancellable CallbackInfo ci) {
        var hand = ProjectileUtil.getWeaponHoldingHand(mob, Items.CROSSBOW);
        var stack = mob.getItemInHand(hand);
        if (!originalMove.get() || stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).items().size() >= LoadMultiple.maxProjectilesOrDefault(stack)) {
            original.call(instance, value);
            return;
        }

        stack.set(LockedLoadedComponents.ADDITIONAL_CHARGED_PROJECTILES, stack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY));
        mob.startUsingItem(hand);
        original.call(instance, CrossbowState.CHARGING);
        ci.cancel();
    }

    @ModifyReturnValue(
            method = "canRun",
            at = @At("RETURN")
    )
    private boolean alwaysRun(boolean original) {
        return true;
    }
}
