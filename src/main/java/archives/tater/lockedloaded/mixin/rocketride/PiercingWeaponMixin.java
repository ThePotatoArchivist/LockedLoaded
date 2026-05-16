package archives.tater.lockedloaded.mixin.rocketride;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.item.component.PiercingWeapon;

import static java.lang.Math.max;

@Mixin(PiercingWeapon.class)
public class PiercingWeaponMixin {
    @WrapOperation(
            method = "canHitEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPassengerOfSameVehicle(Lnet/minecraft/world/entity/Entity;)Z")
    )
    private static boolean allowSpearRocket(Entity instance, Entity other, Operation<Boolean> original) {
        return original.call(instance, other) && !(other instanceof FireworkRocketEntity);
    }

    @WrapOperation(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttackRangeWith(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/component/AttackRange;")
    )
    private AttackRange adjustAttackRange(LivingEntity instance, ItemStack weaponItem, Operation<AttackRange> original) {
        var result = original.call(instance, weaponItem);
        if (!(instance.getVehicle() instanceof FireworkRocketEntity)) return result;
        return new AttackRange(
                max(0, result.minReach() - 1),
                result.maxReach(),
                max(0, result.minCreativeReach() - 1),
                result.maxCreativeReach(),
                result.hitboxMargin(),
                result.mobFactor()
        );
    }

    @WrapOperation(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;stabAttack(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/Entity;FZZZ)Z")
    )
    private boolean dismountRocket(LivingEntity instance, EquipmentSlot weaponSlot, Entity target, float baseDamage, boolean dealsDamage, boolean dealsKnockback, boolean dismounts, Operation<Boolean> original) {
        var result = original.call(instance, weaponSlot, target, baseDamage, dealsDamage, dealsKnockback, dismounts);
        if (!result || !(target instanceof FireworkRocketEntity)) return result;
        if (instance.getVehicle() == target) instance.stopRiding();
        target.setDeltaMovement(instance.getLookAngle().scale(target.getDeltaMovement().length()));
        return true;
    }
}
