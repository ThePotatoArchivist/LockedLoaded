package archives.tater.lockedloaded.mixin.rocketride;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.component.FireworkExplosion;

import java.util.List;

@Mixin(FireworkRocketEntity.class)
public interface FireworkRocketEntityAccessor {
    @Invoker
    List<FireworkExplosion> invokeGetExplosions();
}
