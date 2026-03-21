package archives.tater.lockedloaded.mixin.enchantmenteffect.piercedeflection;

import archives.tater.lockedloaded.enchantment.PierceDeflection;
import archives.tater.lockedloaded.registry.LockedLoadedAttachments;
import archives.tater.lockedloaded.registry.LockedLoadedEnchantmentEffects;

import org.spongepowered.asm.mixin.Mixin;
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

import java.util.HashSet;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {

    public AbstractArrowMixin(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/entity/EntityType;DDDLnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("TAIL")
    )
    private void setDeflection(EntityType<? extends AbstractArrow> type, double x, double y, double z, Level level2, ItemStack pickupItemStack, ItemStack firedFromWeapon, CallbackInfo ci) {
        if (firedFromWeapon == null || !(level2 instanceof ServerLevel serverLevel)) return;
        var pierced = new HashSet<PierceDeflection>();
        EnchantmentHelper.runIterationOnItem(firedFromWeapon, (enchantment, level) -> {
            Enchantment.applyEffects(
                    enchantment.value().getEffects(LockedLoadedEnchantmentEffects.PROJECTILE_PIERCE_DEFLECTION),
                    Enchantment.itemContext(serverLevel, level, firedFromWeapon),
                    pierced::addAll
            );
        });
        setAttached(LockedLoadedAttachments.PIERCE_DEFLECTION, pierced);
    }
}
