package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

public class WorldGenEndGatewayConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenEndGatewayConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(BlockPosition.a.optionalFieldOf("exit").forGetter((worldgenendgatewayconfiguration) -> {
            return worldgenendgatewayconfiguration.b;
        }), Codec.BOOL.fieldOf("exact").forGetter((worldgenendgatewayconfiguration) -> {
            return worldgenendgatewayconfiguration.c;
        })).apply(instance, WorldGenEndGatewayConfiguration::new);
    });
    private final Optional<BlockPosition> b;
    private final boolean c;

    private WorldGenEndGatewayConfiguration(Optional<BlockPosition> optional, boolean flag) {
        this.b = optional;
        this.c = flag;
    }

    public static WorldGenEndGatewayConfiguration a(BlockPosition blockposition, boolean flag) {
        return new WorldGenEndGatewayConfiguration(Optional.of(blockposition), flag);
    }

    public static WorldGenEndGatewayConfiguration a() {
        return new WorldGenEndGatewayConfiguration(Optional.empty(), false);
    }

    public Optional<BlockPosition> b() {
        return this.b;
    }

    public boolean c() {
        return this.c;
    }
}
