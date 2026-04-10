package archives.tater.lockedloaded.mixin.damagecooldown;

import archives.tater.lockedloaded.registry.LockedLoadedTags;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract LivingEntity getLastAttacker();

    @Definition(id = "is", method = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z")
    @Definition(id = "BYPASSES_COOLDOWN", field = "Lnet/minecraft/tags/DamageTypeTags;BYPASSES_COOLDOWN:Lnet/minecraft/tags/TagKey;")
    @Expression("?.is(BYPASSES_COOLDOWN)")
    @WrapOperation(
            method = "hurtServer",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean bypassCooldownSameAttacker(DamageSource instance, TagKey<DamageType> tag, Operation<Boolean> original) {
        return original.call(instance, tag) || instance.is(LockedLoadedTags.BYPASSES_COOLDOWN_SAME_ATTACKER) && Objects.equals(instance.getEntity(), getLastAttacker());
    }
}
