package expresscarts.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import expresscarts.ExpressMinecartEntity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.MinecartItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(MinecartItem.class)
public abstract class MinecartItemMixin {
    @ModifyExpressionValue(method = "useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;useExperimentalMovement(Lnet/minecraft/world/level/Level;)Z"))
    private boolean useExperimentalBehaviorForExpressMinecart(boolean original, @Local AbstractMinecart abstractMinecart) {
        return original || abstractMinecart instanceof ExpressMinecartEntity;
    }

}
