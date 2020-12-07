package net.minecraft.server;

import com.google.gson.GsonBuilder;

public class LootSerialization {

    public static GsonBuilder a() {
        return (new GsonBuilder()).registerTypeAdapter(LootValueBounds.class, new LootValueBounds.a()).registerTypeAdapter(LootValueBinomial.class, new LootValueBinomial.a()).registerTypeAdapter(LootValueConstant.class, new LootValueConstant.a()).registerTypeHierarchyAdapter(LootItemCondition.class, LootItemConditions.a()).registerTypeHierarchyAdapter(LootTableInfo.EntityTarget.class, new LootTableInfo.EntityTarget.a());
    }

    public static GsonBuilder b() {
        return a().registerTypeAdapter(LootIntegerLimit.class, new LootIntegerLimit.a()).registerTypeHierarchyAdapter(LootEntryAbstract.class, LootEntries.a()).registerTypeHierarchyAdapter(LootItemFunction.class, LootItemFunctions.a());
    }

    public static GsonBuilder c() {
        return b().registerTypeAdapter(LootSelector.class, new LootSelector.b()).registerTypeAdapter(LootTable.class, new LootTable.b());
    }
}
