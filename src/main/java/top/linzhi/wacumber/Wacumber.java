package top.linzhi.wacumber;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import top.linzhi.wacumber.block.ModBlocks;
import top.linzhi.wacumber.client.ModTooltips;
import top.linzhi.wacumber.effect.ModMobEffects;
import top.linzhi.wacumber.entity.ModEntities;
import top.linzhi.wacumber.entity.MutsumiPuppetEntity;
import top.linzhi.wacumber.item.armor.ModArmorMaterials;
import top.linzhi.wacumber.itemgroup.ModCreativeTabs;
import top.linzhi.wacumber.item.ModItems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredItem;

// 值应与 META-INF/neoforge.mods.toml 中的条目一致
@Mod(Wacumber.MODID)
public class Wacumber {
    // mod id，与 gradle.properties 的 mod_id、资源目录 assets/wacumber 保持一致
    public static final String MODID = "wacumber";
    // 直接引用 slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // ========== 主类物品清单（黄瓜物品栏的数据源） ==========
    // Java 的定长数组无法在注册方法里"追加"，所以这里用 ArrayList 实现可增长的清单。
    // ModItems 的注册方法（registerItem / registerBlockItem / register）会自动把新物品
    // 追加进本清单 —— 主类无需手动罗列物品；ModCreativeTabs 遍历本清单填装黄瓜物品栏。
    public static final List<DeferredItem<? extends Item>> MOD_ITEMS = new ArrayList<>();

    // 主类构造器：FML 会自动识别并注入 IEventBus、ModContainer 参数
    public Wacumber(IEventBus modEventBus, ModContainer modContainer) {
        // commonSetup 挂到模组事件总线（mod 加载期事件）
        modEventBus.addListener(this::commonSetup);

        // 首次访问 ModBlocks/ModItems 会触发其静态初始化（执行 CUCUMBER_PLANT、CUCUMBER 等注册常量），
        // 随后把方块/物品注册总线挂到模组事件总线。方块需先于依赖它的种子物品注册。
        ModBlocks.register(modEventBus);
        // 首次访问 ModItems 会触发其静态初始化（执行 CUCUMBER 等注册常量，
        // 并把物品自动追加进上面的 MOD_ITEMS），随后把物品注册总线挂到模组事件总线
        ModItems.register(modEventBus);
        // 登记所有物品的 tooltip（方案一：集中注册表 + ItemTooltipEvent）
        ModTooltips.registerAll();
        // 黄瓜物品栏注册进注册总线
        ModCreativeTabs.register(modEventBus);
        // 状态效果（静默生长等）注册进注册总线
        ModMobEffects.register(modEventBus);
        // 盔甲材质（黄瓜）注册进注册总线；其修复材料引用 ModItems.CUCUMBER，故放在物品注册之后
        ModArmorMaterials.register(modEventBus);
        // 实体类型（黄瓜球、睦偶等）注册进注册总线
        ModEntities.register(modEventBus);
        // 实体基础属性注册（EntityAttributeCreationEvent 也是 mod 总线事件）
        modEventBus.addListener(this::registerEntityAttributes);

        // 注册到 NeoForge 游戏事件总线：仅当本类存在 @SubscribeEvent 方法时需要
        NeoForge.EVENT_BUS.register(this);

        // 注册 ModConfigSpec，让 FML 生成并加载配置文件
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    /** 注册实体基础属性（睦偶：20 血 / 移速 0.3 / 索敌 32 格） */
    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MUTSUMI_PUPPET.get(), MutsumiPuppetEntity.createAttributes().build());
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // 一些通用的初始化代码
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // @SubscribeEvent：事件总线自动发现并调用本方法
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // 服务端启动时做点什么
        LOGGER.info("HELLO from server starting");
    }
}
