package top.linzhi.wacumber.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import top.linzhi.wacumber.Wacumber;
import top.linzhi.wacumber.entity.MutsumiPuppetEntity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * 睦偶模型（由 Blockbench 导出后整理）。
 *
 * <p>相比原始导出做了这些修正：
 * <ol>
 *   <li>补齐全部 import、类名规范化为 {@code MutsumiPuppetModel}；</li>
 *   <li>{@link #LAYER_LOCATION} 命名空间由 {@code modid} 改为 {@code wacumber}；</li>
 *   <li>把原本混在 {@code head} 里的三段长发拆成独立部件 {@code hair}
 *       （枢轴放在后脑 z=4，方块坐标相应回移，位置不变），以便单独做飘动动画；</li>
 *   <li>{@code renderToBuffer} 使用 1.21 的签名（颜色参数为 int）。</li>
 * </ol>
 *
 * <p>坐标沿用原导出：头 y 0~8、身体 y 8~18、腿 y 18~24（模型总高 24 像素 = 1.5 格）。
 */
public class MutsumiPuppetModel extends EntityModel<MutsumiPuppetEntity> {

    /** 模型层：wacumber:mutsumi_puppet#main */
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Wacumber.MODID, "mutsumi_puppet"), "main");

    // ---- 坐姿参数（想微调"坐在地上"的效果改这里）----
    /** body 的原始 pivot y */
    private static final float BODY_PIVOT_Y = 24.0F;
    /** head 的原始 pivot y */
    private static final float HEAD_PIVOT_Y = 8.0F;
    /** 坐下时整体下沉的像素（模型 y 向下为正） */
    private static final float SIT_SINK = 5.0F;
    /** 坐下时双腿完全水平前伸（90°），使脚与屁股处于同一水平面，整条腿贴地 */
    private static final float SIT_LEG_FORWARD = (float) (Math.PI / 2.0);
    /** 坐下时双腿从髋部向外张开的弧度（V 字的一半夹角）；若张开方向相反，把下面两次赋值的符号对调 */
    private static final float SIT_LEG_SPREAD = 0.70F;

    // 子类（墨偶模型）复用这些部件与动画，故为 protected
    protected final ModelPart head;
    protected final ModelPart hair;
    protected final ModelPart body;
    protected final ModelPart leftArm;
    protected final ModelPart rightArm;
    protected final ModelPart leftLeg;
    protected final ModelPart rightLeg;

    public MutsumiPuppetModel(ModelPart root) {
        this.head = root.getChild("head");
        this.hair = this.head.getChild("hair");
        this.body = root.getChild("body");
        this.leftArm = this.body.getChild("left_arm");
        this.rightArm = this.body.getChild("right_arm");
        this.leftLeg = this.body.getChild("left_leg");
        this.rightLeg = this.body.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // ---- 头（枢轴在颈部 y=8）----
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(33, 0).addBox(1.0F, -8.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(42, 0).addBox(-1.0F, -8.0F, -5.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(49, 0).addBox(-4.0F, -8.0F, -5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(58, 0).addBox(4.0F, -8.0F, -4.0F, 1.0F, 8.0F, 9.0F, new CubeDeformation(0.0F))
                        .texOffs(79, 0).addBox(-5.0F, -8.0F, -4.0F, 1.0F, 8.0F, 9.0F, new CubeDeformation(0.0F))
                        .texOffs(100, 0).addBox(-4.0F, -8.0F, 4.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 8.0F, 0.0F));

        // ---- 长发（独立部件，挂在头下；枢轴 z=4 位于后脑，便于绕发根摆动）----
        head.addOrReplaceChild("hair", CubeListBuilder.create()
                        .texOffs(0, 18).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(19, 18).addBox(-4.5F, 3.0F, 0.0F, 9.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(42, 18).addBox(-5.0F, 7.0F, 0.0F, 10.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 4.0F));

        // ---- 身体（枢轴 y=24 对应脚底）----
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(67, 18).addBox(-3.0F, -16.0F, -2.0F, 6.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(88, 18).addBox(-3.0F, -9.0F, -2.0F, 6.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 30).addBox(-4.0F, -8.0F, -3.0F, 8.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        // ---- 四肢（沿用原导出的层级：挂在身体下）----
        body.addOrReplaceChild("left_arm", CubeListBuilder.create()
                        .texOffs(29, 30).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(4.0F, -16.0F, 0.0F));

        body.addOrReplaceChild("right_arm", CubeListBuilder.create()
                        .texOffs(38, 30).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-4.0F, -16.0F, 0.0F));

        body.addOrReplaceChild("left_leg", CubeListBuilder.create()
                        .texOffs(47, 30).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.5F, -6.0F, 0.0F));

        body.addOrReplaceChild("right_leg", CubeListBuilder.create()
                        .texOffs(56, 30).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-1.5F, -6.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(MutsumiPuppetEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        // 头部朝向（跟随视线 / 看向玩家）
        this.head.yRot = netHeadYaw * (float) (Math.PI / 180.0);
        this.head.xRot = headPitch * (float) (Math.PI / 180.0);

        // 头发飘动：静止时也有细微起伏，移动时摆幅增大并随步频摆动
        float idleWave = Mth.cos(ageInTicks * 0.09F) * 0.12F;
        float walkWave = Mth.cos(limbSwing * 0.6662F) * 0.25F * limbSwingAmount;
        this.hair.xRot = 0.10F + idleWave + walkWave;
        // 轻微的左右甩动，让飘动更自然
        this.hair.zRot = Mth.sin(ageInTicks * 0.06F) * 0.06F;

        // 四肢协同摇摆（对角同相：左臂 ↔ 右腿）
        float swingA = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        float swingB = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;

        if (entity.isInSittingPose()) {
            // ---- 坐姿：双腿向前平放，并从髋部向外张开成 V 字，身体与头一起下沉 ----
            this.body.y = BODY_PIVOT_Y + SIT_SINK;
            this.head.y = HEAD_PIVOT_Y + SIT_SINK;

            // 水平前伸 + 绕髋部外旋 → 从正面看就是 V 字形
            this.leftLeg.xRot = -SIT_LEG_FORWARD;
            this.rightLeg.xRot = -SIT_LEG_FORWARD;
            this.leftLeg.zRot = -SIT_LEG_SPREAD;
            this.rightLeg.zRot = SIT_LEG_SPREAD;

            // 手臂自然垂在体侧、略向前
            this.leftArm.xRot = -0.20F;
            this.rightArm.xRot = -0.20F;
            this.leftArm.zRot = -0.12F;
            this.rightArm.zRot = 0.12F;
        } else {
            // ---- 站立 / 行走：恢复正常 pivot 与四肢摇摆 ----
            this.body.y = BODY_PIVOT_Y;
            this.head.y = HEAD_PIVOT_Y;

            this.rightArm.xRot = swingA;
            this.leftArm.xRot = swingB;
            this.rightLeg.xRot = swingB;
            this.leftLeg.xRot = swingA;
            this.leftArm.zRot = 0.0F;
            this.rightArm.zRot = 0.0F;
            this.leftLeg.zRot = 0.0F;
            this.rightLeg.zRot = 0.0F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, int color) {
        // body 的子级（四肢）与 head 的子级（hair）会被递归渲染
        this.head.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.body.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
