package net.minecraft.server;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class DataConverterSchemaV1904 extends DataConverterSchemaNamed {

    public DataConverterSchemaV1904(int i, Schema schema) {
        super(i, schema);
    }

    protected static void a(Schema schema, Map<String, Supplier<TypeTemplate>> map, String s) {
        schema.register(map, s, () -> {
            return DataConverterSchemaV100.a(schema);
        });
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);

        a(schema, map, "minecraft:cat");
        return map;
    }
}
