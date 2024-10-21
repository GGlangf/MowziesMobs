package com.bobmowzie.mowziesmobs.server.ability;

import com.bobmowzie.mowziesmobs.server.capability.AbilityCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

public class AbilityClientEventHandler {
    public static void onRenderTick(RenderFrameEvent.Post event) { // FIXME 1.21 :: Post correct here?
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            AbilityCapability.Capability abilityCapability = AbilityHandler.INSTANCE.getAbilityCapability(player);
            if (abilityCapability != null) {
                for (Ability<?> ability : abilityCapability.getAbilities()) {
                    ability.onRenderTick(event);
                }
            }
        }
    }
}