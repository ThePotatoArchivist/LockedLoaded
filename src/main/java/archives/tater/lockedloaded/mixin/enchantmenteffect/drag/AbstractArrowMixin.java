package archives.tater.lockedloaded.mixin.enchantmenteffect.drag;

import archives.tater.lockedloaded.registry.LockedLoadedAttachments;
import archives.tater.lockedloaded.registry.LockedLoadedEnchantmentEffects;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import org.apache.commons.lang3.mutable.MutableFloat;

import static java.util.Objects.requireNonNull;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {
    public AbstractArrowMixin(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    @Shadow
    protected abstract float getAirDrag();

    @Inject(
            method = "<init>(Lnet/minecraft/world/entity/EntityType;DDDLnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("TAIL")
    )
    private void setDrag(EntityType<? extends AbstractArrow> type, double x, double y, double z, Level level, ItemStack pickupItemStack, ItemStack firedFromWeapon, CallbackInfo ci) {
        if (firedFromWeapon == null || !(level instanceof ServerLevel serverLevel)) return;
        var drag = new MutableFloat(getAirDrag());
        EnchantmentHelper.runIterationOnItem(firedFromWeapon, (enchantment, enchantmentLevel) -> {
            Enchantment.applyEffects(
                    enchantment.value().getEffects(LockedLoadedEnchantmentEffects.PROJECTILE_DRAG),
                    Enchantment.entityContext(serverLevel, enchantmentLevel, this, position()),
                    effect -> drag.setValue(effect.process(enchantmentLevel, random, drag.floatValue()))
            );
        });
        if (drag.floatValue() != getAirDrag())
            setAttached(LockedLoadedAttachments.PROJECTILE_DRAG, drag.floatValue());
    }

    @ModifyReturnValue(
            method = "getAirDrag",
            at = @At("RETURN")
    )
    private float applyDrag(float original) {
        if (!hasAttached(LockedLoadedAttachments.PROJECTILE_DRAG)) return original; // Don't use getAttachedOrElse to avoid boxing
        return requireNonNull(getAttached(LockedLoadedAttachments.PROJECTILE_DRAG));
    }
}
