package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.function.Consumer;

public class LootSelectorEmpty extends LootSelectorEntry {

    private LootSelectorEmpty(int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
        super(i, j, alootitemcondition, alootitemfunction);
    }

    @Override
    public LootEntryType a() {
        return LootEntries.a;
    }

    @Override
    public void a(Consumer<ItemStack> consumer, LootTableInfo loottableinfo) {}

    public static LootSelectorEntry.a<?> b() {
        return a(LootSelectorEmpty::new);
    }

    public static class a extends LootSelectorEntry.e<LootSelectorEmpty> {

        public a() {}

        @Override
        public LootSelectorEmpty b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
            return new LootSelectorEmpty(i, j, alootitemcondition, alootitemfunction);
        }
    }
}
