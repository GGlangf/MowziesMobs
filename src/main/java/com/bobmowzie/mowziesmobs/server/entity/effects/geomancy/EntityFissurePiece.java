package com.bobmowzie.mowziesmobs.server.entity.effects.geomancy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EntityFissurePiece extends Entity {
    public EntityFissurePiece(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(@NotNull final SynchedEntityData.Builder builder) { }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {

    }
}
