package com.bobmowzie.mowziesmobs.server.entity.effects.geomancy;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityFissure extends Projectile {
    private float speed = 1;

    public EntityFissure(EntityType<? extends EntityFissure> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void tick() {
        super.tick();
        Vec3 moveVec = getForward().scale(speed);
        setDeltaMovement(moveVec);
    }
}
