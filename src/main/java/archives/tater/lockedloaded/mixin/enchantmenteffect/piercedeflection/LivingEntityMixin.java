package archives.tater.lockedloaded.mixin.enchantmenteffect.piercedeflection;

import archives.tater.lockedloaded.enchantment.PierceDeflection;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Definition(id = "getPierceLevel", method = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;getPierceLevel()B")
    @Expression("?.getPierceLevel() > 0")
    @ModifyExpressionValue(
            method = "applyItemBlocking",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean pierceShield(boolean original, @Local(name = "abstractArrow") AbstractArrow abstractArrow) {
        return original && PierceDeflection.SHIELD.canArrowPierce(abstractArrow);
    }

    @SuppressWarnings("ConstantValue")
    @WrapOperation(
            method = "getDamageAfterArmorAbsorb",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean pierceShulkerArmor(DamageSource instance, TagKey<DamageType> tag, Operation<Boolean> original) {
        return original.call(instance, tag) ||
                ((Object) this instanceof Shulker)
                        && instance.getDirectEntity() instanceof AbstractArrow arrow
                        && PierceDeflection.SHULKER_SHELL.canArrowPierce(arrow)
                        && arrow.getPierceLevel() > 0;
    }
}
