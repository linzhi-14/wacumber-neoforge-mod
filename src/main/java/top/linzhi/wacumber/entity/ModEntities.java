package top.linzhi.wacumber.entity;

import top.linzhi.wacumber.entity.projectile.CucumberBallEntity;
import top.linzhi.wacumber.Wacumber;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
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

    /**
     * 墨偶：注册 id 为 "wacumber:mortis_puppet"。
     * 由黄瓜二分剑砍中睦偶时诞生（见 {@code event.ToolAbilityHandler}）；
     * 行为与睦偶相同，黄瓜球伤害 10 且点燃目标 5 秒。
     */
    public static final DeferredHolder<EntityType<?>, EntityType<MortisPuppetEntity>> MORTIS_PUPPET =
            ENTITY_TYPES.register("mortis_puppet", () -> EntityType.Builder
                    .of(MortisPuppetEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.5F)
                    .clientTrackingRange(10)
                    .build("mortis_puppet"));

    private ModEntities() {
        // 纯工具类，禁止实例化
    }

    /**
     * 注册实体基础属性（睦偶与墨偶共用一套：165 血 / 移速 0.3 / 索敌 32 格）。
     * 由 {@link #register(IEventBus)} 自动挂到 mod 事件总线上，主类无需参与。
     */
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(MUTSUMI_PUPPET.get(), MutsumiPuppetEntity.createAttributes().build());
        // 墨偶直接继承睦偶，属性一致
        event.put(MORTIS_PUPPET.get(), MutsumiPuppetEntity.createAttributes().build());
    }

    /**
     * 注入实体类型注册总线，并顺带挂载实体属性注册。
     * 在主类构造器里调用一次：{@code ModEntities.register(modEventBus);}
     */
    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
        // 实体属性注册（EntityAttributeCreationEvent 同样是 mod 总线事件）
        modEventBus.addListener(ModEntities::registerAttributes);
    }
}
