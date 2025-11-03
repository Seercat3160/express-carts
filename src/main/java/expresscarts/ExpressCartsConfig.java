package expresscarts;

import dev.xpple.betterconfig.api.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

public class ExpressCartsConfig {
    @Config(comment = "maxMinecartSpeedComment")
    public static double maxMinecartSpeed = 16;
    public static Component maxMinecartSpeedComment() {
        return Component.translatable("expresscarts.config.comment.maxMinecartSpeed");
    }

    @Config(comment = "waterSpeedMultiplierComment")
    public static double waterSpeedMultiplier = 0.5;
    public static Component waterSpeedMultiplierComment() {
        return Component.translatable("expresscarts.config.comment.waterSpeedMultiplier");
    }

    @Config(comment = "brakingEnabledComment")
    public static boolean brakingEnabled = true;
    public static Component brakingEnabledComment() {
        return Component.translatable("expresscarts.config.comment.brakingEnabled");
    }

    @Config(comment = "brakeSlowdownComment")
    public static double brakeSlowdown = 0.8;
    public static Component brakeSlowdownComment() {
        return Component.translatable("expresscarts.config.comment.brakeSlowdown");
    }

    @Config(comment = "blockSpeedMultipliersComment")
    public static Map<Block, Double> blockSpeedMultipliers = new HashMap<>();
    public static Component blockSpeedMultipliersComment() {
        return Component.translatable("expresscarts.config.comment.blockSpeedMultipliers");
    }

    @Config(comment = "fastUnpoweredSlowdownComment")
    public static boolean fastUnpoweredSlowdown = false;
    public static Component fastUnpoweredSlowdownComment() {
        return Component.translatable("expresscarts.config.comment.fastUnpoweredSlowdown");
    }

    @Config(comment = "loadChunksComment")
    public static boolean loadChunks = false;
    public static Component loadChunksComment() {
        return Component.translatable("expresscarts.config.comment.loadChunks");
    }

    @Config(comment = "fallDamageMultiplierComment")
    public static float fallDamageMultiplier = 1f;
    public static Component fallDamageMultiplierComment() {
        return Component.translatable("expresscarts.config.comment.fallDamageMultiplier");
    }
}
