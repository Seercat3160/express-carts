package expresscarts.entities;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import expresscarts.ChunkLoader;
import expresscarts.ExpressCartMarker;
import expresscarts.ExpressCartsConfig;
import expresscarts.MinecraftDisplayBlockCompat;
import expresscarts.items.ModItems;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.MinecartChest;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

//? >= 26.2
import net.minecraft.world.entity.EntityTypes;
//? if < 26.1 {
/*import xyz.nucleoid.packettweaker.PacketContext;
*///?} else
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

public class ExpressMinecartChest extends MinecartChest implements PolymerEntity, MinecraftDisplayBlockCompat, ExpressCartMarker {
    private final ChunkLoader chunkLoader;

    public ExpressMinecartChest(EntityType<? extends MinecartChest> type, Level level) {
        super(type, level);
        this.chunkLoader = new ChunkLoader(this);
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        //? if >=26.2 {
        return EntityTypes.CHEST_MINECART;
        //?} else
        //return EntityType.CHEST_MINECART;
    }

    @Override
    protected @NotNull Item getDropItem() {
        return ModItems.EXPRESS_CHEST_MINECART;
    }

    @Override
    public @NotNull ItemStack getPickResult() {
        return new ItemStack(ModItems.EXPRESS_CHEST_MINECART);
    }

    @Override
    public @NonNull BlockState getDefaultDisplayBlockState() {
        // Used so clients can see that this is different to a regular chest minecart.
        return Blocks.TRAPPED_CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH);
    }

    @Override
    public void tick() {
        super.tick();
        this.chunkLoader.tick();
    }
}
