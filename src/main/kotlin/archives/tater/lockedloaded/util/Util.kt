@file:JvmName("LockedLoadedUtil")

package archives.tater.lockedloaded.util

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.advancements.criterion.MinMaxBounds.FloatDegrees
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponentType
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.util.context.ContextKeySet
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.Validatable
import net.minecraft.world.phys.Vec3
import java.util.function.Function


inline fun <T: Any, U: Any> getFirstEnchantmentComponent(stack: ItemStack, type: DataComponentType<T>, combine: (T, Int) -> U): U? {
    if (stack.isEmpty) return null
    for ((enchantment, level) in stack.enchantments.entrySet())
        return combine(enchantment.value().effects()[type] ?: continue, level)
    return null
}

/**
 * @see net.minecraft.world.item.enchantment.EnchantmentEffectComponents.validatedListCodec
 */
fun <T : Validatable> validatedListCodec(elementCodec: Codec<T>, paramSet: ContextKeySet): Codec<List<T>> =
    elementCodec.listOf().validate(Validatable.listValidatorForContext<T>(paramSet))

fun <T: Validatable> holderValidatorForContext(paramSet: ContextKeySet): Function<Holder<T>, DataResult<Holder<T>>> {
    val validator = Validatable.validatorForContext<T>(paramSet)
    return { holder ->
        validator.apply(holder.value()).map { holder }
    }
}

fun <T, C : Collection<T>> Codec<List<T>>.collection(constructor: (List<T>) -> C): Codec<C> = xmap(
    constructor,
    Collection<T>::toList
)

fun getDirection(entity: Entity, position: Vec3): Direction {
    val center = entity.centerPos
    val normalizedOffset = (position - center).multiply(1.0 / entity.bbWidth, 1.0 / entity.bbHeight, 1.0 / entity.bbWidth)
    return Direction.getApproximateNearest(normalizedOffset)
}

val Entity.centerPos: Vec3 get() = position().add(0.0, bbHeight / 2.0, 0.0)

fun <T> pick(items: List<T>, random: RandomSource): T =
    items[random.nextInt(items.size)]

@JvmOverloads
fun getOneStack(table: LootTable, context: LootContext, onProblem: Runnable? = null): ItemStack {
    val stacks = mutableListOf<ItemStack>()
    table.getRandomItems(context, stacks::add)
    return when (stacks.size) {
        0 -> ItemStack.EMPTY
        1 -> stacks.first()
        else -> {
            onProblem?.run()
            stacks.first()
        }
    }
}

/**
 * @see net.minecraft.commands.arguments.selector.EntitySelectorParser.createRotationPredicate
 */
fun FloatDegrees.matches(degrees: Float): Boolean {
    val min = Mth.wrapDegrees(min().orElse(0f))
    val max = Mth.wrapDegrees(max().orElse(359f))
    val rotation: Float = Mth.wrapDegrees(degrees)
    return if (min > max) rotation >= min || rotation <= max else rotation in min..max
}