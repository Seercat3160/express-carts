package expresscarts;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import expresscarts.mixin.AbstractMinecartAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import java.util.Optional;

public class ExpressMinecartEntity extends Minecart implements PolymerEntity {
    private static final int CARPET_OFFSET_PIXELS = 2;
    private ChunkPos ticketChunkPos;
    private Long ticketTimer = 0L;

    public ExpressMinecartEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.MINECART;
    }

    @Override
    public void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
        data.removeIf((x) -> x.serializer() == AbstractMinecartAccessor.getCustomDisplayBlock().serializer()
                || x.serializer() == AbstractMinecartAccessor.getCustomBlockOffset().serializer());

        // we force sending custom block data of our own rather than any the actual entity serverside might have,
        // so that our custom carts are always distinguishable to vanilla clients.
        data.add(SynchedEntityData.DataValue.create(AbstractMinecartAccessor.getCustomBlockOffset(), this.getDefaultDisplayOffset()));
        data.add(SynchedEntityData.DataValue.create(AbstractMinecartAccessor.getCustomDisplayBlock(), Optional.of(this.getDefaultDisplayBlockState())));
    }

    @Override
    public @NotNull BlockState getDefaultDisplayBlockState() {
        return Blocks.RED_CARPET.defaultBlockState();
    }

    @Override
    public int getDefaultDisplayOffset() {
        // shift carpet block down 2 pixels relative to default offset to sit flush on the minecart floor
        return super.getDefaultDisplayOffset() - CARPET_OFFSET_PIXELS;
    }

    @Override
    protected @NotNull Item getDropItem() {
        return ModItems.EXPRESS_MINECART;
    }


    @Override
    public @NotNull ItemStack getPickResult() {
        return new ItemStack(ModItems.EXPRESS_MINECART);
    }

    @Override
    // apply the brakes when the controlling player holds back while in the cart
    protected @NotNull Vec3 applyNaturalSlowdown(Vec3 velocity) {
        Vec3 vel = super.applyNaturalSlowdown(velocity);

        if (ExpressCartsConfig.brakingEnabled && this.getFirstPassenger() instanceof ServerPlayer player && player.getLastClientInput().backward()) {
            // stop completely if going slowly. otherwise, slow down (but not as quickly as an unpowered powered rail)
            return vel.length() < 0.03 ? Vec3.ZERO : vel.scale(ExpressCartsConfig.brakeSlowdown);
        }

        return vel;
    }

    @Override
    public void tick() {
        super.tick();
        if (ExpressCartsConfig.loadChunks) {
            if (this.level() instanceof ServerLevel serverLevel) {
                ChunkPos chunkPos = this.chunkPosition();
                if (--this.ticketTimer <= 0L || chunkPos != this.ticketChunkPos) {
                    this.ticketChunkPos = chunkPos;
                    this.ticketTimer = placeTicket(serverLevel, chunkPos);
                }
            }
        }
    }

    private long placeTicket(ServerLevel serverLevel, ChunkPos chunkPos) {
        serverLevel.getChunkSource().addTicketWithRadius(TicketType.ENDER_PEARL, chunkPos, 2);
        return TicketType.ENDER_PEARL.timeout();
    }

    @Override
    protected void propagateFallToPassengers(double d, float f, DamageSource damageSource) {
        // we scale any existing f we get, rather than completely overriding, to preserve (relative) behaviour of any blocks which already change fall damage
        var newDamageScale = f * ExpressCartsConfig.fallDamageMultiplier;
        for (Entity entity : this.getPassengers()) {
            entity.causeFallDamage(d, newDamageScale, damageSource);
        }
    }
}
