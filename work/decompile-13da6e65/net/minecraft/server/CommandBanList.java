package net.minecraft.server;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;

public class CommandBanList {

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("banlist").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(3);
        })).executes((commandcontext) -> {
            PlayerList playerlist = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList();

            return a((CommandListenerWrapper) commandcontext.getSource(), Lists.newArrayList(Iterables.concat(playerlist.getProfileBans().d(), playerlist.getIPBans().d())));
        })).then(CommandDispatcher.a("ips").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().getIPBans().d());
        }))).then(CommandDispatcher.a("players").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().getProfileBans().d());
        })));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<? extends ExpirableListEntry<?>> collection) {
        if (collection.isEmpty()) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.banlist.none"), false);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.banlist.list", new Object[]{collection.size()}), false);
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                ExpirableListEntry<?> expirablelistentry = (ExpirableListEntry) iterator.next();

                commandlistenerwrapper.sendMessage(new ChatMessage("commands.banlist.entry", new Object[]{expirablelistentry.e(), expirablelistentry.getSource(), expirablelistentry.getReason()}), false);
            }
        }

        return collection.size();
    }
}
