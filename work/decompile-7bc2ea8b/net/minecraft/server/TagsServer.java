package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class TagsServer<T> extends Tags<T> {

    private final IRegistry<T> a;

    public TagsServer(IRegistry<T> iregistry, String s, String s1) {
        super(iregistry::getOptional, s, s1);
        this.a = iregistry;
    }

    public void a(PacketDataSerializer packetdataserializer) {
        Map<MinecraftKey, Tag<T>> map = this.b();

        packetdataserializer.d(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<MinecraftKey, Tag<T>> entry = (Entry) iterator.next();

            packetdataserializer.a((MinecraftKey) entry.getKey());
            packetdataserializer.d(((Tag) entry.getValue()).getTagged().size());
            Iterator iterator1 = ((Tag) entry.getValue()).getTagged().iterator();

            while (iterator1.hasNext()) {
                T t0 = iterator1.next();

                packetdataserializer.d(this.a.a(t0));
            }
        }

    }

    public void b(PacketDataSerializer packetdataserializer) {
        Map<MinecraftKey, Tag<T>> map = Maps.newHashMap();
        int i = packetdataserializer.i();

        for (int j = 0; j < i; ++j) {
            MinecraftKey minecraftkey = packetdataserializer.o();
            int k = packetdataserializer.i();
            Builder<T> builder = ImmutableSet.builder();

            for (int l = 0; l < k; ++l) {
                builder.add(this.a.fromId(packetdataserializer.i()));
            }

            map.put(minecraftkey, Tag.b(builder.build()));
        }

        this.b((Map) map);
    }
}
