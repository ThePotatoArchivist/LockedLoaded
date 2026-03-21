package archives.tater.lockedloaded.mixin.enchantmenteffect.ricochet;

import archives.tater.lockedloaded.registry.LockedLoadedAttachments;
import archives.tater.lockedloaded.registry.LockedLoadedEnchantmentEffects;
import archives.tater.lockedloaded.registry.LockedLoadedSounds;
import archives.tater.lockedloaded.util.LockedLoadedUtil;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jspecify.annotations.Nullable;

import static java.lang.Math.max;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {
    @Shadow
    private @Nullable IntOpenHashSet piercingIgnoreEntityIds;

    @Shadow
    public abstract byte getPierceLevel();

    @Shadow
    protected abstract void setPierceLevel(byte pieceLevel);

    public AbstractArrowMixin(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    @Unique
    private boolean ricocheted = false;

    @Inject(
            method = "<init>(Lnet/minecraft/world/entity/EntityType;DDDLnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("TAIL")
    )
    private void setRicochetCount(EntityType<? extends AbstractArrow> type, double x, double y, double z, Level level2, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon, CallbackInfo ci) {
        if (firedFromWeapon == null || !(level2 instanceof ServerLevel serverLevel)) return;
        setAttached(LockedLoadedAttachments.ORIGINAL_PIERCE_COUNT, getPierceLevel());
        var value = new MutableFloat(0);
        EnchantmentHelper.runIterationOnItem(firedFromWeapon, (enchantment, level) ->
                enchantment.value().modifyItemFilteredCount(LockedLoadedEnchantmentEffects.PROJECTILE_RICOCHET, serverLevel, level, firedFromWeapon, value)
        );
        var ricochetCount = (byte) max(0, value.intValue());
        if (ricochetCount > 0)
            setAttached(LockedLoadedAttachments.RICOCHET_LEVEL, ricochetCount);
    }

    @Unique
    private void ricochet(Vec3 location, Direction direction, byte ricochetLevel) {
        var axis = direction.getAxis();
        var movement = getDeltaMovement();
        setPos(location.add(direction.getUnitVec3().scale(getBbWidth())));
        setDeltaMovement(movement.with(axis, -movement.get(axis)));
        needsSync = true;
        setAttached(LockedLoadedAttachments.RICOCHET_LEVEL, (byte) (ricochetLevel - 1));
        playSound(LockedLoadedSounds.RICOCHET, 1.0F, 1.2F / (random.nextFloat() * 0.2F + 0.9F));
        if (piercingIgnoreEntityIds != null)
            piercingIgnoreEntityIds.clear();
        byte originalPierceCount = getAttachedOrElse(LockedLoadedAttachments.ORIGINAL_PIERCE_COUNT, (byte) 0);
        if (originalPierceCount > getPierceLevel())
            setPierceLevel(originalPierceCount);
    }

    @Inject(
            method = "onHitBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;"),
            cancellable = true
    )
    private void ricochetBlock(BlockHitResult hitResult, CallbackInfo ci) {
        byte ricochetLevel = getAttachedOrElse(LockedLoadedAttachments.RICOCHET_LEVEL, (byte) 0);
        if (ricochetLevel <= 0) return;

        ricochet(hitResult.getLocation(), hitResult.getDirection(), ricochetLevel);

        ci.cancel();
    }

    @Inject(
            method = "stepMoveAndHit",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;hitTargetsOrDeflectSelf(Ljava/util/Collection;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;")
    )
    private void resetRicocheted(BlockHitResult blockHitResult, CallbackInfo ci) {
        ricocheted = false;
    }

    @WrapOperation(
            method = "onHitEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;deflect(Lnet/minecraft/world/entity/projectile/ProjectileDeflection;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/EntityReference;Z)Z")
    )
    private boolean ricochetEntity(AbstractArrow instance, ProjectileDeflection projectileDeflection, Entity entity, EntityReference<Entity> entityReference, boolean b, Operation<Boolean> original, EntityHitResult entityHitResult, @Share("ricocheted") LocalBooleanRef ricocheted) {
        byte ricochetLevel = getAttachedOrElse(LockedLoadedAttachments.RICOCHET_LEVEL, (byte) 0);
        if (ricochetLevel <= 0) return original.call(instance, projectileDeflection, entity, entityReference, b);

        ricochet(entityHitResult.getLocation(), LockedLoadedUtil.getDirection(entity, entityHitResult.getLocation()), ricochetLevel);
        ricocheted.set(true);
        this.ricocheted = true;

        return true;
    }
    
    @WrapWithCondition(
            method = "onHitEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V")
    )
    private boolean skipMovementDecrease(AbstractArrow instance, Vec3 vec3, @Share("ricocheted") LocalBooleanRef ricocheted) {
        return !ricocheted.get();
    }

    @Definition(id = "NONE", field = "Lnet/minecraft/world/entity/projectile/ProjectileDeflection;NONE:Lnet/minecraft/world/entity/projectile/ProjectileDeflection;")
    @Expression("? != NONE")
    @ModifyExpressionValue(
            method = "stepMoveAndHit",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean stopHittingEntities(boolean original) {
        return original || ricocheted;
    }
}
