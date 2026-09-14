package top.linzhi.wacumber.client.renderer;

import top.linzhi.wacumber.Wacumber;
import top.linzhi.wacumber.client.model.MutsumiPuppetModel;
import top.linzhi.wacumber.entity.MutsumiPuppetEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * 睦偶渲染器：用 {@link MutsumiPuppetModel} 渲染，贴图
 * {@code assets/wacumber/textures/entity/mutsumi_puppet.png}。
 */
public class MutsumiPuppetRenderer extends MobRenderer<MutsumiPuppetEntity, MutsumiPuppetModel> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Wacumber.MODID, "textures/entity/mutsumi_puppet.png");

    public MutsumiPuppetRenderer(EntityRendererProvider.Context context) {
        super(context, new MutsumiPuppetModel(context.bakeLayer(MutsumiPuppetModel.LAYER_LOCATION)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(MutsumiPuppetEntity entity) {
        return TEXTURE;
    }
}
