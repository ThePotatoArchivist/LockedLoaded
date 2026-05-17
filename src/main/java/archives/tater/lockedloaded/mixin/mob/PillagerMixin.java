package archives.tater.lockedloaded.mixin.mob;

import archives.tater.lockedloaded.registry.LockedLoadedEnchantments;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.objectweb.asm.Opcodes;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.level.Level;

@Mixin(Pillager.class)
public abstract class PillagerMixin extends AbstractIllager {
    protected PillagerMixin(EntityType<? extends AbstractIllager> type, Level level) {
        super(type, level);
    }

    @ModifyExpressionValue(
            method = "applyRaidBuffs",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/enchantment/providers/VanillaEnchantmentProviders;RAID_PILLAGER_POST_WAVE_5:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC)
    )
    private ResourceKey<EnchantmentProvider> customEnchantments(ResourceKey<EnchantmentProvider> original) {
        if (getRandom().nextFloat() < 0.2f)
            return LockedLoadedEnchantments.RAID_PILLAGER_POST_WAVE_5_RARE;
        if (getRandom().nextFloat() < 0.4f)
            return LockedLoadedEnchantments.RAID_PILLAGER_POST_WAVE_5_COMMON;
        return original;
    }
}
