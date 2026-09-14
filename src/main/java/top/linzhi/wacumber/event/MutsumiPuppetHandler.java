package top.linzhi.wacumber.event;

import top.linzhi.wacumber.Wacumber;
import top.linzhi.wacumber.block.ModBlocks;
import top.linzhi.wacumber.entity.ModEntities;
import top.linzhi.wacumber.entity.MutsumiPuppetEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * 睦偶的召唤与护卫行为。
 *
 * <ol>
 *   <li><b>召唤仪式</b>：竖直摆放「睦子米方块 ×2（下）+ 雕刻南瓜（上）」，
 *       放上南瓜的瞬间三格被消耗，并在底部生成一只睦偶，认放置者为主人。</li>
 *   <li><b>护卫反击</b>：主人被其他生物/玩家攻击时，主人附近（16 格内）属于自己的睦偶
 *       立即锁定攻击者，随后由 {@code RangedAttackGoal} 发射黄瓜球（伤害 5）。</li>
 * </ol>
 */
@EventBusSubscriber(modid = Wacumber.MODID)
public final class MutsumiPuppetHandler {

    /** 护卫响应半径（格） */
    private static final double GUARD_RADIUS = 16.0D;

    private MutsumiPuppetHandler() {
        // 纯事件处理器，禁止实例化
    }

    // ==================== 召唤仪式 ====================

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        LevelAccessor accessor = event.getLevel();
        if (!(accessor instanceof ServerLevel level)) {
            return;
        }
        // 只认「雕刻南瓜」被放下
        if (!event.getPlacedBlock().is(Blocks.CARVED_PUMPKIN)) {
            return;
        }
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        BlockPos pumpkinPos = event.getPos();
        BlockPos middlePos = pumpkinPos.below();
        BlockPos bottomPos = middlePos.below();
        // 下方两格都必须是睦子米方块
        if (!level.getBlockState(middlePos).is(ModBlocks.MUTSUMI_BLOCK.get())) {
            return;
        }
        if (!level.getBlockState(bottomPos).is(ModBlocks.MUTSUMI_BLOCK.get())) {
            return;
        }

        // 消耗三格
        level.removeBlock(pumpkinPos, false);
        level.removeBlock(middlePos, false);
        level.removeBlock(bottomPos, false);

        // 在底部位置生成睦偶并认主
        MutsumiPuppetEntity puppet = ModEntities.MUTSUMI_PUPPET.get().create(level);
        if (puppet == null) {
            return;
        }
        puppet.moveTo(bottomPos.getX() + 0.5D, bottomPos.getY(), bottomPos.getZ() + 0.5D,
                player.getYRot(), 0.0F);
        puppet.tameBy(player);
        puppet.setPersistenceRequired(); // 不随距离自然清除
        level.addFreshEntity(puppet);

        level.playSound(null, bottomPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 1.0F, 1.2F);
    }

    // ==================== 护卫反击 ====================

    @SubscribeEvent
    public static void onOwnerHurt(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player owner) || owner.level().isClientSide()) {
            return;
        }
        Entity attackerEntity = event.getSource().getEntity();
        if (!(attackerEntity instanceof LivingEntity attacker) || attacker == owner) {
            return;
        }
        if (!(owner.level() instanceof ServerLevel level)) {
            return;
        }
        // 让主人附近、归属于该玩家的睦偶锁定攻击者
        for (MutsumiPuppetEntity puppet : level.getEntitiesOfClass(MutsumiPuppetEntity.class,
                owner.getBoundingBox().inflate(GUARD_RADIUS))) {
            if (owner.getUUID().equals(puppet.getOwnerUUID())) {
                puppet.setTarget(attacker);
            }
        }
    }
}
