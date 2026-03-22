package archives.tater.lockedloaded.enchantment

import archives.tater.lockedloaded.util.pick
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.component.DataComponents
import net.minecraft.util.RandomSource
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.util.valueproviders.IntProvider
import net.minecraft.util.valueproviders.IntProviders
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.FireworkExplosion
import net.minecraft.world.item.component.Fireworks
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import it.unimi.dsi.fastutil.ints.IntArrayList
import java.util.*
import java.util.stream.IntStream
import kotlin.jvm.optionals.getOrNull

class RandomFireworks(
    predicates: List<LootItemCondition>,
    val shapes: List<FireworkExplosion.Shape> = ALL_SHAPES,
    val duration: Optional<IntProvider> = Optional.empty(),
    val explosions: IntProvider = ONE,
    val colors: IntProvider = ONE,
    val fadeColors: IntProvider = ONE,
    val trailChance: Float = 0f,
    val twinkleChance: Float = 0f,
) : LootItemConditionalFunction(predicates) {
    private fun generateExplosion(random: RandomSource) = FireworkExplosion(
        pick(shapes, random),
        generateColors(colors.sample(random), random),
        generateColors(fadeColors.sample(random), random),
        random.nextFloat() <= trailChance,
        random.nextFloat() <= twinkleChance
    )

    private fun generateFirework(current: Fireworks?, random: RandomSource) = Fireworks(
        duration.getOrNull()?.sample(random) ?: current?.flightDuration() ?: 1,
        List(explosions.sample(random)) { generateExplosion(random) }
    )

    override fun run(
        itemStack: ItemStack,
        context: LootContext
    ): ItemStack = itemStack.also {
        it[DataComponents.FIREWORKS] = generateFirework(it[DataComponents.FIREWORKS], context.random)
    }

    override fun codec(): MapCodec<out LootItemConditionalFunction> = CODEC

    companion object {
        val ALL_SHAPES = FireworkExplosion.Shape.entries.toList()
        val ALL_COLORS = DyeColor.entries.map { it.fireworkColor }
        val ONE: ConstantInt = ConstantInt.of(1)

        val CODEC: MapCodec<RandomFireworks> = RecordCodecBuilder.mapCodec { commonFields(it).and(it.group(
            FireworkExplosion.Shape.CODEC.listOf().optionalFieldOf("shapes", ALL_SHAPES).forGetter(RandomFireworks::shapes),
            IntProviders.POSITIVE_CODEC.optionalFieldOf("duration").forGetter(RandomFireworks::duration),
            IntProviders.POSITIVE_CODEC.optionalFieldOf("explosions", ONE).forGetter(RandomFireworks::explosions),
            IntProviders.POSITIVE_CODEC.optionalFieldOf("colors", ONE).forGetter(RandomFireworks::colors),
            IntProviders.POSITIVE_CODEC.optionalFieldOf("fade_colors", ONE).forGetter(RandomFireworks::fadeColors),
            Codec.floatRange(0f, 1f).optionalFieldOf("trail_chance", 0f).forGetter(RandomFireworks::trailChance),
            Codec.floatRange(0f, 1f).optionalFieldOf("twinkle_chance", 0f).forGetter(RandomFireworks::twinkleChance),
        )).apply(it, ::RandomFireworks) }

        private fun generateColors(count: Int, random: RandomSource) = IntArrayList.toListWithExpectedSize(
            IntStream.range(0, count).map { pick(ALL_COLORS, random) }, count
        )
    }
}