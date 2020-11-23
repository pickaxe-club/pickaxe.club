package net.minecraft.server;

import java.util.Collection;
import java.util.Optional;

public class TagsEntity {

    private static Tags<EntityTypes<?>> e = new Tags<>((minecraftkey) -> {
        return Optional.empty();
    }, "", false, "");
    private static int f;
    public static final Tag<EntityTypes<?>> SKELETONS = a("skeletons");
    public static final Tag<EntityTypes<?>> RADIERS = a("raiders");
    public static final Tag<EntityTypes<?>> BEEHIVE_INHABITORS = a("beehive_inhabitors");
    public static final Tag<EntityTypes<?>> ARROWS = a("arrows");

    public static void a(Tags<EntityTypes<?>> tags) {
        TagsEntity.e = tags;
        ++TagsEntity.f;
    }

    public static Tags<EntityTypes<?>> a() {
        return TagsEntity.e;
    }

    private static Tag<EntityTypes<?>> a(String s) {
        return new TagsEntity.a(new MinecraftKey(s));
    }

    public static class a extends Tag<EntityTypes<?>> {

        private int a = -1;
        private Tag<EntityTypes<?>> b;

        public a(MinecraftKey minecraftkey) {
            super(minecraftkey);
        }

        public boolean a(EntityTypes<?> entitytypes) {
            if (this.a != TagsEntity.f) {
                this.b = TagsEntity.e.b(this.c());
                this.a = TagsEntity.f;
            }

            return this.b.isTagged(entitytypes);
        }

        @Override
        public Collection<EntityTypes<?>> a() {
            if (this.a != TagsEntity.f) {
                this.b = TagsEntity.e.b(this.c());
                this.a = TagsEntity.f;
            }

            return this.b.a();
        }

        @Override
        public Collection<Tag.b<EntityTypes<?>>> b() {
            if (this.a != TagsEntity.f) {
                this.b = TagsEntity.e.b(this.c());
                this.a = TagsEntity.f;
            }

            return this.b.b();
        }
    }
}
