package net.minecraft.server;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class DataConverterSchemaV1460 extends DataConverterSchemaNamed {

    public DataConverterSchemaV1460(int i, Schema schema) {
        super(i, schema);
    }

    protected static void a(Schema schema, Map<String, Supplier<TypeTemplate>> map, String s) {
        schema.register(map, s, () -> {
            return DataConverterSchemaV100.a(schema);
        });
    }

    protected static void b(Schema schema, Map<String, Supplier<TypeTemplate>> map, String s) {
        schema.register(map, s, () -> {
            return DSL.optionalFields("Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)));
        });
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();

        schema.registerSimple(map, "minecraft:area_effect_cloud");
        a(schema, map, "minecraft:armor_stand");
        schema.register(map, "minecraft:arrow", (s) -> {
            return DSL.optionalFields("inBlockState", DataConverterTypes.BLOCK_STATE.in(schema));
        });
        a(schema, map, "minecraft:bat");
        a(schema, map, "minecraft:blaze");
        schema.registerSimple(map, "minecraft:boat");
        a(schema, map, "minecraft:cave_spider");
        schema.register(map, "minecraft:chest_minecart", (s) -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.BLOCK_STATE.in(schema), "Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)));
        });
        a(schema, map, "minecraft:chicken");
        schema.register(map, "minecraft:commandblock_minecart", (s) -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.BLOCK_STATE.in(schema));
        });
        a(schema, map, "minecraft:cow");
        a(schema, map, "minecraft:creeper");
        schema.register(map, "minecraft:donkey", (s) -> {
            return DSL.optionalFields("Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.a(schema));
        });
        schema.registerSimple(map, "minecraft:dragon_fireball");
        schema.registerSimple(map, "minecraft:egg");
        a(schema, map, "minecraft:elder_guardian");
        schema.registerSimple(map, "minecraft:ender_crystal");
        a(schema, map, "minecraft:ender_dragon");
        schema.register(map, "minecraft:enderman", (s) -> {
            return DSL.optionalFields("carriedBlockState", DataConverterTypes.BLOCK_STATE.in(schema), DataConverterSchemaV100.a(schema));
        });
        a(schema, map, "minecraft:endermite");
        schema.registerSimple(map, "minecraft:ender_pearl");
        schema.registerSimple(map, "minecraft:evocation_fangs");
        a(schema, map, "minecraft:evocation_illager");
        schema.registerSimple(map, "minecraft:eye_of_ender_signal");
        schema.register(map, "minecraft:falling_block", (s) -> {
            return DSL.optionalFields("BlockState", DataConverterTypes.BLOCK_STATE.in(schema), "TileEntityData", DataConverterTypes.BLOCK_ENTITY.in(schema));
        });
        schema.registerSimple(map, "minecraft:fireball");
        schema.register(map, "minecraft:fireworks_rocket", (s) -> {
            return DSL.optionalFields("FireworksItem", DataConverterTypes.ITEM_STACK.in(schema));
        });
        schema.register(map, "minecraft:furnace_minecart", (s) -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.BLOCK_STATE.in(schema));
        });
        a(schema, map, "minecraft:ghast");
        a(schema, map, "minecraft:giant");
        a(schema, map, "minecraft:guardian");
        schema.register(map, "minecraft:hopper_minecart", (s) -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.BLOCK_STATE.in(schema), "Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)));
        });
        schema.register(map, "minecraft:horse", (s) -> {
            return DSL.optionalFields("ArmorItem", DataConverterTypes.ITEM_STACK.in(schema), "SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.a(schema));
        });
        a(schema, map, "minecraft:husk");
        schema.registerSimple(map, "minecraft:illusion_illager");
        schema.register(map, "minecraft:item", (s) -> {
            return DSL.optionalFields("Item", DataConverterTypes.ITEM_STACK.in(schema));
        });
        schema.register(map, "minecraft:item_frame", (s) -> {
            return DSL.optionalFields("Item", DataConverterTypes.ITEM_STACK.in(schema));
        });
        schema.registerSimple(map, "minecraft:leash_knot");
        schema.register(map, "minecraft:llama", (s) -> {
            return DSL.optionalFields("Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), "DecorItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.a(schema));
        });
        schema.registerSimple(map, "minecraft:llama_spit");
        a(schema, map, "minecraft:magma_cube");
        schema.register(map, "minecraft:minecart", (s) -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.BLOCK_STATE.in(schema));
        });
        a(schema, map, "minecraft:mooshroom");
        schema.register(map, "minecraft:mule", (s) -> {
            return DSL.optionalFields("Items", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.a(schema));
        });
        a(schema, map, "minecraft:ocelot");
        schema.registerSimple(map, "minecraft:painting");
        schema.registerSimple(map, "minecraft:parrot");
        a(schema, map, "minecraft:pig");
        a(schema, map, "minecraft:polar_bear");
        schema.register(map, "minecraft:potion", (s) -> {
            return DSL.optionalFields("Potion", DataConverterTypes.ITEM_STACK.in(schema));
        });
        a(schema, map, "minecraft:rabbit");
        a(schema, map, "minecraft:sheep");
        a(schema, map, "minecraft:shulker");
        schema.registerSimple(map, "minecraft:shulker_bullet");
        a(schema, map, "minecraft:silverfish");
        a(schema, map, "minecraft:skeleton");
        schema.register(map, "minecraft:skeleton_horse", (s) -> {
            return DSL.optionalFields("SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.a(schema));
        });
        a(schema, map, "minecraft:slime");
        schema.registerSimple(map, "minecraft:small_fireball");
        schema.registerSimple(map, "minecraft:snowball");
        a(schema, map, "minecraft:snowman");
        schema.register(map, "minecraft:spawner_minecart", (s) -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.BLOCK_STATE.in(schema), DataConverterTypes.UNTAGGED_SPAWNER.in(schema));
        });
        schema.register(map, "minecraft:spectral_arrow", (s) -> {
            return DSL.optionalFields("inBlockState", DataConverterTypes.BLOCK_STATE.in(schema));
        });
        a(schema, map, "minecraft:spider");
        a(schema, map, "minecraft:squid");
        a(schema, map, "minecraft:stray");
        schema.registerSimple(map, "minecraft:tnt");
        schema.register(map, "minecraft:tnt_minecart", (s) -> {
            return DSL.optionalFields("DisplayState", DataConverterTypes.BLOCK_STATE.in(schema));
        });
        a(schema, map, "minecraft:vex");
        schema.register(map, "minecraft:villager", (s) -> {
            return DSL.optionalFields("Inventory", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", DataConverterTypes.ITEM_STACK.in(schema), "buyB", DataConverterTypes.ITEM_STACK.in(schema), "sell", DataConverterTypes.ITEM_STACK.in(schema)))), DataConverterSchemaV100.a(schema));
        });
        a(schema, map, "minecraft:villager_golem");
        a(schema, map, "minecraft:vindication_illager");
        a(schema, map, "minecraft:witch");
        a(schema, map, "minecraft:wither");
        a(schema, map, "minecraft:wither_skeleton");
        schema.registerSimple(map, "minecraft:wither_skull");
        a(schema, map, "minecraft:wolf");
        schema.registerSimple(map, "minecraft:xp_bottle");
        schema.registerSimple(map, "minecraft:xp_orb");
        a(schema, map, "minecraft:zombie");
        schema.register(map, "minecraft:zombie_horse", (s) -> {
            return DSL.optionalFields("SaddleItem", DataConverterTypes.ITEM_STACK.in(schema), DataConverterSchemaV100.a(schema));
        });
        a(schema, map, "minecraft:zombie_pigman");
        a(schema, map, "minecraft:zombie_villager");
        return map;
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();

        b(schema, map, "minecraft:furnace");
        b(schema, map, "minecraft:chest");
        b(schema, map, "minecraft:trapped_chest");
        schema.registerSimple(map, "minecraft:ender_chest");
        schema.register(map, "minecraft:jukebox", (s) -> {
            return DSL.optionalFields("RecordItem", DataConverterTypes.ITEM_STACK.in(schema));
        });
        b(schema, map, "minecraft:dispenser");
        b(schema, map, "minecraft:dropper");
        schema.registerSimple(map, "minecraft:sign");
        schema.register(map, "minecraft:mob_spawner", (s) -> {
            return DataConverterTypes.UNTAGGED_SPAWNER.in(schema);
        });
        schema.register(map, "minecraft:piston", (s) -> {
            return DSL.optionalFields("blockState", DataConverterTypes.BLOCK_STATE.in(schema));
        });
        b(schema, map, "minecraft:brewing_stand");
        schema.registerSimple(map, "minecraft:enchanting_table");
        schema.registerSimple(map, "minecraft:end_portal");
        schema.registerSimple(map, "minecraft:beacon");
        schema.registerSimple(map, "minecraft:skull");
        schema.registerSimple(map, "minecraft:daylight_detector");
        b(schema, map, "minecraft:hopper");
        schema.registerSimple(map, "minecraft:comparator");
        schema.registerSimple(map, "minecraft:banner");
        schema.registerSimple(map, "minecraft:structure_block");
        schema.registerSimple(map, "minecraft:end_gateway");
        schema.registerSimple(map, "minecraft:command_block");
        b(schema, map, "minecraft:shulker_box");
        schema.registerSimple(map, "minecraft:bed");
        return map;
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map1) {
        schema.registerType(false, DataConverterTypes.LEVEL, DSL::remainder);
        schema.registerType(false, DataConverterTypes.RECIPE, () -> {
            return DSL.constType(a());
        });
        schema.registerType(false, DataConverterTypes.PLAYER, () -> {
            return DSL.optionalFields("RootVehicle", DSL.optionalFields("Entity", DataConverterTypes.ENTITY_TREE.in(schema)), "Inventory", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), "EnderItems", DSL.list(DataConverterTypes.ITEM_STACK.in(schema)), DSL.optionalFields("ShoulderEntityLeft", DataConverterTypes.ENTITY_TREE.in(schema), "ShoulderEntityRight", DataConverterTypes.ENTITY_TREE.in(schema), "recipeBook", DSL.optionalFields("recipes", DSL.list(DataConverterTypes.RECIPE.in(schema)), "toBeDisplayed", DSL.list(DataConverterTypes.RECIPE.in(schema)))));
        });
        schema.registerType(false, DataConverterTypes.CHUNK, () -> {
            return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(DataConverterTypes.ENTITY_TREE.in(schema)), "TileEntities", DSL.list(DataConverterTypes.BLOCK_ENTITY.in(schema)), "TileTicks", DSL.list(DSL.fields("i", DataConverterTypes.BLOCK_NAME.in(schema))), "Sections", DSL.list(DSL.optionalFields("Palette", DSL.list(DataConverterTypes.BLOCK_STATE.in(schema))))));
        });
        schema.registerType(true, DataConverterTypes.BLOCK_ENTITY, () -> {
            return DSL.taggedChoiceLazy("id", a(), map1);
        });
        schema.registerType(true, DataConverterTypes.ENTITY_TREE, () -> {
            return DSL.optionalFields("Passengers", DSL.list(DataConverterTypes.ENTITY_TREE.in(schema)), DataConverterTypes.ENTITY.in(schema));
        });
        schema.registerType(true, DataConverterTypes.ENTITY, () -> {
            return DSL.taggedChoiceLazy("id", a(), map);
        });
        schema.registerType(true, DataConverterTypes.ITEM_STACK, () -> {
            return DSL.hook(DSL.optionalFields("id", DataConverterTypes.ITEM_NAME.in(schema), "tag", DSL.optionalFields("EntityTag", DataConverterTypes.ENTITY_TREE.in(schema), "BlockEntityTag", DataConverterTypes.BLOCK_ENTITY.in(schema), "CanDestroy", DSL.list(DataConverterTypes.BLOCK_NAME.in(schema)), "CanPlaceOn", DSL.list(DataConverterTypes.BLOCK_NAME.in(schema)))), DataConverterSchemaV705.b, HookFunction.IDENTITY);
        });
        schema.registerType(false, DataConverterTypes.HOTBAR, () -> {
            return DSL.compoundList(DSL.list(DataConverterTypes.ITEM_STACK.in(schema)));
        });
        schema.registerType(false, DataConverterTypes.OPTIONS, DSL::remainder);
        schema.registerType(false, DataConverterTypes.STRUCTURE, () -> {
            return DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", DataConverterTypes.ENTITY_TREE.in(schema))), "blocks", DSL.list(DSL.optionalFields("nbt", DataConverterTypes.BLOCK_ENTITY.in(schema))), "palette", DSL.list(DataConverterTypes.BLOCK_STATE.in(schema)));
        });
        schema.registerType(false, DataConverterTypes.BLOCK_NAME, () -> {
            return DSL.constType(a());
        });
        schema.registerType(false, DataConverterTypes.ITEM_NAME, () -> {
            return DSL.constType(a());
        });
        schema.registerType(false, DataConverterTypes.BLOCK_STATE, DSL::remainder);
        Supplier<TypeTemplate> supplier = () -> {
            return DSL.compoundList(DataConverterTypes.ITEM_NAME.in(schema), DSL.constType(DSL.intType()));
        };

        schema.registerType(false, DataConverterTypes.STATS, () -> {
            return DSL.optionalFields("stats", DSL.optionalFields("minecraft:mined", DSL.compoundList(DataConverterTypes.BLOCK_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:crafted", (TypeTemplate) supplier.get(), "minecraft:used", (TypeTemplate) supplier.get(), "minecraft:broken", (TypeTemplate) supplier.get(), "minecraft:picked_up", (TypeTemplate) supplier.get(), DSL.optionalFields("minecraft:dropped", (TypeTemplate) supplier.get(), "minecraft:killed", DSL.compoundList(DataConverterTypes.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:killed_by", DSL.compoundList(DataConverterTypes.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:custom", DSL.compoundList(DSL.constType(a()), DSL.constType(DSL.intType())))));
        });
        schema.registerType(false, DataConverterTypes.SAVED_DATA, () -> {
            return DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(DataConverterTypes.STRUCTURE_FEATURE.in(schema)), "Objectives", DSL.list(DataConverterTypes.OBJECTIVE.in(schema)), "Teams", DSL.list(DataConverterTypes.TEAM.in(schema))));
        });
        schema.registerType(false, DataConverterTypes.STRUCTURE_FEATURE, () -> {
            return DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", DataConverterTypes.BLOCK_STATE.in(schema), "CB", DataConverterTypes.BLOCK_STATE.in(schema), "CC", DataConverterTypes.BLOCK_STATE.in(schema), "CD", DataConverterTypes.BLOCK_STATE.in(schema))));
        });
        schema.registerType(false, DataConverterTypes.OBJECTIVE, DSL::remainder);
        schema.registerType(false, DataConverterTypes.TEAM, DSL::remainder);
        schema.registerType(true, DataConverterTypes.UNTAGGED_SPAWNER, () -> {
            return DSL.optionalFields("SpawnPotentials", DSL.list(DSL.fields("Entity", DataConverterTypes.ENTITY_TREE.in(schema))), "SpawnData", DataConverterTypes.ENTITY_TREE.in(schema));
        });
        schema.registerType(false, DataConverterTypes.ADVANCEMENTS, () -> {
            return DSL.optionalFields("minecraft:adventure/adventuring_time", DSL.optionalFields("criteria", DSL.compoundList(DataConverterTypes.BIOME.in(schema), DSL.constType(DSL.string()))), "minecraft:adventure/kill_a_mob", DSL.optionalFields("criteria", DSL.compoundList(DataConverterTypes.ENTITY_NAME.in(schema), DSL.constType(DSL.string()))), "minecraft:adventure/kill_all_mobs", DSL.optionalFields("criteria", DSL.compoundList(DataConverterTypes.ENTITY_NAME.in(schema), DSL.constType(DSL.string()))), "minecraft:husbandry/bred_all_animals", DSL.optionalFields("criteria", DSL.compoundList(DataConverterTypes.ENTITY_NAME.in(schema), DSL.constType(DSL.string()))));
        });
        schema.registerType(false, DataConverterTypes.BIOME, () -> {
            return DSL.constType(a());
        });
        schema.registerType(false, DataConverterTypes.ENTITY_NAME, () -> {
            return DSL.constType(a());
        });
        schema.registerType(false, DataConverterTypes.POI_CHUNK, DSL::remainder);
        schema.registerType(true, DataConverterTypes.WORLD_GEN_SETTINGS, DSL::remainder);
    }
}
