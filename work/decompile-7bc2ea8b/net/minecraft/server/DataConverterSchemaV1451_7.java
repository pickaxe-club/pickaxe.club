package net.minecraft.server;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class DataConverterSchemaV1451_7 extends DataConverterSchemaNamed {

    public DataConverterSchemaV1451_7(int i, Schema schema) {
        super(i, schema);
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map1) {
        super.registerTypes(schema, map, map1);
        schema.registerType(false, DataConverterTypes.STRUCTURE_FEATURE, () -> {
            return DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", DataConverterTypes.BLOCK_STATE.in(schema), "CB", DataConverterTypes.BLOCK_STATE.in(schema), "CC", DataConverterTypes.BLOCK_STATE.in(schema), "CD", DataConverterTypes.BLOCK_STATE.in(schema))));
        });
    }
}
