package top.linzhi.wacumber.client.renderer;

import top.linzhi.wacumber.client.model.MortisPuppetModel;
import top.linzhi.wacumber.client.model.MutsumiPuppetModel;
import top.linzhi.wacumber.entity.ModEntities;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * 客户端实体渲染器注册。
 *
 * <p>{@link EntityRenderersEvent} 属于 **mod 事件总线**（{@code IModBusEvent}），
 * 因此由 {@code WacumberClient} 构造器用 {@code modEventBus.addListener} 挂载，
 * 而不是用 {@code @EventBusSubscriber}（后者默认挂游戏总线）。
 */
public final class ModClientRenderers {

    private ModClientRenderers() {
        // 纯工具类，禁止实例化
    }

    /** 注册实体模型层定义（必须先于渲染器注册，否则 bakeLayer 找不到层） */
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MutsumiPuppetModel.LAYER_LOCATION, MutsumiPuppetModel::createBodyLayer);
        event.registerLayerDefinition(MortisPuppetModel.LAYER_LOCATION, MortisPuppetModel::createBodyLayer);
    }

    /** 注册所有实体渲染器 */
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 黄瓜球：像雪球一样使用投掷物渲染（渲染它的物品贴图）
        event.registerEntityRenderer(ModEntities.CUCUMBER_BALL.get(), ThrownItemRenderer::new);
        // 睦偶：自定义模型 + 贴图
        event.registerEntityRenderer(ModEntities.MUTSUMI_PUPPET.get(), MutsumiPuppetRenderer::new);
        // 墨偶：复用睦偶的渲染器泛型（其模型继承睦偶模型），只换模型层与贴图
        event.registerEntityRenderer(ModEntities.MORTIS_PUPPET.get(), MortisPuppetRenderer::new);
    }
}
