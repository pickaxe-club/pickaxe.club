package net.minecraft.server;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class DataConverterShulker extends DataConverterNamedEntity {

    public DataConverterShulker(Schema schema, boolean flag) {
        super(schema, flag, "EntityShulkerColorFix", DataConverterTypes.ENTITY, "minecraft:shulker");
    }

    public Dynamic<?> a(Dynamic<?> dynamic) {
        return !dynamic.get("Color").map(Dynamic::asNumber).result().isPresent() ? dynamic.set("Color", dynamic.createByte((byte) 10)) : dynamic;
    }

    @Override
    protected Typed<?> a(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::a);
    }
}
