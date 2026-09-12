package top.linzhi.wacumber.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * 静默生长（Silent Growth）。
 *
 * <p>效果本体只是一个标记：具体作用（作物 1.5 倍速生长、钓鱼宝藏概率提升）
 * 由 {@code top.linzhi.wacumber.event.SilentGrowthHandler} 监听游戏事件实现，
 * 因此这里无需覆写 tick / 属性相关方法。
 */
public class SilentGrowthEffect extends MobEffect {

    public SilentGrowthEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}
