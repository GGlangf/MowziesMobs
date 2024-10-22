package com.bobmowzie.mowziesmobs.server.message;

import com.bobmowzie.mowziesmobs.MMCommon;
import com.bobmowzie.mowziesmobs.server.ability.Ability;
import com.bobmowzie.mowziesmobs.server.ability.AbilityType;
import com.bobmowzie.mowziesmobs.server.capability.AbilityData;
import com.bobmowzie.mowziesmobs.server.capability.DataHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record MessageJumpToAbilitySection(int entityId, int index, int sectionIndex) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageJumpToAbilitySection> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MMCommon.MODID, "message_jump_to_ability_section"));
    public static final StreamCodec<ByteBuf, MessageJumpToAbilitySection> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            MessageJumpToAbilitySection::entityId,
            ByteBufCodecs.INT,
            MessageJumpToAbilitySection::index,
            ByteBufCodecs.INT,
            MessageJumpToAbilitySection::sectionIndex,
            MessageJumpToAbilitySection::new
    );

    public static void handleClient(final MessageJumpToAbilitySection packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = MMCommon.PROXY.getClientLevel();

            if (level != null && level.getEntity(packet.entityId()) instanceof LivingEntity living) {
                AbilityData data = DataHandler.getData(living, DataHandler.ABILITY_DATA);
                AbilityType<?, ?> abilityType = data.getAbilityTypesOnEntity(living)[packet.index()];
                Ability<?> instance = data.getAbilityMap().get(abilityType);

                if (instance.isUsing()) {
                    instance.jumpToSection(packet.sectionIndex());
                }
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
