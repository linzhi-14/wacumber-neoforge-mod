package top.linzhi.wacumber.item;

import top.linzhi.wacumber.Wacumber;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * 模组物品标签（Item Tag）定义。
 *
 * <p>标签内容写在数据包 JSON 里：{@code data/wacumber/tags/item/xxx.json}，
 * 代码侧只持有 {@link TagKey} 句柄，判定统一用 {@code stack.is(TAG)}。
 */
public final class ModItemTags {

    /**
     * 具备「挖掘掉落翻倍」能力的黄瓜工具（当前含黄瓜镐、黄瓜战斧）。
     * 数据文件：{@code data/wacumber/tags/item/cucumber_tools.json}
     */
    public static final TagKey<Item> CUCUMBER_TOOLS = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(Wacumber.MODID, "cucumber_tools"));

    private ModItemTags() {
        // 纯常量类，禁止实例化
    }
}
