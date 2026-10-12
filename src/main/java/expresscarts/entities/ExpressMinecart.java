package expresscarts.entities;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import expresscarts.ChunkLoader;
import expresscarts.ExpressCartMarker;
import expresscarts.ExpressCartsConfig;
import expresscarts.MinecraftDisplayBlockCompat;
import expresscarts.items.ModItems;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
//? >= 26.2
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
//? if < 26.1 {
/*import xyz.nucleoid.packettweaker.PacketContext;
*///?} else
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

public class ExpressMinecart extends Minecart implements PolymerEntity, MinecraftDisplayBlockCompat, ExpressCartMarker {
    private final ChunkLoader chunkLoader;

    public ExpressMinecart(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.chunkLoader = new ChunkLoader(this);
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        //? if >=26.2 {
        return EntityTypes.MINECART;
        //?} else
        //return EntityType.MINECART;
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
    public @NonNull BlockState getDefaultDisplayBlockState() {
        // Used so clients can see that this is different to a vanilla minecart.
        //? if >= 26.2 {
        return Blocks.CARPET.red().defaultBlockState();
        //?} else
        //return Blocks.RED_CARPET.defaultBlockState();
    }

    @Override
    public int getDefaultDisplayOffset() {
        // To sit flat on the floor of the minecart.
        return 4;
    }

    @Override
    // apply the brakes when the controlling player holds back while in the cart
    protected @NonNull Vec3 applyNaturalSlowdown(@NonNull Vec3 velocity) {
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
        this.chunkLoader.tick();
    }

    @Override
    protected void propagateFallToPassengers(double d, float f, @NonNull DamageSource damageSource) {
        // we scale any existing f we get, rather than completely overriding, to preserve (relative) behaviour of any blocks which already change fall damage
        var newDamageScale = f * ExpressCartsConfig.fallDamageMultiplier;
        for (Entity entity : this.getPassengers()) {
            entity.causeFallDamage(d, newDamageScale, damageSource);
        }
    }
}
