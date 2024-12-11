package com.bobmowzie.mowziesmobs.server.entity.effects.geomancy;

import com.bobmowzie.mowziesmobs.client.model.tools.geckolib.MowzieAnimationController;
import com.bobmowzie.mowziesmobs.server.capability.AbilityCapability;
import com.bobmowzie.mowziesmobs.server.capability.CapabilityHandler;
import com.bobmowzie.mowziesmobs.server.capability.FrozenCapability;
import com.bobmowzie.mowziesmobs.server.entity.MowzieGeckoEntity;
import com.bobmowzie.mowziesmobs.server.entity.effects.EntityMagicEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class EntityEarthSpike extends EntityGeomancyBase {
    protected MowzieAnimationController<EntityEarthSpike> controller = new MowzieAnimationController<>(this, "controller", 0, this::predicate, 0);

    public EntityEarthSpike(EntityType<? extends EntityMagicEffect> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityEarthSpike(EntityType<? extends EntityMagicEffect> type, Level worldIn, LivingEntity caster, BlockState blockState) {
        super(type, worldIn, caster, blockState, null);
        setDeathTime(180);
    }

    @Override
    public void tick() {
        super.tick();
        setDoRemoveTimer(true);
//        if (tickCount > 180) explode();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(controller);
    }

    private static RawAnimation EMERGE = RawAnimation.begin().thenPlay("emerge");
    protected <E extends GeoEntity> PlayState predicate(AnimationState<E> event) {
        event.getController().setAnimation(EMERGE);
        return PlayState.CONTINUE;
    }
}
