package archives.tater.lockedloaded.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.objectweb.asm.Opcodes;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.level.Level;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends Entity {
    @Shadow
    private int life;

    public FireworkRocketEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @WrapWithCondition(
            method = "tick",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/projectile/FireworkRocketEntity;life:I", opcode = Opcodes.PUTFIELD)
    )
    private boolean increaseLifeMounted(FireworkRocketEntity instance, int value) {
        return life <= 0 || getPassengers().isEmpty() || tickCount % 2 == 0;
    }
}
