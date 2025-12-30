package expresscarts;

import dev.xpple.betterconfig.api.ModConfigBuilder;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import expresscarts.config.BlockArgumentType;
import expresscarts.config.BlockTypeAdapter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.MinecartDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class ExpressCarts implements ModInitializer {
    public static final String MOD_ID = "expresscarts";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final EntityType<ExpressMinecartEntity> EXPRESS_MINECART_ENTITY = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(ExpressCarts.MOD_ID, "minecart"),
            EntityType.Builder.of(ExpressMinecartEntity::new, MobCategory.MISC).noLootTable()
                    .sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8)
                    .build(ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(),
                            Identifier.fromNamespaceAndPath(ExpressCarts.MOD_ID, "minecart"))));

    @Override
    public void onInitialize() {
        ModMetadata metadata = FabricLoader.getInstance().getModContainer(ExpressCarts.MOD_ID).orElseThrow().getMetadata();

        new ModConfigBuilder<CommandSourceStack, CommandBuildContext>(ExpressCarts.MOD_ID, ExpressCartsConfig.class)
                .registerTypeHierarchy(Block.class, new BlockTypeAdapter(), context -> new BlockArgumentType())
                .build();

        PolymerEntityUtils.registerType(EXPRESS_MINECART_ENTITY);
        PolymerResourcePackUtils.addModAssets(ExpressCarts.MOD_ID);

        ModItems.initialize();

        DispenserBlock.registerBehavior(ModItems.EXPRESS_MINECART, new MinecartDispenseItemBehavior(EXPRESS_MINECART_ENTITY));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("expresscarts").executes(context -> {
                context.getSource().sendSuccess(() -> buildConfigurationMessage(metadata), false);
                return 1;
            }));
        });

        LOGGER.info("Express Carts ready!");
    }

    // TODO: improve this
    Component buildConfigurationMessage(ModMetadata metadata) {
        // Ideally we'd have hover text on each config entry with the comment to explain what it does, but there's no public api for that in BetterConfig
        return Component.empty()
                .append(Component.translatable(
                        "expresscarts.command.expresscarts",
                        Component.literal(metadata.getName())
                                .withStyle(Style.EMPTY
                                        .withUnderlined(true)
                                        .withColor(ChatFormatting.BLUE)
                                        .withHoverEvent(new HoverEvent.ShowText(Component.translatable("expresscarts.command.expresscarts.hover.github")))
                                        .withClickEvent(new ClickEvent.OpenUrl(URI.create(metadata.getContact().get("sources").orElse("https://example.com"))))
                                ),
                        metadata.getVersion().getFriendlyString())
                )
                .append("\nCurrent Configuration:\n")
                .append(Component.translatable("expresscarts.command.expresscarts.config.maxMinecartSpeed", ExpressCartsConfig.maxMinecartSpeed))
                .append("\n")
                .append(Component.translatable("expresscarts.command.expresscarts.config.waterSpeedMultiplier", ExpressCartsConfig.waterSpeedMultiplier))
                .append("\n")
                .append(Component.translatable("expresscarts.command.expresscarts.config.brakingEnabled", String.valueOf(ExpressCartsConfig.brakingEnabled)))
                .append("\n")
                .append(Component.translatable("expresscarts.command.expresscarts.config.brakeSlowdown", ExpressCartsConfig.brakeSlowdown))
                .append("\n")
                .append(Component.translatable("expresscarts.command.expresscarts.config.fastUnpoweredSlowdown", String.valueOf(ExpressCartsConfig.fastUnpoweredSlowdown)))
                .append("\n")
                .append(Component.translatable("expresscarts.command.expresscarts.config.fallDamageMultiplier", ExpressCartsConfig.fallDamageMultiplier))
                .append("\n")
                .append(Component.translatable("expresscarts.command.expresscarts.config.loadChunks", String.valueOf(ExpressCartsConfig.loadChunks)))
                .append("\n")
                .append(buildBlockOverridesMessage());
    }

    Component buildBlockOverridesMessage() {
        var overrides = ExpressCartsConfig.blockSpeedMultipliers;

        if (overrides.isEmpty()) {
            return Component.translatable("expresscarts.command.expresscarts.config.blockSpeedMultipliers.none");
        }

        MutableComponent messageComponent;
        if (overrides.size() == 1) {
            messageComponent = Component.translatable("expresscarts.command.expresscarts.config.blockSpeedMultipliers.one");
        } else {
            messageComponent = Component.translatable("expresscarts.command.expresscarts.config.blockSpeedMultipliers.some", overrides.size()).copy();
        }

        for (var entry : overrides.entrySet()) {
            messageComponent.append("\n");
            messageComponent.append(Component.translatable("expresscarts.command.expresscarts.config.blockSpeedMultipliers.entry", BuiltInRegistries.BLOCK.getKey(entry.getKey()).toString(), entry.getValue()));
        }

        return messageComponent;
    }

}
