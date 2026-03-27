package expresscarts;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
//? if < 26.1 {
/*import xyz.nucleoid.packettweaker.PacketContext;
*///?} else
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

public class ExpressMinecartItem extends MinecartItem implements PolymerItem {
    private final Item polymerItem;

    public ExpressMinecartItem(EntityType<? extends AbstractMinecart> type, Item.Properties properties, Item polymerItem) {
        super(type, properties);
        this.polymerItem = polymerItem;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return this.polymerItem;
    }


    //? if < 26.1 {
    /*@Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, PacketContext context) {
        ItemStack out = PolymerItemUtils.createItemStack(itemStack, tooltipType, context);
    *///? } else {
    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, PacketContext context, HolderLookup.Provider lookup) {
        ItemStack out = PolymerItemUtils.createItemStack(itemStack, tooltipType, context, lookup);
    //? }
        out.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return out;
    }

    //? if < 26.1 {
    /*@Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
    *///? } else {
    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
    //? }
        if (PolymerResourcePackUtils.hasMainPack(context)) {
            return stack.get(DataComponents.ITEM_MODEL);
        } else {
            return this.getPolymerItem(stack, context).getDefaultInstance().get(DataComponents.ITEM_MODEL);
        }
    }

}