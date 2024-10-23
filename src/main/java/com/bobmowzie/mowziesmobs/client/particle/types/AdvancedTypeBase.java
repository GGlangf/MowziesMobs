package com.bobmowzie.mowziesmobs.client.particle.types;

import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleRotation;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class AdvancedTypeBase implements ParticleOptions {
    private final @NotNull Holder<ParticleType<?>> type;
    private final @NotNull ParticleRotation rotation;
    private final @NotNull ParticleComponent[] components;

    private float red, green, blue, alpha;
    private float scale;
    private float duration;
    private float airDrag;
    private boolean emissive;

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ParticleType<?>>> TYPE_STREAM_CODEC = StreamCodec.of(
            (buffer, type) -> buffer.writeInt(BuiltInRegistries.PARTICLE_TYPE.getId(type.value())),
            buffer -> BuiltInRegistries.PARTICLE_TYPE.getHolder(buffer.readInt()).orElseThrow()
    );

    public AdvancedTypeBase(@NotNull Holder<ParticleType<?>> type) {
        this.type = type;
        this.rotation = new ParticleRotation.FaceCamera(0);
        this.components = new ParticleComponent[]{};
    }

    public AdvancedTypeBase(@NotNull Holder<ParticleType<?>> type, @NotNull ParticleRotation rotation, @NotNull ParticleComponent[] components, float red, float green, float blue, float alpha, float scale, float duration, float airDrag, boolean emissive) {
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
    }

    public static ParticleRotation determineRotation(String rotationType, float faceCameraAngle, float yaw, float pitch, float roll) {
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
