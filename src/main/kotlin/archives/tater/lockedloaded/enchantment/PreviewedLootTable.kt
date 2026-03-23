package archives.tater.lockedloaded.enchantment

import archives.tater.lockedloaded.LockedLoaded
import archives.tater.lockedloaded.util.getOneStack
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.context.ContextKeySet
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.Validatable

@JvmRecord
data class PreviewedLootTable(
    val table: LootTable,
    val preview: ItemStackTemplate
) {
    fun getStack(entity: Entity, enchantmentLevel: Int): ItemStack = when (val level = entity.level()) {
        is ServerLevel -> getOneStack(table, Enchantment.entityContext(level, enchantmentLevel, entity, entity.position())) {
            LockedLoaded.logger.warn("Default projectile loot table returned more than one stack!")
        }
        else -> preview.create()
    }

    companion object {
        fun codec(context: ContextKeySet): Codec<PreviewedLootTable> = RecordCodecBuilder.create { it.group(
            LootTable.DIRECT_CODEC.validate(Validatable.validatorForContext(context)).fieldOf("table").forGetter(PreviewedLootTable::table),
            ItemStackTemplate.CODEC.fieldOf("preview").forGetter(PreviewedLootTable::preview)
        ).apply(it, ::PreviewedLootTable) }
    }
}