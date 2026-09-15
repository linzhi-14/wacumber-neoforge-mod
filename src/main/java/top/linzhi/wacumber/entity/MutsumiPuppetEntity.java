package top.linzhi.wacumber.entity;

import org.jetbrains.annotations.NotNull;
import top.linzhi.wacumber.entity.projectile.CucumberBallEntity;
import top.linzhi.wacumber.item.ModItems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 睦偶（Mutsumi Puppet）—— 可驯服的宠物型实体。
 *
 * <p>行为：
 * <ul>
 *   <li>跟随主人（{@link FollowOwnerGoal}）；</li>
 *   <li>时不时转头看向附近的玩家（{@link LookAtPlayerGoal}）；</li>
 *   <li><b>主人被攻击时锁定攻击者</b>（{@link OwnerHurtByTargetGoal}），
 *       并每隔 {@value #ATTACK_INTERVAL_TICKS} tick 发射一发黄瓜球；</li>
 *   <li>自身被攻击也会反击（{@link HurtByTargetGoal}）；不参与繁殖。</li>
 * </ul>
 *
 * <p>召唤方式见 {@code top.linzhi.wacumber.event.PuppetSummonHandler}
 * （下方 2 格睦子米方块 + 上方雕刻南瓜）。
 */
public class MutsumiPuppetEntity extends TamableAnimal implements RangedAttackMob {

    /** 攻击间隔：20 tick = 1 秒一发 */
    private static final int ATTACK_INTERVAL_TICKS = 20;
    /** 攻击射程（格） */
    private static final float ATTACK_RANGE = 16.0F;
    /** 睦偶黄瓜球默认伤害（墨偶覆写为 10） */
    private static final float DEFAULT_BALL_DAMAGE = 5.0F;

    /** 是否已经因被黄瓜二分剑砍中而诞生过墨偶（每只睦偶只触发一次，写入 NBT 持久化） */
    private boolean spawnedMortis;

    public MutsumiPuppetEntity(EntityType<? extends MutsumiPuppetEntity> type, Level level) {
        super(type, level);
    }

    /** 实体基础属性：20 血 / 移速 0.3 / 索敌 32 格（需不小于射程） */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 153.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 0.0D); // 只远程攻击，不做近战
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // 远程攻击：射程内且有目标时按间隔发射黄瓜球
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0D, ATTACK_INTERVAL_TICKS, ATTACK_RANGE));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.1D, 10.0F, 2.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        // 主人被攻击 → 锁定攻击者；自己被攻击也会反击
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    /**
     * Shift + 右键：切换「坐下 / 站起」。
     * 坐下时双腿向前平放并向外张开（见模型 setupAnim），并且不再移动。
     * 非 Shift 右键仍交回 {@link TamableAnimal} 默认处理。
     */
    @Override
    public @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isTame() && player.isSecondaryUseActive()) {
            if (!this.level().isClientSide()) {
                boolean sitting = !this.isOrderedToSit();
                this.setOrderedToSit(sitting);
                this.setInSittingPose(sitting); // 姿态标志（模型据此切换坐姿）
                this.jumping = false;
                this.getNavigation().stop();
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        return super.mobInteract(player, hand);
    }

    /** 坐姿时定住：停止寻路并清零水平速度（竖直速度保留，避免悬空卡住） */
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.isInSittingPose()) {
            this.getNavigation().stop();
            this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
        }
    }

    /** 远程攻击：发射一发伤害为 5 的黄瓜球（弹道数值对齐原版雪傀儡） */
    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        CucumberBallEntity ball = new CucumberBallEntity(this.level(), this);
        ball.setItem(new ItemStack(ModItems.CUCUMBER_BALL.get()));
        ball.setDamage(getBallDamage());
        ball.setFireSeconds(getBallFireSeconds());

        double dx = target.getX() - this.getX();
        // ★ 与原版雪傀儡一致：瞄准目标「眼睛下方 1.1 格」（约胸口），而不是直接瞄眼睛，
        //   否则抛物线补偿叠加后弹道会整体偏高、打不中。
        double dy = target.getEyeY() - 1.1D - ball.getY();
        double dz = target.getZ() - this.getZ();
        // 抛物线补偿：水平距离的 20%（与原版同系数）
        double gravityCompensation = Math.sqrt(dx * dx + dz * dz) * 0.2D;
        ball.shoot(dx, dy + gravityCompensation, dz, 1.6F,
                12.0F - this.level().getDifficulty().getId() * 4.0F);
        this.level().addFreshEntity(ball);

        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F,
                0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    /** 黄瓜球伤害（墨偶覆写为 10） */
    protected float getBallDamage() {
        return DEFAULT_BALL_DAMAGE;
    }

    /** 黄瓜球点燃目标的秒数（睦偶为 0；墨偶覆写为 5） */
    protected float getBallFireSeconds() {
        return 0.0F;
    }

    /** 是否已经诞生过墨偶（被二分剑砍中一次后为 true，之后再砍不再生成） */
    public boolean hasSpawnedMortis() {
        return this.spawnedMortis;
    }

    /** 标记「已诞生过墨偶」 */
    public void markMortisSpawned() {
        this.spawnedMortis = true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("SpawnedMortis", this.spawnedMortis);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.spawnedMortis = tag.getBoolean("SpawnedMortis");
    }

    /** 由召唤逻辑调用：认该玩家为主人 */
    public void tameBy(Player owner) {
        this.setOwnerUUID(owner.getUUID());
        this.setTame(true, true);
        this.setOrderedToSit(false);
    }

    /** 睦偶不参与繁殖 */
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    @Override
    public boolean canMate(Animal other) {
        return false;
    }

    /** 不用食物驯服 / 催情（认主由召唤逻辑完成） */
    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }
}
