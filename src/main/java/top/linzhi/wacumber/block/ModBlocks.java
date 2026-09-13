package top.linzhi.wacumber.block;

import top.linzhi.wacumber.Wacumber;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 模组方块注册类：统一管理 "wacumber" 命名空间下所有方块的注册。
 *
 * <p>使用约定：
 * <ol>
 *   <li>主类构造器调用 {@link #register(IEventBus)}，把方块注册总线注入模组事件总线（只需一次）；</li>
 *   <li>新方块 = 在本类里写一行注册常量，例如
 *       {@code public static final DeferredBlock<XxxBlock> XXX = registerBlock("xxx", 属性);}</li>
 * </ol>
 */
public final class ModBlocks {
    // 方块注册总线（DeferredRegister.Blocks）：所有方块都注册在 "wacumber" 命名空间下
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Wacumber.MODID);

    // ============ 模组方块都在这里注册 ============

    /**
     * 黄瓜植株（作物方块）：注册 id 为 "wacumber:cucumber_plant"。
     * 无对应方块物品（BlockItem）——与原版小麦一样，植株只能靠黄瓜种子种出来，
     * 因此它不会出现在 ModItems / 黄瓜物品栏中。
     * 方块属性对齐原版小麦（无碰撞、可随机刻生长、徒手即碎、作物音效）。
     */
    public static final DeferredBlock<CucumberCropBlock> CUCUMBER_PLANT =
            BLOCKS.register("cucumber_plant", () -> new CucumberCropBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.PLANT)
                            .noCollission()      // 无碰撞箱，可穿过
                            .randomTicks()       // ★ 关键：让方块收到随机刻才能像小麦一样生长
                            .instabreak()        // 像草一样徒手瞬间破坏
                            .sound(SoundType.CROP)
                            .noOcclusion()       // 植株藤蔓模型穿出碰撞范围时避免相邻面剔除错乱
                            .pushReaction(PushReaction.DESTROY)));

    /**
     * 睦子米方块：注册 id 为 "wacumber:mutsumi_block"。
     * 促进周围 5×5×5 内作物生长（+20%），放置约 10 游戏日后随随机刻腐化成哭泣形态。
     * 方块属性对齐原版史莱姆方块（摩擦 0.8、史莱姆音效、不遮挡视线、不窒息），
     * 另需 {@code randomTicks()} 才能收到随机刻进行腐化判定。
     */
    public static final DeferredBlock<MutsumiBlock> MUTSUMI_BLOCK =
            BLOCKS.register("mutsumi_block", () -> new MutsumiBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PINK)
                            .strength(0.5F)
                            .friction(0.8F)                          // 同史莱姆方块
                            .sound(SoundType.SLIME_BLOCK)            // 同史莱姆方块
                            .noOcclusion()
                            .isViewBlocking((state, level, pos) -> false)
                            .isSuffocating((state, level, pos) -> false)
                            .randomTicks()));                        // ★ 腐化依赖随机刻

    /**
     * 哭泣的睦子米方块：注册 id 为 "wacumber:mutsumi_cry_block"。
     * 不再促进作物生长；可用空玻璃瓶右键使其恢复，并把空瓶变成幸运药水。
     * 方块属性同样对齐原版史莱姆方块。
     */
    public static final DeferredBlock<MutsumiCryingBlock> MUTSUMI_CRY_BLOCK =
            BLOCKS.register("mutsumi_cry_block", () -> new MutsumiCryingBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PINK)
                            .strength(0.5F)
                            .friction(0.8F)
                            .sound(SoundType.SLIME_BLOCK)
                            .noOcclusion()
                            .isViewBlocking((state, level, pos) -> false)
                            .isSuffocating((state, level, pos) -> false)));

    private ModBlocks() {
        // 纯工具类，禁止实例化
    }

    /**
     * 注入方块注册总线：把 BLOCKS 挂到模组事件总线，触发真实注册。
     * 在主类构造器里调用一次：{@code ModBlocks.register(modEventBus);}
     */
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
