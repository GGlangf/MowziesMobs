package com.bobmowzie.mowziesmobs.server.entity.effects.geomancy;

import com.bobmowzie.mowziesmobs.server.entity.EntityHandler;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EntityFissure extends Projectile {
    public static int TICKS_PER_PIECE = 7;
    public boolean traveling = true;

    public EntityFissure(EntityType<? extends EntityFissure> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void tick() {
        super.tick();
        if (traveling) {
            float speed = EntityFissurePiece.PIECE_SIZE / (float) TICKS_PER_PIECE;
            Vec3 moveVec = getForward().scale(speed);
            setDeltaMovement(moveVec);
            stepForwardTrace();
        }

        if (tickCount > 60) traveling = false;
        if (tickCount > 180) discard();

        if (!level().isClientSide()) {
            if (traveling && tickCount % TICKS_PER_PIECE == 1f) {
                EntityFissurePiece piece = new EntityFissurePiece(EntityHandler.FISSURE_PIECE.get(), level());
                piece.setPos(position());
                piece.setYRot(getYRot());
                piece.setOwner(this);
                level().addFreshEntity(piece);
            }
        }
        else {
            if (traveling) {
                BlockState blockBeneath = level().getBlockState(getOnPos());
                for (byte i = 0; i < 8; i++) {
                    Vec3 offset = new Vec3(0.3, 0, 0).yRot(random.nextFloat() * (float) Math.PI * 2f);
                    Vec3 vel = offset.normalize().scale(60).yRot(random.nextFloat() * 0.5f - 0.25f).add(0, random.nextDouble() * 2 + 0.5, 0);
                    level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockBeneath), getX() + offset.x - getDeltaMovement().x() * 2.2, getY(), getZ() + offset.z - getDeltaMovement().z() * 2.2, vel.x, vel.y, vel.z);
                }
            }
        }
    }

    public void stepForwardTrace() {
        Vec3 forwardPos = position().add(getDeltaMovement());
        Vec3 startPos = forwardPos.add(0, 1.1, 0);
        Vec3 endPos = forwardPos.add(0, -1.1, 0);
        BlockHitResult result = level().clip(new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

        if (result.getType() != HitResult.Type.BLOCK || result.isInside()) {
            discard();
            return;
        }
        setPos(result.getLocation());
        if (this.level() instanceof ServerLevel) {
            ((ServerLevel) this.level()).getChunkSource().broadcast(this, new ClientboundTeleportEntityPacket(this));
        }
    }
}
