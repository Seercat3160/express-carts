package expresscarts;

import dev.xpple.betterconfig.api.ModConfigBuilder;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
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
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.DispenserBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class ExpressCarts implements ModInitializer {
    public static final String MOD_ID = "expresscarts";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final EntityType<ExpressMinecartEntity> EXPRESS_MINECART_ENTITY = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(ExpressCarts.MOD_ID, "minecart"),
            EntityType.Builder.of(ExpressMinecartEntity::new, MobCategory.MISC).noLootTable()
                    .sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8)
                    .build(ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(),
                            ResourceLocation.fromNamespaceAndPath(ExpressCarts.MOD_ID, "minecart"))));

    @Override
    public void onInitialize() {
        ModMetadata metadata = FabricLoader.getInstance().getModContainer(ExpressCarts.MOD_ID).orElseThrow().getMetadata();

        new ModConfigBuilder<CommandSourceStack, CommandBuildContext>(ExpressCarts.MOD_ID, ExpressCartsConfig.class).build();

        PolymerEntityUtils.registerType(EXPRESS_MINECART_ENTITY);
        PolymerResourcePackUtils.addModAssets(ExpressCarts.MOD_ID);

        ModItems.initialize();

        DispenserBlock.registerBehavior(ModItems.EXPRESS_MINECART, new MinecartDispenseItemBehavior(EXPRESS_MINECART_ENTITY));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("expresscarts").executes(context -> {
                context.getSource().sendSuccess(() -> Component.translatable(
                        "expresscarts.command.expresscarts",
                        Component.literal(metadata.getName()).withStyle(Style.EMPTY.withUnderlined(true).withColor(ChatFormatting.BLUE).withHoverEvent(new HoverEvent.ShowText(Component.translatable("expresscarts.command.expresscarts.hover.github"))).withClickEvent(new ClickEvent.OpenUrl(URI.create(metadata.getContact().get("sources").orElse("https://example.com"))))),
                        metadata.getVersion().getFriendlyString()), false);
                context.getSource().sendSuccess(() -> Component.translatable("expresscarts.command.expresscarts.config.maxMinecartSpeed", ExpressCartsConfig.maxMinecartSpeed), false);
                context.getSource().sendSuccess(() -> Component.translatable("expresscarts.command.expresscarts.config.waterSpeedMultiplier", ExpressCartsConfig.waterSpeedMultiplier), false);
                context.getSource().sendSuccess(() -> Component.translatable("expresscarts.command.expresscarts.config.brakingEnabled", String.valueOf(ExpressCartsConfig.brakingEnabled)), false);
                context.getSource().sendSuccess(() -> Component.translatable("expresscarts.command.expresscarts.config.brakeSlowdown", ExpressCartsConfig.brakeSlowdown), false);
                return 1;
            }));
        });

        LOGGER.info("Express Carts ready!");
    }

}
