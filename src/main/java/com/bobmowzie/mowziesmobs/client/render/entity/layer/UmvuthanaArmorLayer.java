package com.bobmowzie.mowziesmobs.client.render.entity.layer;

import com.bobmowzie.mowziesmobs.server.entity.umvuthana.EntityUmvuthana;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtil;

public class UmvuthanaArmorLayer extends GeoRenderLayer<EntityUmvuthana> {
    private final HumanoidModel defaultBipedModel;

    public UmvuthanaArmorLayer(GeoRenderer<EntityUmvuthana> entityRendererIn, EntityRendererProvider.Context context) {
        super(entityRendererIn);
        defaultBipedModel = new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
    }

    @Override
    public void renderForBone(PoseStack poseStack, EntityUmvuthana animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        super.renderForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        if (bone.isHidden()) return;
        poseStack.pushPose();
        RenderUtil.translateToPivotPoint(poseStack, bone);
        String boneName = "maskTwitcher";
        String handBoneName = "maskHand";
        if (bone.getName().equals(boneName) || bone.getName().equals(handBoneName)) {
            renderArmor(animatable, bufferSource, poseStack, packedLight);
        }
        bufferSource.getBuffer(renderType);
        poseStack.popPose();
    }

    private void renderArmor(LivingEntity entityLivingBaseIn, MultiBufferSource bufferIn, PoseStack poseStack, int packedLightIn) {
        ItemStack itemStack = entityLivingBaseIn.getItemBySlot(EquipmentSlot.HEAD);
        if (itemStack.getItem() instanceof ArmorItem armoritem) {
            if (armoritem.getType() == ArmorItem.Type.HELMET) {
                boolean glintIn = itemStack.hasFoil();
                HumanoidModel<?> model = getArmorModelHook(entityLivingBaseIn, itemStack, EquipmentSlot.HEAD, defaultBipedModel);;
                // FIXME 1.21 :: should all layers go through this?
                armoritem.getMaterial().value().layers().forEach(layer -> {
                    // FIXME 1.21 :: is inner model = false correct? (HumanoidArmorLayer has a method to check but it's only valid for Leggings)
                    ResourceLocation armorTexture = armoritem.getArmorTexture(itemStack, entityLivingBaseIn, EquipmentSlot.HEAD, layer, false);
                    if (armorTexture != null) {
                        VertexConsumer ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(armorTexture), false, glintIn);
                        poseStack.mulPose((new Quaternionf()).rotationXYZ(0.0F, 0.0F, (float) Math.PI));
                        poseStack.scale(1.511f, 1.511f, 1.511f);
                        poseStack.translate(0, -0.55, 0.15);
                        model.renderToBuffer(poseStack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
                    }
                });
            }
        }
    }

    protected HumanoidModel<?> getArmorModelHook(LivingEntity entity, ItemStack itemStack, EquipmentSlot slot, HumanoidModel model) {
        Model basicModel = ClientHooks.getArmorModel(entity, itemStack, slot, model);
        return basicModel instanceof HumanoidModel ? (HumanoidModel<?>) basicModel : model;
    }
}
