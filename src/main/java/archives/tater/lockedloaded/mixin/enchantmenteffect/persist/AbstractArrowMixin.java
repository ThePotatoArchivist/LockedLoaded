package archives.tater.lockedloaded.mixin.enchantmenteffect.persist;

import archives.tater.lockedloaded.registry.LockedLoadedAttachments;
import archives.tater.lockedloaded.registry.LockedLoadedEnchantmentEffects;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
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
                    enchantment.value().getEffects(LockedLoadedEnchantmentEffects.PROJECTILE_PERSIST),
                    Enchantment.itemContext(serverLevel, enchantmentLevel, firedFromWeapon),
                    _ -> setAttached(LockedLoadedAttachments.PROJECTILE_PERSISTENT, Unit.INSTANCE)
            );
        });
    }

    @Definition(id = "getPierceLevel", method = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;getPierceLevel()B")
    @Expression("?.getPierceLevel() <= 0")
    @ModifyExpressionValue(
            method = "onHitEntity",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean noDespawn(boolean original) {
        if (!original) return false;
        if (!hasAttached(LockedLoadedAttachments.PROJECTILE_PERSISTENT)) return true;
        setAttached(LockedLoadedAttachments.PROJECTILE_DEALT_DAMAGE, Unit.INSTANCE);
        return false;
    }

    @ModifyReturnValue(
            method = "canHitEntity",
            at = @At("RETURN")
    )
    private boolean noHit(boolean original) {
        return original && !hasAttached(LockedLoadedAttachments.PROJECTILE_DEALT_DAMAGE);
    }
}
