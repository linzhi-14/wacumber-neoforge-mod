package top.linzhi.wacumber.entity.projectile;

import top.linzhi.wacumber.entity.MutsumiPuppetEntity;
import top.linzhi.wacumber.item.ModItems;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

/**
 * 黄瓜球（投掷物实体）。
 *
 * <p>继承原版 {@link Snowball}，飞行/粒子行为与雪球一致，但**命中伤害由 {@link #setDamage(float)} 决定**：
 * <ul>
 *   <li>玩家右键投掷：伤害 0 —— 击中任何生物都不造成伤害（也覆盖掉原版雪球对烈焰人的 3 点伤害）；</li>
 *   <li>睦偶发射：伤害 5 —— 由 {@code MutsumiPuppetEntity} 在生成弹射物时设置。</li>
 * </ul>
 */
public class CucumberBallEntity extends Snowball {

    /** 命中伤害，0 = 无伤害 */
    private float damage;

    public CucumberBallEntity(EntityType<? extends CucumberBallEntity> type, Level level) {
        super(type, level);
    }

    public CucumberBallEntity(Level level, LivingEntity shooter) {
        super(level, shooter);
    }

    public CucumberBallEntity(Level level, double x, double y, double z) {
        super(level, x, y, z);
    }

    /** 设置命中伤害（睦偶发射时传 5） */
    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return this.damage;
    }

    /** 渲染与默认物品：黄瓜球 */
    @Override
    protected Item getDefaultItem() {
        return ModItems.CUCUMBER_BALL.get();
    }

    /**
     * 命中生物时的处理：
     * <ul>
     *   <li>伤害为 0（玩家投掷）→ 什么都不做；</li>
     *   <li><b>目标是玩家或睦偶 → 直接跳过</b>：既不造成伤害，也正因为没有调用 {@code hurt}，
     *       不会写入 {@code lastHurtByMob}，因此不会产生任何仇恨（睦偶之间不会互相激怒）；</li>
     *   <li>其它生物 → 造成 {@link #damage} 点伤害（睦偶发射时为 5）。</li>
     * </ul>
     * 刻意不调用 {@code super.onHitEntity}，以免原版雪球对烈焰人的额外伤害生效。
     */
    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        // 对玩家与睦偶（含主人、其它睦偶）完全无伤害、无仇恨
        if (target instanceof Player || target instanceof MutsumiPuppetEntity) {
            return;
        }
        if (this.damage > 0.0F && target instanceof LivingEntity living) {
            living.hurt(this.damageSources().thrown(this, this.getOwner()), this.damage);
        }
    }
}
