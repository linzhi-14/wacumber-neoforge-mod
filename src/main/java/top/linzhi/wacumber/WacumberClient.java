package top.linzhi.wacumber;

import top.linzhi.wacumber.client.renderer.ModClientRenderers;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Wacumber.MODID, dist = Dist.CLIENT)
// 客户端侧同 modid 的第二个 @Mod 入口，用于注册仅客户端存在的扩展点
// EventBusSubscriber：自动注册本类中所有 @SubscribeEvent 静态方法
@EventBusSubscriber(modid = Wacumber.MODID, value = Dist.CLIENT)
public class WacumberClient {
    public WacumberClient(ModContainer container, IEventBus modEventBus) {
        // 让 NeoForge 为模组生成配置界面
        //（Mods 界面 → 点击本模组 → Config）
        // 记得在 en_us.json 中为各配置项补充翻译
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        // 实体渲染器注册：EntityRenderersEvent 属于 mod 事件总线
        modEventBus.addListener(ModClientRenderers::registerLayerDefinitions);
        modEventBus.addListener(ModClientRenderers::registerRenderers);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // 客户端初始化代码
        Wacumber.LOGGER.info("HELLO FROM CLIENT SETUP");
        Wacumber.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
