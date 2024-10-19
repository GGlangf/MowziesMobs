package com.bobmowzie.mowziesmobs.client.particle.types;

import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleRotation;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class RibbonParticleType extends AdvancedParticleType {
    public static final Deserializer<RibbonParticleType> DESERIALIZER = new Deserializer<RibbonParticleType>() {
        public RibbonParticleType fromCommand(ParticleType<RibbonParticleType> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            double airDrag = reader.readDouble();
            reader.expect(' ');
            double red = reader.readDouble();
            reader.expect(' ');
            double green = reader.readDouble();
            reader.expect(' ');
            double blue = reader.readDouble();
            reader.expect(' ');
            double alpha = reader.readDouble();
            reader.expect(' ');
            String rotationMode = reader.readString();
            reader.expect(' ');
            double scale = reader.readDouble();
            reader.expect(' ');
            double yaw = reader.readDouble();
            reader.expect(' ');
            double pitch = reader.readDouble();
            reader.expect(' ');
            double roll = reader.readDouble();
            reader.expect(' ');
            boolean emissive = reader.readBoolean();
            reader.expect(' ');
            double duration = reader.readDouble();
            reader.expect(' ');
            reader.readDouble();
            reader.expect(' ');
            int length = reader.readInt();
            ParticleRotation rotation;
            if (rotationMode.equals("face_camera")) rotation = new ParticleRotation.FaceCamera((float) 0);
            else if (rotationMode.equals("euler")) rotation = new ParticleRotation.EulerAngles((float)yaw, (float)pitch, (float)roll);
            else rotation = new ParticleRotation.OrientVector(new Vec3(yaw, pitch, roll));
            return new RibbonParticleType(particleTypeIn, rotation, scale, red, green, blue, alpha, airDrag, duration, emissive, length);
        }

        public RibbonParticleType fromNetwork(ParticleType<RibbonParticleType> particleTypeIn, FriendlyByteBuf buffer) {
            double airDrag = buffer.readFloat();
            double red = buffer.readFloat();
            double green = buffer.readFloat();
            double blue = buffer.readFloat();
            double alpha = buffer.readFloat();
            String rotationMode = buffer.readUtf();
            double scale = buffer.readFloat();
            double yaw = buffer.readFloat();
            double pitch = buffer.readFloat();
            double roll = buffer.readFloat();
            boolean emissive = buffer.readBoolean();
            double duration = buffer.readFloat();
            buffer.readFloat();
            int length = buffer.readInt();
            ParticleRotation rotation;
            if (rotationMode.equals("face_camera")) rotation = new ParticleRotation.FaceCamera((float) 0);
            else if (rotationMode.equals("euler")) rotation = new ParticleRotation.EulerAngles((float)yaw, (float)pitch, (float)roll);
            else rotation = new ParticleRotation.OrientVector(new Vec3(yaw, pitch, roll));
            return new RibbonParticleType(particleTypeIn, rotation, scale, red, green, blue, alpha, airDrag, duration, emissive, length);
        }
    };

    private final int length;

    public RibbonParticleType(ParticleType<? extends RibbonParticleType> type, ParticleRotation rotation, double scale, double r, double g, double b, double a, double drag, double duration, boolean emissive, int length) {
        this(type, rotation, scale, r, g, b, a, drag, duration, emissive, length, new ParticleComponent[]{});
    }

    public RibbonParticleType(ParticleType<? extends RibbonParticleType> type, ParticleRotation rotation, double scale, double r, double g, double b, double a, double drag, double duration, boolean emissive, int length, ParticleComponent[] components) {
        super(type, rotation, scale, r, g, b, a, drag, duration, emissive, false, components);
        this.length = length;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        super.writeToNetwork(buffer);
        buffer.writeInt(this.length);
    }

    @SuppressWarnings("deprecation")
    @Override
    public String writeToString() {
        return super.writeToString() + " " + this.length;
    }

    public int getLength() {
        return this.length;
    }

    public static Codec<RibbonParticleType> CODEC_RIBBON(ParticleType<RibbonParticleType> particleType) {
        return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(
                Codec.DOUBLE.fieldOf("scale").forGetter(RibbonParticleType::scale),
                Codec.DOUBLE.fieldOf("r").forGetter(RibbonParticleType::red),
                Codec.DOUBLE.fieldOf("g").forGetter(RibbonParticleType::green),
                Codec.DOUBLE.fieldOf("b").forGetter(RibbonParticleType::blue),
                Codec.DOUBLE.fieldOf("a").forGetter(RibbonParticleType::alpha),
                Codec.DOUBLE.fieldOf("drag").forGetter(RibbonParticleType::airDrag),
                Codec.DOUBLE.fieldOf("duration").forGetter(RibbonParticleType::duration),
                Codec.BOOL.fieldOf("emissive").forGetter(RibbonParticleType::emissive),
                Codec.INT.fieldOf("length").forGetter(RibbonParticleType::getLength)
                ).apply(codecBuilder, (scale, r, g, b, a, drag, duration, emissive, length) ->
                    new RibbonParticleType(particleType, new ParticleRotation.FaceCamera(0), scale, r, g, b, a, drag, duration, emissive, length, new ParticleComponent[]{}))
        );
    }
}
