package com.bobmowzie.mowziesmobs.client.particle.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class ParticleRotation {
    public abstract void setPrevValues();

    public abstract Type type();

    public enum Type implements StringRepresentable {
        FACE_CAMERA("face_camera"),
        EULER_ANGLES("euler_angles"),
        ORIENT_VECTOR("orient_vector");

        private final String id;

        Type(final String id) {
            this.id = id;
        }

        @Override
        public @NotNull String getSerializedName() {
            return id;
        }
    }

    private static final Codec<Type> BEHAVIOUR_CODEC = StringRepresentable.fromEnum(Type::values);

    // FIXME 1.21 :: unsure if this codec works or not, didn't get the the game to trigger an encode or decode
    public static final Codec<ParticleRotation> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<T> encode(ParticleRotation rotation, DynamicOps<T> ops, T prefix) {
            BEHAVIOUR_CODEC.encode(rotation.type(), ops, prefix);

            switch (rotation.type()) {
                case FACE_CAMERA -> {
                    FaceCamera faceCamera = (FaceCamera) rotation;
                    Codec.FLOAT.encode(faceCamera.angle, ops, prefix);
                }
                case EULER_ANGLES -> {
                    EulerAngles eulerAngles = (EulerAngles) rotation;
                    Codec.FLOAT.encode(eulerAngles.yaw, ops, prefix);
                    Codec.FLOAT.encode(eulerAngles.pitch, ops, prefix);
                    Codec.FLOAT.encode(eulerAngles.roll, ops, prefix);
                }
                case ORIENT_VECTOR -> {
                    OrientVector orientVector = (OrientVector) rotation;
                    Vec3.CODEC.encode(orientVector.orientation, ops, prefix);
                }
            }

            return DataResult.success(prefix);
        }

        @Override
        public <T> DataResult<Pair<ParticleRotation, T>> decode(DynamicOps<T> ops, T input) {
            Optional<Pair<Type, T>> typeOptional = BEHAVIOUR_CODEC.decode(ops, input).result();

            if (typeOptional.isPresent()) {
                Pair<Type, T> type = typeOptional.get();

                return DataResult.success(Pair.of(switch (type.getFirst()) {
                    case FACE_CAMERA -> {
                        Pair<Float, T> angle = Codec.FLOAT.decode(ops, input).getOrThrow();
                        yield new FaceCamera(angle.getFirst());
                    }
                    case EULER_ANGLES -> {
                        Pair<Float, T> yaw = Codec.FLOAT.decode(ops, input).getOrThrow();
                        Pair<Float, T> pitch = Codec.FLOAT.decode(ops, input).getOrThrow();
                        Pair<Float, T> roll = Codec.FLOAT.decode(ops, input).getOrThrow();
                        yield new EulerAngles(yaw.getFirst(), pitch.getFirst(), roll.getFirst());
                    }
                    case ORIENT_VECTOR -> {
                        Pair<Vec3, T> orientation = Vec3.CODEC.decode(ops, input).getOrThrow();
                        yield new OrientVector(orientation.getFirst());
                    }
                }, ops.empty()));
            } else {
                return DataResult.error(() -> "No valid type specified");
            }
        }
    };

    @SuppressWarnings("DataFlowIssue") // cast is safe
    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleRotation> STREAM_CODEC = StreamCodec.of(
            (buffer, rotation) -> {
                NeoForgeStreamCodecs.enumCodec(Type.class).encode(buffer, rotation.type());

                switch (rotation.type()) {
                    case FACE_CAMERA:
                        FaceCamera faceCamera = (FaceCamera) rotation;
                        buffer.writeFloat(faceCamera.angle);
                    case EULER_ANGLES:
                        EulerAngles eulerAngles = (EulerAngles) rotation;
                        buffer.writeFloat(eulerAngles.yaw);
                        buffer.writeFloat(eulerAngles.pitch);
                        buffer.writeFloat(eulerAngles.roll);
                        break;
                    case ORIENT_VECTOR:
                        OrientVector orientVector = (OrientVector) rotation;
                        buffer.writeVec3(orientVector.orientation);
                        break;
                }
            },
            buffer -> {
                Type type = NeoForgeStreamCodecs.enumCodec(Type.class).decode(buffer);

                return switch (type) {
                    case FACE_CAMERA -> {
                        float angle = buffer.readFloat();
                        yield new FaceCamera(angle);
                    }
                    case EULER_ANGLES -> {
                        float yaw = buffer.readFloat();
                        float pitch = buffer.readFloat();
                        float roll = buffer.readFloat();
                        yield new EulerAngles(yaw, pitch, roll);
                    }
                    case ORIENT_VECTOR -> {
                        Vec3 orientation = buffer.readVec3();
                        yield new OrientVector(orientation);
                    }
                };
            }
    );

    public static class FaceCamera extends ParticleRotation {
        public static final Type TYPE = Type.FACE_CAMERA;

        public float angle;
        public float prevAngle;

        public FaceCamera(float angle) {
            this.angle = angle;
        }

        @Override
        public void setPrevValues() {
            prevAngle = angle;
        }

        @Override
        public Type type() {
            return TYPE;
        }
    }

    public static class EulerAngles extends ParticleRotation {
        public static final Type TYPE = Type.EULER_ANGLES;

        public float yaw, pitch, roll;
        public float prevYaw, prevPitch, prevRoll;

        public EulerAngles(float yaw, float pitch, float roll) {
            this.yaw = this.prevYaw = yaw;
            this.pitch = this.prevPitch = pitch;
            this.roll = this.prevRoll = roll;
        }

        @Override
        public void setPrevValues() {
            prevYaw = yaw;
            prevPitch = pitch;
            prevRoll = roll;
        }

        @Override
        public Type type() {
            return TYPE;
        }
    }

    public static class OrientVector extends ParticleRotation {
        public static final Type TYPE = Type.ORIENT_VECTOR;

        public Vec3 orientation;
        public Vec3 prevOrientation;

        public OrientVector(Vec3 orientation) {
            this.orientation = this.prevOrientation = orientation;
        }

        @Override
        public void setPrevValues() {
            prevOrientation = orientation;
        }

        @Override
        public Type type() {
            return TYPE;
        }
    }
}
