package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public class LootItemFunctionSetTable extends LootItemFunctionConditional {

    private final MinecraftKey a;
    private final long c;

    private LootItemFunctionSetTable(LootItemCondition[] alootitemcondition, MinecraftKey minecraftkey, long i) {
        super(alootitemcondition);
        this.a = minecraftkey;
        this.c = i;
    }

    @Override
    public ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        if (itemstack.isEmpty()) {
            return itemstack;
        } else {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            nbttagcompound.setString("LootTable", this.a.toString());
            if (this.c != 0L) {
                nbttagcompound.setLong("LootTableSeed", this.c);
            }

            itemstack.getOrCreateTag().set("BlockEntityTag", nbttagcompound);
            return itemstack;
        }
    }

    @Override
    public void a(LootCollector lootcollector) {
        if (lootcollector.a(this.a)) {
            lootcollector.a("Table " + this.a + " is recursively called");
        } else {
            super.a(lootcollector);
            LootTable loottable = lootcollector.c(this.a);

            if (loottable == null) {
                lootcollector.a("Unknown loot table called " + this.a);
            } else {
                loottable.a(lootcollector.a("->{" + this.a + "}", this.a));
            }

        }
    }

    public static class a extends LootItemFunctionConditional.c<LootItemFunctionSetTable> {

        protected a() {
            super(new MinecraftKey("set_loot_table"), LootItemFunctionSetTable.class);
        }

        public void a(JsonObject jsonobject, LootItemFunctionSetTable lootitemfunctionsettable, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) lootitemfunctionsettable, jsonserializationcontext);
            jsonobject.addProperty("name", lootitemfunctionsettable.a.toString());
            if (lootitemfunctionsettable.c != 0L) {
                jsonobject.addProperty("seed", lootitemfunctionsettable.c);
            }

        }

        @Override
        public LootItemFunctionSetTable b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "name"));
            long i = ChatDeserializer.a(jsonobject, "seed", 0L);

            return new LootItemFunctionSetTable(alootitemcondition, minecraftkey, i);
        }
    }
}
