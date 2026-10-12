package expresscarts;

import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;

/**
 * Marker interface to identify our minecarts, since we can't otherwise have them all inherit from a common parent.
 */
public interface ExpressCartMarker {
    static boolean isExpressCart(AbstractMinecart cart) {
        return cart instanceof ExpressCartMarker;
    }
}
