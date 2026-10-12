package expresscarts.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import expresscarts.ExpressCartMarker;
import expresscarts.entities.ExpressMinecart;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {
    @Definition(id = "AbstractMinecart", type = AbstractMinecart.class)
    @Expression("? instanceof AbstractMinecart")
    @WrapOperation(method = "sendChanges()V", at = @At("MIXINEXTRAS:EXPRESSION"))
    // Don't send special minecart packets regarding our minecarts, instead treating them as a regular entity.
    // This is required because on the client, the minecarts use the old behaviour so it doesn't know what to do with
    // the special minecart movement packets.
    private boolean fakeDefaultController(Object obj, Operation<Boolean> original) {
        boolean isExpressCart = obj instanceof AbstractMinecart cart && ExpressCartMarker.isExpressCart(cart);
        return !isExpressCart && original.call(obj);
    }
}
