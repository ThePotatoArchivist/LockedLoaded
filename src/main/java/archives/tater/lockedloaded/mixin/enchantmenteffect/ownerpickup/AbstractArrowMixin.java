package archives.tater.lockedloaded.mixin.enchantmenteffect.ownerpickup;

import archives.tater.lockedloaded.registry.LockedLoadedAttachments;
import archives.tater.lockedloaded.registry.LockedLoadedEnchantmentEffects;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {
    public AbstractArrowMixin(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/entity/EntityType;DDDLnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("TAIL")
    )
    private void setPersistent(EntityType<? extends AbstractArrow> type, double x, double y, double z, Level level2, ItemStack pickupItemStack, ItemStack firedFromWeapon, CallbackInfo ci) {
        if (firedFromWeapon == null || !(level2 instanceof ServerLevel serverLevel)) return;
        EnchantmentHelper.runIterationOnItem(firedFromWeapon, (enchantment, enchantmentLevel) -> {
            Enchantment.applyEffects(
                    enchantment.value().getEffects(LockedLoadedEnchantmentEffects.PROJECTILE_OWNER_PICKUP),
                    Enchantment.itemContext(serverLevel, enchantmentLevel, firedFromWeapon),
                    _ -> setAttached(LockedLoadedAttachments.PROJECTILE_OWNER_PICKUP, Unit.INSTANCE)
            );
        });
    }

    @WrapOperation(
            method = "tryPickup",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z")
    )
    private boolean requireOwner(Inventory instance, ItemStack itemStack, Operation<Boolean> original, Player player) {
        return (!hasAttached(LockedLoadedAttachments.PROJECTILE_OWNER_PICKUP) || getOwner() == null || !getOwner().canInteractWithLevel() || ownedBy(player)) && original.call(instance, itemStack);
    }
}
