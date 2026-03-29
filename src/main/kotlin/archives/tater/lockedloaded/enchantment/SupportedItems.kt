package archives.tater.lockedloaded.enchantment

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancements.criterion.ItemPredicate
import net.minecraft.core.component.DataComponentType
import net.minecraft.util.Util
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import java.util.function.Predicate

@JvmRecord
data class SupportedItems(
    val predicate: ItemPredicate,
    val replace: Boolean = false
) {
    companion object {
        val CODEC: Codec<SupportedItems> = RecordCodecBuilder.create { it.group(
            ItemPredicate.CODEC.fieldOf("predicate").forGetter(SupportedItems::predicate),
            Codec.BOOL.optionalFieldOf("replace", false).forGetter(SupportedItems::replace),
        ).apply(it, ::SupportedItems) }

        val SHORT_CODEC: Codec<SupportedItems> = Codec.either(CODEC, ItemPredicate.CODEC).xmap(
            { either -> either.map({ it }, ::SupportedItems) },
            { if (it.replace) Either.left(it) else Either.right(it.predicate) }
        )

        @JvmStatic
        fun getSupportedItems(weapon: ItemStack, type: DataComponentType<SupportedItems>, original: Predicate<ItemStack>): Predicate<ItemStack> = Util.allOf(buildList {
            add(original)
            EnchantmentHelper.runIterationOnItem(weapon) { enchantment, _ ->
                val effect = enchantment.value().effects()[type] ?: return@runIterationOnItem
                if (effect.replace)
                    remove(original)
                add(effect.predicate)
            }
        })
    }
}