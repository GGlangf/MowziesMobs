package com.bobmowzie.mowziesmobs.server.entity.bluff;

import com.bobmowzie.mowziesmobs.client.particle.AdvancedTerrainParticle;
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
import com.bobmowzie.mowziesmobs.server.entity.EntityHandler;
import com.bobmowzie.mowziesmobs.server.entity.MowzieEntity;
import com.bobmowzie.mowziesmobs.server.entity.MowzieGeckoEntity;
import com.bobmowzie.mowziesmobs.server.entity.effects.geomancy.EntityFissure;
import com.bobmowzie.mowziesmobs.server.loot.LootTableHandler;
import com.bobmowzie.mowziesmobs.server.potion.EffectGeomancy;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.EnumSet;

public class EntityBluff extends MowzieGeckoEntity {
    private float allowedHeightOffset = 0.5F;
    private int nextHeightOffsetChangeTick;

    @OnlyIn(Dist.CLIENT)
    public Vec3[] feetPos;
    @OnlyIn(Dist.CLIENT)
    public Vec3[] corePos;

    // -- ABILITIES -- //
    public static final AbilityType<EntityBluff, HurtAbility<EntityBluff>> HURT_ABILITY = new AbilityType<>("bluff_hurt", (type, entity) -> new HurtAbility<>(type, entity, RawAnimation.begin().thenPlay("hurt"), 5, 0));
    public static final AbilityType<EntityBluff, DieAbility<EntityBluff>> DIE_ABILITY = new AbilityType<>("bluff_die", (type, entity) -> new DieAbility<>(type, entity, RawAnimation.begin().thenPlay("death"), 30));
    public static final AbilityType<EntityBluff, BluffAttackAbility> ATTACK_ABILITY = new AbilityType<>("bluff_attack", BluffAttackAbility::new);

    public EntityBluff(EntityType<? extends MowzieEntity> type, Level world) {
        super(type, world);
        this.xpReward = 14;
        if (world.isClientSide) {
            feetPos = new Vec3[]{new Vec3(0, 0, 0)};
            corePos = new Vec3[]{new Vec3(0, 0, 0)};
        }
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
        goalSelector.addGoal(2, new UseAbilityAI<>(this, ATTACK_ABILITY));
        this.goalSelector.addGoal(1, new UseAbilityAI<>(this, DIE_ABILITY));
        this.goalSelector.addGoal(2, new UseAbilityAI<>(this, HURT_ABILITY, false));

        this.goalSelector.addGoal(4, new BluffAttackGoal(this));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
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
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.MOVEMENT_SPEED, 0.23f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3f)
                .add(Attributes.FOLLOW_RANGE, 32);
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

