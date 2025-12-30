package expresscarts.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import expresscarts.ExpressMinecartEntity;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {
    @Definition(id = "AbstractMinecart", type = AbstractMinecart.class)
    @Expression("? instanceof AbstractMinecart")
    @WrapOperation(method = "sendChanges()V", at = @At("MIXINEXTRAS:EXPRESSION"))
    // Don't send special minecart packets regarding ExpressMinecartEntity, instead treating it as a regular entity
    private boolean fakeDefaultController(Object obj, Operation<Boolean> original) {
        return !(obj instanceof ExpressMinecartEntity) && original.call(obj);
    }
}
