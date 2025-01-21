package com.bobmowzie.mowziesmobs.server.entity.effects.geomancy;

import com.bobmowzie.mowziesmobs.server.entity.EntityHandler;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class EntityFissure extends Projectile {
    public static int TICKS_PER_PIECE = 7;

    public EntityFissure(EntityType<? extends EntityFissure> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void defineSynchedData(@NotNull final SynchedEntityData.Builder builder) { }

    @Override
    public void tick() {
        super.tick();
        TICKS_PER_PIECE = 7;
        float speed = EntityFissurePiece.PIECE_SIZE / (float) TICKS_PER_PIECE;
        Vec3 moveVec = getForward().scale(speed);
        setDeltaMovement(moveVec);
        stepForwardTrace();
        if (tickCount > 80) discard();

        if (!level().isClientSide()) {
            if (tickCount % TICKS_PER_PIECE == 1f) {
                EntityFissurePiece piece = new EntityFissurePiece(EntityHandler.FISSURE_PIECE.get(), level());
                piece.setPos(position());
                piece.setYRot(getYRot());
                piece.setOwner(this);
                level().addFreshEntity(piece);
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
