package expresscarts;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

public class ChunkLoader {
    private ChunkPos ticketChunkPos;
    private long ticketTimer = 0L;

    private final Entity entity;

    public ChunkLoader(Entity entity) {
        this.entity = entity;
    }

    private long placeTicket(ServerLevel serverLevel, ChunkPos chunkPos) {
        serverLevel.getChunkSource().addTicketWithRadius(TicketType.ENDER_PEARL, chunkPos, 2);
        return TicketType.ENDER_PEARL.timeout();
    }

    public void tick() {
        if (ExpressCartsConfig.loadChunks) {
            if (this.entity.level() instanceof ServerLevel serverLevel) {
                ChunkPos chunkPos = this.entity.chunkPosition();
                if (--this.ticketTimer <= 0L || chunkPos != this.ticketChunkPos) {
                    this.ticketChunkPos = chunkPos;
                    this.ticketTimer = placeTicket(serverLevel, chunkPos);
                }
            }
        }
    }
}
