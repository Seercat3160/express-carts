package expresscarts.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(AbstractMinecart.class)
public interface AbstractMinecartAccessor {
    @Accessor("DATA_ID_CUSTOM_DISPLAY_BLOCK")
    static EntityDataAccessor<Optional<BlockState>> getCustomDisplayBlock() {
        throw new AssertionError();
    }

    @Accessor("DATA_ID_DISPLAY_OFFSET")
    static EntityDataAccessor<Integer> getCustomBlockOffset() {
        throw new AssertionError();
    }
}
