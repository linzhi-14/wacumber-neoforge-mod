package top.linzhi.wacumber.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * 墨偶（Mortis Puppet）—— 由黄瓜二分剑砍中睦偶时诞生。
 *
 * <p>完全继承 {@link MutsumiPuppetEntity} 的模型、动画、坐姿与行为（跟随主人 / 看玩家 /
 * 护卫反击 / Shift 右键坐下 / 发射黄瓜球），只有两点不同：
 * <ol>
 *   <li>黄瓜球伤害提升为 {@value #MORTIS_BALL_DAMAGE}（睦偶是 5）；</li>
 *   <li>黄瓜球命中后点燃目标 {@value #MORTIS_BALL_FIRE_SECONDS} 秒。</li>
 * </ol>
 *
 * <p>生成与「不可二分」规则见 {@code top.linzhi.wacumber.event.ToolAbilityHandler}：
 * 睦偶被二分剑砍中第一次会生成墨偶（且该次不掉血），再砍只受普通伤害；
 * 墨偶无论被砍多少次都只受普通伤害。
 */
public class MortisPuppetEntity extends MutsumiPuppetEntity {

    /** 墨偶黄瓜球伤害 */
    private static final float MORTIS_BALL_DAMAGE = 10.0F;
    /** 墨偶黄瓜球点燃秒数 */
    private static final float MORTIS_BALL_FIRE_SECONDS = 5.0F;

    public MortisPuppetEntity(EntityType<? extends MortisPuppetEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected float getBallDamage() {
        return MORTIS_BALL_DAMAGE;
    }

    @Override
    protected float getBallFireSeconds() {
        return MORTIS_BALL_FIRE_SECONDS;
    }
}
