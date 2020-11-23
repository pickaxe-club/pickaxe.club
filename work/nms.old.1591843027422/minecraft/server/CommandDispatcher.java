package net.minecraft.server;

import com.google.common.collect.Maps;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import com.google.common.base.Joiner;
import java.util.LinkedHashSet;
import org.bukkit.craftbukkit.command.VanillaCommandWrapper;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;
// CraftBukkit end

public class CommandDispatcher {

    private static final Logger LOGGER = LogManager.getLogger();
    private final com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> b = new com.mojang.brigadier.CommandDispatcher();

    // CraftBukkit start
    public final CommandDispatcher init(boolean flag) {
        CommandAdvancement.a(this.b);
        CommandExecute.a(this.b);
        CommandBossBar.a(this.b);
        CommandClear.a(this.b);
        CommandClone.a(this.b);
        CommandData.a(this.b);
        CommandDatapack.a(this.b);
        CommandDebug.a(this.b);
        CommandGamemodeDefault.a(this.b);
        CommandDifficulty.a(this.b);
        CommandEffect.a(this.b);
        CommandMe.a(this.b);
        CommandEnchant.a(this.b);
        CommandXp.a(this.b);
        CommandFill.a(this.b);
        CommandForceload.a(this.b);
        CommandFunction.a(this.b);
        CommandGamemode.a(this.b);
        CommandGamerule.a(this.b);
        CommandGive.a(this.b);
        CommandHelp.a(this.b);
        CommandKick.a(this.b);
        CommandKill.a(this.b);
        CommandList.a(this.b);
        CommandLocate.a(this.b);
        CommandLoot.a(this.b);
        CommandTell.a(this.b);
        CommandParticle.a(this.b);
        CommandPlaySound.a(this.b);
        CommandPublish.a(this.b);
        CommandReload.a(this.b);
        CommandRecipe.a(this.b);
        CommandReplaceItem.a(this.b);
        CommandSay.a(this.b);
        CommandSchedule.a(this.b);
        CommandScoreboard.a(this.b);
        CommandSeed.a(this.b);
        CommandSetBlock.a(this.b);
        CommandSpawnpoint.a(this.b);
        CommandSetWorldSpawn.a(this.b);
        CommandSpectate.a(this.b);
        CommandSpreadPlayers.a(this.b);
        CommandStopSound.a(this.b);
        CommandSummon.a(this.b);
        CommandTag.a(this.b);
        CommandTeam.a(this.b);
        CommandTeamMsg.a(this.b);
        CommandTeleport.a(this.b);
        CommandTellRaw.a(this.b);
        CommandTime.a(this.b);
        CommandTitle.a(this.b);
        CommandTrigger.a(this.b);
        CommandWeather.a(this.b);
        CommandWorldBorder.a(this.b);
        if (SharedConstants.b) {
            GameTestHarnessTestCommand.a(this.b);
        }

        if (flag) {
            CommandBanIp.a(this.b);
            CommandBanList.a(this.b);
            CommandBan.a(this.b);
            CommandDeop.a(this.b);
            CommandOp.a(this.b);
            CommandPardon.a(this.b);
            CommandPardonIP.a(this.b);
            CommandSaveAll.a(this.b);
            CommandSaveOff.a(this.b);
            CommandSaveOn.a(this.b);
            CommandIdleTimeout.a(this.b);
            CommandStop.a(this.b);
            CommandWhitelist.a(this.b);
        }

        this.b.findAmbiguities((commandnode, commandnode1, commandnode2, collection) -> {
            // CommandDispatcher.LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", this.b.getPath(commandnode1), this.b.getPath(commandnode2), collection); // CraftBukkit
        });
        return this;
    }

    public CommandDispatcher() {
        // CraftBukkit end
        this.b.setConsumer((commandcontext, flag1, i) -> {
            ((CommandListenerWrapper) commandcontext.getSource()).a(commandcontext, flag1, i);
        });
    }

    // CraftBukkit start
    public int dispatchServerCommand(CommandListenerWrapper sender, String command) {
        Joiner joiner = Joiner.on(" ");
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        ServerCommandEvent event = new ServerCommandEvent(sender.getBukkitSender(), command);
        org.bukkit.Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return 0;
        }
        command = event.getCommand();

        String[] args = command.split(" ");

        String cmd = args[0];
        if (cmd.startsWith("minecraft:")) cmd = cmd.substring("minecraft:".length());
        if (cmd.startsWith("bukkit:")) cmd = cmd.substring("bukkit:".length());

