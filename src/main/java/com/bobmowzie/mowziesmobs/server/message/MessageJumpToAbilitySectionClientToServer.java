package com.bobmowzie.mowziesmobs.server.message;

import com.bobmowzie.mowziesmobs.server.ability.Ability;
import com.bobmowzie.mowziesmobs.server.ability.AbilityType;
import com.bobmowzie.mowziesmobs.server.capability.AbilityCapability;
import com.bobmowzie.mowziesmobs.server.capability.CapabilityHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MessageJumpToAbilitySectionClientToServer {
    private int index;
    private int sectionIndex;

    public MessageJumpToAbilitySectionClientToServer() {

    }

    public MessageJumpToAbilitySectionClientToServer(int index, int sectionIndex) {
        this.index = index;
        this.sectionIndex = sectionIndex;
    }

    public static void serialize(final MessageJumpToAbilitySectionClientToServer message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.index);
        buf.writeVarInt(message.sectionIndex);
    }

    public static MessageJumpToAbilitySectionClientToServer deserialize(final FriendlyByteBuf buf) {
        final MessageJumpToAbilitySectionClientToServer message = new MessageJumpToAbilitySectionClientToServer();
        message.index = buf.readVarInt();
        message.sectionIndex = buf.readVarInt();
        return message;
    }

    public static class Handler implements BiConsumer<MessageJumpToAbilitySectionClientToServer, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(final MessageJumpToAbilitySectionClientToServer message, final Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            final ServerPlayer player = context.getSender();
            context.enqueueWork(() -> {
                AbilityCapability.IAbilityCapability abilityCapability = CapabilityHandler.getCapability(player, CapabilityHandler.ABILITY_CAPABILITY);
                if (abilityCapability != null) {
                    AbilityType<?, ?> abilityType = abilityCapability.getAbilityTypesOnEntity(player)[message.index];
                    Ability instance = abilityCapability.getAbilityMap().get(abilityType);
                    if (instance.isUsing()) instance.jumpToSection(message.sectionIndex);
                }
            });
            context.setPacketHandled(true);
        }
    }
}
