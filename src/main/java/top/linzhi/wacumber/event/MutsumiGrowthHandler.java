package top.linzhi.wacumber.event;

import top.linzhi.wacumber.Wacumber;
import top.linzhi.wacumber.block.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;

/**
 * 睦子米方块促进作物生长。
 *
 * <p>作物正常生长后，若其周围 <b>5×5×5</b> 范围内存在普通睦子米方块
 * （{@link ModBlocks#MUTSUMI_BLOCK}），则以 20% 概率额外长一级 → 约 +20% 生长速度。
 *
 * <p>哭泣的睦子米方块（{@code MUTSUMI_CRY_BLOCK}）不提供该加成。
 * 与「静默生长」效果独立叠加。
 */
@EventBusSubscriber(modid = Wacumber.MODID)
public final class MutsumiGrowthHandler {

    /** 水平半径 ±2 → 5×5 */
    private static final int HORIZONTAL_RADIUS = 2;
    /** 垂直半径 ±2 → 5 层 */
    private static final int VERTICAL_RADIUS = 2;
    /** 额外生长概率：+20% 速度 */
    private static final float EXTRA_GROW_CHANCE = 0.2F;

    private MutsumiGrowthHandler() {
        // 纯事件处理器，禁止实例化
    }

    @SubscribeEvent
    public static void onCropGrow(CropGrowEvent.Post event) {
        LevelAccessor accessor = event.getLevel();
        if (!(accessor instanceof ServerLevel level)) {
            return;
        }
        BlockState state = event.getState(); // 本次生长「之后」的状态
        if (!(state.getBlock() instanceof CropBlock crop)) {
            return;
        }
        int age = crop.getAge(state);
        if (age >= crop.getMaxAge()) {
            return; // 已成熟
        }
        BlockPos pos = event.getPos();
        if (!hasMutsumiNearby(level, pos)) {
            return;
        }
        if (level.random.nextFloat() >= EXTRA_GROW_CHANCE) {
            return;
        }
        // 直接提升 age：setBlock 不会再触发 CropGrowEvent，因此不会递归
        level.setBlock(pos, crop.getStateForAge(age + 1), Block.UPDATE_ALL);
    }

    /** 作物周围 5×5×5 内是否有普通睦子米方块 */
    private static boolean hasMutsumiNearby(ServerLevel level, BlockPos center) {
        Block mutsumi = ModBlocks.MUTSUMI_BLOCK.get();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -HORIZONTAL_RADIUS; dx <= HORIZONTAL_RADIUS; dx++) {
            for (int dy = -VERTICAL_RADIUS; dy <= VERTICAL_RADIUS; dy++) {
                for (int dz = -HORIZONTAL_RADIUS; dz <= HORIZONTAL_RADIUS; dz++) {
                    cursor.setWithOffset(center, dx, dy, dz);
                    if (level.getBlockState(cursor).is(mutsumi)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
