package archives.tater.lockedloaded.network

import archives.tater.lockedloaded.LockedLoaded
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.phys.Vec3

data class ServerboundRideFireworkPayload(val movement: Vec3) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    companion object {
        val TYPE = CustomPacketPayload.Type<ServerboundRideFireworkPayload>(LockedLoaded.id("ride_firework"))
        val CODEC = Vec3.STREAM_CODEC.map(::ServerboundRideFireworkPayload, ServerboundRideFireworkPayload::movement)
    }
}