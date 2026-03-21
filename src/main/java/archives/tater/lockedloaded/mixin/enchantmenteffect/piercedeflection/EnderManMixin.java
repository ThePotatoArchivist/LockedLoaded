package archives.tater.lockedloaded.mixin.enchantmenteffect.piercedeflection;

import archives.tater.lockedloaded.enchantment.PierceDeflection;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.monster.EnderMan;

@Mixin(EnderMan.class)
public class EnderManMixin {

    @WrapOperation(
            method = "hurtServer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean pierceEnderman(DamageSource instance, TagKey<DamageType> tag, Operation<Boolean> original) {
        return original.call(instance, tag) && !PierceDeflection.ENDERMAN_TELEPORT.canMaybeArrowPierce(instance.getDirectEntity());
    }
}
