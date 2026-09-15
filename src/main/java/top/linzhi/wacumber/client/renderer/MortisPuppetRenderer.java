package top.linzhi.wacumber.client.renderer;

import top.linzhi.wacumber.Wacumber;
import top.linzhi.wacumber.client.model.MortisPuppetModel;
import top.linzhi.wacumber.client.model.MutsumiPuppetModel;
import top.linzhi.wacumber.entity.MutsumiPuppetEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * 墨偶渲染器。
 *
 * <p>墨偶实体继承睦偶实体，所以泛型直接用 {@code MobRenderer<MutsumiPuppetEntity, MutsumiPuppetModel>}：
 * 模型换成墨偶自身的 {@link MortisPuppetModel}（继承睦偶模型，方块不同、动画复用），
 * 贴图为 {@code textures/entity/mortis_puppet.png}。
 */
public class MortisPuppetRenderer extends MobRenderer<MutsumiPuppetEntity, MutsumiPuppetModel> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Wacumber.MODID, "textures/entity/mortis_puppet.png");

    public MortisPuppetRenderer(EntityRendererProvider.Context context) {
        super(context, new MortisPuppetModel(context.bakeLayer(MortisPuppetModel.LAYER_LOCATION)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(MutsumiPuppetEntity entity) {
        return TEXTURE;
    }
}
