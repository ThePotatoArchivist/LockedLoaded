package archives.tater.lockedloaded.mixin.enchantmenteffect.piercedeflection;

import archives.tater.lockedloaded.enchantment.PierceDeflection;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonSittingPhase;

@Mixin(AbstractDragonSittingPhase.class)
public class AbstractDragonSittingPhaseMixin {
    @WrapOperation(
            method = "onHurt",
            at = @At(value = "CONSTANT", args = "classValue=net/minecraft/world/entity/projectile/arrow/AbstractArrow")
    )
    private boolean pierceDragon(Object object, Operation<Boolean> original) {
        return original.call(object) && !PierceDeflection.ENDER_DRAGON_PERCH.canMaybeArrowPierce(object);
    }
}
