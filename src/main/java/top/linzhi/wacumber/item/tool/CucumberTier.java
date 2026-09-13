package top.linzhi.wacumber.item.tool;

import top.linzhi.wacumber.item.ModItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/**
 * 自定义「黄瓜」工具等级。
 *
 * <ul>
 *   <li>耐久 1247</li>
 *   <li>挖掘等级 3 级（钻石级，可挖黑曜石等 {@link BlockTags#INCORRECT_FOR_DIAMOND_TOOL} 之外的方块）</li>
 *   <li>附魔能力 14（与铁一致）</li>
 *   <li>挖掘速度 / 攻击加成取钻石档（8.0 / 3.0）</li>
 * </ul>
 *
 * <p>1.21 中工具等级不再走注册表：{@code Tier} 是普通接口，物品拿到自定义实现即可，
 * 因此这里用单例枚举实现，不需要任何注册步骤。
 */
public enum CucumberTier implements Tier {
    INSTANCE;

    @Override
    public int getUses() {
        return 1247;
    }

    @Override
    public float getSpeed() {
        return 8.0F;
    }

    @Override
    public float getAttackDamageBonus() {
        return 3.0F;
    }

    /** 3 级挖矿：除钻石级工具能挖的方块外，都算「挖了也不掉落」 */
    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
    }

    /** 附魔能力与铁相同 */
    @Override
    public int getEnchantmentValue() {
        return 14;
    }

    /** 用黄瓜修复 */
    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ModItems.CUCUMBER.get());
    }
}
