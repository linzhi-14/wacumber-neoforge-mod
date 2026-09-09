package top.linzhi.wacumber.item;

import top.linzhi.wacumber.Wacumber;
import top.linzhi.wacumber.block.ModBlocks;

import java.util.function.Supplier;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
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
     * 属性见 {@link #cucumberItemProperties()}。
     */
    public static final DeferredItem<Item> CUCUMBER = registerItem("cucumber", cucumberItemProperties());

    /**
     * 黄瓜种子：注册 id 为 "wacumber:cucumber_seed"。
     * 使用原版 {@link ItemNameBlockItem}（小麦种子同款）——右键农田即可种下黄瓜植株。
     */
    public static final DeferredItem<ItemNameBlockItem> CUCUMBER_SEED = register(
            "cucumber_seed",
            () -> new ItemNameBlockItem(ModBlocks.CUCUMBER_PLANT.get(), new Item.Properties()));

    private ModItems() {
        // 纯工具类，禁止实例化
    }

    /**
     * 注册一个普通物品（使用默认 Item 类），注册后自动加入主类物品清单。
     *
     * @param name       注册路径，如 "cucumber"，最终 id 为 "wacumber:cucumber"
     * @param properties 物品属性（可带 food / attributes 等）
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

    /** 黄瓜的物品属性：食物（饥饿 2 / 饱和度系数 3 / 幸运 I 3 分钟），手持攻击伤害合计 1 */
    private static Item.Properties cucumberItemProperties() {
        return new Item.Properties()
                .food(new FoodProperties.Builder()
                        .nutrition(2) // 补充饥饿值 2
                        // 饱和度系数 3：实际饱和量 = 饥饿值 × 系数 × 2 = 2 × 3 × 2 = 12
                        // （若你想要的是"实际 +3 饱和度"，把 3f 改成 0.75f 即可）
                        .saturationModifier(3f)
                        // 吃下后获得 幸运 I（amplifier 0），持续 3 分钟 = 3600 tick，100% 触发
                        .effect(() -> new MobEffectInstance(MobEffects.LUCK, 3 * 60 * 20, 0), 1f)
                        .build())
                .attributes(ItemAttributeModifiers.builder()
                        // 徒手基础攻击伤害为 1.0，这里加成 0.0 → 手持黄瓜总攻击伤害 = 1
                        .add(Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 0.0,
                                        AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        .build());
    }
}
