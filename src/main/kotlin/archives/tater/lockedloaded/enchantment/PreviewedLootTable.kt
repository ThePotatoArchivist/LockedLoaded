package archives.tater.lockedloaded.enchantment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.context.ContextKeySet
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.Validatable

@JvmRecord
data class PreviewedLootTable(
    val table: LootTable,
    val preview: ItemStackTemplate
) {
    companion object {
        fun codec(context: ContextKeySet): Codec<PreviewedLootTable> = RecordCodecBuilder.create { it.group(
            LootTable.DIRECT_CODEC.validate(Validatable.validatorForContext(context)).fieldOf("table").forGetter(PreviewedLootTable::table),
            ItemStackTemplate.CODEC.fieldOf("preview").forGetter(PreviewedLootTable::preview)
        ).apply(it, ::PreviewedLootTable) }
    }
}