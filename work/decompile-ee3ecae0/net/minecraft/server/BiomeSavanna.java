package net.minecraft.server;

public final class BiomeSavanna extends BiomeBase {

    protected BiomeSavanna() {
        super((new BiomeBase.a()).a(WorldGenSurface.G, WorldGenSurface.v).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.SAVANNA).a(0.125F).b(0.05F).c(1.2F).d(0.0F).a(4159204).b(329011).a((String) null));
        this.a(WorldGenerator.VILLAGE.b((WorldGenFeatureConfiguration) (new WorldGenFeatureVillageConfiguration("village/savanna/town_centers", 6))));
        this.a(WorldGenerator.PILLAGER_OUTPOST.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e));
        this.a(WorldGenerator.MINESHAFT.b((WorldGenFeatureConfiguration) (new WorldGenMineshaftConfiguration(0.004D, WorldGenMineshaft.Type.NORMAL))));
        this.a(WorldGenerator.STRONGHOLD.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.e));
        BiomeDecoratorGroups.a(this);
        BiomeDecoratorGroups.c(this);
        BiomeDecoratorGroups.d(this);
        BiomeDecoratorGroups.f(this);
        BiomeDecoratorGroups.J(this);
        BiomeDecoratorGroups.g(this);
        BiomeDecoratorGroups.h(this);
        BiomeDecoratorGroups.l(this);
        BiomeDecoratorGroups.y(this);
        BiomeDecoratorGroups.V(this);
        BiomeDecoratorGroups.L(this);
        BiomeDecoratorGroups.Z(this);
        BiomeDecoratorGroups.aa(this);
        BiomeDecoratorGroups.am(this);
        BiomeDecoratorGroups.ap(this);
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.SHEEP, 12, 4, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.PIG, 10, 4, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.CHICKEN, 10, 4, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.COW, 8, 4, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.HORSE, 1, 2, 6));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.DONKEY, 1, 1, 1));
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
