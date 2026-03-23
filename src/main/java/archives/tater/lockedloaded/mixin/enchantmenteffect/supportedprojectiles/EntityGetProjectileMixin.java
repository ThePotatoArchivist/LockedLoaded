package archives.tater.lockedloaded.mixin.enchantmenteffect.supportedprojectiles;

import archives.tater.lockedloaded.enchantment.SupportedItems;
import archives.tater.lockedloaded.registry.LockedLoadedEnchantmentEffects;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

@Mixin({Player.class, Monster.class})
public class EntityGetProjectileMixin {
    @ModifyExpressionValue(
            method = "getProjectile",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ProjectileWeaponItem;getSupportedHeldProjectiles()Ljava/util/function/Predicate;")
    )
    private Predicate<ItemStack> supportedHeldProjectiles(Predicate<ItemStack> original, ItemStack heldWeapon) {
        return SupportedItems.getSupportedItems(heldWeapon, LockedLoadedEnchantmentEffects.SUPPORTED_HELD_PROJECTILES, original);
    }
}
