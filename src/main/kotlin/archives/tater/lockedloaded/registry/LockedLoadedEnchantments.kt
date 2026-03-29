package archives.tater.lockedloaded.registry

import archives.tater.lockedloaded.LockedLoaded
import archives.tater.lockedloaded.enchantment.PierceDeflection
import archives.tater.lockedloaded.registry.LockedLoadedEnchantmentEffects.PROJECTILE_PIERCE_DEFLECTION
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantments
import kotlin.jvm.optionals.getOrNull

object LockedLoadedEnchantments {
    fun of(path: String): ResourceKey<Enchantment> = ResourceKey.create(Registries.ENCHANTMENT, LockedLoaded.id(path))

    @JvmField val MULTICHAMBERED = of("multichambered")
    @JvmField val PUMP_CHARGE = of("pump_charge")
    @JvmField val MAGAZINE = of("magazine")
    @JvmField val SHARPSHOOTING = of("sharpshooting")
    @JvmField val ROCKETRY = of("rocketry")
    @JvmField val RECOVERY = of("recovery")
    @JvmField val TWIRLING_CURSE = of("twirling_curse")

    @JvmField val MAGAZINE_EXCLUSIVE: TagKey<Enchantment> = TagKey.create(Registries.ENCHANTMENT, LockedLoaded.id("exclusive_set/magazine"))
    @JvmField val MULTICHAMBERED_EXCLUSIVE: TagKey<Enchantment> = TagKey.create(Registries.ENCHANTMENT, LockedLoaded.id("exclusive_set/multichambered"))
    @JvmField val PUMP_CHARGE_EXCLUSIVE: TagKey<Enchantment> = TagKey.create(Registries.ENCHANTMENT, LockedLoaded.id("exclusive_set/pump_charge"))

    internal fun init() {
        EnchantmentEvents.MODIFY.register { key, builder, source ->
            if (source.isBuiltin && key == Enchantments.PIERCING) builder.apply {
                withEffect(PROJECTILE_PIERCE_DEFLECTION, setOf(
                    PierceDeflection.SHIELD,
                    PierceDeflection.SHULKER_SHELL,
                    PierceDeflection.BREEZE,
                    PierceDeflection.WITHER_ARMOR,
                    PierceDeflection.ENDER_DRAGON_PERCH,
                ))
            }
        }

        DynamicRegistrySetupCallback.EVENT.register {
            it.getOptional(Registries.ENCHANTMENT).getOrNull()?.apply {
                addAlias(Identifier.fromNamespaceAndPath("omnicrossbow", "multichambered"), MULTICHAMBERED.identifier())
                addAlias(Identifier.fromNamespaceAndPath("rocketriding", "rocketry"), ROCKETRY.identifier())
            }
        }
    }
}