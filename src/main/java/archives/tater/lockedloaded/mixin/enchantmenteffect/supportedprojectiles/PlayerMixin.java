package archives.tater.lockedloaded.mixin.enchantmenteffect.supportedprojectiles;

import archives.tater.lockedloaded.enchantment.SupportedItems;
import archives.tater.lockedloaded.registry.LockedLoadedEnchantmentEffects;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

@Mixin(Player.class)
public class PlayerMixin {
    @ModifyExpressionValue(
            method = "getProjectile",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ProjectileWeaponItem;getAllSupportedProjectiles()Ljava/util/function/Predicate;")
    )
    private Predicate<ItemStack> supportedProjectiles(Predicate<ItemStack> original, ItemStack heldWeapon) {
        return SupportedItems.getSupportedItems(heldWeapon, LockedLoadedEnchantmentEffects.SUPPORTED_PROJECTILES, original);
    }
}
