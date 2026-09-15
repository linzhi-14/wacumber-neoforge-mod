package top.linzhi.wacumber.client.model;

import top.linzhi.wacumber.Wacumber;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

/**
 * 墨偶模型。
 *
 * <p>与睦偶模型同构（同名部件、相同枢轴、同样三段长发），因此直接继承
 * {@link MutsumiPuppetModel} 复用全部动画（头发飘动、四肢协同摆动、Shift 坐下的 V 字坐姿）
 * 与渲染逻辑，这里只提供墨偶自己的方块数据与模型层。
 *
 * <p>长发依旧拆为独立 {@code hair} 部件（枢轴置于后脑 z=4，方块坐标相应回移）。
 */
public class MortisPuppetModel extends MutsumiPuppetModel {

    /** 模型层：wacumber:mortis_puppet#main */
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Wacumber.MODID, "mortis_puppet"), "main");

    public MortisPuppetModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // ---- 头（枢轴在颈部 y=8）----
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(33, 0).addBox(-5.0F, -8.0F, -5.0F, 10.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
                        .texOffs(74, 0).addBox(1.0F, -7.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(83, 0).addBox(-1.0F, -7.0F, -5.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(90, 0).addBox(-4.0F, -7.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(99, 0).addBox(4.0F, -7.0F, -4.0F, 1.0F, 7.0F, 9.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 17).addBox(-5.0F, -7.0F, -4.0F, 1.0F, 7.0F, 9.0F, new CubeDeformation(0.0F))
                        .texOffs(21, 17).addBox(-4.0F, -7.0F, 4.0F, 8.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(37, 3).addBox(-4.0F, -9.0F, -3.0F, 7.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 8.0F, 0.0F));

        // ---- 长发（独立部件，枢轴 z=4 位于后脑；方块 z 相应回移到 0）----
        head.addOrReplaceChild("hair", CubeListBuilder.create()
                        .texOffs(40, 17).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(59, 17).addBox(-4.5F, 3.0F, 0.0F, 9.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(82, 17).addBox(-5.0F, 7.0F, 0.0F, 10.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 4.0F));

        // ---- 身体（枢轴 y=24 对应脚底）----
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(107, 17).addBox(-3.0F, -16.0F, -2.0F, 6.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 34).addBox(-3.0F, -9.0F, -2.0F, 6.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(19, 46).addBox(2.0F, -7.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 47).addBox(1.0F, -8.0F, -3.0F, 3.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(21, 35).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                        .texOffs(37, 46).addBox(-4.0F, -7.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(57, 47).addBox(-4.0F, -8.0F, -3.0F, 3.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        // ---- 四肢（沿用原导出的层级：挂在身体下）----
        body.addOrReplaceChild("left_arm", CubeListBuilder.create()
                        .texOffs(50, 34).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(4.0F, -16.0F, 0.0F));

        body.addOrReplaceChild("right_arm", CubeListBuilder.create()
                        .texOffs(59, 34).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-4.0F, -16.0F, 0.0F));

        body.addOrReplaceChild("left_leg", CubeListBuilder.create()
                        .texOffs(68, 34).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.5F, -5.0F, 0.0F));

        body.addOrReplaceChild("right_leg", CubeListBuilder.create()
                        .texOffs(77, 34).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-1.5F, -5.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }
}
