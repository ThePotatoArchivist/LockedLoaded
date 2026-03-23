package archives.tater.lockedloaded.mixin.enchantmenteffect.modifyprojectileitem;

import archives.tater.lockedloaded.registry.LockedLoadedEnchantmentEffects;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import org.apache.commons.lang3.mutable.MutableObject;

@Mixin(ProjectileWeaponItem.class)
public class ProjectileWeaponItemMixin {
    @SuppressWarnings("unchecked")
    @ModifyExpressionValue(
            method = "shoot",
            at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;")
    )
    private <E> E modifyProjectileItem(E original, ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon) {
        if (!EnchantmentHelper.has(weapon, LockedLoadedEnchantmentEffects.MODIFY_PROJECTILE_ITEM)) return original;
        if (!(original instanceof ItemStack stack)) return original;

        var result = new MutableObject<>(stack);
        EnchantmentHelper.runIterationOnItem(weapon, (enchantment, enchantmentLevel) -> {
            var context = Enchantment.entityContext(level, enchantmentLevel, shooter, shooter.position());
            Enchantment.applyEffects(
                    enchantment.value().getEffects(LockedLoadedEnchantmentEffects.MODIFY_PROJECTILE_ITEM),
                    context,
                    effect -> result.setValue(effect.apply(result.get(), context))
            );
        });

        return (E) result.get();
    }
}
