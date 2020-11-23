package net.minecraft.server;

public final class BiomeMesaPlateauMutated extends BiomeBase {

    public BiomeMesaPlateauMutated() {
        super((new BiomeBase.a()).a(WorldGenSurface.N, WorldGenSurface.C).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.MESA).a(0.45F).b(0.3F).c(2.0F).d(0.0F).a(4159204).b(329011).a("wooded_badlands_plateau"));
        this.a(WorldGenerator.MINESHAFT.b((WorldGenFeatureConfiguration) (new WorldGenMineshaftConfiguration(0.004D, WorldGenMineshaft.Type.MESA))));
        this.a(WorldGenerator.STRONGHOLD.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e));
        BiomeDecoratorGroups.a(this);
        BiomeDecoratorGroups.c(this);
        BiomeDecoratorGroups.d(this);
        BiomeDecoratorGroups.f(this);
        BiomeDecoratorGroups.g(this);
        BiomeDecoratorGroups.h(this);
        BiomeDecoratorGroups.i(this);
        BiomeDecoratorGroups.l(this);
        BiomeDecoratorGroups.E(this);
        BiomeDecoratorGroups.M(this);
        BiomeDecoratorGroups.Z(this);
        BiomeDecoratorGroups.ab(this);
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
