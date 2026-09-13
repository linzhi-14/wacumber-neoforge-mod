package top.linzhi.wacumber.item.armor;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import top.linzhi.wacumber.item.ModItems;
import top.linzhi.wacumber.Wacumber;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 模组盔甲材质（ArmorMaterial）注册类。
 *
 * <p>1.21 起 {@code ArmorMaterial} 是数据驱动的注册表对象，
 * 每件护甲值由 defense map 决定，韧性（toughness）是**全套单一值**。
 * 由于黄瓜套装要求每件韧性不同（1/4/3/1），这里把 toughness 置 0，
 * 由 {@link CucumberArmorItem#getDefaultAttributeModifiers()} 逐件补上。
 */
public final class ModArmorMaterials {
    // 盔甲材质注册总线
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, Wacumber.MODID);

    /**
     * 黄瓜盔甲材质：注册 id 为 "wacumber:cucumber"。
     * defense = 头盔 2 / 胸甲 6 / 护腿 4 / 靴子 1；附魔性 14（同铁）；用黄瓜修复。
     * 贴图由 {@link ArmorMaterial.Layer} 推导为
     * {@code textures/models/armor/cucumber_armor_layer_1.png}（头盔/胸甲/靴子）
     * 与 {@code ..._layer_2.png}（护腿）。
     */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> CUCUMBER =
            ARMOR_MATERIALS.register("cucumber", () -> new ArmorMaterial(
                    defenseBySlot(),
                    14,                           // 附魔能力（与铁一致）
                    SoundEvents.ARMOR_EQUIP_IRON,                 // 装备音效
                    () -> Ingredient.of(ModItems.CUCUMBER.get()), // 修复材料：黄瓜
                    //模型贴图
                    List.of(new ArmorMaterial.Layer(
                            ResourceLocation.fromNamespaceAndPath(Wacumber.MODID, "cucumber_armor"))),
                    0.0F,                               // 韧性交给每件物品的独立修饰符
                    0.0F));                                       // 无击退抗性

    /** 每槽位护甲值：头盔 2 / 胸甲 6 / 护腿 4 / 靴子 1 */
    private static Map<ArmorItem.Type, Integer> defenseBySlot() {
        Map<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        defense.put(ArmorItem.Type.HELMET, 2);
        defense.put(ArmorItem.Type.CHESTPLATE, 6);
        defense.put(ArmorItem.Type.LEGGINGS, 4);
        defense.put(ArmorItem.Type.BOOTS, 1);
        return defense;
    }

    private ModArmorMaterials() {
        // 纯工具类，禁止实例化
    }

    /**
     * 注入盔甲材质注册总线。
     * 在主类构造器里调用一次：{@code ModArmorMaterials.register(modEventBus);}
     */
    public static void register(IEventBus modEventBus) {
        ARMOR_MATERIALS.register(modEventBus);
    }
}
