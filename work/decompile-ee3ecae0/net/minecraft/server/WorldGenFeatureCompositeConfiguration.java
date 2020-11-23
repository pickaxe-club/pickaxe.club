package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class WorldGenFeatureCompositeConfiguration implements WorldGenFeatureConfiguration {

    public final WorldGenFeatureConfigured<?, ?> a;
    public final WorldGenDecoratorConfigured<?> b;

    public WorldGenFeatureCompositeConfiguration(WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured, WorldGenDecoratorConfigured<?> worldgendecoratorconfigured) {
        this.a = worldgenfeatureconfigured;
        this.b = worldgendecoratorconfigured;
    }

    @Override
    public <T> Dynamic<T> a(DynamicOps<T> dynamicops) {
        return new Dynamic(dynamicops, dynamicops.createMap(ImmutableMap.of(dynamicops.createString("feature"), this.a.a(dynamicops).getValue(), dynamicops.createString("decorator"), this.b.a(dynamicops).getValue())));
    }

    public String toString() {
        return String.format("< %s [%s | %s] >", this.getClass().getSimpleName(), IRegistry.FEATURE.getKey(this.a.b), IRegistry.DECORATOR.getKey(this.b.a));
    }

    public static <T> WorldGenFeatureCompositeConfiguration a(Dynamic<T> dynamic) {
        WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured = WorldGenFeatureConfigured.a(dynamic.get("feature").orElseEmptyMap());
        WorldGenDecoratorConfigured<?> worldgendecoratorconfigured = WorldGenDecoratorConfigured.a(dynamic.get("decorator").orElseEmptyMap());

        return new WorldGenFeatureCompositeConfiguration(worldgenfeatureconfigured, worldgendecoratorconfigured);
    }
}
