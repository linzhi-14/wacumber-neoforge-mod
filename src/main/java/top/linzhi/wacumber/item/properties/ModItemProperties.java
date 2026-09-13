package top.linzhi.wacumber.item.properties;

import top.linzhi.wacumber.effect.ModMobEffects;
import top.linzhi.wacumber.item.tool.CucumberTier;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * 模组物品的 {@link Item.Properties} 工厂：把「物品属性」与「注册流程」分离。
 *
 * <p>{@code ModItems} 只保留「一行一个物品」的注册语句，具体数值（食物、攻击力、攻速等）
 * 全部集中到本类，调整数值 / 对比平衡时只看这一个文件。
 * 工具类物品的等级、耐久由 {@link CucumberTier} 决定，不在这里重复。
 */
public final class ModItemProperties {

    private ModItemProperties() {
        // 纯工厂类，禁止实例化
    }

    /**
     * 黄瓜（食物）：饥饿值 2、饱和度系数 3（实际饱和 = 2 × 3 × 2 = 12）、
     * 吃下后幸运 I 持续 3 分钟（100% 触发）；手持攻击伤害加成 0（总伤害 = 徒手 1）。
     */
    public static Item.Properties cucumber() {
        return new Item.Properties()
                .food(new FoodProperties.Builder()
                        .nutrition(2)
                        // 若想要"实际 +3 饱和度"，把 3f 改成 0.75f
                        .saturationModifier(3f)
                        .effect(() -> new MobEffectInstance(MobEffects.LUCK, 3 * 60 * 20, 0), 1f)
                        .effect(() -> new MobEffectInstance(ModMobEffects.SILENT_GROWTH, 1 * 60 * 20, 0), 1f)
                        .build())
                .attributes(ItemAttributeModifiers.builder()
                        .add(Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 0.0,
                                        AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        .build());
    }

    /** 黄瓜种子：默认属性，无特殊加成 */
    public static Item.Properties cucumberSeed() {
        return new Item.Properties();
    }

    /**
     * 黄瓜二分剑：攻击伤害不加成（总伤害 = 徒手 1，实际伤害由事件清零，只保留手感与耐久消耗），
     * 攻速比原版剑低0.6。
     */
    public static Item.Properties cucumberSword() {
        return new Item.Properties().attributes(ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 0.0,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -3.0,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build());
    }

    /** 黄瓜镐：伤害 / 攻速沿用原版镐（+1 / -2.8），挖掘等级与耐久由 {@link CucumberTier} 决定 */
    public static Item.Properties cucumberPickaxe() {
        return new Item.Properties()
                .attributes(PickaxeItem.createAttributes(CucumberTier.INSTANCE, 1.0F, -2.8F));
    }

    /** 黄瓜战斧：伤害 / 攻速沿用原版斧（+6 / -3.1），保留剥皮 / 去氧化能力 */
    public static Item.Properties cucumberAxe() {
        return new Item.Properties()
                .attributes(AxeItem.createAttributes(CucumberTier.INSTANCE, 6.0F, -3.1F));
    }

    /**
     * 黄瓜护甲：耐久按原版护甲倍率 28（介于铁 15 与钻石 33 之间）。
     * 护甲值与韧性不在这里设置——它们由 {@code CucumberArmorItem#getDefaultAttributeModifiers()}
     * 逐件指定（因为 ArmorItem 构造器会忽略 Properties.attributes）。
     */
    public static Item.Properties cucumberArmor(ArmorItem.Type type) {
        return new Item.Properties().durability(type.getDurability(28));
    }
}
