package archives.tater.lockedloaded.loot.condition

import archives.tater.lockedloaded.util.matches
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancements.criterion.MinMaxBounds.FloatDegrees
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition

@JvmRecord
data class EntityLookingCondition(
    val target: EntityTarget,
    val xRot: FloatDegrees = FloatDegrees.ANY,
    val yRot: FloatDegrees = FloatDegrees.ANY,
) : LootItemCondition, LootItemCondition.Builder {

    override fun test(context: LootContext): Boolean = target.get(context)?.let {
        xRot.matches(it.xRot) && yRot.matches(it.yRot)
    } == true

    override fun codec(): MapCodec<out LootItemCondition> = CODEC

    override fun build(): LootItemCondition = this

    companion object {
        val CODEC: MapCodec<EntityLookingCondition> = RecordCodecBuilder.mapCodec { it.group(
            EntityTarget.CODEC.fieldOf("target").forGetter(EntityLookingCondition::target),
            FloatDegrees.CODEC.optionalFieldOf("x_rot", FloatDegrees.ANY).forGetter(EntityLookingCondition::xRot),
            FloatDegrees.CODEC.optionalFieldOf("y_rot", FloatDegrees.ANY).forGetter(EntityLookingCondition::yRot),
        ).apply(it, ::EntityLookingCondition) }
    }
}