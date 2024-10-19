package com.bobmowzie.mowziesmobs.client.particle.types;

import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleRotation;
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

    // FIXME 1.21 :: only network relevant?
    private final String rotationType;
    private float faceCameraAngle;
    private float yaw;
    private float pitch;
    private float roll;

    public RibbonParticleType(@NotNull Holder<ParticleType<?>> type, float red, float green, float blue, float alpha, float scale, float duration, float airDrag, boolean emissive, int length) {
        this(type, red, green, blue, alpha, scale, duration, airDrag, emissive, new ParticleRotation.FaceCamera(0), new ParticleComponent[]{}, length);
    }

    public RibbonParticleType(@NotNull Holder<ParticleType<?>> type, float red, float green, float blue, float alpha, float scale, float duration, float airDrag, boolean emissive, ParticleRotation rotation, ParticleComponent[] components, int length) {
        super(type, rotation, components, red, green, blue, alpha, scale, duration, airDrag, emissive);
        this.length = length;
        this.rotationType = rotation.getId();

        switch (rotationType) {
            case "face_camera":
                this.faceCameraAngle = ((ParticleRotation.FaceCamera) rotation).faceCameraAngle;
                break;
            case "euler":
                this.yaw = ((ParticleRotation.EulerAngles) rotation).yaw;
                this.pitch = ((ParticleRotation.EulerAngles) rotation).pitch;
                this.roll = ((ParticleRotation.EulerAngles) rotation).roll;
                break;
            case "orient":
                this.yaw = (float) ((ParticleRotation.OrientVector) rotation).orientation.x();
                this.pitch = (float) ((ParticleRotation.OrientVector) rotation).orientation.y();
                this.roll = (float) ((ParticleRotation.OrientVector) rotation).orientation.z();
                break;
            default:
                throw new IllegalArgumentException("Invalid rotation type [" + rotationType + "]");
        }
    }

    public RibbonParticleType(@NotNull Holder<ParticleType<?>> type, float red, float green, float blue, float alpha, float scale, float duration, float airDrag, boolean emissive, int length, String rotationType, float faceCameraAngle, float yaw, float pitch, float roll) {
        this(type, red, green, blue, alpha, scale, duration, airDrag, emissive, determineRotation(rotationType, faceCameraAngle, yaw, pitch, roll), new ParticleComponent[]{}, length);
        this.faceCameraAngle = faceCameraAngle;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
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
                    Codec.INT.fieldOf("emissive").forGetter(RibbonParticleType::length)
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
            ByteBufCodecs.STRING_UTF8, RibbonParticleType::rotationType,
            ByteBufCodecs.FLOAT, RibbonParticleType::faceCameraAngle,
            ByteBufCodecs.FLOAT, RibbonParticleType::yaw,
            ByteBufCodecs.FLOAT, RibbonParticleType::pitch,
            ByteBufCodecs.FLOAT, RibbonParticleType::roll,
            AdvancedParticleType::new
    );

    public int length() {
        return this.length;
    }

    public String rotationType() {
        return rotationType;
    }

    public float faceCameraAngle() {
        return faceCameraAngle;
    }

    public float yaw() {
        return yaw;
    }

    public float pitch() {
        return pitch;
    }

    public float roll() {
        return roll;
    }
}
