package top.linzhi.wacumber.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 睦子米方块。
 *
 * <p>两个作用：
 * <ol>
 *   <li>促进周围 5×5×5 范围内的作物生长（+20% 速度），由
 *       {@code top.linzhi.wacumber.event.MutsumiGrowthHandler} 监听 {@code CropGrowEvent.Post} 实现；</li>
 *   <li>放置后经过约 10 个游戏日会「腐化」成 {@link MutsumiCryingBlock}。</li>
 * </ol>
 *
 * <p>腐化用随机刻概率近似：方块平均每约 1365 tick 被随机刻选中一次
 * （每区块每 tick 3 次随机刻 / 4096 个位置），10 游戏日 = 240000 tick
 * → 期望选中约 176 次，故取 1/175 概率。注意这是近似实现：
 * 仅当所在区块处于加载状态时才计时，且实际腐化时间存在随机方差。
 *
 * <p>继承 {@link SlimeBlock}：拥有史莱姆方块的全部物理行为（落在上面被弹起、
 * 站在上面加速/减速、不遮挡视线等），只是额外加了上面的两个功能。
 */
public class MutsumiBlock extends SlimeBlock {
    /** 每次随机刻的腐化概率（期望 ≈ 10 游戏日） */
    private static final float DECAY_CHANCE = 1.0F / 175.0F;

    public MutsumiBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() < DECAY_CHANCE) {
            level.setBlockAndUpdate(pos, ModBlocks.MUTSUMI_CRY_BLOCK.get().defaultBlockState());
        }
    }
}
