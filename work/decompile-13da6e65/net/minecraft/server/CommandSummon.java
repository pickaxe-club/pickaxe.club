package net.minecraft.server;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class CommandSummon {

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.summon.failed"));
    private static final SimpleCommandExceptionType b = new SimpleCommandExceptionType(new ChatMessage("commands.summon.failed.uuid"));
    private static final SimpleCommandExceptionType c = new SimpleCommandExceptionType(new ChatMessage("commands.summon.invalidPosition"));

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("summon").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(((RequiredArgumentBuilder) CommandDispatcher.a("entity", (ArgumentType) ArgumentEntitySummon.a()).suggests(CompletionProviders.e).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntitySummon.a(commandcontext, "entity"), ((CommandListenerWrapper) commandcontext.getSource()).getPosition(), new NBTTagCompound(), true);
        })).then(((RequiredArgumentBuilder) CommandDispatcher.a("pos", (ArgumentType) ArgumentVec3.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntitySummon.a(commandcontext, "entity"), ArgumentVec3.a(commandcontext, "pos"), new NBTTagCompound(), true);
        })).then(CommandDispatcher.a("nbt", (ArgumentType) ArgumentNBTTag.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntitySummon.a(commandcontext, "entity"), ArgumentVec3.a(commandcontext, "pos"), ArgumentNBTTag.a(commandcontext, "nbt"), false);
        })))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, MinecraftKey minecraftkey, Vec3D vec3d, NBTTagCompound nbttagcompound, boolean flag) throws CommandSyntaxException {
        BlockPosition blockposition = new BlockPosition(vec3d);

        if (!World.l(blockposition)) {
            throw CommandSummon.c.create();
        } else {
            NBTTagCompound nbttagcompound1 = nbttagcompound.clone();

            nbttagcompound1.setString("id", minecraftkey.toString());
            WorldServer worldserver = commandlistenerwrapper.getWorld();
            Entity entity = EntityTypes.a(nbttagcompound1, worldserver, (entity1) -> {
                entity1.setPositionRotation(vec3d.x, vec3d.y, vec3d.z, entity1.yaw, entity1.pitch);
                return entity1;
            });

            if (entity == null) {
                throw CommandSummon.a.create();
            } else {
                if (flag && entity instanceof EntityInsentient) {
                    ((EntityInsentient) entity).prepare(commandlistenerwrapper.getWorld(), commandlistenerwrapper.getWorld().getDamageScaler(entity.getChunkCoordinates()), EnumMobSpawn.COMMAND, (GroupDataEntity) null, (NBTTagCompound) null);
                }

                if (!worldserver.addAllEntitiesSafely(entity)) {
                    throw CommandSummon.b.create();
                } else {
                    commandlistenerwrapper.sendMessage(new ChatMessage("commands.summon.success", new Object[]{entity.getScoreboardDisplayName()}), true);
                    return 1;
                }
            }
        }
    }
}
