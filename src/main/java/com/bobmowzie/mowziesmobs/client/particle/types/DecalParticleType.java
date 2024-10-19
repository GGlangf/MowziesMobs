package com.bobmowzie.mowziesmobs.client.particle.types;

import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleRotation;
import com.bobmowzie.mowziesmobs.server.message.NetworkHandler;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class DecalParticleType extends ParticleType<DecalParticleType> implements ParticleOptions {
    private final @NotNull Holder<ParticleType<?>> type;

    private float red, green, blue, alpha;
    private float scale;
    private float duration;
    private float airDrag;
    private boolean emissive;

    private final @NotNull ParticleRotation rotation;
    private final @NotNull ParticleComponent[] components;

    private int spriteSize;
    private int bufferSize;

    // For the registry
    public DecalParticleType(@NotNull Holder<ParticleType<?>> type) {
        super(false);

        this.type = type;
        this.rotation = new ParticleRotation.FaceCamera(0);
        this.components = new ParticleComponent[]{};
    }

    public DecalParticleType(@NotNull Holder<ParticleType<?>> type, float red, float green, float blue, float alpha, float scale, float duration, float airDrag, boolean emissive, float angle, int spriteSize, int bufferSize) {
        this(type, red, green, blue, alpha, scale, duration, airDrag, emissive, angle, spriteSize, bufferSize, new ParticleComponent[]{});
    }

    public DecalParticleType(@NotNull Holder<ParticleType<?>> type, float red, float green, float blue, float alpha, float scale, float duration, float airDrag, boolean emissive, float angle, int spriteSize, int bufferSize, ParticleComponent[] components) {
        super(false);

        this.type = type;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.scale = scale;
        this.duration = duration;
        this.airDrag = airDrag;
        this.emissive = emissive;
        this.rotation = new ParticleRotation.EulerAngles(angle, 0, 0);
        this.components = components;
        this.spriteSize = spriteSize;
        this.bufferSize = bufferSize;
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return type.value();
    }

    @Override
    public @NotNull MapCodec<DecalParticleType> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, DecalParticleType> streamCodec() {
        return STREAM_CODEC;
    }

    public static final MapCodec<DecalParticleType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BuiltInRegistries.PARTICLE_TYPE.holderByNameCodec().fieldOf("type").forGetter(DecalParticleType::type),
                    Codec.FLOAT.fieldOf("red").forGetter(DecalParticleType::red),
                    Codec.FLOAT.fieldOf("green").forGetter(DecalParticleType::green),
                    Codec.FLOAT.fieldOf("blue").forGetter(DecalParticleType::blue),
                    Codec.FLOAT.fieldOf("alpha").forGetter(DecalParticleType::alpha),
                    Codec.FLOAT.fieldOf("scale").forGetter(DecalParticleType::scale),
                    Codec.FLOAT.fieldOf("duration").forGetter(DecalParticleType::duration),
                    Codec.FLOAT.fieldOf("air_drag").forGetter(DecalParticleType::airDrag),
                    Codec.BOOL.fieldOf("emissive").forGetter(DecalParticleType::emissive),
                    Codec.FLOAT.fieldOf("angle").forGetter(DecalParticleType::airDrag),
                    Codec.INT.fieldOf("sprite_size").forGetter(DecalParticleType::spriteSize),
                    Codec.INT.fieldOf("buffer_size").forGetter(DecalParticleType::bufferSize)
            ).apply(instance, DecalParticleType::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DecalParticleType> STREAM_CODEC = NetworkHandler.composite(
            AdvancedParticleType.TYPE_STREAM_CODEC, DecalParticleType::type,
            ByteBufCodecs.FLOAT, DecalParticleType::red,
            ByteBufCodecs.FLOAT, DecalParticleType::green,
            ByteBufCodecs.FLOAT, DecalParticleType::blue,
            ByteBufCodecs.FLOAT, DecalParticleType::alpha,
            ByteBufCodecs.FLOAT, DecalParticleType::scale,
            ByteBufCodecs.FLOAT, DecalParticleType::duration,
            ByteBufCodecs.FLOAT, DecalParticleType::airDrag,
            ByteBufCodecs.BOOL, DecalParticleType::emissive,
            ByteBufCodecs.FLOAT, DecalParticleType::angle,
            ByteBufCodecs.INT, DecalParticleType::spriteSize,
            ByteBufCodecs.INT, DecalParticleType::bufferSize,
            DecalParticleType::new
    );

    public float angle() {
        if (this.rotation() instanceof ParticleRotation.EulerAngles euler) {
            return euler.yaw;
        }

        return 0;
    }

    public int spriteSize() {
        return spriteSize;
    }

    public int bufferSize() {
        return bufferSize;
    }

    public Holder<ParticleType<?>> type() {
        return type;
    }

    public float red() {
        return red;
    }

    public float green() {
        return green;
    }

    public float blue() {
        return blue;
    }

    public float alpha() {
        return alpha;
    }

    public float airDrag() {
        return airDrag;
    }

    public float scale() {
        return scale;
    }

    public boolean emissive() {
        return emissive;
    }

    public float duration() {
        return duration;
    }

    public ParticleRotation rotation() {
        return rotation;
    }

    public ParticleComponent[] components() {
        return components;
    }
}
