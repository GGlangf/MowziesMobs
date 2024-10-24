package com.bobmowzie.mowziesmobs.client.particle.util;

import com.mojang.serialization.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

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

    private static final Codec<Type> TYPE_CODEC = StringRepresentable.fromEnum(Type::values);

    public static final MapCodec<ParticleRotation> CODEC = new MapCodec<>() {
        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) { // FIXME 1.21 :: unsure if this is correct
            return Stream.of(
                    ops.createString("type"),
                    ops.createString("angle"),
                    ops.createString("yaw"),
                    ops.createString("pitch"),
                    ops.createString("roll"),
                    ops.createString("orientation")
            );
        }

        @Override
        public <T> DataResult<ParticleRotation> decode(DynamicOps<T> ops, MapLike<T> input) {
            DataResult<Type> typeResult = TYPE_CODEC.fieldOf("type").decode(ops, input);

            if (typeResult.isSuccess()) {
                Type type = typeResult.getOrThrow();
                ParticleRotation rotation;

                switch (type) {
                    case FACE_CAMERA -> {
                        DataResult<Float> angle = Codec.FLOAT.fieldOf("angle").decode(ops, input);

                        if (angle.isSuccess()) {
                            rotation = new FaceCamera(angle.getOrThrow());
                        } else {
                            return DataResult.error(() -> "Invalid values for the rotation [" + angle + "]");
                        }
                    }
                    case EULER_ANGLES -> {
                        DataResult<Float> yaw = Codec.FLOAT.fieldOf("yaw").decode(ops, input);
                        DataResult<Float> pitch = Codec.FLOAT.fieldOf("pitch").decode(ops, input);
                        DataResult<Float> roll = Codec.FLOAT.fieldOf("roll").decode(ops, input);

                        if (yaw.isSuccess() && pitch.isSuccess() && roll.isSuccess()) {
                            rotation = new EulerAngles(yaw.getOrThrow(), pitch.getOrThrow(), roll.getOrThrow());
                        } else {
                            return DataResult.error(() -> "Invalid values for the rotation [" + yaw + "] [" + pitch + "] [" + roll + "]");
                        }
                    }
                    case ORIENT_VECTOR -> {
                        DataResult<Vec3> orientation = Vec3.CODEC.fieldOf("orientation").decode(ops, input);

                        if (orientation.isSuccess()) {
                            rotation = new OrientVector(orientation.getOrThrow());
                        } else {
                            return DataResult.error(() -> "Invalid values for the rotation [" + orientation + "]");
                        }
                    }
                    default -> {
                        return DataResult.error(() -> "Invalid rotation type [" + type + "]");
                    }
                }

                return DataResult.success(rotation);
            } else {
                return DataResult.error(() -> "Invalid rotation [" + typeResult + "]");
            }
        }

        @Override
        public <T> RecordBuilder<T> encode(ParticleRotation rotation, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            TYPE_CODEC.fieldOf("type").encode(rotation.type(), ops, prefix);

            switch (rotation.type()) {
                case FACE_CAMERA -> {
                    FaceCamera faceCamera = (FaceCamera) rotation;
                    Codec.FLOAT.fieldOf("angle").encode(faceCamera.angle, ops, prefix);
                }
                case EULER_ANGLES -> {
                    EulerAngles eulerAngles = (EulerAngles) rotation;
                    Codec.FLOAT.fieldOf("yaw").encode(eulerAngles.yaw, ops, prefix);
                    Codec.FLOAT.fieldOf("pitch").encode(eulerAngles.pitch, ops, prefix);
                    Codec.FLOAT.fieldOf("roll").encode(eulerAngles.roll, ops, prefix);
                }
                case ORIENT_VECTOR -> {
                    OrientVector orientVector = (OrientVector) rotation;
                    Vec3.CODEC.fieldOf("orientation").encode(orientVector.orientation, ops, prefix);
                }
            }

            return prefix;
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
