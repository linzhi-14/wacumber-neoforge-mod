package top.linzhi.wacumber.client;

import top.linzhi.wacumber.Wacumber;
import top.linzhi.wacumber.item.ModItems;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * 通用 tooltip 注册表（方案一：集中事件 + 注册表）。
 *
 * <p>给任意物品加 tooltip，不再需要为每个物品建一个 Item 子类：
 * <ol>
 *   <li>在 {@link #registerAll()} 里为物品登记"普通行"与"Shift 行"（文案走语言键，颜色在代码里配）；</li>
 *   <li>客户端渲染 tooltip 时，本类的 {@link #onItemTooltip} 通过 ItemTooltipEvent 自动补上这些行。</li>
 * </ol>
 *
 * <p>Shift 行为：默认"替换式"——按住 Shift 且登记了 shiftLines 时只显示 shiftLines；
 * 也可用 {@link #register(DeferredItem, List, List, boolean)} 指定为"追加式"（普通行常驻 + shift 追加深绿行）。
 */
@EventBusSubscriber(modid = Wacumber.MODID, value = Dist.CLIENT)
public final class ModTooltips {

    /** 一行 tooltip：语言键 + 若干 ChatFormatting（颜色等）；不传样式则用默认灰色 */
    public record TooltipLine(String langKey, List<ChatFormatting> formats) {

        public static TooltipLine of(String langKey, ChatFormatting... formats) {
            return new TooltipLine(langKey, List.of(formats));
        }

        /** 转成可翻译的文本组件（渲染时按当前语言取词） */
        Component render() {
            MutableComponent component = Component.translatable(langKey);
            return formats.isEmpty() ? component
                    : component.withStyle(formats.toArray(new ChatFormatting[0]));
        }
    }

    /** 单个物品的完整 tooltip 规格 */
    public record Spec(List<TooltipLine> normalLines, List<TooltipLine> shiftLines, boolean replaceOnShift) {}

    private record Registration(DeferredItem<? extends Item> item, Spec spec) {}

    private static final List<Registration> REGISTRATIONS = new ArrayList<>();

    private ModTooltips() {
        // 纯工具类，禁止实例化
    }

    /** 登记 tooltip（默认：按住 Shift 时"替换"普通行） */
    public static void register(DeferredItem<? extends Item> item,
                                List<TooltipLine> normalLines, List<TooltipLine> shiftLines) {
        register(item, normalLines, shiftLines, true);
    }

    /**
     * 登记 tooltip。
     *
     * @param replaceOnShift true=按住 Shift 只显示 shiftLines；false=按住 Shift 在普通行后追加 shiftLines
     */
    public static void register(DeferredItem<? extends Item> item,
                                List<TooltipLine> normalLines, List<TooltipLine> shiftLines,
                                boolean replaceOnShift) {
        REGISTRATIONS.add(new Registration(item, new Spec(normalLines, shiftLines, replaceOnShift)));
    }

    /** 集中登记所有物品的 tooltip，主类构造器调用一次；以后新物品的 tooltip 也加在这里 */
    public static void registerAll() {
        // —— 黄瓜 ——
        register(ModItems.CUCUMBER,
                List.of(
                        TooltipLine.of("tooltip.wacumber.cucumber.line1", ChatFormatting.GREEN),
                        TooltipLine.of("tooltip.wacumber.cucumber.line2", ChatFormatting.GOLD)),
                List.of(
                        TooltipLine.of("tooltip.wacumber.cucumber.shift1", ChatFormatting.DARK_GREEN)),
                true);

        // —— 黄瓜二分剑（无 Shift 行：按住 Shift 也显示普通行）——
        register(ModItems.CUCUMBER_SWORD,
                List.of(
                        TooltipLine.of("tooltip.wacumber.cucumber_sword.line1", ChatFormatting.DARK_GREEN)),
                List.of(),
                true);

        // —— 黄瓜镐 ——
        register(ModItems.CUCUMBER_PICKAXE,
                List.of(
                        TooltipLine.of("tooltip.wacumber.cucumber_pickaxe.line1", ChatFormatting.DARK_GREEN)),
                List.of(
                        TooltipLine.of("tooltip.wacumber.cucumber_pickaxe.shift1", ChatFormatting.DARK_GREEN)),
                true);

        // —— 黄瓜战斧 ——
        register(ModItems.CUCUMBER_AXE,
                List.of(
                        TooltipLine.of("tooltip.wacumber.cucumber_axe.line1", ChatFormatting.DARK_GREEN)),
                List.of(
                        TooltipLine.of("tooltip.wacumber.cucumber_axe.shift1", ChatFormatting.DARK_GREEN)),
                true);
    }

    // ============ 客户端：统一渲染 tooltip ============

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }
        for (Registration registration : REGISTRATIONS) {
            // 检查物品是否匹配
            if (!stack.is(registration.item().get())) {
                continue;
            }
            Spec spec = registration.spec();
            boolean shiftDown = Screen.hasShiftDown();
            List<TooltipLine> lines = spec.normalLines();
            if (shiftDown && !spec.shiftLines().isEmpty()) {
                lines = spec.replaceOnShift()
                        ? spec.shiftLines()                          // 替换式：只显示 shift 行
                        : concat(spec.normalLines(), spec.shiftLines()); // 追加式：普通行 + shift 行
            }
            for (TooltipLine line : lines) {
                event.getToolTip().add(line.render());
            }
            break;
        }
    }
    /**
     * 合并两个 TooltipLine 列表。
     *
     * @return 新列表，内容为 first + second
     */
    private static List<TooltipLine> concat(List<TooltipLine> first, List<TooltipLine> second) {
        List<TooltipLine> merged = new ArrayList<>(first);
        merged.addAll(second);
        return merged;
    }
}
