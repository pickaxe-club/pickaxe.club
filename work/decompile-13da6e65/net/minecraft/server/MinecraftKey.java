package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class MinecraftKey implements Comparable<MinecraftKey> {

    public static final Codec<MinecraftKey> a = Codec.STRING.comapFlatMap(MinecraftKey::c, MinecraftKey::toString).stable();
    private static final SimpleCommandExceptionType d = new SimpleCommandExceptionType(new ChatMessage("argument.id.invalid"));
    protected final String namespace;
    protected final String key;

    protected MinecraftKey(String[] astring) {
        this.namespace = StringUtils.isEmpty(astring[0]) ? "minecraft" : astring[0];
        this.key = astring[1];
        if (!e(this.namespace)) {
            throw new ResourceKeyInvalidException("Non [a-z0-9_.-] character in namespace of location: " + this.namespace + ':' + this.key);
        } else if (!d(this.key)) {
            throw new ResourceKeyInvalidException("Non [a-z0-9/._-] character in path of location: " + this.namespace + ':' + this.key);
        }
    }

    public MinecraftKey(String s) {
        this(b(s, ':'));
    }

    public MinecraftKey(String s, String s1) {
        this(new String[]{s, s1});
    }

    public static MinecraftKey a(String s, char c0) {
        return new MinecraftKey(b(s, c0));
    }

    @Nullable
    public static MinecraftKey a(String s) {
        try {
            return new MinecraftKey(s);
        } catch (ResourceKeyInvalidException resourcekeyinvalidexception) {
            return null;
        }
    }

    protected static String[] b(String s, char c0) {
        String[] astring = new String[]{"minecraft", s};
        int i = s.indexOf(c0);

        if (i >= 0) {
            astring[1] = s.substring(i + 1, s.length());
            if (i >= 1) {
                astring[0] = s.substring(0, i);
            }
        }

        return astring;
    }

    private static DataResult<MinecraftKey> c(String s) {
        try {
            return DataResult.success(new MinecraftKey(s));
        } catch (ResourceKeyInvalidException resourcekeyinvalidexception) {
            return DataResult.error("Not a valid resource location: " + s + " " + resourcekeyinvalidexception.getMessage());
        }
    }

    public String getKey() {
        return this.key;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String toString() {
        return this.namespace + ':' + this.key;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof MinecraftKey)) {
            return false;
        } else {
            MinecraftKey minecraftkey = (MinecraftKey) object;

            return this.namespace.equals(minecraftkey.namespace) && this.key.equals(minecraftkey.key);
        }
    }

    public int hashCode() {
        return 31 * this.namespace.hashCode() + this.key.hashCode();
    }

    public int compareTo(MinecraftKey minecraftkey) {
        int i = this.key.compareTo(minecraftkey.key);

        if (i == 0) {
            i = this.namespace.compareTo(minecraftkey.namespace);
        }

        return i;
    }

    public static MinecraftKey a(StringReader stringreader) throws CommandSyntaxException {
        int i = stringreader.getCursor();

        while (stringreader.canRead() && a(stringreader.peek())) {
            stringreader.skip();
        }

        String s = stringreader.getString().substring(i, stringreader.getCursor());

        try {
            return new MinecraftKey(s);
        } catch (ResourceKeyInvalidException resourcekeyinvalidexception) {
            stringreader.setCursor(i);
            throw MinecraftKey.d.createWithContext(stringreader);
        }
    }

    public static boolean a(char c0) {
        return c0 >= '0' && c0 <= '9' || c0 >= 'a' && c0 <= 'z' || c0 == '_' || c0 == ':' || c0 == '/' || c0 == '.' || c0 == '-';
    }

    private static boolean d(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (!b(s.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static boolean e(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (!c(s.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean b(char c0) {
        return c0 == '_' || c0 == '-' || c0 >= 'a' && c0 <= 'z' || c0 >= '0' && c0 <= '9' || c0 == '/' || c0 == '.';
    }

    private static boolean c(char c0) {
        return c0 == '_' || c0 == '-' || c0 >= 'a' && c0 <= 'z' || c0 >= '0' && c0 <= '9' || c0 == '.';
    }

    public static class a implements JsonDeserializer<MinecraftKey>, JsonSerializer<MinecraftKey> {

        public a() {}

        public MinecraftKey deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            return new MinecraftKey(ChatDeserializer.a(jsonelement, "location"));
        }

        public JsonElement serialize(MinecraftKey minecraftkey, Type type, JsonSerializationContext jsonserializationcontext) {
            return new JsonPrimitive(minecraftkey.toString());
        }
    }
}
