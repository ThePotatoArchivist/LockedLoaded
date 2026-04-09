package archives.tater.lockedloaded.mixin.enchantmenteffect.fireworkknockback;

import archives.tater.lockedloaded.mixin.rocketride.FireworkRocketEntityAccessor;
import archives.tater.lockedloaded.registry.LockedLoadedAttachments;
import archives.tater.lockedloaded.registry.LockedLoadedEnchantmentEffects;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import org.apache.commons.lang3.mutable.MutableFloat;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
    @ModifyExpressionValue(
            method = "createProjectile",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/Entity;DDDZ)Lnet/minecraft/world/entity/projectile/FireworkRocketEntity;")
    )
    private FireworkRocketEntity setFireworkKnockback(FireworkRocketEntity original, Level level, LivingEntity shooter, ItemStack heldItem, ItemStack projectile, boolean isCrit) {
        if (!(level instanceof ServerLevel serverLevel) || !EnchantmentHelper.has(heldItem, LockedLoadedEnchantmentEffects.FIREWORK_OWNER_KNOCKBACK)) return original;
        var explosions = ((FireworkRocketEntityAccessor) original).invokeGetExplosions().size();
        var knockback = new MutableFloat();
        EnchantmentHelper.runIterationOnItem(heldItem, (enchantment, enchantmentLevel) -> {
            var context = Enchantment.itemContext(serverLevel, enchantmentLevel, heldItem);
            Enchantment.applyEffects(enchantment.value().getEffects(LockedLoadedEnchantmentEffects.FIREWORK_OWNER_KNOCKBACK), context, fireworkKnockback ->
                    knockback.add(fireworkKnockback.get(enchantmentLevel, explosions))
            );
        });
        original.setAttached(LockedLoadedAttachments.FIREWORK_OWNER_KNOCKBACK, knockback.floatValue());
        return original;
    }
}
