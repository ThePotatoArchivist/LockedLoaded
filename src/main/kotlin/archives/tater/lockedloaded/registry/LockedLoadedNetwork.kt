package archives.tater.lockedloaded.registry

import archives.tater.lockedloaded.LockedLoaded
import archives.tater.lockedloaded.network.ServerboundRideFireworkPayload
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.world.entity.projectile.FireworkRocketEntity

internal fun initNetwork() {
    with (PayloadTypeRegistry.serverboundPlay()) {
        register(ServerboundRideFireworkPayload.TYPE, ServerboundRideFireworkPayload.CODEC)
    }

    ServerPlayNetworking.registerGlobalReceiver(ServerboundRideFireworkPayload.TYPE) { payload, context ->
        val firework = context.player().rootVehicle as? FireworkRocketEntity ?: run {
            LockedLoaded.logger.warn("{} was not riding a firework", context.player().plainTextName)
            return@registerGlobalReceiver
        }
        firework.deltaMovement = payload.movement
    }
}