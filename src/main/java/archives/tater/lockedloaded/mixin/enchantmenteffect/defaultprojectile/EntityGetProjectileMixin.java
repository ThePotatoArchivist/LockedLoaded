package archives.tater.lockedloaded.mixin.enchantmenteffect.defaultprojectile;

import archives.tater.lockedloaded.LockedLoaded;
import archives.tater.lockedloaded.registry.LockedLoadedEnchantmentEffects;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import static archives.tater.lockedloaded.util.LockedLoadedUtil.getFirstEnchantmentComponent;
import static archives.tater.lockedloaded.util.LockedLoadedUtil.getOneStack;
import static java.util.Objects.requireNonNullElse;

@Mixin({Player.class, Monster.class})
public abstract class EntityGetProjectileMixin extends LivingEntity {
    protected EntityGetProjectileMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @ModifyExpressionValue(
            method = "getProjectile",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/item/ItemStack;")
    )
    private ItemStack useDefaultProjectile(ItemStack original, ItemStack heldWeapon) {
        return requireNonNullElse(
                getFirstEnchantmentComponent(heldWeapon, LockedLoadedEnchantmentEffects.DEFAULT_PROJECTILE_ITEM, (table, enchantmentLevel) ->
                        level() instanceof ServerLevel level
                                ? getOneStack(
                                        table.table(),
                                        Enchantment.entityContext(level, enchantmentLevel, this, position()),
                                        () -> LockedLoaded.logger.warn("Default projectile loot table returned more than one stack!")
                                )
                                : table.preview().create()
                ),
                original
        );
    }
}
