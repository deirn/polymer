package eu.pb4.polymer.block;

import eu.pb4.polymer.other.DoubleBooleanEvent;
import eu.pb4.polymer.other.MineEvent;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkSectionPos;

public class BlockHelper {
    public static final int NESTED_DEFAULT_DISTANCE = 32;

    /**
     * This event allows you to force server side mining for any block/item
     */
    public static final MineEvent SERVER_SIDE_MINING_CHECK = new MineEvent();
    /**
     * This event allows you to force syncing of light updates between server and clinet
     */
    public static final DoubleBooleanEvent<ServerWorld, ChunkSectionPos> SEND_LIGHT_UPDATE_PACKET = new DoubleBooleanEvent<>();
    private static final Object2BooleanMap<BlockState> IS_LIGHT_SOURCE_CACHE = new Object2BooleanOpenHashMap<>();

    /**
     * This method is used internally to check if BlockState is VirtualBlock with light source not equal to it's visual block
     *
     * @param blockState
     * @return
     */
    public static boolean isVirtualLightSource(BlockState blockState) {
        if (blockState.getBlock() instanceof VirtualBlock virtualBlock) {
            if (BlockHelper.IS_LIGHT_SOURCE_CACHE.containsKey(blockState)) {
                return BlockHelper.IS_LIGHT_SOURCE_CACHE.getBoolean(blockState);
            } else {
                if (blockState.getLuminance() != virtualBlock.getVirtualBlockState(blockState).getLuminance()) {
                    BlockHelper.IS_LIGHT_SOURCE_CACHE.put(blockState, true);
                    return true;
                }

                BlockHelper.IS_LIGHT_SOURCE_CACHE.put(blockState, false);
                return false;
            }
        }
        return false;
    }

    /**
     * This method is minimal wrapper around {@link VirtualBlock#getVirtualBlockState(BlockState)} to make sure
     * It gets replaced if it represents other VirtualBlock
     *
     * @param block VirtualBlock
     * @param blockState Server side BlockState
     * @param maxDistance Maximum number of checks for nested virtual blocks
     * @return Client side BlockState
     */
    public static BlockState getBlockStateSafely(VirtualBlock block, BlockState blockState, int maxDistance) {
        BlockState out = block.getVirtualBlockState(blockState);

        int req = 0;
        while (out.getBlock() instanceof VirtualBlock newBlock && newBlock != block && req < maxDistance) {
            out = newBlock.getVirtualBlockState(out);
            req++;
        }
        return out;
    }

    /**
     * This method is minimal wrapper around {@link VirtualBlock#getVirtualBlockState(BlockState)} to make sure
     * It gets replaced if it represents other VirtualBlock
     *
     * @param block VirtualBlock
     * @param blockState Server side BlockState
     * @return Client side BlockState
     */
    public static BlockState getBlockStateSafely(VirtualBlock block, BlockState blockState) {
        return getBlockStateSafely(block, blockState, NESTED_DEFAULT_DISTANCE);
    }
}
