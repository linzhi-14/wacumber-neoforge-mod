package top.linzhi.wacumber.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 哭泣的睦子米方块（{@link MutsumiBlock} 腐化后的形态）。
 *
 * <p>不再促进作物生长；但玩家可以拿**空玻璃瓶**右键它：
 * 方块恢复为 {@link MutsumiBlock}，手上的空瓶变成一瓶**原版幸运药水**
 * （{@code Potions.LUCK}）。创造模式不消耗空瓶。
 *
 * <p>与 {@link MutsumiBlock} 一样继承 {@link SlimeBlock}，拥有史莱姆方块的全部物理行为。
 */
public class MutsumiCryingBlock extends SlimeBlock {

    public MutsumiCryingBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!stack.is(Items.GLASS_BOTTLE)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide()) {
            // 客户端只预告成功，真正逻辑在服务端执行
            return ItemInteractionResult.SUCCESS;
        }

        // 方块恢复成普通睦子米方块
        level.setBlockAndUpdate(pos, ModBlocks.MUTSUMI_BLOCK.get().defaultBlockState());

        ItemStack luckPotion = PotionContents.createItemStack(Items.POTION, Potions.LUCK);
        if (player.getAbilities().instabuild) {
            // 创造模式：直接把手上的空瓶替换成幸运药水
            player.setItemInHand(hand, luckPotion);
        } else {
            stack.shrink(1);
            if (stack.isEmpty()) {
                player.setItemInHand(hand, luckPotion);
            } else if (!player.getInventory().add(luckPotion)) {
                player.drop(luckPotion, false);
            }
        }

        level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        return ItemInteractionResult.SUCCESS;
    }
}
