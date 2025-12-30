package expresscarts.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import expresscarts.ExpressCartsConfig;
import expresscarts.ExpressMinecartEntity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(NewMinecartBehavior.class)
public abstract class NewMinecartBehaviorMixin extends MinecartBehavior {
    protected NewMinecartBehaviorMixin(AbstractMinecart minecart) {
        super(minecart);
    }

    @Inject(method = "getMaxSpeed", at = @At("HEAD"), cancellable = true)
    // If we're using this controller for an ExpressMinecartEntity, use the configured speed for that, including any multipliers
    private void onGetMaxSpeed(CallbackInfoReturnable<Double> cir) {
        if (this.minecart instanceof ExpressMinecartEntity) {
            var baseSpeed = ExpressCartsConfig.maxMinecartSpeed;
            var waterSpeedMultiplier = this.minecart.isInWater() ? ExpressCartsConfig.waterSpeedMultiplier : 1.0;
            var blockMultiplier = 1.0;

            if (!ExpressCartsConfig.blockSpeedMultipliers.isEmpty()) {
                if (this.minecart.isOnRails()) {
                    var railPos = this.minecart.getCurrentBlockPosOrRailBelow();
                    // check the block below the rail
                    var blockPos = railPos.below();
                    var blockState = this.level().getBlockState(blockPos);

                    blockMultiplier = ExpressCartsConfig.blockSpeedMultipliers.getOrDefault(blockState.getBlock(), 1.0);
                }
            }

            cir.setReturnValue((baseSpeed * blockMultiplier * waterSpeedMultiplier) / 20.0);
        }
    }

    @WrapOperation(method = "calculateHaltTrackSpeed(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/phys/Vec3;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;"))
    // Modify the scale applied to the cart's velocity when passing over an unpowered powered rail
    private Vec3 unpoweredPoweredRailVelocityScale(Vec3 instance, double scale, Operation<Vec3> original) {
        if (this.minecart instanceof ExpressMinecartEntity && ExpressCartsConfig.fastUnpoweredSlowdown) {
            return original.call(instance, 0.4); // seems reasonable in testing. stops similar or faster than vanilla carts (0.5) even at high max speeds (>128) with a long runway
        } else {
            return original.call(instance, scale);
        }
    }

}
