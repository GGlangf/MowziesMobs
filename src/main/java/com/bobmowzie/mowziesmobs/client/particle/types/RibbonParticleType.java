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

public class RibbonParticleType extends AdvancedTypeBase {
    private final int length;

    public RibbonParticleType(@NotNull Holder<ParticleType<?>> type, float red, float green, float blue, float alpha, float scale, float duration, float airDrag, boolean emissive, int length, ParticleRotation rotation) {
        this(type, red, green, blue, alpha, scale, duration, airDrag, emissive, rotation, new ParticleComponent[]{}, length);
    }

    public RibbonParticleType(@NotNull Holder<ParticleType<?>> type, float red, float green, float blue, float alpha, float scale, float duration, float airDrag, boolean emissive, ParticleRotation rotation, ParticleComponent[] components, int length) {
        super(type, rotation, components, red, green, blue, alpha, scale, duration, airDrag, emissive);
        this.length = length;
    }

    public static final MapCodec<RibbonParticleType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BuiltInRegistries.PARTICLE_TYPE.holderByNameCodec().fieldOf("type").forGetter(RibbonParticleType::type),
                    Codec.FLOAT.fieldOf("red").forGetter(RibbonParticleType::red),
                    Codec.FLOAT.fieldOf("green").forGetter(RibbonParticleType::green),
                    Codec.FLOAT.fieldOf("blue").forGetter(RibbonParticleType::blue),
                    Codec.FLOAT.fieldOf("alpha").forGetter(RibbonParticleType::alpha),
                    Codec.FLOAT.fieldOf("scale").forGetter(RibbonParticleType::scale),
                    Codec.FLOAT.fieldOf("duration").forGetter(RibbonParticleType::duration),
                    Codec.FLOAT.fieldOf("air_drag").forGetter(RibbonParticleType::airDrag),
                    Codec.BOOL.fieldOf("emissive").forGetter(RibbonParticleType::emissive),
                    Codec.INT.fieldOf("length").forGetter(RibbonParticleType::length),
                    ParticleRotation.CODEC.fieldOf("rotation").forGetter(RibbonParticleType::rotation)
            ).apply(instance, RibbonParticleType::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, RibbonParticleType> STREAM_CODEC = NetworkHandler.composite(
            TYPE_STREAM_CODEC, RibbonParticleType::type,
            ByteBufCodecs.FLOAT, RibbonParticleType::red,
            ByteBufCodecs.FLOAT, RibbonParticleType::green,
            ByteBufCodecs.FLOAT, RibbonParticleType::blue,
            ByteBufCodecs.FLOAT, RibbonParticleType::alpha,
            ByteBufCodecs.FLOAT, RibbonParticleType::scale,
            ByteBufCodecs.FLOAT, RibbonParticleType::duration,
            ByteBufCodecs.FLOAT, RibbonParticleType::airDrag,
            ByteBufCodecs.BOOL, RibbonParticleType::emissive,
            ByteBufCodecs.INT, RibbonParticleType::length,
            ParticleRotation.STREAM_CODEC, RibbonParticleType::rotation,
            RibbonParticleType::new
    );

    public int length() {
        return this.length;
    }
}
