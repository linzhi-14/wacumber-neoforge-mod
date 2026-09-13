package top.linzhi.wacumber.item.armor;

import top.linzhi.wacumber.Wacumber;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * 黄瓜盔甲（自定义属性版本）。
 *
 * <p>{@link ArmorItem} 的构造器把默认属性硬编码为「材质 defense + 材质 toughness」，
 * 会忽略 {@code Item.Properties().attributes(...)}，而 {@link ArmorMaterial#toughness()} 又是
 * 全套单一值。因此这里继承并覆写 {@link #getDefaultAttributeModifiers()}，
 * 为每件单独指定护甲值与韧性。
 */
public class CucumberArmorItem extends ArmorItem {

    private final int defense;
    private final float toughness;

    public CucumberArmorItem(Holder<ArmorMaterial> material, ArmorItem.Type type,
                             int defense, float toughness, Item.Properties properties) {
        super(material, type, properties);
        this.defense = defense;
        this.toughness = toughness;
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        String typeName = getType().getName();
        EquipmentSlotGroup slot = slotGroupOf(getType());
        return ItemAttributeModifiers.builder()
                .add(Attributes.ARMOR,
                        new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath(Wacumber.MODID, "cucumber_armor." + typeName),
                                defense, AttributeModifier.Operation.ADD_VALUE),
                        slot)
                .add(Attributes.ARMOR_TOUGHNESS,
                        new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath(Wacumber.MODID, "cucumber_armor_toughness." + typeName),
                                toughness, AttributeModifier.Operation.ADD_VALUE),
                        slot)
                .build();
    }

    /** 护甲槽位 → 属性修饰符作用槽位组 */
    private static EquipmentSlotGroup slotGroupOf(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> EquipmentSlotGroup.HEAD;
            case CHESTPLATE -> EquipmentSlotGroup.CHEST;
            case LEGGINGS -> EquipmentSlotGroup.LEGS;
            case BOOTS -> EquipmentSlotGroup.FEET;
            default -> EquipmentSlotGroup.ARMOR;
        };
    }
}
