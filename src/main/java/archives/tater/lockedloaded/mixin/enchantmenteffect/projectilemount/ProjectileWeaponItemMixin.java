package archives.tater.lockedloaded.mixin.enchantmenteffect.projectilemount;

import archives.tater.lockedloaded.registry.LockedLoadedEnchantmentEffects;
import archives.tater.lockedloaded.registry.LockedLoadedLootContext;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

@Mixin(ProjectileWeaponItem.class)
public class ProjectileWeaponItemMixin {
    @ModifyExpressionValue(
            method = "shoot",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectile(Lnet/minecraft/world/entity/projectile/Projectile;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Ljava/util/function/Consumer;)Lnet/minecraft/world/entity/projectile/Projectile;")
    )
    private <T extends Projectile> T mountProjectile(T original, ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon) {
        EnchantmentHelper.runIterationOnItem(weapon, (enchantment, enchantmentLevel) -> {
            Enchantment.applyEffects(
                    enchantment.value().getEffects(LockedLoadedEnchantmentEffects.PROJECTILE_MOUNT),
                    LockedLoadedLootContext.projectileContext(level, enchantmentLevel, original),
                    _ -> shooter.startRiding(original)
            );
        });

        return original;
    }
}
