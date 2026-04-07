package archives.tater.lockedloaded.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import com.google.common.collect.BiMap;

@Mixin(LootContextParamSets.class)
public interface LootContextParamSetsAccessor {
    @Accessor
    static BiMap<Identifier, ContextKeySet> getREGISTRY() {
        throw new UnsupportedOperationException();
    }
}
