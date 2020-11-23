package net.minecraft.server;

import com.google.common.base.Joiner;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.longs.LongSet;

public class CommandForceload {

    private static final Dynamic2CommandExceptionType a = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("commands.forceload.toobig", new Object[]{object, object1});
    });
    private static final Dynamic2CommandExceptionType b = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("commands.forceload.query.failure", new Object[]{object, object1});
    });
    private static final SimpleCommandExceptionType c = new SimpleCommandExceptionType(new ChatMessage("commands.forceload.added.failure"));
    private static final SimpleCommandExceptionType d = new SimpleCommandExceptionType(new ChatMessage("commands.forceload.removed.failure"));

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("forceload").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(CommandDispatcher.a("add").then(((RequiredArgumentBuilder) CommandDispatcher.a("from", (ArgumentType) ArgumentVec2I.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentVec2I.a(commandcontext, "from"), ArgumentVec2I.a(commandcontext, "from"), true);
        })).then(CommandDispatcher.a("to", (ArgumentType) ArgumentVec2I.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentVec2I.a(commandcontext, "from"), ArgumentVec2I.a(commandcontext, "to"), true);
        }))))).then(((LiteralArgumentBuilder) CommandDispatcher.a("remove").then(((RequiredArgumentBuilder) CommandDispatcher.a("from", (ArgumentType) ArgumentVec2I.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentVec2I.a(commandcontext, "from"), ArgumentVec2I.a(commandcontext, "from"), false);
        })).then(CommandDispatcher.a("to", (ArgumentType) ArgumentVec2I.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentVec2I.a(commandcontext, "from"), ArgumentVec2I.a(commandcontext, "to"), false);
        })))).then(CommandDispatcher.a("all").executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource());
        })))).then(((LiteralArgumentBuilder) CommandDispatcher.a("query").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource());
        })).then(CommandDispatcher.a("pos", (ArgumentType) ArgumentVec2I.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentVec2I.a(commandcontext, "pos"));
        }))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BlockPosition2D blockposition2d) throws CommandSyntaxException {
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(blockposition2d.a >> 4, blockposition2d.b >> 4);
        WorldServer worldserver = commandlistenerwrapper.getWorld();
        ResourceKey<World> resourcekey = worldserver.getDimensionKey();
        boolean flag = worldserver.getForceLoadedChunks().contains(chunkcoordintpair.pair());

        if (flag) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.forceload.query.success", new Object[]{chunkcoordintpair, resourcekey.a()}), false);
            return 1;
        } else {
            throw CommandForceload.b.create(chunkcoordintpair, resourcekey.a());
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper) {
        WorldServer worldserver = commandlistenerwrapper.getWorld();
        ResourceKey<World> resourcekey = worldserver.getDimensionKey();
        LongSet longset = worldserver.getForceLoadedChunks();
        int i = longset.size();

        if (i > 0) {
            String s = Joiner.on(", ").join(longset.stream().sorted().map(ChunkCoordIntPair::new).map(ChunkCoordIntPair::toString).iterator());

            if (i == 1) {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.forceload.list.single", new Object[]{resourcekey.a(), s}), false);
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.forceload.list.multiple", new Object[]{i, resourcekey.a(), s}), false);
            }
        } else {
            commandlistenerwrapper.sendFailureMessage(new ChatMessage("commands.forceload.added.none", new Object[]{resourcekey.a()}));
        }

        return i;
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper) {
        WorldServer worldserver = commandlistenerwrapper.getWorld();
        ResourceKey<World> resourcekey = worldserver.getDimensionKey();
        LongSet longset = worldserver.getForceLoadedChunks();

        longset.forEach((i) -> {
            worldserver.setForceLoaded(ChunkCoordIntPair.getX(i), ChunkCoordIntPair.getZ(i), false);
        });
        commandlistenerwrapper.sendMessage(new ChatMessage("commands.forceload.removed.all", new Object[]{resourcekey.a()}), true);
        return 0;
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BlockPosition2D blockposition2d, BlockPosition2D blockposition2d1, boolean flag) throws CommandSyntaxException {
        int i = Math.min(blockposition2d.a, blockposition2d1.a);
        int j = Math.min(blockposition2d.b, blockposition2d1.b);
        int k = Math.max(blockposition2d.a, blockposition2d1.a);
        int l = Math.max(blockposition2d.b, blockposition2d1.b);

        if (i >= -30000000 && j >= -30000000 && k < 30000000 && l < 30000000) {
            int i1 = i >> 4;
            int j1 = j >> 4;
            int k1 = k >> 4;
            int l1 = l >> 4;
            long i2 = ((long) (k1 - i1) + 1L) * ((long) (l1 - j1) + 1L);

            if (i2 > 256L) {
                throw CommandForceload.a.create(256, i2);
            } else {
                WorldServer worldserver = commandlistenerwrapper.getWorld();
                ResourceKey<World> resourcekey = worldserver.getDimensionKey();
                ChunkCoordIntPair chunkcoordintpair = null;
                int j2 = 0;

                for (int k2 = i1; k2 <= k1; ++k2) {
                    for (int l2 = j1; l2 <= l1; ++l2) {
                        boolean flag1 = worldserver.setForceLoaded(k2, l2, flag);

                        if (flag1) {
                            ++j2;
                            if (chunkcoordintpair == null) {
                                chunkcoordintpair = new ChunkCoordIntPair(k2, l2);
                            }
                        }
                    }
                }

                if (j2 == 0) {
                    throw (flag ? CommandForceload.c : CommandForceload.d).create();
                } else {
                    if (j2 == 1) {
                        commandlistenerwrapper.sendMessage(new ChatMessage("commands.forceload." + (flag ? "added" : "removed") + ".single", new Object[]{chunkcoordintpair, resourcekey.a()}), true);
                    } else {
                        ChunkCoordIntPair chunkcoordintpair1 = new ChunkCoordIntPair(i1, j1);
                        ChunkCoordIntPair chunkcoordintpair2 = new ChunkCoordIntPair(k1, l1);

                        commandlistenerwrapper.sendMessage(new ChatMessage("commands.forceload." + (flag ? "added" : "removed") + ".multiple", new Object[]{j2, resourcekey.a(), chunkcoordintpair1, chunkcoordintpair2}), true);
                    }

                    return j2;
                }
            }
        } else {
            throw ArgumentPosition.b.create();
        }
    }
}
