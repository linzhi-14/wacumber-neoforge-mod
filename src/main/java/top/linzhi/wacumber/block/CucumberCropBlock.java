package top.linzhi.wacumber.block;

import top.linzhi.wacumber.item.ModItems;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * 黄瓜作物方块（仿原版甜菜 BeetrootBlock 的四阶段写法）。
 *
 * <p>生长机制继承自 {@link CropBlock}：种在农田上、随机刻生长、骨粉催熟。
 * 黄瓜只有 4 个阶段 age 0~3，对应贴图 stage1~stage4，age=3 为成熟。
 * 成熟挖掘的掉落（3 黄瓜 + 1~3 种子）由 loot table 驱动，不在代码里写死。
 */
public class CucumberCropBlock extends CropBlock {
    /** 最大成熟阶段：0~3 共 4 个状态 */
    public static final int MAX_AGE = 3;
    /** 年龄属性：直接复用原版 0~3 的属性（甜菜同款） */
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

    public CucumberCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    /** 让父类逻辑使用我们 0~3 的属性，而不是默认 0~7 */
    @Override
    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    /** 黄瓜最大年龄 = 3 */
    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    /** 把 AGE 属性注册进方块状态定义（不覆写会与父类 0~7 的 AGE 冲突） */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    /** 创造模式用「选取方块」时给到黄瓜种子（而非默认的小麦种子） */
    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.CUCUMBER_SEED.get();
    }
}
