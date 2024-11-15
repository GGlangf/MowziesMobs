package com.bobmowzie.mowziesmobs.server.entity.effects.geomancy;

import com.bobmowzie.mowziesmobs.server.entity.effects.EntityMagicEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class EntityEarthSpike extends EntityGeomancyBase{
    public EntityEarthSpike(EntityType<? extends EntityMagicEffect> type, Level worldIn) {
        super(type, worldIn);
    }
}
