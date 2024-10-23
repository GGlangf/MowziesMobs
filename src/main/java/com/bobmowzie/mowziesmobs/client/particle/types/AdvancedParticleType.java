package com.bobmowzie.mowziesmobs.client.particle.types;

import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleRotation;
import com.bobmowzie.mowziesmobs.server.message.NetworkHandler;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class AdvancedParticleType extends AdvancedTypeBase {
    private final boolean canCollide;

    public AdvancedParticleType(@NotNull Holder<ParticleType<?>> type, float red, float green, float blue, float alpha, float scale, float duration, float airDrag, boolean emissive, boolean canCollide, ParticleRotation rotation) {
        this(type, red, green, blue, alpha, scale, duration, airDrag, emissive, canCollide, rotation,  new ParticleComponent[]{});
    }

    public AdvancedParticleType(@NotNull Holder<ParticleType<?>> type, float red, float green, float blue, float alpha, float scale, float duration, float airDrag, boolean emissive, boolean canCollide, ParticleRotation rotation, ParticleComponent[] components) {
        super(type, rotation, components, red, green, blue, alpha, scale, duration, airDrag, emissive);
        this.canCollide = canCollide;
    }

    public static final MapCodec<AdvancedParticleType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BuiltInRegistries.PARTICLE_TYPE.holderByNameCodec().fieldOf("type").forGetter(AdvancedParticleType::type),
                    Codec.FLOAT.fieldOf("red").forGetter(AdvancedParticleType::red),
                    Codec.FLOAT.fieldOf("green").forGetter(AdvancedParticleType::green),
                    Codec.FLOAT.fieldOf("blue").forGetter(AdvancedParticleType::blue),
                    Codec.FLOAT.fieldOf("alpha").forGetter(AdvancedParticleType::alpha),
                    Codec.FLOAT.fieldOf("scale").forGetter(AdvancedParticleType::scale),
                    Codec.FLOAT.fieldOf("duration").forGetter(AdvancedParticleType::duration),
                    Codec.FLOAT.fieldOf("air_drag").forGetter(AdvancedParticleType::airDrag),
                    Codec.BOOL.fieldOf("emissive").forGetter(AdvancedParticleType::emissive),
                    Codec.BOOL.fieldOf("can_collide").forGetter(AdvancedParticleType::canCollide),
                    ParticleRotation.CODEC.fieldOf("rotation").forGetter(AdvancedParticleType::rotation)
            ).apply(instance, AdvancedParticleType::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AdvancedParticleType> STREAM_CODEC = NetworkHandler.composite(
            TYPE_STREAM_CODEC, AdvancedParticleType::type,
            ByteBufCodecs.FLOAT, AdvancedParticleType::red,
            ByteBufCodecs.FLOAT, AdvancedParticleType::green,
            ByteBufCodecs.FLOAT, AdvancedParticleType::blue,
            ByteBufCodecs.FLOAT, AdvancedParticleType::alpha,
            ByteBufCodecs.FLOAT, AdvancedParticleType::scale,
            ByteBufCodecs.FLOAT, AdvancedParticleType::duration,
            ByteBufCodecs.FLOAT, AdvancedParticleType::airDrag,
            ByteBufCodecs.BOOL, AdvancedParticleType::emissive,
            ByteBufCodecs.BOOL, AdvancedParticleType::canCollide,
            ParticleRotation.STREAM_CODEC, AdvancedParticleType::rotation,
            AdvancedParticleType::new
    );

    public boolean canCollide() {
        return canCollide;
    }
}
