package top.linzhi.wacumber.item;

import top.linzhi.wacumber.Wacumber;
import top.linzhi.wacumber.block.ModBlocks;
import top.linzhi.wacumber.item.properties.ModItemProperties;

import java.util.function.Supplier;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 模组物品注册类：统一管理 "wacumber" 命名空间下所有物品的注册。
 *
 * <p>使用约定：
 * <ol>
 *   <li>主类构造器调用 {@link #register(IEventBus)}，把物品注册总线注入模组事件总线（只需一次）；</li>
 *   <li>新物品 = 在本类里写一行注册常量，例如
 *       {@code public static final DeferredItem<Item> XXX = registerItem("xxx", 属性);}</li>
 *   <li>物品属性（食物 / 攻击力 / 攻速等）统一写在
 *       {@link ModItemProperties}，本类只负责注册；</li>
 *   <li>每个注册方法（{@link #registerItem} / {@link #registerBlockItem} / {@link #register}）
 *       都会把注册好的物品自动追加进主类 {@code Wacumber.MOD_ITEMS} 清单，
 *       黄瓜物品栏遍历该清单自动展示 —— 不需要再手动往清单里加物品。</li>
 * </ol>
 */
public final class ModItems {
    // 物品注册总线（DeferredRegister.Items）：所有物品都注册在 "wacumber" 命名空间下
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Wacumber.MODID);

    // ============ 模组物品都在这里注册 ============
    // 本类首次被访问时（主类构造器调用 ModItems.register 时）执行静态初始化，
    // 下面每个注册常量都会把物品句柄自动追加进 Wacumber.MOD_ITEMS。

    /**
     * 黄瓜：食物物品，注册 id 为 "wacumber:cucumber"。
     * 使用普通 Item 即可——tooltip 由 ModTooltips 注册表集中管理（见 ModTooltips.registerAll）。
     */
    public static final DeferredItem<Item> CUCUMBER =
            registerItem("cucumber", ModItemProperties.cucumber());

    /**
     * 黄瓜种子：注册 id 为 "wacumber:cucumber_seed"。
     * 使用原版 {@link ItemNameBlockItem}（小麦种子同款）——右键农田即可种下黄瓜植株。
     */
    public static final DeferredItem<ItemNameBlockItem> CUCUMBER_SEED = register(
            "cucumber_seed",
            () -> new ItemNameBlockItem(ModBlocks.CUCUMBER_PLANT.get(), ModItemProperties.cucumberSeed()));

    /**
     * 黄瓜二分剑：命中不造成伤害，改为把目标当前血量减半（向下取整）；
     * 目标当前血量 ≤ 5 时直接斩杀，否则在目标坐标生成一只同血量分身。
     * 效果实现见 {@code top.linzhi.wacumber.event.ToolAbilityHandler}。
     */
    public static final DeferredItem<SwordItem> CUCUMBER_SWORD = register(
            "cucumber_sword",
            () -> new CucumberSwordItem(CucumberTier.INSTANCE, ModItemProperties.cucumberSword()));

    /**
     * 黄瓜镐：挖掘「它能挖动的方块」时掉落翻倍，冷却 5 秒。
     * 效果实现见 {@code top.linzhi.wacumber.event.ToolAbilityHandler#onBlockDrops}。
     */
    public static final DeferredItem<PickaxeItem> CUCUMBER_PICKAXE = register(
            "cucumber_pickaxe",
            () -> new PickaxeItem(CucumberTier.INSTANCE, ModItemProperties.cucumberPickaxe()));

    /**
     * 黄瓜战斧：挖掘「它能挖动的方块」时掉落翻倍，冷却 5 秒；
     * 同时保留原版斧的剥皮 / 去氧化 / 脱蜡功能。效果同镐。
     */
    public static final DeferredItem<AxeItem> CUCUMBER_AXE = register(
            "cucumber_axe",
            () -> new AxeItem(CucumberTier.INSTANCE, ModItemProperties.cucumberAxe()));

    private ModItems() {
        // 纯工具类，禁止实例化
    }

    /**
     * 注册一个普通物品（使用默认 Item 类），注册后自动加入主类物品清单。
     *
     * @param name       注册路径，如 "cucumber"，最终 id 为 "wacumber:cucumber"
     * @param properties 物品属性（可带 food / attributes 等，来自 {@link ModItemProperties}）
     */
    public static DeferredItem<Item> registerItem(String name, Item.Properties properties) {
        DeferredItem<Item> item = ITEMS.register(name, () -> new Item(properties));
        Wacumber.MOD_ITEMS.add(item); // ★ 自动登记进主类 MOD_ITEMS 清单（黄瓜物品栏据此展示）
        return item;
    }

    /**
     * 注册一个方块物品（BlockItem，使用默认物品属性），注册后自动加入主类物品清单。
     *
     * @param name  注册路径，通常与方块路径一致
     * @param block 方块注册句柄（来自 DeferredRegister.Blocks）
     */
    public static DeferredItem<BlockItem> registerBlockItem(String name, DeferredBlock<? extends Block> block) {
        DeferredItem<BlockItem> item = ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        Wacumber.MOD_ITEMS.add(item); // ★ 自动登记进主类 MOD_ITEMS 清单
        return item;
    }

    /**
     * 通用注册入口（自定义 Item 子类时使用），注册后自动加入主类物品清单。
     *
     * @param name        注册路径
     * @param itemFactory 物品实例工厂，如 {@code () -> new MySwordItem(...)}
     */
    public static <I extends Item> DeferredItem<I> register(String name, Supplier<? extends I> itemFactory) {
        DeferredItem<I> item = ITEMS.register(name, itemFactory);
        Wacumber.MOD_ITEMS.add(item); // ★ 自动登记进主类 MOD_ITEMS 清单
        return item;
    }

    /**
     * 注入物品注册总线：把 ITEMS 挂到模组事件总线，触发真实注册。
     * 在主类构造器里调用一次：{@code ModItems.register(modEventBus);}
     * （首次调用本方法也会触发本类的静态初始化，即执行上面的注册常量。）
     */
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
