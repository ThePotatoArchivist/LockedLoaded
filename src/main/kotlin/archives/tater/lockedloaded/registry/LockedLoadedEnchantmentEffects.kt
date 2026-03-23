package archives.tater.lockedloaded.registry

import archives.tater.lockedloaded.LockedLoaded
import archives.tater.lockedloaded.enchantment.*
import archives.tater.lockedloaded.util.McUnit
import archives.tater.lockedloaded.util.validatedListCodec
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.enchantment.ConditionalEffect
import net.minecraft.world.item.enchantment.LevelBasedValue
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets


object LockedLoadedEnchantmentEffects {
    private inline fun <T: Any> register(path: String, init: DataComponentType.Builder<T>.() -> Unit): DataComponentType<T> = Registry.register(
        BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE,
        LockedLoaded.id(path),
        DataComponentType.builder<T>().apply(init).build()
    )

    private fun <T: Any> register(path: String, codec: Codec<T>) = register(path) {
        persistent(codec)
    }

    private fun registerEntityEffect(path: String, codec: MapCodec<out EnchantmentEntityEffect>) {
        Registry.register(BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE, LockedLoaded.id(path), codec)
    }

    // First one will take priority
    @JvmField
    val LOAD_MULTIPLE = register("load_multiple", LoadMultiple.CODEC)

    @JvmField
    val PROJECTILE_FIRED_COUNT = register("projectile_fired_count", validatedListCodec(
        ConditionalEffect.codec(EnchantmentValueEffect.CODEC),
        LootContextParamSets.ENCHANTED_ENTITY
    ))

    @JvmField
    val PROJECTILE_UNCERTAINTY = register("projectile_uncertainty", validatedListCodec(
        ConditionalEffect.codec(ProjectileUncertainty.CODEC),
        LootContextParamSets.ENCHANTED_ENTITY
    ))

    @JvmField
    val PROJECTILE_VELOCITY = register("projectile_velocity", validatedListCodec(
        ConditionalEffect.codec(EnchantmentValueEffect.CODEC),
        LootContextParamSets.ENCHANTED_ENTITY
    ))

    @JvmField
    val CHARGED_PROJECTILE_INDICATOR = register("charged_projectile_indicator", ChargedProjectileIndicator.CODEC)

    @JvmField
    val CROSSBOW_COOLDOWN = register("crossbow_cooldown", validatedListCodec(
        ConditionalEffect.codec(EnchantmentValueEffect.CODEC),
        LootContextParamSets.ENCHANTED_ITEM
    ))

    @JvmField
    val PROJECTILE_RICOCHET = register("projectile_ricochet", validatedListCodec(
        ConditionalEffect.codec(EnchantmentValueEffect.CODEC),
        LootContextParamSets.ENCHANTED_ITEM
    ))

    @JvmField
    val PROJECTILE_IGNORE_OWNER = register("projectile_ignore_owner", validatedListCodec(
        ConditionalEffect.codec(McUnit.CODEC),
        LootContextParamSets.ENCHANTED_ITEM
    ))

    @JvmField
    val PROJECTILE_PIERCE_DEFLECTION = register("projectile_pierce_deflection", validatedListCodec(
        ConditionalEffect.codec(PierceDeflection.SET_CODEC),
        LootContextParamSets.ENCHANTED_ITEM
    ))

    @JvmField
    val MODIFY_PROJECTILE_ITEM = register("modify_projectile_item", validatedListCodec(
        ConditionalEffect.codec(LootItemFunctions.TYPED_CODEC),
        LootContextParamSets.ENCHANTED_ITEM
    ))

    @JvmField
    val DEFAULT_PROJECTILE_ITEM = register("default_projectile_item", PreviewedLootTable.codec(LootContextParamSets.ENCHANTED_ENTITY))

    @JvmField
    val SUPPORTED_PROJECTILES = register("supported_projectiles", SupportedItems.SHORT_CODEC)

    @JvmField
    val SUPPORTED_HELD_PROJECTILES = register("supported_held_projectiles", SupportedItems.SHORT_CODEC)

    @JvmField
    val FIREWORK_KNOCKBACK = register("firework_knockback", validatedListCodec(
        ConditionalEffect.codec(EnchantmentValueEffect.CODEC),
        LootContextParamSets.ENCHANTED_ITEM
    ))

    @JvmField
    val CROSSBOW_SPIN = register("crossbow_spin", LevelBasedValue.CODEC)

    internal fun init() {}
}