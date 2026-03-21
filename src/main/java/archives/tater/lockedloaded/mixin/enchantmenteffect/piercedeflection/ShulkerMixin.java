package archives.tater.lockedloaded.mixin.enchantmenteffect.piercedeflection;

import archives.tater.lockedloaded.enchantment.PierceDeflection;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.Shulker;

@Mixin(Shulker.class)
public class ShulkerMixin {
    @ModifyExpressionValue(
            method = "hurtServer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Shulker;isClosed()Z")
    )
    private boolean pierceShulker(boolean original, ServerLevel level, DamageSource source) {
        return original && !PierceDeflection.SHULKER_SHELL.canMaybeArrowPierce(source.getDirectEntity());
    }
}
