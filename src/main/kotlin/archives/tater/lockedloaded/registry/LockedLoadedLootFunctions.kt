package archives.tater.lockedloaded.registry

import archives.tater.lockedloaded.LockedLoaded
import archives.tater.lockedloaded.loot.function.RandomFireworks
import com.mojang.serialization.MapCodec
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.storage.loot.functions.LootItemFunction

private fun register(path: String, codec: MapCodec<out LootItemFunction>) {
    Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, LockedLoaded.id(path), codec)
}

internal fun initLootFunctions() {
    register("random_fireworks", RandomFireworks.CODEC)
}