        if (this.level().isClientSide && isAlive()) {
            if (feetPos != null && feetPos.length > 0) {
                feetPos[0] = position().add(0, 0.05f, 0);
                if (tickCount % 4 == 0) {
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.RING2.get(), feetPos[0].x(), feetPos[0].y(), feetPos[0].z(), 0, 0, 0, false, 0, Math.PI/2f, 0, 0, 1.5F, 0.83f, 1, 0.39f, 1, 1, 20, true, false, new ParticleComponent[]{
                            new ParticleComponent.PinLocation(feetPos),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0f), false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(1f, 7f), false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.POS_Y, ParticleComponent.KeyTrack.startAndEnd(0f, 1.5f), true)
                    });
                }
            }
            BlockState state = level().getBlockState(getOnPos());
            if (EffectGeomancy.isBlockUseable(state) && feetPos != null && feetPos.length > 0) {
                if (tickCount % 2 == 0) {
                    Vec3 pos = new Vec3(1, 0, 0).yRot((float) (random.nextDouble() * Math.PI * 2.0)).scale(random.nextFloat());
                    float phaseOffset = random.nextFloat();
                    float scale = (float)random.nextGaussian() * 0.2f + 0.3f;
                    AdvancedTerrainParticle.spawnTerrainParticle(level(), ParticleHandler.TERRAIN.get(), getX() + pos.x(), getY() + pos.y() + 1, getZ() + pos.z(), 0, 0 ,0, 0, 1f, 1f, 25 + random.nextFloat() * 5, state, new ParticleComponent[]{
                            new ParticleComponent.Orbit(feetPos, ParticleComponent.KeyTrack.startAndEnd(0 + phaseOffset, 0.8f + phaseOffset), ParticleComponent.KeyTrack.startAndEnd(random.nextFloat() * 0.75f, 0.1f + random.nextFloat()), ParticleComponent.constant(0), ParticleComponent.constant(1), ParticleComponent.constant(0), false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.POS_Y, ParticleComponent.KeyTrack.startAndEnd(0f, 1.1f), true),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, new ParticleComponent.KeyTrack(
                                    new float[]{0, scale, scale, 0},
                                    new float[]{0, 0.1f, 0.9f, 1}
                            ), false)
                    });
                }
            }
        }

        if (getActiveAbility() == null && tickCount % 120 == 0){
            sendAbilityMessage(ATTACK_ABILITY);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (source == damageSources().fall()) return false;
        return super.hurt(source, damage);
    }

    public void aiStep() {
        if (!this.onGround() && this.getDeltaMovement().y < 0.0D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
        }

        super.aiStep();
    }

    protected void customServerAiStep() {
        boolean isDoingAttack = getActiveAbilityType() == ATTACK_ABILITY;
        if (!isDoingAttack) {
            --this.nextHeightOffsetChangeTick;
            if (this.nextHeightOffsetChangeTick <= 0) {
                this.nextHeightOffsetChangeTick = 100;
                this.allowedHeightOffset = (float) this.random.triangle(0.5D, 6.891D);
            }

            LivingEntity livingentity = this.getTarget();
            if (livingentity != null && livingentity.getEyeY() > this.getEyeY() + (double) this.allowedHeightOffset && this.canAttack(livingentity)) {
                Vec3 vec3 = this.getDeltaMovement();
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, ((double) 0.3F - vec3.y) * (double) 0.3F, 0.0D));
                this.hasImpulse = true;
            }
        }

        super.customServerAiStep();
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return LootTableHandler.BLUFF;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    public static class BluffAttackAbility extends Ability<EntityBluff> {
        public static AbilitySection[] SECTION_TRACK = new AbilitySection[] {
            new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.STARTUP, 11),
            new AbilitySection.AbilitySectionInfinite(AbilitySection.AbilitySectionType.MISC),
            new AbilitySection.AbilitySectionInstant(AbilitySection.AbilitySectionType.ACTIVE),
            new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.RECOVERY, 34)
        };

        public BluffAttackAbility(AbilityType abilityType, EntityBluff user) {
            super(abilityType, user, SECTION_TRACK);
        }

        private static final RawAnimation ATTACK_START_ANIMATION = RawAnimation.begin().then("attack_start", Animation.LoopType.HOLD_ON_LAST_FRAME);
        private static final RawAnimation ATTACK_END_ANIMATION = RawAnimation.begin().then("attack_end", Animation.LoopType.HOLD_ON_LAST_FRAME);

        @Override
        public void start() {
            super.start();
            playAnimation(ATTACK_START_ANIMATION);
        }

        @Override
        public void tickUsing() {
            super.tickUsing();
            if (getCurrentSection().sectionType == AbilitySection.AbilitySectionType.STARTUP) {
                getUser().setDeltaMovement(0, 0, 0);
            }
            if (getCurrentSection().sectionType == AbilitySection.AbilitySectionType.MISC) {
                double fallSpeed = getUser().getDeltaMovement().y;
                fallSpeed -= 2;
                fallSpeed = Math.max(fallSpeed, -7);
                getUser().setDeltaMovement(0, fallSpeed, 0);
                getUser().hasImpulse = true;
                if (getUser().onGround()) {
                    jumpToSection(2);
                }
            }
        }

        @Override
        public <E extends GeoEntity> PlayState animationPredicate(AnimationState<E> e, GeckoPlayer.Perspective perspective) {
            if (getCurrentSection().sectionType == AbilitySection.AbilitySectionType.STARTUP) {
                e.getController().transitionLength(4);
            }
            return super.animationPredicate(e, perspective);
        }

        @Override
        protected void beginSection(AbilitySection section) {
            super.beginSection(section);
            if (section.sectionType == AbilitySection.AbilitySectionType.ACTIVE) {
                if (!getLevel().isClientSide()) {
                    EntityFissure fissure = new EntityFissure(EntityHandler.FISSURE.get(), getLevel());
                    fissure.setOwner(getUser());
                    fissure.setPos(getUser().position().add(0, 0, 0));
                    fissure.setYRot(getUser().getYRot());
                    getLevel().addFreshEntity(fissure);
                }

                playAnimation(ATTACK_END_ANIMATION);
                if (getLevel().isClientSide()) {
                    BlockState blockBeneath = getUser().level().getBlockState(getUser().getOnPos());
                    for (byte i = 0; i < 30; i++) {
                        getLevel().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockBeneath), getUser().getX(), getUser().getBlockY() + 0.1f, getUser().getZ(), getUser().random.nextFloat() * 3f - 1.5f, 2.2d, getUser().random.nextFloat() * 3f - 1.5f);
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

    static class BluffAttackGoal extends Goal {
        private final EntityBluff bluff;
        private int attackStep;
        private int attackTime;
        private int lastSeen;

        public BluffAttackGoal(EntityBluff bluff) {
            this.bluff = bluff;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.bluff.getTarget();
            return livingentity != null && livingentity.isAlive() && this.bluff.canAttack(livingentity);
        }

        public void start() {
            this.attackStep = 0;
        }

        public void stop() {
            this.lastSeen = 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            --this.attackTime;
            LivingEntity livingentity = this.bluff.getTarget();
            if (livingentity != null) {
                boolean flag = this.bluff.getSensing().hasLineOfSight(livingentity);
                if (flag) {
                    this.lastSeen = 0;
                } else {
                    ++this.lastSeen;
                }

                double d0 = this.bluff.distanceToSqr(livingentity);
                if (d0 < this.getFollowDistance() * this.getFollowDistance() && flag) {
                    double d1 = livingentity.getX() - this.bluff.getX();
                    double d2 = livingentity.getY(0.5D) - this.bluff.getY(0.5D);
                    double d3 = livingentity.getZ() - this.bluff.getZ();
                    if (this.attackTime <= 0) {
                        ++this.attackStep;
                        if (this.attackStep == 1) {
                            this.attackTime = 60;
                        } else if (this.attackStep <= 2) {
                            this.attackTime = 6;
                        } else {
                            this.attackTime = 100;
                            this.attackStep = 0;
                        }

                        if (this.attackStep > 1) {
                            bluff.allowedHeightOffset = 1;
                            bluff.sendAbilityMessage(ATTACK_ABILITY);
                        }
                    }

                    this.bluff.getLookControl().setLookAt(livingentity, 10.0F, 10.0F);
                } else if (this.lastSeen < 5) {
                    this.bluff.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0D);
                }

                super.tick();
            }
        }

        private double getFollowDistance() {
            return 12;
        }
    }
}


