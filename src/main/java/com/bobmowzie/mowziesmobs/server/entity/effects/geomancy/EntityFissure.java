package com.bobmowzie.mowziesmobs.server.entity.effects.geomancy;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class EntityFissure extends Projectile {
    private float speed = 1;

    public EntityFissure(EntityType<? extends EntityFissure> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void defineSynchedData(@NotNull final SynchedEntityData.Builder builder) { }

    @Override
    public void tick() {
        super.tick();
        Vec3 moveVec = getForward().scale(speed);
        setDeltaMovement(moveVec);
    }
}
