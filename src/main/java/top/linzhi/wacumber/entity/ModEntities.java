package top.linzhi.wacumber.entity;

import top.linzhi.wacumber.entity.projectile.CucumberBallEntity;
import top.linzhi.wacumber.Wacumber;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 模组实体类型（EntityType）注册类。
 *
 * <p>使用约定：
 * <ol>
 *   <li>主类构造器调用 {@link #register(IEventBus)}，把实体注册总线挂到模组事件总线（只需一次）；</li>
 *   <li>新实体 = 在本类里写一行注册常量；渲染器在客户端注册（见 client/ModClientRenderers）。</li>
 * </ol>
 */
public final class ModEntities {
    // 实体类型注册总线
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Wacumber.MODID);

    /**
     * 黄瓜球（投掷物）：注册 id 为 "wacumber:cucumber_ball"。
     * 尺寸 / 追踪范围对齐原版雪球（0.25 格、追踪 4 区块、每 10 tick 同步一次）。
     */
    public static final DeferredHolder<EntityType<?>, EntityType<CucumberBallEntity>> CUCUMBER_BALL =
            ENTITY_TYPES.register("cucumber_ball", () -> EntityType.Builder
                    .<CucumberBallEntity>of(CucumberBallEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("cucumber_ball"));

    /**
     * 睦偶（宠物实体）：注册 id 为 "wacumber:mutsumi_puppet"。
     * 尺寸 0.6 × 1.5 格（模型高 24 像素）；属性在
     * {@code EntityAttributeCreationEvent} 中注册（见主类）。
     */
    public static final DeferredHolder<EntityType<?>, EntityType<MutsumiPuppetEntity>> MUTSUMI_PUPPET =
            ENTITY_TYPES.register("mutsumi_puppet", () -> EntityType.Builder
                    .of(MutsumiPuppetEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.5F)
                    .clientTrackingRange(10)
                    .build("mutsumi_puppet"));

    private ModEntities() {
        // 纯工具类，禁止实例化
    }

    /**
     * 注入实体类型注册总线。
     * 在主类构造器里调用一次：{@code ModEntities.register(modEventBus);}
     */
    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }
}
