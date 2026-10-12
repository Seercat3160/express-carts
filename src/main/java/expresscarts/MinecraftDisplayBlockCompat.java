package expresscarts;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import expresscarts.mixin.AbstractMinecartAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

/**
 * Modifies entity data sent to the client such that the server-side default display block state and offset are
 * preserved for vanilla clients.
 */
public interface MinecraftDisplayBlockCompat extends PolymerEntity {
    int getDefaultDisplayOffset();
    BlockState getDefaultDisplayBlockState();

    @Override
    default void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
        data.removeIf(element ->
                element.id() == AbstractMinecartAccessor.getCustomBlockOffset().id() || element.id() == AbstractMinecartAccessor.getCustomDisplayBlock().id()
        );

        // The client sees this as a vanilla minecart, so we have to explicitly send our default values.
        data.add(SynchedEntityData.DataValue.create(AbstractMinecartAccessor.getCustomDisplayBlock(), Optional.of(this.getDefaultDisplayBlockState())));
        data.add(SynchedEntityData.DataValue.create(AbstractMinecartAccessor.getCustomBlockOffset(), this.getDefaultDisplayOffset()));
    }
}
