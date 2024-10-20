package com.bobmowzie.mowziesmobs.client.render.entity.layer;

import com.bobmowzie.mowziesmobs.client.model.entity.ModelBipedAnimated;
import com.bobmowzie.mowziesmobs.client.render.entity.MowzieGeoArmorRenderer;
import com.bobmowzie.mowziesmobs.mixin.client.HumanoidArmorLayerAccess;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class GeckoArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends HumanoidArmorLayer<T, M, A> {
    public GeckoArmorLayer(RenderLayerParent<T, M> layerParent, A innerModel, A outerModel, ModelManager modelManager) {
        super(layerParent, innerModel, outerModel, modelManager);
    }

    // FIXME 1.21 :: geckolib (mixin) hook 'InternalUtil#tryRenderGeoArmorPiece' would cause the custom 'renderArmorPiece' logic here to be skipped
    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.renderArmorPiece(poseStack, buffer, livingEntity, EquipmentSlot.CHEST, packedLight, this.getArmorModel(EquipmentSlot.CHEST), limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        this.renderArmorPiece(poseStack, buffer, livingEntity, EquipmentSlot.LEGS, packedLight, this.getArmorModel(EquipmentSlot.LEGS), limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        this.renderArmorPiece(poseStack, buffer, livingEntity, EquipmentSlot.FEET, packedLight, this.getArmorModel(EquipmentSlot.FEET), limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        this.renderArmorPiece(poseStack, buffer, livingEntity, EquipmentSlot.HEAD, packedLight, this.getArmorModel(EquipmentSlot.HEAD), limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }

    // FIXME 1.21 :: in the latest neoforge version these other variables are passed on to the armor piece renderer (supposedly for animation)
    protected void renderArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource, T livingEntity, EquipmentSlot slot, int packedLight, A p_model, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = livingEntity.getItemBySlot(slot);
        if (itemstack.getItem() instanceof ArmorItem armoritem) {
            if (armoritem.getEquipmentSlot() == slot) {
                this.getParentModel().copyPropertiesTo(p_model);
                this.setPartVisibility(p_model, slot);
                Model model = getArmorModelHook(livingEntity, itemstack, slot, p_model);
                boolean flag = this.usesInnerModel(slot);
                ArmorMaterial armormaterial = armoritem.getMaterial().value();

                IClientItemExtensions extensions = IClientItemExtensions.of(itemstack);
                extensions.setupModelAnimations(livingEntity, itemstack, slot, model, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
                int fallbackColor = extensions.getDefaultDyeColor(itemstack);
                ModelBipedAnimated.setUseMatrixMode(p_model, true); // Custom logic

                for (int layerIdx = 0; layerIdx < armormaterial.layers().size(); layerIdx++) {
                    ArmorMaterial.Layer armormaterial$layer = armormaterial.layers().get(layerIdx);
                    int j = extensions.getArmorLayerTintColor(itemstack, livingEntity, armormaterial$layer, layerIdx, fallbackColor);
                    if (j != 0) {
                        var texture = ClientHooks.getArmorTexture(livingEntity, itemstack, armormaterial$layer, flag, slot);
                        this.renderModel(poseStack, bufferSource, packedLight, model, j, texture);
                    }
                }

                ArmorTrim armortrim = itemstack.get(DataComponents.TRIM);
                if (armortrim != null) {
                    ModelBipedAnimated.setUseMatrixMode(p_model, true); // Custom logic
                    ((HumanoidArmorLayerAccess) this).mowziesmobs$renderTrim(armoritem.getMaterial(), poseStack, bufferSource, packedLight, armortrim, model, flag);
                }

                if (itemstack.hasFoil()) {
                    ModelBipedAnimated.setUseMatrixMode(p_model, true); // Custom logic
                    // FIXME 1.21 :: access transformer seems to have problems
                    ((HumanoidArmorLayerAccess) this).mowziesmobs$renderGlint(poseStack, bufferSource, packedLight, model);
                }
            }
        }
    }

    private void renderModel(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Model model, int color, ResourceLocation armorResource) {
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.armorCutoutNoCull(armorResource));

        // Custom logic
        if (model instanceof MowzieGeoArmorRenderer<?> mowzieRenderer) {
            mowzieRenderer.usingCustomPlayerAnimations = true;
        }

        model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, color);
    }
}
