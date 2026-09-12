package top.linzhi.wacumber.event;

import java.util.ArrayList;
import java.util.List;

import top.linzhi.wacumber.Wacumber;
import top.linzhi.wacumber.item.ModItemTags;
import top.linzhi.wacumber.item.ModItems;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

/**
 * 黄瓜工具的独特能力（挂在游戏事件总线上）。
 *
 * <ol>
 *   <li><b>黄瓜二分剑</b>：命中不造成伤害，改为把目标当前血量减半（向下取整）；
 *       当前血量 ≤ 5 时直接斩杀；否则在目标坐标生成一只同血量分身。</li>
 *   <li><b>黄瓜镐 / 黄瓜战斧</b>：挖「它们挖得动的方块」时掉落翻倍，冷却 5 秒。</li>
 * </ol>
 */
@EventBusSubscriber(modid = Wacumber.MODID)
public final class ToolAbilityHandler {

    /** 斩杀阈值：当前血量 ≤ 5（2.5 颗心）直接死亡 */
    private static final float KILL_THRESHOLD = 5.0F;

    /** 双倍掉落冷却：5 秒 = 100 tick */
    private static final int DOUBLE_DROP_COOLDOWN_TICKS = 100;

    private ToolAbilityHandler() {
        // 纯事件处理器，禁止实例化
    }

    // ==================== 黄瓜二分剑 ====================

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();
        // 只在服务端处理；且必须是玩家近战直击（副手/弹射物不算）
        if (target.level().isClientSide()) {
            return;
        }
        if (!(event.getSource().getDirectEntity() instanceof Player player)) {
            return;
        }
        if (!player.getMainHandItem().is(ModItems.CUCUMBER_SWORD.get())) {
            return;
        }

        // 先取消本次普通伤害（二分剑不造成伤害），再执行二分效果
        event.setAmount(0.0F);
        applyHalving(target);
    }

    /** 二分逻辑：≤5 血斩杀；否则血量减半并生成同血量分身 */
    private static void applyHalving(LivingEntity target) {
        if (!target.isAlive()) {
            return;
        }
        float current = target.getHealth();

        // 小于等于五滴血：立即死亡，不生成分身
        if (current <= KILL_THRESHOLD) {
            target.kill();
            return;
        }

        float half = (float) Math.floor(current / 2.0F);
        target.setHealth(half);

        // 判断实体是否在服务器级别
        if (!(target.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        // 复制一只同类型、同血量的生物，位置/朝向与目标一致
        Entity clone = target.getType().create(serverLevel);
        if (clone == null) {
            return; // 玩家等无法由类型创建的实体没有分身
        }
        clone.moveTo(target.getX(), target.getY(), target.getZ(), target.getYRot(), target.getXRot());
        if (clone instanceof LivingEntity livingClone) {
            livingClone.setHealth(half);
        }
        serverLevel.addFreshEntity(clone);
    }

    // ==================== 黄瓜镐 / 黄瓜战斧：双倍掉落 ====================

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof Player player)) {
            return;
        }
        ItemStack tool = event.getTool();
        // 是否双倍掉落工具由数据包标签决定：data/wacumber/tags/item/cucumber_tools.json
        if (!tool.is(ModItemTags.CUCUMBER_TOOLS)) {
            return;
        }
        Item item = tool.getItem();
        // 只对「这件工具挖得动的方块」生效（与镐/斧的正确工具判定一致）
        if (!tool.isCorrectToolForDrops(event.getState())) {
            return;
        }
        List<ItemEntity> drops = event.getDrops();
        if (drops.isEmpty()) {
            return; // 本来就没掉落，不消耗冷却
        }
        // 5 秒冷却（走原版物品冷却，热键栏会显示冷却进度）
        ItemCooldowns cooldowns = player.getCooldowns();
        if (cooldowns.isOnCooldown(item)) {
            return;
        }
        cooldowns.addCooldown(item, DOUBLE_DROP_COOLDOWN_TICKS);

        // 掉落翻倍：复制一份掉落物（不改原堆叠数量，避免出现超堆叠）
        ServerLevel level = event.getLevel();
        List<ItemEntity> extraDrops = new ArrayList<>(drops.size());
        for (ItemEntity drop : drops) {
            extraDrops.add(new ItemEntity(level, drop.getX(), drop.getY(), drop.getZ(), drop.getItem().copy()));
        }
        drops.addAll(extraDrops);
    }
}
