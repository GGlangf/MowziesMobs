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
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class AdvancedTypeBase implements ParticleOptions {
    public static final MapCodec<AdvancedTypeBase> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.PARTICLE_TYPE.holderByNameCodec().fieldOf("type").forGetter(AdvancedTypeBase::type),
            ParticleRotation.CODEC.fieldOf("rotation").forGetter(AdvancedTypeBase::rotation),
            Codec.FLOAT.fieldOf("red").forGetter(AdvancedTypeBase::red),
            Codec.FLOAT.fieldOf("green").forGetter(AdvancedTypeBase::green),
            Codec.FLOAT.fieldOf("blue").forGetter(AdvancedTypeBase::blue),
            Codec.FLOAT.fieldOf("alpha").forGetter(AdvancedTypeBase::alpha),
            Codec.FLOAT.fieldOf("scale").forGetter(AdvancedTypeBase::scale),
            Codec.FLOAT.fieldOf("duration").forGetter(AdvancedTypeBase::duration),
            Codec.FLOAT.fieldOf("air_drag").forGetter(AdvancedTypeBase::airDrag),
            Codec.BOOL.fieldOf("emissive").forGetter(AdvancedTypeBase::emissive),
            Codec.BOOL.fieldOf("can_collide").forGetter(AdvancedTypeBase::canCollide)
    ).apply(instance, AdvancedTypeBase::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AdvancedTypeBase> STREAM_CODEC = NetworkHandler.composite(
            ByteBufCodecs.holderRegistry(Registries.PARTICLE_TYPE), AdvancedTypeBase::type,
            ByteBufCodecs.fromCodecWithRegistries(ParticleRotation.CODEC), AdvancedTypeBase::rotation,
            ByteBufCodecs.FLOAT, AdvancedTypeBase::red,
            ByteBufCodecs.FLOAT, AdvancedTypeBase::green,
            ByteBufCodecs.FLOAT, AdvancedTypeBase::blue,
            ByteBufCodecs.FLOAT, AdvancedTypeBase::alpha,
            ByteBufCodecs.FLOAT, AdvancedTypeBase::scale,
            ByteBufCodecs.FLOAT, AdvancedTypeBase::duration,
            ByteBufCodecs.FLOAT, AdvancedTypeBase::airDrag,
            ByteBufCodecs.BOOL, AdvancedTypeBase::emissive,
            ByteBufCodecs.BOOL, AdvancedTypeBase::canCollide,
            AdvancedTypeBase::new
    );

    private final @NotNull Holder<ParticleType<?>> type;
    private final @NotNull ParticleRotation rotation;
    private final @NotNull ParticleComponent[] components;

    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;
    
    private final float scale;
    private final float duration;
    private final float airDrag;
    private final boolean emissive;
    private final boolean canCollide;

    public AdvancedTypeBase(final AdvancedTypeBase base) {
        this(base.type(), base.rotation(), base.components(), base.red(), base.green(), base.blue(), base.alpha(), base.scale(), base.duration(), base.airDrag(), base.emissive(), base.canCollide());
    }

    public AdvancedTypeBase(@NotNull Holder<ParticleType<?>> type, @NotNull ParticleRotation rotation, float red, float green, float blue, float alpha, float scale, float duration, float airDrag, boolean emissive, boolean canCollide) {
        this(type, rotation, new ParticleComponent[]{}, red, green, blue, alpha, scale, duration, airDrag, emissive, canCollide);
    }

    public AdvancedTypeBase(@NotNull Holder<ParticleType<?>> type, @NotNull ParticleRotation rotation, @NotNull ParticleComponent[] components, float red, float green, float blue, float alpha, float scale, float duration, float airDrag, boolean emissive, boolean canCollide) {
        this.type = type;
        this.rotation = rotation;
        this.components = components;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.scale = scale;
        this.duration = duration;
        this.airDrag = airDrag;
        this.emissive = emissive;
        this.canCollide = canCollide;
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return type.value();
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
    
    public boolean canCollide() {
        return canCollide;
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
