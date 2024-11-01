package com.bobmowzie.mowziesmobs.server.entity.bluff;

import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.bobmowzie.mowziesmobs.client.particle.util.AdvancedParticleBase;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent;
import com.bobmowzie.mowziesmobs.client.render.entity.player.GeckoPlayer;
import com.bobmowzie.mowziesmobs.server.ability.Ability;
import com.bobmowzie.mowziesmobs.server.ability.AbilitySection;
import com.bobmowzie.mowziesmobs.server.ability.AbilityType;
import com.bobmowzie.mowziesmobs.server.ability.abilities.mob.DieAbility;
import com.bobmowzie.mowziesmobs.server.ability.abilities.mob.HurtAbility;
import com.bobmowzie.mowziesmobs.server.ai.UseAbilityAI;
import com.bobmowzie.mowziesmobs.server.entity.MowzieEntity;
import com.bobmowzie.mowziesmobs.server.entity.MowzieGeckoEntity;
import com.bobmowzie.mowziesmobs.server.entity.sculptor.EntitySculptor;
import com.bobmowzie.mowziesmobs.server.sound.MMSounds;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.scores.Team;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class EntityBluff extends MowzieGeckoEntity {

    // -- ABILITIES -- //

    public static final AbilityType<EntityBluff, HurtAbility<EntityBluff>> HURT_ABILITY = new AbilityType<>("bluff_hurt", (type, entity) -> new HurtAbility<>(type, entity, RawAnimation.begin().thenPlay("hurt"), 5, 0));
    public static final AbilityType<EntityBluff, DieAbility<EntityBluff>> DIE_ABILITY = new AbilityType<>("bluff_die", (type, entity) -> new DieAbility<>(type, entity, RawAnimation.begin().thenPlay("death"), 30));
    public static final AbilityType<EntityBluff, BluffAttackAbility> ATTACK_ABILITY = new AbilityType<>("bluff_attack", BluffAttackAbility::new);
    public EntityBluff(EntityType<? extends MowzieEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public AbilityType getHurtAbility() {
        return HURT_ABILITY;
    }

    @Override
    public AbilityType getDeathAbility() {
        return DIE_ABILITY;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F, 0.06f) {
            public void start() {
                this.lookTime = this.adjustedTickDelay(80 + this.mob.getRandom().nextInt(80));
            }
        });
        goalSelector.addGoal(2, new UseAbilityAI<>(this, ATTACK_ABILITY, false));
        this.goalSelector.addGoal(1, new UseAbilityAI<>(this, DIE_ABILITY));
        this.goalSelector.addGoal(2, new UseAbilityAI<>(this, HURT_ABILITY, false));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }


    @Override
    protected <E extends GeoEntity> void loopingAnimations(AnimationState<E> event) {
        super.loopingAnimations(event);
        event.getController().transitionLength(5);
    }

    @Override
    public AbilityType<?, ?>[] getAbilities() {
        return new AbilityType[] {HURT_ABILITY, DIE_ABILITY, ATTACK_ABILITY};
    }

    public static AttributeSupplier.Builder createAttributes() {
        return MowzieEntity.createAttributes().add(Attributes.ATTACK_DAMAGE, 8)
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.3f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3f)
                .add(Attributes.FOLLOW_RANGE, 20);
    }


    @Override
    public void tick() {
        super.tick();

        if (getActiveAbilityType() == DIE_ABILITY){
            this.yBodyRot = this.yHeadRot = this.yRotO;
            if (level().isClientSide){
                for (int i = 0; i < 3; i++) {
                    if (random.nextFloat() < 0.1f) {
                        AdvancedParticleBase.spawnParticle(level(), ParticleHandler.PIXEL.get(), getRandomX(0.4f), getY() + 1f, getRandomZ(0.4f), 0f, random.nextFloat() / 15f, 0f, true, 0f, 0, 0f, 0, 2 + (random.nextFloat()*2f), 163d / 256d, 247d / 256d, 74d / 256d, 0.5, 0.9, 17 + random.nextFloat() * 10, true, true, new ParticleComponent[]{
                                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, new ParticleComponent.KeyTrack(
                                        new float[]{1f, 0},
                                        new float[]{0.0f, 1}
                                ), false),
                                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.MOTION_Y, new ParticleComponent.KeyTrack(
                                        new float[]{0.1f, 0},
                                        new float[]{0.0f, 1}
                                ), false)
                        });
                    }
                }
            }
        }


        if (getTarget() != null) {
            LivingEntity target = getTarget();

            if (getActiveAbility() == null && targetDistance < 5.0f && random.nextInt(5) == 0){
                sendAbilityMessage(ATTACK_ABILITY);
            }
        }
    }

    public static class BluffAttackAbility extends Ability<EntityBluff> {
        public static AbilitySection[] SECTION_TRACK = new AbilitySection[] {
            new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.STARTUP, 1),
                    new AbilitySection.AbilitySectionInstant(AbilitySection.AbilitySectionType.ACTIVE),
                    new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.RECOVERY, 1)
        };

        public BluffAttackAbility(AbilityType abilityType, EntityBluff user) {
            super(abilityType, user, SECTION_TRACK);
        }

        private static final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE);

        @Override
        public void start() {
            super.start();
            playAnimation(ATTACK_ANIMATION);
        }

        @Override
        public <E extends GeoEntity> PlayState animationPredicate(AnimationState<E> e, GeckoPlayer.Perspective perspective) {
            e.getController().transitionLength(5);
            return super.animationPredicate(e, perspective);

        }

        @Override
        protected void beginSection(AbilitySection section) {
            super.beginSection(section);
            getUser().getNavigation().stop();
            getUser().setDeltaMovement(0d,0d,0d);
            if (section.sectionType == AbilitySection.AbilitySectionType.ACTIVE) {
                EntityBluff entity = getUser();
                BlockState blockBeneath = getUser().level().getBlockState(getUser().getBlockPosBelowThatAffectsMyMovement());

                if(getUser().level().isClientSide()){
                    for (byte i = 0; i < 80; i++){
                        getUser().level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockBeneath), getUser().getX(), getUser().getBlockY() + 0.1f, getUser().getZ(), getUser().random.nextFloat()*3f - 1.5f, 2.2d,getUser().random.nextFloat()*3f - 1.5f);
                        }
                    }
                }
        }
    }

    public static class BluffChaseAbility extends Ability<EntityBluff> {
        public static AbilitySection[] SECTION_TRACK = new AbilitySection[] {
                new AbilitySection.AbilitySectionInfinite(AbilitySection.AbilitySectionType.MISC),
                new AbilitySection.AbilitySectionInstant(AbilitySection.AbilitySectionType.RECOVERY)
        };

        public BluffChaseAbility(AbilityType abilityType, EntityBluff user) {
            super(abilityType, user, SECTION_TRACK);
        }

        private static final RawAnimation WALK = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);

        @Override
        public void start() {
            super.start();
            playAnimation(WALK);
        }

        @Override
        public <E extends GeoEntity> PlayState animationPredicate(AnimationState<E> e, GeckoPlayer.Perspective perspective) {
            e.getController().transitionLength(5);
            return super.animationPredicate(e, perspective);

        }

        @Override
        protected void beginSection(AbilitySection section) {
            super.beginSection(section);

            if (section.sectionType == AbilitySection.AbilitySectionType.MISC && getUser().getTarget() != null) {
                EntityBluff entity = getUser();
                LivingEntity target = getUser().getTarget();

                if (entity.tickCount % 5 == 0){
                    entity.moveTo(target.position());
                }

                if (entity.distanceTo(target) < 5f){

                }
            }
        }
    }
}


