package net.minecraft.server;

import java.util.function.Consumer;

public class ResourcePackSourceVanilla implements ResourcePackSource {

    private final ResourcePackVanilla a = new ResourcePackVanilla(new String[]{"minecraft"});

    public ResourcePackSourceVanilla() {}

    @Override
    public void a(Consumer<ResourcePackLoader> consumer, ResourcePackLoader.a resourcepackloader_a) {
        ResourcePackLoader resourcepackloader = ResourcePackLoader.a("vanilla", false, () -> {
            return this.a;
        }, resourcepackloader_a, ResourcePackLoader.Position.BOTTOM, PackSource.b);

        if (resourcepackloader != null) {
            consumer.accept(resourcepackloader);
        }

    }
}
