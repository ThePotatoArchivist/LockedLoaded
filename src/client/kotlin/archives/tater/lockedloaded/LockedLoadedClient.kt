package archives.tater.lockedloaded

import archives.tater.lockedloaded.client.render.ChargedProjectileIndicatorRenderer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey
import net.minecraft.world.InteractionHand

object LockedLoadedClient : ClientModInitializer {
	@JvmField val MAINHAND_SPIN_PROGRESS = RenderStateDataKey.create<Float> { "lockedloaded:mainhand_spin_progress" }
	@JvmField val OFFHAND_SPIN_PROGRESS = RenderStateDataKey.create<Float> { "lockedloaded:offhand_spin_progress" }

	@JvmStatic
	fun spinProgress(hand: InteractionHand) = when (hand) {
		InteractionHand.MAIN_HAND -> MAINHAND_SPIN_PROGRESS
		InteractionHand.OFF_HAND -> OFFHAND_SPIN_PROGRESS
	}

	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ChargedProjectileIndicatorRenderer.register()
	}
}