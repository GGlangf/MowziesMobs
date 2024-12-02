package com.bobmowzie.mowziesmobs.client.render.entity;

import com.bobmowzie.mowziesmobs.MowziesMobs;
import com.bobmowzie.mowziesmobs.server.entity.effects.geomancy.EntityFissurePiece;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalDouble;

public class RenderFissurePiece extends EntityRenderer<EntityFissurePiece> {
    private static final ResourceLocation texture = new ResourceLocation(MowziesMobs.MODID, "textures/particle/crack_5_test.png");
    private static final float TEXTURE_WIDTH = 256;
    private static final float TEXTURE_HEIGHT = 32;
    private static final float PIXEL_SCALE = 1 / 16f;

    public RenderFissurePiece(EntityRendererProvider.Context mgr) {
        super(mgr);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityFissurePiece entity) {
        return texture;
    }

    private static OptionalDouble max(double... v) {
        return Arrays.stream(v).max();
    }

    @Override
    public void render(EntityFissurePiece entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        Vec3 corner0 = new Vec3(-0.25, 0, -0.25).yRot(entityYaw);
        Vec3 corner1 = new Vec3(0.25, 0, 0.25).yRot(entityYaw);
        double extent = max(corner0.x(), corner1.x(), corner0.z(), corner1.z()).orElse(1);
        Vec3 minCorner = new Vec3(-extent, -1, -extent).add(entityIn.getX(), entityIn.getY(), entityIn.getZ());
        Vec3 maxCorner = new Vec3(extent, 1, extent).add(entityIn.getX(), entityIn.getY(), entityIn.getZ());

        matrixStackIn.pushPose();
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(getTextureLocation(entityIn)));

