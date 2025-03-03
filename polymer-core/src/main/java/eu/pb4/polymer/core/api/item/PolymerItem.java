package eu.pb4.polymer.core.api.item;

import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import eu.pb4.polymer.core.impl.PolymerImplUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Interface used for creation of server-side items
 */
public interface PolymerItem extends PolymerSyncedObject<Item> {
    /**
     * Returns main/default item used on client for specific player
     *
     * @param itemStack ItemStack of virtual item
     * @param player    Player for which it's send
     * @return Vanilla (or other) Item instance
     */
    Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player);

    /**
     * Method used for creation of client-side ItemStack
     *
     * @param itemStack Server-side ItemStack
     * @param player    Player for which it's send
     * @return Client-side ItemStack
     */
    default ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType tooltipType, RegistryWrapper.WrapperLookup lookup, @Nullable ServerPlayerEntity player) {
        if (PolymerImplUtils.POLYMER_ITEM_CLASS_CACHE.getBoolean(this.getClass())) {
            RegistryWrapper.WrapperLookup old = null;
            if (player == null) {
                old = PolymerImplUtils.WRAPPER_LOOKUP_PASSER.get();
                PolymerImplUtils.WRAPPER_LOOKUP_PASSER.set(lookup);
            }
            var x = this.getPolymerItemStack(itemStack, tooltipType, player);
            if (player == null) {
                PolymerImplUtils.WRAPPER_LOOKUP_PASSER.set(old);
            }
            return x;
        }
        return PolymerItemUtils.createItemStack(itemStack, tooltipType, lookup, player);
    }


    /**
     * Method used for getting custom model data of items
     *
     * @param itemStack Server-side ItemStack
     * @param player    Player for which it's send
     * @return Custom model data or -1 if not present
     */
    default int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return -1;
    }

    /**
     * Method used for getting custom armor color of items
     * It's designed to be used alongside {@link eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils#requestArmor(Identifier)}
     * Make sure colors isn't even so it won't get wrong texture
     *
     * @param itemStack Server-side ItemStack
     * @param player    Player for which it's send
     * @return Custom color or -1 if not present
     */
    default int getPolymerArmorColor(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return -1;
    }

    /**
     * This method allows to modify tooltip text
     * If you just want to add your own one, use {@link Item#appendTooltip(ItemStack, Item.TooltipContext, List, TooltipType)}
     *
     * @param tooltip Current tooltip text
     * @param stack   Server-side ItemStack
     * @param player  Target player
     */
    default void modifyClientTooltip(List<Text> tooltip, ItemStack stack, @Nullable ServerPlayerEntity player) {
    }
    @Override
    default Item getPolymerReplacement(ServerPlayerEntity player) {
        return this.getPolymerItem(((Item) this).getDefaultStack(), player);
    }

    default boolean handleMiningOnServer(ItemStack tool, BlockState targetBlock, BlockPos pos, ServerPlayerEntity player) {
        return false;
    }

    /**
     * @deprecated Replaced with PolymerItem#getPolymerItemStack(ItemStack, TooltipType, ServerPlayerEntity)
     */
    @Deprecated(forRemoval = true)
    default ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType tooltipType, @Nullable ServerPlayerEntity player) {
        return PolymerItemUtils.createItemStack(itemStack, tooltipType, player);
    }

    default boolean shouldStorePolymerItemStackCount() {
        return false;
    }
}