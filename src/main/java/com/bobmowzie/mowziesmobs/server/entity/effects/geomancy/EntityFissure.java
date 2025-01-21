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
    private float speed = 0.3f;

    public EntityFissure(EntityType<? extends EntityFissure> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void defineSynchedData(@NotNull final SynchedEntityData.Builder builder) { }

    @Override
    public void tick() {
        super.tick();
        speed = 0.5f;
        Vec3 moveVec = getForward().scale(speed);
        setDeltaMovement(moveVec);
        stepForwardTrace();
        if (tickCount > 80) remove(RemovalReason.DISCARDED);

        if (!level().isClientSide()) {
            float blocksTraveled = speed * tickCount;
            if (tickCount == 2) {//blocksTraveled % 1f == 0f) {
                EntityFissurePiece piece = new EntityFissurePiece(EntityHandler.FISSURE_PIECE.get(), level());
                piece.setPos(position());
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

        if (result.getType() == HitResult.Type.BLOCK) {
            setPos(result.getLocation());
            if (this.level() instanceof ServerLevel) {
                ((ServerLevel) this.level()).getChunkSource().broadcast(this, new ClientboundTeleportEntityPacket(this));
            }
        }
    }
}
