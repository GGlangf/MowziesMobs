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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class AdvancedParticleType extends ParticleType<AdvancedParticleType> implements ParticleOptions {
    private final @NotNull Holder<ParticleType<?>> type;

    private float red, green, blue, alpha;
    private float scale;
    private float duration;
    private float airDrag;
    private boolean emissive;
    private boolean canCollide;

    private final @NotNull ParticleRotation rotation;
    private final @NotNull ParticleComponent[] components;

    // FIXME 1.21 :: only network relevant?
    private String rotationType;
    private float faceCameraAngle;
    private float yaw;
    private float pitch;
    private float roll;

    // For the registry
    public AdvancedParticleType(@NotNull Holder<ParticleType<?>> type) {
        super(false);

        this.type = type;
        this.rotation = new ParticleRotation.FaceCamera(0);
        this.components = new ParticleComponent[]{};
        this.rotationType = "face_camera";
    }

    public AdvancedParticleType(Holder<ParticleType<?>> type, float red, float green, float blue, float alpha, float scale, float duration, float airDrag, boolean emissive, boolean canCollide) {
        this(type, red, green, blue, alpha, scale, duration, airDrag, emissive, canCollide, new ParticleRotation.FaceCamera(0),  new ParticleComponent[]{});
    }

    public AdvancedParticleType(@NotNull Holder<ParticleType<?>> type, float red, float green, float blue, float alpha, float scale, float duration, float airDrag, boolean emissive, boolean canCollide, ParticleRotation rotation, ParticleComponent[] components) {
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
        this.canCollide = canCollide;
        this.rotation = rotation;
        this.components = components;
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

    public AdvancedParticleType(@NotNull Holder<ParticleType<?>> type, float red, float green, float blue, float alpha, float scale, float duration, float airDrag, boolean emissive, boolean canCollide, String rotationType, float faceCameraAngle, float yaw, float pitch, float roll) {
        this(type, red, green, blue, alpha, scale, duration, airDrag, emissive, canCollide, determineRotation(rotationType, faceCameraAngle, yaw, pitch, roll),  new ParticleComponent[]{});
        this.rotationType = rotationType;
        this.faceCameraAngle = faceCameraAngle;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }

    private static ParticleRotation determineRotation(String rotationType, float faceCameraAngle, float yaw, float pitch, float roll) {
        return switch (rotationType) {
            case "face_camera":
                yield new ParticleRotation.FaceCamera(faceCameraAngle);
            case "euler":
                yield new ParticleRotation.EulerAngles(yaw, pitch, roll);
            case "orient":
                yield new ParticleRotation.OrientVector(new Vec3(yaw, pitch, roll));
            default:
                throw new IllegalArgumentException("Invalid rotation type [" + rotationType + "]");
        };
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return type.value();
    }

    @Override
    public @NotNull MapCodec<AdvancedParticleType> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, AdvancedParticleType> streamCodec() {
        return STREAM_CODEC;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ParticleType<?>>> TYPE_STREAM_CODEC = StreamCodec.of(
            (buffer, type) -> buffer.writeInt(BuiltInRegistries.PARTICLE_TYPE.getId(type.value())),
            buffer -> BuiltInRegistries.PARTICLE_TYPE.getHolder(buffer.readInt()).orElseThrow()
    );

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
                    Codec.BOOL.fieldOf("can_collide").forGetter(AdvancedParticleType::canCollide)
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
            ByteBufCodecs.STRING_UTF8, AdvancedParticleType::rotationType,
            ByteBufCodecs.FLOAT, AdvancedParticleType::faceCameraAngle,
            ByteBufCodecs.FLOAT, AdvancedParticleType::yaw,
            ByteBufCodecs.FLOAT, AdvancedParticleType::pitch,
            ByteBufCodecs.FLOAT, AdvancedParticleType::roll,
            AdvancedParticleType::new
    );

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

    public boolean canCollide() {
        return canCollide;
    }

    public ParticleRotation rotation() {
        return rotation;
    }

    public ParticleComponent[] components() {
        return components;
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
