package com.bobmowzie.mowziesmobs.server.entity.effects.geomancy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityFissurePiece extends Entity {
    public static final float PIECE_SIZE = 2f;
    private int growTick = 0;

    @Nullable
    private EntityFissure owner;
    @Nullable
    private UUID ownerUUID;

    public EntityFissurePiece(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide() && (getOwner() == null || getOwner().isRemoved())) discard();

        if (growTick < EntityFissure.TICKS_PER_PIECE) {
            growTick++;
        }
    }

    @Override
    protected void defineSynchedData(@NotNull final SynchedEntityData.Builder builder) { }

    public void setOwner(@Nullable EntityFissure owner) {
        this.owner = owner;
        this.ownerUUID = owner == null ? null : owner.getUUID();
    }

    @Nullable
    public EntityFissure getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel)this.level()).getEntity(this.ownerUUID);
            if (entity instanceof EntityFissure) {
                this.owner = (EntityFissure)entity;
            }
        }

        return this.owner;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("Owner")) {
            this.ownerUUID = compound.getUUID("Owner");
        }
        growTick = compound.getInt("growTick");

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.ownerUUID != null) {
            compound.putUUID("Owner", this.ownerUUID);
        }
        compound.putInt("growTick", growTick);
    }

    public int getGrowTick() {
        return growTick;
    }
}
