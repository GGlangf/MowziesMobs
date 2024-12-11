package com.bobmowzie.mowziesmobs.client.render.entity;

import com.bobmowzie.mowziesmobs.client.model.entity.ModelEarthSpike;
import com.bobmowzie.mowziesmobs.client.model.entity.ModelPillar;
import com.bobmowzie.mowziesmobs.client.model.tools.geckolib.MowzieGeoBone;
import com.bobmowzie.mowziesmobs.client.model.tools.geckolib.MowzieGeoModel;
import com.bobmowzie.mowziesmobs.client.render.entity.layer.GeckoBlockLayer;
import com.bobmowzie.mowziesmobs.client.render.entity.layer.ItemLayerSculptorStaff;
import com.bobmowzie.mowziesmobs.server.entity.effects.geomancy.EntityEarthSpike;
import com.bobmowzie.mowziesmobs.server.entity.effects.geomancy.EntityGeomancyBase;
import com.bobmowzie.mowziesmobs.server.entity.effects.geomancy.EntityPillar;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import software.bernie.geckolib.util.RenderUtils;

@OnlyIn(Dist.CLIENT)
public class RenderEarthSpike extends RenderGeomancyBase<EntityEarthSpike> {
    private static final ResourceLocation TEXTURE_DIRT = new ResourceLocation("textures/block/dirt.png");

    public RenderEarthSpike(EntityRendererProvider.Context mgr) {
        super(mgr, new ModelEarthSpike());
        this.addRenderLayer(new GeckoBlockLayer<>(this,
                (bone, animatable) -> {
                    if (bone.getName().contains("block")) {
                        return animatable.getBlock();
                    }
                    return null;
                }));
    }

    @Override
    public ResourceLocation getTextureLocation(EntityEarthSpike entity) {
        return TEXTURE_DIRT;
    }

    @Override
    public void render(EntityEarthSpike entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected void applyRotations(EntityEarthSpike animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        rotationYaw = Mth.rotLerp(partialTick, animatable.yRotO, animatable.getYRot());
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
    }

    @Override
    public void renderCube(PoseStack poseStack, GeoCube cube, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

    }
}
