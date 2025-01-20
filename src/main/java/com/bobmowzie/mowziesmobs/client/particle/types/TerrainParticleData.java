package com.bobmowzie.mowziesmobs.client.particle.types;

import com.bobmowzie.mowziesmobs.client.particle.util.ParticleRotation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.state.BlockState;

public class TerrainParticleData extends AdvancedTypeBase {
    public static final MapCodec<TerrainParticleData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    AdvancedTypeBase.CODEC.fieldOf("base").forGetter(identity -> identity),
                    BlockState.CODEC.fieldOf("state").forGetter(TerrainParticleData::state)
            ).apply(instance, TerrainParticleData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TerrainParticleData> STREAM_CODEC = StreamCodec.composite(
            AdvancedTypeBase.STREAM_CODEC, identity -> identity,
            ByteBufCodecs.fromCodecWithRegistries(BlockState.CODEC), TerrainParticleData::state,
            TerrainParticleData::new
    );

    private final BlockState state;
    private BlockPos position;

    public TerrainParticleData(final AdvancedTypeBase base, final BlockState state) {
        super(base);
        this.state = state;
    }

    // FIXME :: this is never called anywhere but the position is used?
    public TerrainParticleData setPosition(final BlockPos position) {
        this.position = position;
        return this;
    }

    public float angle() {
        if (this.rotation() instanceof ParticleRotation.EulerAngles angles) {
            return angles.yaw;
        }

        return 0;
    }

    public BlockState state() {
        return state;
    }

    public BlockPos position() {
        return position;
    }
}
