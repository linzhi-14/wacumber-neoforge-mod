package top.linzhi.wacumber.event;

import top.linzhi.wacumber.Wacumber;
import top.linzhi.wacumber.effect.ModMobEffects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;

/**
 * 「静默生长」状态效果的实际作用（挂在游戏事件总线上）。
 *
 * <ol>
 *   <li><b>作物 1.5 倍速生长</b>：以玩家为中心、直径 18 格（半径 9）内的作物，
 *       每次正常生长后再以 50% 概率额外长一级 → 平均生长速度 = 1.5 倍。</li>
 *   <li><b>钓鱼宝藏概率提升</b>：钓鱼上钩时有 20% 概率额外追加一份
 *       原版「宝藏」战利品表掉落（原渔获不变）。</li>
 * </ol>
 */
@EventBusSubscriber(modid = Wacumber.MODID)
public final class SilentGrowthHandler {

    /** 作用范围：直径 18 格 → 半径 9 格 */
    private static final double RADIUS = 9.0D;

    /** 额外生长概率：0.5 → 平均 1.5 倍速 */
    private static final float EXTRA_GROW_CHANCE = 0.5F;

    /** 钓鱼追加宝藏的触发概率 */
    private static final float TREASURE_CHANCE = 0.2F;

    /** 原版钓鱼宝藏战利品表：data/minecraft/loot_table/gameplay/fishing/treasure.json */
    private static final ResourceKey<LootTable> FISHING_TREASURE = ResourceKey.create(
            Registries.LOOT_TABLE,
            ResourceLocation.withDefaultNamespace("gameplay/fishing/treasure"));

    private SilentGrowthHandler() {
        // 纯事件处理器，禁止实例化
    }

    // ==================== 作物 1.5 倍速生长 ====================

    @SubscribeEvent
    public static void onCropGrow(CropGrowEvent.Post event) {
        LevelAccessor accessor = event.getLevel();
        if (!(accessor instanceof ServerLevel level)) {
            return;
        }
        BlockState state = event.getState(); // 本次生长「之后」的状态
        if (!(state.getBlock() instanceof CropBlock crop)) {
            return;
        }
        int age = crop.getAge(state);
        if (age >= crop.getMaxAge()) {
            return; // 已成熟
        }
        BlockPos pos = event.getPos();
        if (!hasSilentGrowthNearby(level, pos)) {
            return;
        }
        if (level.random.nextFloat() >= EXTRA_GROW_CHANCE) {
            return;
        }
        // 直接提升 age：setBlock 不会再触发 CropGrowEvent，因此不会递归
        level.setBlock(pos, crop.getStateForAge(age + 1), Block.UPDATE_ALL);
    }

    /** 以该方块为中心、半径 9 格（直径 18）内是否存在带静默生长效果的玩家 */
    private static boolean hasSilentGrowthNearby(ServerLevel level, BlockPos pos) {
        Vec3 center = Vec3.atCenterOf(pos);
        double radiusSqr = RADIUS * RADIUS;
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(center) <= radiusSqr
                    && player.hasEffect(ModMobEffects.SILENT_GROWTH)) {
                return true;
            }
        }
        return false;
    }

    // ==================== 钓鱼：稀有物品概率提升 ====================

    @SubscribeEvent
    public static void onItemFished(ItemFishedEvent event) {
        Player player = event.getEntity();
        if (!player.hasEffect(ModMobEffects.SILENT_GROWTH)) {
            return;
        }
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        if (level.random.nextFloat() >= TREASURE_CHANCE) {
            return;
        }

        FishingHook hook = event.getHookEntity();
        LootTable treasure = level.getServer().reloadableRegistries().getLootTable(FISHING_TREASURE);
        // 参数集与钓鱼原版判定一致：ORIGIN / TOOL / THIS_ENTITY（宝藏池要求开放水域判定）
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, hook.position())
                .withParameter(LootContextParams.TOOL, player.getMainHandItem())
                .withParameter(LootContextParams.THIS_ENTITY, hook)
                .withLuck(player.getLuck())
                .create(LootContextParamSets.FISHING);
        // 原渔获保持不变，额外追加一份宝藏掉落
        event.getDrops().addAll(treasure.getRandomItems(params));
    }
}