        for(BlockPos blockpos : BlockPos.betweenClosed(BlockPos.containing(minCorner), BlockPos.containing(maxCorner))) {
            BlockState block = entityIn.level().getBlockState(blockpos.below());
            renderBlockDecal(entityIn, entityIn.level(), block, blockpos, entityIn.getX(), entityIn.getY(), entityIn.getZ(), matrixStackIn, ivertexbuilder, packedLightIn);
        }
        matrixStackIn.popPose();
    }

    private static Vec2 rotateVec2(Vec2 v, float angle) {
        return new Vec2(v.x * (float) Math.cos(angle) - v.y * (float) Math.sin(angle),
                v.x * (float) Math.sin(angle) + v.y * (float) Math.cos(angle));
    }

    /*public Optional<Vec2> getLineSegmentIntersection(BallObject ball) {
        Vec2 ballVel = ball.getVel();
        // Get the axis perpendicular to the axis the line segment runs along
        Direction perp = this.axis.getOpposite();
        // If the ball is not moving at all along the perpendicular axis, then collision cannot occur
        if (ballVel.get(perp) == 0) return null;

        Vec2 ballStart = ball.getPos();
        Vec2 colliderCenter = collidable.getPos().add(offset);
        float segmentPos = colliderCenter.get(perp);
        Vec2 intersect;
        if (ballVel.get(axis) == 0) {
            intersect = new Vec2(ballStart.get(axis), segmentPos, this.axis);
        }
        else {
            // Get the equation for this line in slope-intercept form
            float slope = ballVel.get(perp) / ballVel.get(axis);
            float intercept = ballStart.get(perp) - slope * ballStart.get(axis);
            // Ball's path can be represented by 'p = slope * a + intercept'
            // where a and p are the positions along the parallel and perpendicular segment axes.
            // Solve for a given p from the line segment's center.
            float intersectAxis = (segmentPos - intercept) / slope;
            intersect = new Vec2(intersectAxis, segmentPos, this.axis);
        }

        // If the intersection is at the ball's position (within threshold), it's not valid
        float distanceFromBall = intersect.subtract(ballStart).getLength();
        if (distanceFromBall <= DISTANCE_THRESHOLD) return null;

        // The line segments are not infinite, so make sure this intersection point lies on both
        // Check if it's on the line segment collider
        if (Math.abs(intersect.get(axis) - colliderCenter.get(axis)) > length / 2f) return null;
        // Check if it's on the ball's path.
        // We already know the points are collinear, just need to check if intersect is between ball start and ball end.
        // Use the dot products
        float dot = intersect.subtract(ballStart).dot(ballVel);
        if (dot < 0 || dot > ballVel.dot(ballVel)) return null;

        // Passed! Return the intersected collision object
        return new Collision(collidable, axis, intersect, distanceFromBall);
    }*/

    private static void renderBlockDecal(EntityFissurePiece entity, Level level, BlockState blockstate, BlockPos blockpos, double x, double y, double z, PoseStack matrixStack, VertexConsumer builder, int packedLightIn) {
        PoseStack.Pose matrixstack$entry = matrixStack.last();
        Matrix4f matrix4f = matrixstack$entry.pose();
        Matrix3f matrix3f = matrixstack$entry.normal();
        float spriteScale = 1f;
        Vec2 center = new Vec2((float) x, (float) z);
        double ex = entity.xOld + (entity.getX() - entity.xOld);
        double ey = entity.yOld + (entity.getY() - entity.yOld);
        double ez = entity.zOld + (entity.getZ() - entity.zOld);
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {

            BlockPos pos = blockpos;
            VoxelShape shape = blockstate.getBlockSupportShape(entity.level(), pos);
            float minXx = 0;
            float maxXx = 0;
            float yy = 0;
            float minZz = 0;
            float maxZz = 0;
            if (!shape.isEmpty()) {
                AABB aabb = shape.bounds();
                int bx = pos.getX(), by = pos.getY(), bz = pos.getZ();
                minXx = (float) (bx + aabb.minX - ex);
                maxXx = (float) (bx + aabb.maxX - ex);
                yy = (float) (by + aabb.minY - ey + 0.015625f);
                minZz = (float) (bz + aabb.minZ - ez);
                maxZz = (float) (bz + aabb.maxZ - ez);
                float minUu = (float) ((minXx / 2f / 1 + 0.5f) * 1);
                float maxUu = (float) ((maxXx / 2f / 1 + 0.5f) * 1);
                float minVv = (float) (minZz / 2f / 1 + 0.5f) * 1;
                float maxVv = (float) (maxZz / 2f / 1 + 0.5f) * 1;
//                drawVertex(matrix4f, matrix3f, builder, minXx, yy, minZz, minUu, minVv, 1, packedLightIn);
//                drawVertex(matrix4f, matrix3f, builder, minXx, yy, maxZz, minUu, maxVv, 1, packedLightIn);
//                drawVertex(matrix4f, matrix3f, builder, maxXx, yy, maxZz, maxUu, maxVv, 1, packedLightIn);
//                drawVertex(matrix4f, matrix3f, builder, maxXx, yy, minZz, maxUu, minVv, 1, packedLightIn);
            }

            if (blockstate.isCollisionShapeFullBlock(level, blockpos)) {
                VoxelShape voxelshape = blockstate.getShape(level, blockpos);
                if (!voxelshape.isEmpty()) {
                    float alpha = 1.0f;
                    if (alpha >= 0.0F) {
                        if (alpha > 1.0F) {
                            alpha = 1.0F;
                        }

                        double rad2 = Math.sqrt(2.0);
                        double minX = x - spriteScale * rad2;
                        double minZ = z - spriteScale * rad2;
                        double maxX = x + spriteScale * rad2;
                        double maxZ = z + spriteScale * rad2;
                        AABB aabb = voxelshape.bounds();
                        float d0 = blockpos.getX() + (float) (aabb.minX - ex);
                        float d1 = blockpos.getX() + (float) (aabb.maxX - ex);
                        float d2 = blockpos.getY() + (float) (aabb.minY - ey) + 0.015625f;
                        float d3 = blockpos.getZ() + (float) (aabb.minZ - ez);
                        float d4 = blockpos.getZ() + (float) (aabb.maxZ - ez);
//                        if (d0 < minX) d0 = (float) minX;
//                        if (d1 > maxX) d1 = (float) maxX;
//                        if (d3 < minZ) d3 = (float) minZ;
//                        if (d4 > maxZ) d4 = (float) maxZ;
                        Vec2 corners[] = new Vec2[] {
                                new Vec2(d0, d3),
                                new Vec2(d1, d3),
                                new Vec2(d1, d4),
                                new Vec2(d0, d4),
                        };

                        float u0 = 0;
                        float u1 = 1;
                        float v0 = 0;
                        float v1 = 1;
                        for (Vec2 corner : corners) {
                            Vec2 cornerRelative = rotateVec2(corner, -entity.getYRot());
                            Vec2 uv = new Vec2((cornerRelative.x / (2.0f * 1) + 0.5f) * (u1 - u0) + u0, (cornerRelative.y / (2.0f * 1) + 0.5f) * (v1 - v0) + v0);
                            drawVertex(matrix4f, matrix3f, builder, corner.x, d2, corner.y, uv.x, uv.y, alpha, packedLightIn);
                        }
                    }

                }
            }
        }
    }

    public static void drawVertex(Matrix4f matrix, Matrix3f normals, VertexConsumer vertexBuilder, float offsetX, float offsetY, float offsetZ, float textureX, float textureY, float alpha, int packedLightIn) {
        vertexBuilder.vertex(matrix, offsetX, offsetY, offsetZ).color(1, 1, 1, 1 * alpha).uv(textureX, textureY).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normals, 0.0F, 1.0F, 0.0F).endVertex();
    }
}
