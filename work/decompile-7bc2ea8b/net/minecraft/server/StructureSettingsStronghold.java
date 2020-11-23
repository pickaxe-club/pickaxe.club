package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class StructureSettingsStronghold {

    public static final Codec<StructureSettingsStronghold> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codecs.a(0, 1023).fieldOf("distance").forGetter(StructureSettingsStronghold::a), Codecs.a(0, 1023).fieldOf("spread").forGetter(StructureSettingsStronghold::b), Codecs.a(1, 4095).fieldOf("count").forGetter(StructureSettingsStronghold::c)).apply(instance, StructureSettingsStronghold::new);
    });
    private final int b;
    private final int c;
    private final int d;

    public StructureSettingsStronghold(int i, int j, int k) {
        this.b = i;
        this.c = j;
        this.d = k;
    }

    public int a() {
        return this.b;
    }

    public int b() {
        return this.c;
    }

    public int c() {
        return this.d;
    }
}
