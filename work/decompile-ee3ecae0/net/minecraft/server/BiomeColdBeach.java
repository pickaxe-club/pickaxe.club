package net.minecraft.server;

public final class BiomeColdBeach extends BiomeBase {

    public BiomeColdBeach() {
        super((new BiomeBase.a()).a(WorldGenSurface.G, WorldGenSurface.z).a(BiomeBase.Precipitation.SNOW).a(BiomeBase.Geography.BEACH).a(0.0F).b(0.025F).c(0.05F).d(0.3F).a(4020182).b(329011).a((String) null));
        this.a(WorldGenerator.MINESHAFT.b((WorldGenFeatureConfiguration) (new WorldGenMineshaftConfiguration(0.004D, WorldGenMineshaft.Type.NORMAL))));
        this.a(WorldGenerator.BURIED_TREASURE.b((WorldGenFeatureConfiguration) (new WorldGenBuriedTreasureConfiguration(0.01F))));
        this.a(WorldGenerator.SHIPWRECK.b((WorldGenFeatureConfiguration) (new WorldGenFeatureShipwreckConfiguration(true))));
        BiomeDecoratorGroups.a(this);
        BiomeDecoratorGroups.c(this);
        BiomeDecoratorGroups.d(this);
        BiomeDecoratorGroups.f(this);
        BiomeDecoratorGroups.g(this);
        BiomeDecoratorGroups.h(this);
        BiomeDecoratorGroups.l(this);
        BiomeDecoratorGroups.U(this);
        BiomeDecoratorGroups.W(this);
        BiomeDecoratorGroups.Z(this);
        BiomeDecoratorGroups.aa(this);
        BiomeDecoratorGroups.am(this);
        BiomeDecoratorGroups.ap(this);
        this.a(EnumCreatureType.AMBIENT, new BiomeBase.BiomeMeta(EntityTypes.BAT, 10, 8, 8));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SPIDER, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE, 95, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE_VILLAGER, 5, 1, 1));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SKELETON, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.CREEPER, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SLIME, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ENDERMAN, 10, 1, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.WITCH, 5, 1, 1));
    }
}
