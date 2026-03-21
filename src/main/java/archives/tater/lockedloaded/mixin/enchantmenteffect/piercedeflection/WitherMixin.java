package archives.tater.lockedloaded.mixin.enchantmenteffect.piercedeflection;

import archives.tater.lockedloaded.enchantment.PierceDeflection;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.boss.wither.WitherBoss;

@Mixin(WitherBoss.class)
public class WitherMixin {

    @WrapOperation(
            method = "hurtServer",
            at = @At(value = "CONSTANT", args = "classValue=net/minecraft/world/entity/projectile/arrow/AbstractArrow")
    )
    private boolean pierceWitherArmor(Object object, Operation<Boolean> original) {
        return original.call(object) && !PierceDeflection.WITHER_ARMOR.canMaybeArrowPierce(object);
    }
}
