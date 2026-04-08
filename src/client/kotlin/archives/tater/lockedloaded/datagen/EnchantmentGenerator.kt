package archives.tater.lockedloaded.datagen

import archives.tater.lockedloaded.enchantment.*
import archives.tater.lockedloaded.loot.condition.EntityLookingCondition
import archives.tater.lockedloaded.loot.function.RandomFireworks
import archives.tater.lockedloaded.registry.LockedLoadedEnchantmentEffects
import archives.tater.lockedloaded.registry.LockedLoadedEnchantments
import archives.tater.lockedloaded.util.*
import net.minecraft.advancements.criterion.CollectionPredicate
import net.minecraft.advancements.criterion.EntityFlagsPredicate.Builder.flags
import net.minecraft.advancements.criterion.MinMaxBounds
import net.minecraft.advancements.criterion.MinMaxBounds.Doubles.atMost
import net.minecraft.advancements.criterion.MinMaxBounds.FloatDegrees
import net.minecraft.advancements.criterion.MinMaxBounds.Ints
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.component.predicates.DataComponentPredicates
import net.minecraft.core.component.predicates.FireworksPredicate
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.EnchantmentTags
import net.minecraft.tags.ItemTags
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.FireworkExplosion.Shape
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantment.*
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents
import net.minecraft.world.item.enchantment.LevelBasedValue
import net.minecraft.world.item.enchantment.effects.AddValue
import net.minecraft.world.item.enchantment.effects.MultiplyValue
import net.minecraft.world.item.enchantment.effects.SetValue
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.LootItemFunction
import net.minecraft.world.level.storage.loot.functions.SequenceFunction
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition.allOf
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition
import java.util.*

object EnchantmentGenerator : RegistrySetBuilder.RegistryBootstrap<Enchantment> {
    override fun run(registry: BootstrapContext<Enchantment>) {
        val items = registry.lookup(Registries.ITEM)
        val entities = registry.lookup(Registries.ENTITY_TYPE)
        val enchantments = registry.lookup(Registries.ENCHANTMENT)
        val crossbowEnchantable = items.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE)
        val crossbowExclusive = enchantments.getOrThrow(EnchantmentTags.CROSSBOW_EXCLUSIVE)

        fun register(key: ResourceKey<Enchantment>, definition: EnchantmentDefinition, init: Enchantment.Builder.() -> Unit) =
            enchantment(definition).apply(init).build(key.identifier()).also {
                registry[key] = it
            }

        register(LockedLoadedEnchantments.MULTICHAMBERED, definition(
            crossbowEnchantable,
            4,
            3,
            dynamicCost(12, 20),
            constantCost(50),
            2,
            EquipmentSlotGroup.MAINHAND
        )) {
            exclusiveWith(enchantments.getOrThrow(LockedLoadedEnchantments.MULTICHAMBERED_EXCLUSIVE))
            withSpecialEffect(LockedLoadedEnchantmentEffects.LOAD_MULTIPLE, LoadMultiple(LevelBasedValue.perLevel(2f)))
            withSpecialEffect(LockedLoadedEnchantmentEffects.CHARGED_PROJECTILE_INDICATOR, ChargedProjectileIndicator(LevelBasedValue.perLevel(2f)))
            withEffect(LockedLoadedEnchantmentEffects.PROJECTILE_FIRED_COUNT, SetValue(LevelBasedValue.constant(1f)))
        }

        register(LockedLoadedEnchantments.PUMP_CHARGE, definition(
            crossbowEnchantable,
            1,
            1,
            constantCost(20),
            constantCost(50),
            8,
            EquipmentSlotGroup.MAINHAND
        )) {
            exclusiveWith(crossbowExclusive)
            withSpecialEffect(LockedLoadedEnchantmentEffects.LOAD_MULTIPLE, LoadMultiple(LevelBasedValue.constant(8f)))
            withSpecialEffect(LockedLoadedEnchantmentEffects.CHARGED_PROJECTILE_INDICATOR, ChargedProjectileIndicator(LevelBasedValue.constant(8f)))
            withEffect(LockedLoadedEnchantmentEffects.PROJECTILE_UNCERTAINTY, ProjectileUncertainty(projectileCount = AddValue(LevelBasedValue.perLevel(2f))))
            withEffect(LockedLoadedEnchantmentEffects.PROJECTILE_VELOCITY, MultiplyValue(LevelBasedValue.constant(0.5f)))
            withEffect(EnchantmentEffectComponents.PROJECTILE_COUNT, SetValue(LevelBasedValue.constant(2f)))
        }

        register(LockedLoadedEnchantments.MAGAZINE, definition(
            crossbowEnchantable,
            1,
            4,
            dynamicCost(20, 10),
            constantCost(50),
            8,
            EquipmentSlotGroup.MAINHAND
        )) {
            exclusiveWith(enchantments.getOrThrow(LockedLoadedEnchantments.MAGAZINE_EXCLUSIVE))
            withEffect(EnchantmentEffectComponents.PROJECTILE_COUNT, SetValue(LevelBasedValue.perLevel(4f)))
            withSpecialEffect(LockedLoadedEnchantmentEffects.CHARGED_PROJECTILE_INDICATOR, ChargedProjectileIndicator(LevelBasedValue.perLevel(4f)))
            withEffect(LockedLoadedEnchantmentEffects.PROJECTILE_FIRED_COUNT, SetValue(LevelBasedValue.constant(1f)))
            withEffect(LockedLoadedEnchantmentEffects.PROJECTILE_UNCERTAINTY, ProjectileUncertainty(projectileCount = AddValue(LevelBasedValue.perLevel(0.5f))))
            withEffect(LockedLoadedEnchantmentEffects.CROSSBOW_COOLDOWN, AddValue(LevelBasedValue.constant(0.5f)))
            withSpecialEffect(EnchantmentEffectComponents.CROSSBOW_CHARGE_TIME, AddValue(LevelBasedValue.perLevel(2f)))
        }

