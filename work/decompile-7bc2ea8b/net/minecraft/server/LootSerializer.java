package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public interface LootSerializer<T> {

    void a(JsonObject jsonobject, T t0, JsonSerializationContext jsonserializationcontext);

    T a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext);
}
