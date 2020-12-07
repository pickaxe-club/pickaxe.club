package net.minecraft.server;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface IReloadListener {

    CompletableFuture<Void> a(IReloadListener.a ireloadlistener_a, IResourceManager iresourcemanager, GameProfilerFiller gameprofilerfiller, GameProfilerFiller gameprofilerfiller1, Executor executor, Executor executor1);

    default String c() {
        return this.getClass().getSimpleName();
    }

    public interface a {

        <T> CompletableFuture<T> a(T t0);
    }
}
