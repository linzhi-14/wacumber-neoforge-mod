package top.linzhi.wacumber.item;

import top.linzhi.wacumber.entity.projectile.CucumberBallEntity;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.level.Level;

/**
 * 黄瓜球物品：像原版雪球一样右键投掷，但**击中不造成伤害**（伤害为 0）。
 *
 * <p>投掷出的实体是 {@link CucumberBallEntity}；玩家投掷走伤害 0，
 * 睦偶通过自己的 AI 生成同样的实体并设置伤害 5。
 */
public class CucumberBallItem extends SnowballItem {

    public CucumberBallItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW,
                SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide()) {
            CucumberBallEntity ball = new CucumberBallEntity(level, player);
            ball.setItem(stack);
            ball.setDamage(0.0F); // 玩家投掷：不造成伤害
            ball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(ball);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        stack.consume(1, player);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    /** 供发射器 / 其它投掷逻辑使用 */
    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        CucumberBallEntity ball = new CucumberBallEntity(level, pos.x(), pos.y(), pos.z());
        ball.setItem(stack);
        ball.setDamage(0.0F);
        return ball;
    }
}
