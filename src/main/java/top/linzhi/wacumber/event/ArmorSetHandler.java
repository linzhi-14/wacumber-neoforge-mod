package top.linzhi.wacumber.event;

import top.linzhi.wacumber.Wacumber;
import top.linzhi.wacumber.item.ModItems;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 黄瓜盔甲全套效果。
 *
 * <ol>
 *   <li><b>血量翻倍</b>：穿齐头盔 / 胸甲 / 护腿 / 靴子时最大生命 ×2
 *       （{@code ADD_MULTIPLIED_TOTAL +100%}），脱下立即移除修饰符并把当前血量夹回上限。</li>
 *   <li><b>受击反击</b>：穿齐全套被攻击时，攻击者获得 1 秒缓慢 III（amplifier 2）。</li>
 * </ol>
 */
@EventBusSubscriber(modid = Wacumber.MODID)
public final class ArmorSetHandler {

    /** 血量修饰符固定 id：便于判断是否已附加、以及移除 */
    private static final ResourceLocation MAX_HEALTH_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(Wacumber.MODID, "cucumber_full_set_max_health");

    /** 缓慢持续时间：1 秒 */
    private static final int SLOW_DURATION_TICKS = 20;
    /** 缓慢 III = amplifier 2 */
    private static final int SLOW_AMPLIFIER = 29;

    private ArmorSetHandler() {
        // 纯事件处理器，禁止实例化
    }

    // ==================== 全套：最大生命 ×2 ====================

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) {
            return;
        }
        boolean fullSet = isWearingFullSet(player);
        boolean boosted = maxHealth.hasModifier(MAX_HEALTH_MODIFIER_ID);
        if (fullSet == boosted) {
            return; // 状态未变化，无需每 tick 操作
        }
        if (fullSet) {
            // 穿齐后上限翻倍
            maxHealth.addOrUpdateTransientModifier(new AttributeModifier(
                    MAX_HEALTH_MODIFIER_ID, 1.0D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        } else {
            maxHealth.removeModifier(MAX_HEALTH_MODIFIER_ID);
            // 脱掉后上限回落，把超出部分夹掉
            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }
    }

    // ==================== 全套：受击给攻击者缓慢 III 十 (嘻嘻嘻嘻嘻嘻嘻) ====================

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }
        if (!isWearingFullSet(player)) {
            return;
        }
        Entity attackerEntity = event.getSource().getEntity();
        if (!(attackerEntity instanceof LivingEntity attacker) || attacker == player) {
            return;
        }
        // 生物（Mob）与其他玩家都算
        attacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                SLOW_DURATION_TICKS, SLOW_AMPLIFIER), player);
    }

    /** 是否穿齐四件黄瓜护甲 */
    private static boolean isWearingFullSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.CUCUMBER_HELMET.get())
                && player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.CUCUMBER_CHESTPLATE.get())
                && player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.CUCUMBER_LEGGINGS.get())
                && player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.CUCUMBER_BOOTS.get());
    }
}
