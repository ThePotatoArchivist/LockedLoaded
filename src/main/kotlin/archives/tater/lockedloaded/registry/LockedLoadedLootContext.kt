package archives.tater.lockedloaded.registry

import archives.tater.lockedloaded.LockedLoaded
import archives.tater.lockedloaded.mixin.LootContextParamSetsAccessor
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.context.ContextKeySet
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import java.util.*

object LockedLoadedLootContext {

    private fun registerKeySet(path: String, init: ContextKeySet.Builder.() -> Unit) = ContextKeySet.Builder()
        .apply(init)
        .build()
        .also {
            LootContextParamSetsAccessor.getREGISTRY()[LockedLoaded.id(path)] = it
        }

    @JvmField
    val ENCHANTED_PROJECTILE = registerKeySet("enchanted_projectile") {
        required(LootContextParams.THIS_ENTITY)
        required(LootContextParams.ENCHANTMENT_LEVEL)
        required(LootContextParams.ORIGIN)
        optional(LootContextParams.TARGET_ENTITY)
    }

    @JvmStatic
    fun projectileContext(level: ServerLevel, enchantmentLevel: Int, projectile: Projectile) = LootParams.Builder(level).apply {
        withParameter(LootContextParams.THIS_ENTITY, projectile)
        withParameter(LootContextParams.ENCHANTMENT_LEVEL, enchantmentLevel)
        withParameter(LootContextParams.ORIGIN, projectile.position())
        withOptionalParameter(LootContextParams.TARGET_ENTITY, projectile.owner)
    }.create(ENCHANTED_PROJECTILE).let {
        LootContext.Builder(it).create(Optional.empty())
    }

    fun init() {

    }
}