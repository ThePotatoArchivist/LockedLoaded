@file:JvmName("OmniUtil")

package archives.tater.lockedloaded.util

import com.mojang.serialization.Codec
import net.minecraft.core.component.DataComponentType
import net.minecraft.util.context.ContextKeySet
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.Validatable

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