package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenFeatureReplaceBlockConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureReplaceBlockConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(IBlockData.b.fieldOf("target").forGetter((worldgenfeaturereplaceblockconfiguration) -> {
            return worldgenfeaturereplaceblockconfiguration.b;
        }), IBlockData.b.fieldOf("state").forGetter((worldgenfeaturereplaceblockconfiguration) -> {
            return worldgenfeaturereplaceblockconfiguration.c;
        })).apply(instance, WorldGenFeatureReplaceBlockConfiguration::new);
    });
    public final IBlockData b;
    public final IBlockData c;

    public WorldGenFeatureReplaceBlockConfiguration(IBlockData iblockdata, IBlockData iblockdata1) {
        this.b = iblockdata;
        this.c = iblockdata1;
    }
}
