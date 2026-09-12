package top.linzhi.wacumber.effect;

import top.linzhi.wacumber.Wacumber;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 模组状态效果（MobEffect）注册类。
 *
 * <p>使用约定：
 * <ol>
 *   <li>主类构造器调用 {@link #register(IEventBus)}，把效果注册总线挂到模组事件总线（只需一次）；</li>
 *   <li>新效果 = 在本类里写一行注册常量；</li>
 *   <li>效果贴图放在 {@code assets/wacumber/textures/mob_effect/<注册 id>.png}，
 *       注册 id 必须与贴图文件名一致。</li>
 * </ol>
 */
public final class ModMobEffects {
    // 状态效果注册总线
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, Wacumber.MODID);

    /**
     * 静默生长：注册 id 为 "wacumber:slient_grow"（贴图 assets/wacumber/textures/mob_effect/slient_grow.png）。
     *
     * <p>颜色取自贴图主色 #094001；效果作用见
     * {@code top.linzhi.wacumber.event.SilentGrowthHandler}。
     */
    public static final DeferredHolder<MobEffect, SilentGrowthEffect> SILENT_GROWTH =
            MOB_EFFECTS.register("slient_grow",
                    () -> new SilentGrowthEffect(MobEffectCategory.BENEFICIAL, 0x094001));

    private ModMobEffects() {
        // 纯工具类，禁止实例化
    }

    /**
     * 注入状态效果注册总线。
     * 在主类构造器里调用一次：{@code ModMobEffects.register(modEventBus);}
     */
    public static void register(IEventBus modEventBus) {
        MOB_EFFECTS.register(modEventBus);
    }
}
