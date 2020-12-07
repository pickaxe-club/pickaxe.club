package net.minecraft.server;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

public class CommandMe {

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) CommandDispatcher.a("me").then(CommandDispatcher.a("action", (ArgumentType) StringArgumentType.greedyString()).executes((commandcontext) -> {
            String s = StringArgumentType.getString(commandcontext, "action");
            Entity entity = ((CommandListenerWrapper) commandcontext.getSource()).getEntity();
            MinecraftServer minecraftserver = ((CommandListenerWrapper) commandcontext.getSource()).getServer();

            if (entity != null) {
                if (entity instanceof EntityPlayer) {
                    ITextFilter itextfilter = ((EntityPlayer) entity).Q();

                    if (itextfilter != null) {
                        itextfilter.a(s).thenAcceptAsync((optional) -> {
                            optional.ifPresent((s1) -> {
                                minecraftserver.getPlayerList().sendMessage(a(commandcontext, s1), ChatMessageType.CHAT, entity.getUniqueID());
                            });
                        }, minecraftserver);
                        return 1;
                    }
                }

                minecraftserver.getPlayerList().sendMessage(a(commandcontext, s), ChatMessageType.CHAT, entity.getUniqueID());
            } else {
                minecraftserver.getPlayerList().sendMessage(a(commandcontext, s), ChatMessageType.SYSTEM, SystemUtils.b);
            }

            return 1;
        })));
    }

    private static IChatBaseComponent a(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return new ChatMessage("chat.type.emote", new Object[]{((CommandListenerWrapper) commandcontext.getSource()).getScoreboardDisplayName(), s});
    }
}
