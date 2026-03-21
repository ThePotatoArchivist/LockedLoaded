package archives.tater.lockedloaded.mixin.enchantmenteffect.piercedeflection;

import archives.tater.lockedloaded.enchantment.PierceDeflection;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.projectile.Projectile;

@Mixin(Breeze.class)
public class BreezeMixin {
    @WrapOperation(
            method = "deflection",
            at = @At(value = "INVOKE:LAST", target = "Lnet/minecraft/world/entity/projectile/Projectile;is(Ljava/lang/Object;)Z")
    )
    private boolean pierceBreeze(Projectile instance, Object o, Operation<Boolean> original) {
        return original.call(instance, o) || PierceDeflection.BREEZE.canMaybeArrowPierce(instance);
    }
}
