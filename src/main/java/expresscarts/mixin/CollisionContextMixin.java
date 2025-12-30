package expresscarts.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import expresscarts.ExpressMinecartEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(CollisionContext.class)
public interface CollisionContextMixin {
    @ModifyExpressionValue(method = "of(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/shapes/CollisionContext;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;useExperimentalMovement(Lnet/minecraft/world/level/Level;)Z"))
    private static boolean handleExpressMinecartEntity(boolean original, Entity entity) {
        return original || entity instanceof ExpressMinecartEntity;
    }
}
