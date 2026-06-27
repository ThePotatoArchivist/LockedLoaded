package archives.tater.lockedloaded

import archives.tater.lockedloaded.registry.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object LockedLoaded : ModInitializer {
	const val MOD_ID = "lockedloaded"

	fun id(path: String): Identifier = Identifier.fromNamespaceAndPath(MOD_ID, path)

	@JvmField
    val logger: Logger = LogManager.getLogger(MOD_ID)

	val NO_ROCKET_RIDING = id("no_rocket_riding")

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LockedLoadedEnchantmentEffects.init()
		LockedLoadedEnchantments.init()
		LockedLoadedAttachments.init()
		LockedLoadedComponents.init()
		LockedLoadedLootContext.init()
		initLootFunctions()
		LockedLoadedLoot.init()
		initNetwork()
		LockedLoadedSounds.init()
		ResourceLoader.registerBuiltinPack(
			NO_ROCKET_RIDING,
			FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(),
			Component.literal("No Rocket Riding"),
			PackActivationType.NORMAL
		)
	}
}