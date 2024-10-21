package com.bobmowzie.mowziesmobs.server.capability;

import com.bobmowzie.mowziesmobs.client.render.entity.player.GeckoPlayer;
import com.bobmowzie.mowziesmobs.server.ability.Ability;
import com.bobmowzie.mowziesmobs.server.ability.AbilityHandler;
import com.bobmowzie.mowziesmobs.server.ability.PlayerAbility;
import com.bobmowzie.mowziesmobs.server.config.ConfigHandler;
import com.bobmowzie.mowziesmobs.server.entity.umvuthana.EntityUmvuthanaFollowerToPlayer;
import com.bobmowzie.mowziesmobs.server.item.ItemEarthrendGauntlet;
import com.bobmowzie.mowziesmobs.server.item.ItemHandler;
import com.bobmowzie.mowziesmobs.server.message.mouse.MessageLeftMouseDown;
import com.bobmowzie.mowziesmobs.server.message.mouse.MessageLeftMouseUp;
import com.bobmowzie.mowziesmobs.server.message.mouse.MessageRightMouseDown;
import com.bobmowzie.mowziesmobs.server.message.mouse.MessageRightMouseUp;
import com.bobmowzie.mowziesmobs.server.potion.EffectHandler;
import com.bobmowzie.mowziesmobs.server.power.Power;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlayerData implements INBTSerializable<CompoundTag> {
    public boolean verticalSwing = false;
    public int untilSunstrike = 0;
    public int untilAxeSwing = 0;
    private int prevTime;
    private int time;
    public boolean mouseRightDown = false;
    public boolean mouseLeftDown = false;
    public boolean prevSneaking;
    private float prevCooledAttackStrength;

    public int tribeCircleTick;
    public List<EntityUmvuthanaFollowerToPlayer> umvuthanaPack = new ArrayList<>();
    public int tribePackRadius = 3;

    @OnlyIn(Dist.CLIENT)
    private GeckoPlayer.GeckoPlayerThirdPerson geckoPlayer;

    public boolean isVerticalSwing() {
        return verticalSwing;
    }

    public void setVerticalSwing(boolean verticalSwing) {
        this.verticalSwing = verticalSwing;
    }

    public int getUntilSunstrike() {
        return untilSunstrike;
    }

    public void setUntilSunstrike(int untilSunstrike) {
        this.untilSunstrike = untilSunstrike;
    }

    public int getUntilAxeSwing() {
        return untilAxeSwing;
    }

    public void setUntilAxeSwing(int untilAxeSwing) {
        this.untilAxeSwing = untilAxeSwing;
    }

    public void setAxeCanAttack(boolean axeCanAttack) {
        this.axeCanAttack = axeCanAttack;
    }

    public boolean getAxeCanAttack() {
        return axeCanAttack;
    }

    public boolean isMouseRightDown() {
        return mouseRightDown;
    }

    public void setMouseRightDown(boolean mouseRightDown) {
        this.mouseRightDown = mouseRightDown;
    }

    public boolean isMouseLeftDown() {
        return mouseLeftDown;
    }

    public void setMouseLeftDown(boolean mouseLeftDown) {
        this.mouseLeftDown = mouseLeftDown;
    }

    public boolean isPrevSneaking() {
        return prevSneaking;
    }

    public void setPrevSneaking(boolean prevSneaking) {
        this.prevSneaking = prevSneaking;
    }

    public int getTribeCircleTick() {
        return tribeCircleTick;
    }

    public void setTribeCircleTick(int tribeCircleTick) {
        this.tribeCircleTick = tribeCircleTick;
    }

    public List<EntityUmvuthanaFollowerToPlayer> getUmvuthanaPack() {
        return umvuthanaPack;
    }

    public void setUmvuthanaPack(List<EntityUmvuthanaFollowerToPlayer> umvuthanaPack) {
        this.umvuthanaPack = umvuthanaPack;
    }

    public int getTribePackRadius() {
        return tribePackRadius;
    }

    public void setTribePackRadius(int tribePackRadius) {
        this.tribePackRadius = tribePackRadius;
    }

    public Vec3 getPrevMotion() {
        return prevMotion;
    }

    public void setUsingSolarBeam(boolean b) {
        this.usingSolarBeam = b;
    }

    public boolean getUsingSolarBeam() {
        return this.usingSolarBeam;
    }

    public float getPrevCooledAttackStrength() {
        return prevCooledAttackStrength;
    }

    public void setPrevCooledAttackStrength(float cooledAttackStrength) {
        prevCooledAttackStrength = cooledAttackStrength;
    }

    @OnlyIn(Dist.CLIENT) // FIXME 1.21 :: maybe cannot be removed due to the class potentially being parsed completely
    public GeckoPlayer.GeckoPlayerThirdPerson getGeckoPlayer() {
        return geckoPlayer;
    }

    private boolean usingSolarBeam;

    public boolean axeCanAttack;

    public Vec3 prevMotion;

    public Power[] powers = new Power[]{};

    public void addedToWorld(EntityJoinLevelEvent event) {
        // Create the geckoplayer instances when an entity joins the world
        // Normally, the animation controllers and lastModel field are only set when rendered for the first time, but this won't work for player animations
        if (event.getLevel().isClientSide()) {
            Player player = (Player) event.getEntity();
            geckoPlayer = new GeckoPlayer.GeckoPlayerThirdPerson(player);
            // Only create 1st person instance if the player joining is this client's player
            if (event.getEntity() == Minecraft.getInstance().player) {
                GeckoPlayer.GeckoPlayerFirstPerson geckoPlayerFirstPerson = new GeckoPlayer.GeckoPlayerFirstPerson(player);
            }
        }
    }

    public void tick(PlayerTickEvent event) {
        Player player = event.getEntity();

        tribeCircleTick++;

        prevMotion = player.position().subtract(new Vec3(player.xo, player.yo, player.zo));
        prevTime = time;
        if (untilSunstrike > 0) {
            untilSunstrike--;
        }
        if (untilAxeSwing > 0) {
            untilAxeSwing--;
        }

        if (!player.level().isClientSide()) {
            if (player.getMainHandItem().getItem() instanceof ItemEarthrendGauntlet || player.getOffhandItem().getItem() instanceof ItemEarthrendGauntlet) {
                player.addEffect(new MobEffectInstance(EffectHandler.GEOMANCY, 20, 0, false, false));
            }

            List<EntityUmvuthanaFollowerToPlayer> pack = umvuthanaPack;
            float theta = (2 * (float) Math.PI / pack.size());
            for (int i = 0; i < pack.size(); i++) {
                EntityUmvuthanaFollowerToPlayer barakoan = pack.get(i);
                barakoan.index = i;
                float distanceToPlayer = player.distanceTo(barakoan);
                if (barakoan.getTarget() == null && barakoan.getActiveAbility() == null) {
                    if (distanceToPlayer > 4)
                        barakoan.getNavigation().moveTo(player.getX() + tribePackRadius * Mth.cos(theta * i), player.getY(), player.getZ() + tribePackRadius * Mth.sin(theta * i), 0.45);
                    else
                        barakoan.getNavigation().stop();
                    if (distanceToPlayer > 20 && player.onGround()) {
                        tryTeleportUmvuthanaFollower(player, barakoan);
                    }
                }
            }
        }

        Ability<?> iceBreathAbility = AbilityHandler.INSTANCE.getAbility(player, AbilityHandler.ICE_BREATH_ABILITY);
        if (iceBreathAbility != null && !iceBreathAbility.isUsing()) {
            for (ItemStack stack : player.getInventory().items) {
                restoreIceCrystalStack(player, stack);
            }
            for (ItemStack stack : player.getInventory().offhand) {
                restoreIceCrystalStack(player, stack);
            }
        }

        useIceCrystalStack(player);

        if (player.level().isClientSide()) {
            if (Minecraft.getInstance().options.keyAttack.isDown() && !mouseLeftDown) {
                mouseLeftDown = true;
                PacketDistributor.sendToServer(new MessageLeftMouseDown());
                for (int i = 0; i < powers.length; i++) {
                    powers[i].onLeftMouseDown(player);
                }
                AbilityData abilityData = DataHandler.getData(player, DataHandler.ABILITY_DATA);
                if (abilityData != null) {
                    for (Ability<?> ability : abilityData.getAbilities()) {
                        if (ability instanceof PlayerAbility) {
                            ((PlayerAbility) ability).onLeftMouseDown(player);
                        }
                    }
                }
            }
            if (Minecraft.getInstance().options.keyUse.isDown() && !mouseRightDown) {
                mouseRightDown = true;
                PacketDistributor.sendToServer(new MessageRightMouseDown());
                for (int i = 0; i < powers.length; i++) {
                    powers[i].onRightMouseDown(player);
                }
                AbilityData abilityData = DataHandler.getData(player, DataHandler.ABILITY_DATA);
                if (abilityData != null) {
                    for (Ability<?> ability : abilityData.getAbilities()) {
                        if (ability instanceof PlayerAbility) {
                            ((PlayerAbility) ability).onRightMouseDown(player);
                        }
                    }
                }
            }
            if (!Minecraft.getInstance().options.keyAttack.isDown() && mouseLeftDown) {
                mouseLeftDown = false;
                PacketDistributor.sendToServer(new MessageLeftMouseUp());
                for (int i = 0; i < powers.length; i++) {
                    powers[i].onLeftMouseUp(player);
                }
                AbilityData abilityData = DataHandler.getData(player, DataHandler.ABILITY_DATA);
                if (abilityData != null) {
                    for (Ability<?> ability : abilityData.getAbilities()) {
                        if (ability instanceof PlayerAbility) {
                            ((PlayerAbility) ability).onLeftMouseUp(player);
                        }
                    }
                }
            }
            if (!Minecraft.getInstance().options.keyUse.isDown() && mouseRightDown) {
                mouseRightDown = false;
                PacketDistributor.sendToServer(new MessageRightMouseUp());
                for (int i = 0; i < powers.length; i++) {
                    powers[i].onRightMouseUp(player);
                }
                AbilityData abilityData = DataHandler.getData(player, DataHandler.ABILITY_DATA);
                if (abilityData != null) {
                    for (Ability<?> ability : abilityData.getAbilities()) {
                        if (ability instanceof PlayerAbility) {
                            ((PlayerAbility) ability).onRightMouseUp(player);
                        }
                    }
                }
            }
        }

        if (player.isShiftKeyDown() && !prevSneaking) {
            for (int i = 0; i < powers.length; i++) {
                powers[i].onSneakDown(player);
            }
            AbilityData abilityData = DataHandler.getData(player, DataHandler.ABILITY_DATA);
            if (abilityData != null) {
                for (Ability<?> ability : abilityData.getAbilities()) {
                    if (ability instanceof PlayerAbility) {
                        ((PlayerAbility) ability).onSneakDown(player);
                    }
                }
            }
        } else if (!player.isShiftKeyDown() && prevSneaking) {
            for (int i = 0; i < powers.length; i++) {
                powers[i].onSneakUp(player);
            }
            AbilityData abilityData = DataHandler.getData(player, DataHandler.ABILITY_DATA);
            if (abilityData != null) {
                for (Ability<?> ability : abilityData.getAbilities()) {
                    if (ability instanceof PlayerAbility) {
                        ((PlayerAbility) ability).onSneakUp(player);
                    }
                }
            }
        }
        prevSneaking = player.isShiftKeyDown();
    }

    private void restoreIceCrystalStack(Player entity, ItemStack stack) {
        if (stack.getItem() == ItemHandler.ICE_CRYSTAL.get()) {
            if (!ConfigHandler.COMMON.TOOLS_AND_ABILITIES.ICE_CRYSTAL.breakable.get()) {
                stack.setDamageValue(Math.max(stack.getDamageValue() - 1, 0));
            }
        }
    }

    private void useIceCrystalStack(Player player) {
        ItemStack stack = player.getUseItem();
        if (stack.getItem() == ItemHandler.ICE_CRYSTAL.get()) {
            Ability<?> iceBreathAbility = AbilityHandler.INSTANCE.getAbility(player, AbilityHandler.ICE_BREATH_ABILITY);
            if (iceBreathAbility != null && iceBreathAbility.isUsing()) {
                InteractionHand handIn = player.getUsedItemHand();
                if (stack.getDamageValue() + 5 < stack.getMaxDamage()) {
                    stack.hurtAndBreak(5, player, LivingEntity.getSlotForHand(handIn));
                } else {
                    if (ConfigHandler.COMMON.TOOLS_AND_ABILITIES.ICE_CRYSTAL.breakable.get()) {
                        stack.hurtAndBreak(5, player, LivingEntity.getSlotForHand(handIn));
                    }
                    iceBreathAbility.end();
                }
            }
        }
    }

    private void tryTeleportUmvuthanaFollower(Player player, EntityUmvuthanaFollowerToPlayer umvuthana) {
        int x = Mth.floor(player.getX()) - 2;
        int z = Mth.floor(player.getZ()) - 2;
        int y = Mth.floor(player.getBoundingBox().minY);

        for (int l = 0; l <= 4; ++l) {
            for (int i1 = 0; i1 <= 4; ++i1) {
                if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && umvuthana.isTeleportFriendlyBlock(x, z, y, l, i1)) {
                    umvuthana.moveTo((float) (x + l) + 0.5F, y, (float) (z + i1) + 0.5F, umvuthana.getYRot(), umvuthana.getXRot());
                    umvuthana.getNavigation().stop();
                    return;
                }
            }
        }
    }

    public int getTick() {
        return time;
    }

    public void decrementTime() {
        time--;
    }

    public int getPackSize() {
        umvuthanaPack.removeIf(Entity::isRemoved);
        return umvuthanaPack.size();
    }

    public void removePackMember(EntityUmvuthanaFollowerToPlayer followerToPlayer) {
        umvuthanaPack.remove(followerToPlayer);
    }

    public void addPackMember(EntityUmvuthanaFollowerToPlayer followerToPlayer) {
        umvuthanaPack.add(followerToPlayer);
    }

    public Power[] getPowers() {
        return powers;
    }

    @Override
    public CompoundTag serializeNBT(@NotNull HolderLookup.Provider lookup) {
        CompoundTag compound = new CompoundTag();
        compound.putInt("untilSunstrike", untilSunstrike);
        compound.putInt("untilAxeSwing", untilAxeSwing);
        compound.putInt("prevTime", prevTime);
        compound.putInt("time", time);
        return compound;
    }

    @Override
    public void deserializeNBT(@NotNull HolderLookup.Provider lookup, CompoundTag compound) {
        untilSunstrike = compound.getInt("untilSunstrike");
        untilAxeSwing = compound.getInt("untilAxeSwing");
        prevTime = compound.getInt("prevTime");
        time = compound.getInt("time");
    }
}