        register(LockedLoadedEnchantments.SHARPSHOOTING, definition(
            crossbowEnchantable,
            1,
            3,
            dynamicCost(12, 20),
            constantCost(50),
            8,
            EquipmentSlotGroup.MAINHAND
        )) {
            exclusiveWith(crossbowExclusive)
            withEffect(LockedLoadedEnchantmentEffects.PROJECTILE_RICOCHET, AddValue(LevelBasedValue.perLevel(1f)))
            withEffect(LockedLoadedEnchantmentEffects.PROJECTILE_IGNORE_OWNER, McUnit.INSTANCE)
            withEffect(EnchantmentEffectComponents.PROJECTILE_PIERCING, AddValue(LevelBasedValue.perLevel(1f)))
        }

        fun fireworksModifier(fireworks: RandomFireworks, duration: Ints): LootItemFunction = filteredFunction(
            ItemPredicate {
                withComponents {
                    partial(DataComponentPredicates.FIREWORKS, FireworksPredicate(
                        Optional.of(CollectionPredicate(
                            Optional.empty(),
                            Optional.empty(),
                            Optional.of(Ints.exactly(0))
                        )),
                        duration
                    ))
                }
            },
            onPass = fireworks
        )

        register(LockedLoadedEnchantments.ROCKETRY, definition(
            crossbowEnchantable,
            1,
            1,
            constantCost(30),
            constantCost(50),
            8,
            EquipmentSlotGroup.MAINHAND
        )) {
            exclusiveWith(crossbowExclusive)

            withSpecialEffect(LockedLoadedEnchantmentEffects.SUPPORTED_PROJECTILES, SupportedItems(
                ItemPredicate {
                    of(items, Items.FIREWORK_ROCKET)
                },
                replace = true
            ))

            withEffect(LockedLoadedEnchantmentEffects.MODIFY_PROJECTILE_ITEM, SequenceFunction.of(listOf(
                fireworksModifier(RandomFireworks(shapes = listOf(Shape.SMALL_BALL), explosions = ConstantInt(1)), Ints.atMost(1)),
                fireworksModifier(RandomFireworks(shapes = listOf(Shape.SMALL_BALL, Shape.STAR, Shape.CREEPER), explosions = ConstantInt(2)), Ints.exactly(2)),
                fireworksModifier(RandomFireworks(explosions = ConstantInt(3)), Ints.atLeast(3)),
            )))

            withSpecialEffect(LockedLoadedEnchantmentEffects.DEFAULT_PROJECTILE_ITEM, PreviewedLootTable(
                LootTable {
                    setParamSet(LootContextParamSets.ENCHANTED_ENTITY)
                    pool {
                        add(LootItem.lootTableItem(Items.FIREWORK_ROCKET))
                    }
                },
                ItemStackTemplate(Items.FIREWORK_ROCKET)
            ))

            withEffect(LockedLoadedEnchantmentEffects.FIREWORK_OWNER_KNOCKBACK, AddValue(LevelBasedValue.constant(4f)))

            withEffect(LockedLoadedEnchantmentEffects.PROJECTILE_MOUNT, McUnit.INSTANCE, allOf(
                LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, EntityPredicate {
                    of(entities, EntityType.FIREWORK_ROCKET)
                }),
                LootItemEntityPropertyCondition.hasProperties(EntityTarget.TARGET_ENTITY, EntityPredicate {
                    moving(MovementPredicate(
                        y = atMost(0.0),
                    ))
                    flags(flags().apply {
                        setOnGround(false)
                        setIsInWater(false)
                        setIsFallFlying(false)
                    })
                }),
                EntityLookingCondition(EntityTarget.TARGET_ENTITY, xRot = FloatDegrees(MinMaxBounds.Bounds.atMost(-45f)))
            ))
        }

        register(LockedLoadedEnchantments.RECOVERY, definition(
            crossbowEnchantable,
            4,
            1,
            constantCost(10),
            constantCost(50),
            1,
            EquipmentSlotGroup.MAINHAND
        )) {
            withEffect(LockedLoadedEnchantmentEffects.PROJECTILE_PERSIST, McUnit.INSTANCE)
            withEffect(LockedLoadedEnchantmentEffects.PROJECTILE_OWNER_PICKUP, McUnit.INSTANCE)
        }

        register(LockedLoadedEnchantments.TWIRLING_CURSE, definition(
            crossbowEnchantable,
            1,
            1,
            constantCost(25),
            constantCost(50),
            8,
            EquipmentSlotGroup.MAINHAND
        )) {
            withSpecialEffect(LockedLoadedEnchantmentEffects.CROSSBOW_SPIN, LevelBasedValue.constant(20f))
        }
    }
}