        // Block disallowed commands
        if (cmd.equalsIgnoreCase("stop") || cmd.equalsIgnoreCase("kick") || cmd.equalsIgnoreCase("op")
                || cmd.equalsIgnoreCase("deop") || cmd.equalsIgnoreCase("ban") || cmd.equalsIgnoreCase("ban-ip")
                || cmd.equalsIgnoreCase("pardon") || cmd.equalsIgnoreCase("pardon-ip") || cmd.equalsIgnoreCase("reload")) {
            return 0;
        }

        // Handle vanilla commands;
        if (sender.getWorld().getServer().getCommandBlockOverride(args[0])) {
            args[0] = "minecraft:" + args[0];
        }

        return this.a(sender, joiner.join(args));
    }

    public int a(CommandListenerWrapper commandlistenerwrapper, String s) {
        return this.a(commandlistenerwrapper, s, s);
    }

    public int a(CommandListenerWrapper commandlistenerwrapper, String s, String label) {
        // CraftBukkit end
        StringReader stringreader = new StringReader(s);

        if (stringreader.canRead() && stringreader.peek() == '/') {
            stringreader.skip();
        }

        commandlistenerwrapper.getServer().getMethodProfiler().enter(s);

        byte b0;

        try {
            byte b1;

            try {
                int i = this.b.execute(stringreader, commandlistenerwrapper);

                return i;
            } catch (CommandException commandexception) {
                commandlistenerwrapper.sendFailureMessage(commandexception.a());
                b1 = 0;
                return b1;
            } catch (CommandSyntaxException commandsyntaxexception) {
                commandlistenerwrapper.sendFailureMessage(ChatComponentUtils.a(commandsyntaxexception.getRawMessage()));
                if (commandsyntaxexception.getInput() != null && commandsyntaxexception.getCursor() >= 0) {
                    int j = Math.min(commandsyntaxexception.getInput().length(), commandsyntaxexception.getCursor());
                    IChatBaseComponent ichatbasecomponent = (new ChatComponentText("")).a(EnumChatFormat.GRAY).a((chatmodifier) -> {
                        chatmodifier.setChatClickable(new ChatClickable(ChatClickable.EnumClickAction.SUGGEST_COMMAND, label)); // CraftBukkit
                    });

                    if (j > 10) {
                        ichatbasecomponent.a("...");
                    }

                    ichatbasecomponent.a(commandsyntaxexception.getInput().substring(Math.max(0, j - 10), j));
                    if (j < commandsyntaxexception.getInput().length()) {
                        IChatBaseComponent ichatbasecomponent1 = (new ChatComponentText(commandsyntaxexception.getInput().substring(j))).a(new EnumChatFormat[]{EnumChatFormat.RED, EnumChatFormat.UNDERLINE});

                        ichatbasecomponent.addSibling(ichatbasecomponent1);
                    }

                    ichatbasecomponent.addSibling((new ChatMessage("command.context.here", new Object[0])).a(new EnumChatFormat[]{EnumChatFormat.RED, EnumChatFormat.ITALIC}));
                    commandlistenerwrapper.sendFailureMessage(ichatbasecomponent);
                }

                b1 = 0;
                return b1;
            } catch (Exception exception) {
                ChatComponentText chatcomponenttext = new ChatComponentText(exception.getMessage() == null ? exception.getClass().getName() : exception.getMessage());

                if (CommandDispatcher.LOGGER.isDebugEnabled()) {
                    CommandDispatcher.LOGGER.error("Command exception: {}", s, exception);
                    StackTraceElement[] astacktraceelement = exception.getStackTrace();

                    for (int k = 0; k < Math.min(astacktraceelement.length, 3); ++k) {
                        chatcomponenttext.a("\n\n").a(astacktraceelement[k].getMethodName()).a("\n ").a(astacktraceelement[k].getFileName()).a(":").a(String.valueOf(astacktraceelement[k].getLineNumber()));
                    }
                }

                commandlistenerwrapper.sendFailureMessage((new ChatMessage("command.failed", new Object[0])).a((chatmodifier) -> {
                    chatmodifier.setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, chatcomponenttext));
                }));
                if (SharedConstants.b) {
                    commandlistenerwrapper.sendFailureMessage(new ChatComponentText(SystemUtils.d(exception)));
                    CommandDispatcher.LOGGER.error("'" + s + "' threw an exception", exception);
                }

                b0 = 0;
            }
        } finally {
            commandlistenerwrapper.getServer().getMethodProfiler().exit();
        }

        return b0;
    }

    public void a(EntityPlayer entityplayer) {
        // CraftBukkit start
        // Register Vanilla commands into builtRoot as before
        Map<CommandNode<CommandListenerWrapper>, CommandNode<ICompletionProvider>> map = Maps.newIdentityHashMap(); // Use identity to prevent aliasing issues
        RootCommandNode vanillaRoot = new RootCommandNode();

        RootCommandNode<CommandListenerWrapper> vanilla = entityplayer.server.vanillaCommandDispatcher.a().getRoot();
        map.put(vanilla, vanillaRoot);
        this.a(vanilla, vanillaRoot, entityplayer.getCommandListener(), (Map) map);

        // Now build the global commands in a second pass
        RootCommandNode<ICompletionProvider> rootcommandnode = new RootCommandNode();

        map.put(this.b.getRoot(), rootcommandnode);
        this.a(this.b.getRoot(), rootcommandnode, entityplayer.getCommandListener(), (Map) map);

        Collection<String> bukkit = new LinkedHashSet<>();
        for (CommandNode node : rootcommandnode.getChildren()) {
            bukkit.add(node.getName());
        }

        PlayerCommandSendEvent event = new PlayerCommandSendEvent(entityplayer.getBukkitEntity(), new LinkedHashSet<>(bukkit));
        event.getPlayer().getServer().getPluginManager().callEvent(event);

        // Remove labels that were removed during the event
        for (String orig : bukkit) {
            if (!event.getCommands().contains(orig)) {
                rootcommandnode.removeCommand(orig);
            }
        }
        // CraftBukkit end
        entityplayer.playerConnection.sendPacket(new PacketPlayOutCommands(rootcommandnode));
    }

    private void a(CommandNode<CommandListenerWrapper> commandnode, CommandNode<ICompletionProvider> commandnode1, CommandListenerWrapper commandlistenerwrapper, Map<CommandNode<CommandListenerWrapper>, CommandNode<ICompletionProvider>> map) {
        Iterator iterator = commandnode.getChildren().iterator();

        while (iterator.hasNext()) {
            CommandNode<CommandListenerWrapper> commandnode2 = (CommandNode) iterator.next();

            if (commandnode2.canUse(commandlistenerwrapper)) {
                ArgumentBuilder argumentbuilder = commandnode2.createBuilder(); // CraftBukkit - decompile error

                argumentbuilder.requires((icompletionprovider) -> {
                    return true;
                });
                if (argumentbuilder.getCommand() != null) {
                    argumentbuilder.executes((commandcontext) -> {
                        return 0;
                    });
                }

                if (argumentbuilder instanceof RequiredArgumentBuilder) {
                    RequiredArgumentBuilder<ICompletionProvider, ?> requiredargumentbuilder = (RequiredArgumentBuilder) argumentbuilder;

                    if (requiredargumentbuilder.getSuggestionsProvider() != null) {
                        requiredargumentbuilder.suggests(CompletionProviders.b(requiredargumentbuilder.getSuggestionsProvider()));
                    }
                }

                if (argumentbuilder.getRedirect() != null) {
                    argumentbuilder.redirect((CommandNode) map.get(argumentbuilder.getRedirect()));
                }

                CommandNode commandnode3 = argumentbuilder.build(); // CraftBukkit - decompile error

                map.put(commandnode2, commandnode3);
                commandnode1.addChild(commandnode3);
                if (!commandnode2.getChildren().isEmpty()) {
                    this.a(commandnode2, commandnode3, commandlistenerwrapper, map);
                }
            }
        }

    }

    public static LiteralArgumentBuilder<CommandListenerWrapper> a(String s) {
        return LiteralArgumentBuilder.literal(s);
    }

    public static <T> RequiredArgumentBuilder<CommandListenerWrapper, T> a(String s, ArgumentType<T> argumenttype) {
        return RequiredArgumentBuilder.argument(s, argumenttype);
    }

    public static Predicate<String> a(CommandDispatcher.a commanddispatcher_a) {
        return (s) -> {
            try {
                commanddispatcher_a.parse(new StringReader(s));
                return true;
            } catch (CommandSyntaxException commandsyntaxexception) {
                return false;
            }
        };
    }

    public com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> a() {
        return this.b;
    }

    @Nullable
    public static <S> CommandSyntaxException a(ParseResults<S> parseresults) {
        return !parseresults.getReader().canRead() ? null : (parseresults.getExceptions().size() == 1 ? (CommandSyntaxException) parseresults.getExceptions().values().iterator().next() : (parseresults.getContext().getRange().isEmpty() ? CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parseresults.getReader()) : CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parseresults.getReader())));
    }

    @FunctionalInterface
    public interface a {

        void parse(StringReader stringreader) throws CommandSyntaxException;
    }
}
