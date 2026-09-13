package top.linzhi.wacumber.item.tool;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

/**
 * 黄瓜二分剑。
 *
 * <p>本身不造成额外攻击伤害，命中效果由
 * {@code top.linzhi.wacumber.event.ToolAbilityHandler} 监听 {@code LivingIncomingDamageEvent} 实现：
 * 把受到的伤害清零，改为将目标当前血量减半（向下取整）；
 * 目标当前血量 ≤ 5 时直接斩杀；否则在目标位置生成一只同血量分身。
 *
 * <p><b>禁用横扫</b>：一次挥击的「主目标伤害 + 横扫伤害」发生在同一 tick，而分身生成位置
 * 与原目标重合，会被本次横扫再打一次 → 再触发二分 → 一次攻击生成两个分身。
 * 因此这里关掉 {@link ItemAbilities#SWORD_SWEEP}，让一次挥击只作用于主目标。
 *
 * <p>物品属性（攻击力 / 攻速）见 {@code item.properties.ModItemProperties#cucumberSword()}。
 */
public class CucumberSwordItem extends SwordItem {

    public CucumberSwordItem(Tier tier, Item.Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        // 二分剑不做横扫：避免横扫伤害打到刚生成的分身造成连环分身
        if (ability == ItemAbilities.SWORD_SWEEP) {
            return false;
        }
        return super.canPerformAction(stack, ability);
    }
}
