package expresscarts;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Function;

public class ModItems {
    public static final Item EXPRESS_MINECART = register("minecart",
            settings -> new ExpressMinecartItem(ExpressCarts.EXPRESS_MINECART_ENTITY, settings, Items.MINECART),
            new Item.Properties().stacksTo(1));

    public static Item register(String name, Function<Item.Properties, Item> itemFactory, Item.Properties properties) {
        // Create the item key.
        ResourceKey<Item> itemKey = ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath(ExpressCarts.MOD_ID, name));

        // Create the item instance.
        Item item = itemFactory.apply(properties.setId(itemKey));

        // Register the item.
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize() {
    }
}
