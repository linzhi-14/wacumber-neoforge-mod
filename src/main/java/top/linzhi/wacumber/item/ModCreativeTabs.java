package top.linzhi.wacumber.item;

import top.linzhi.wacumber.Wacumber;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 创造模式物品栏（Creative Mode Tab）注册类。
 *
 * <p>「黄瓜物品栏」：显示内容完全由主类 {@code Wacumber.MOD_ITEMS} 清单驱动——
 * 遍历清单把每个物品加进来；新物品在 ModItems 注册时会自动追加进清单，无需手动维护。
 */
public final class ModCreativeTabs {
    // 创造标签页注册总线：注册在 "wacumber" 命名空间下
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Wacumber.MODID);

    /**
     * 黄瓜物品栏，注册 id 为 "wacumber:cucumber_tab"。
     * 标题语言键 "itemGroup.cucumber"（zh_cn = 黄瓜物品栏，en_us = Cucumber Tab）。
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CUCUMBER_TAB =
            TABS.register("cucumber_tab", () -> CreativeModeTab.builder()
                    // 物品栏标题
                    .title(Component.translatable("itemGroup.cucumber"))
                    // 图标：使用黄瓜物品（其贴图来自 textures/item/cucumber.png）
                    .icon(() -> new ItemStack(ModItems.CUCUMBER.get()))
                    // 内容：遍历主类物品清单，把每个物品都加入黄瓜物品栏
                    .displayItems((parameters, output) -> {
                        for (DeferredItem<? extends Item> item : Wacumber.MOD_ITEMS) {
                            output.accept(item.get());
                        }
                    })
                    .build());

    private ModCreativeTabs() {
        // 纯工具类，禁止实例化
    }

    /**
     * 把黄瓜物品栏注册总线挂到模组事件总线。
     * 在主类构造器里调用一次：{@code ModCreativeTabs.register(modEventBus);}
     */
    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
