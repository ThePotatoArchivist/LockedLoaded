@file:JvmName("LockedLoadedUtil")

package archives.tater.lockedloaded.util

import com.mojang.serialization.Codec
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponentType
import net.minecraft.util.RandomSource
import net.minecraft.util.context.ContextKeySet
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.Validatable
import net.minecraft.world.phys.Vec3

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

fun <T, C : Collection<T>> Codec<List<T>>.collection(constructor: (List<T>) -> C): Codec<C> = xmap(
    constructor,
    Collection<T>::toList
)

fun getDirection(entity: Entity, position: Vec3): Direction {
    val center = entity.position().add(0.0, entity.bbHeight / 2.0, 0.0)
    val normalizedOffset = (position - center).multiply(1.0 / entity.bbWidth, 1.0 / entity.bbHeight, 1.0 / entity.bbWidth)
    return Direction.getApproximateNearest(normalizedOffset)
}

fun <T> pick(items: List<T>, random: RandomSource): T =
    items[random.nextInt(items.size)]
