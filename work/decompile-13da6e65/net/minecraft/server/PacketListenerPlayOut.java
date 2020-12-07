package net.minecraft.server;

public interface PacketListenerPlayOut extends PacketListener {

    void a(PacketPlayOutSpawnEntity packetplayoutspawnentity);

    void a(PacketPlayOutSpawnEntityExperienceOrb packetplayoutspawnentityexperienceorb);

    void a(PacketPlayOutSpawnEntityLiving packetplayoutspawnentityliving);

    void a(PacketPlayOutScoreboardObjective packetplayoutscoreboardobjective);

    void a(PacketPlayOutSpawnEntityPainting packetplayoutspawnentitypainting);

    void a(PacketPlayOutNamedEntitySpawn packetplayoutnamedentityspawn);

    void a(PacketPlayOutAnimation packetplayoutanimation);

    void a(PacketPlayOutStatistic packetplayoutstatistic);

    void a(PacketPlayOutRecipes packetplayoutrecipes);

    void a(PacketPlayOutBlockBreakAnimation packetplayoutblockbreakanimation);

    void a(PacketPlayOutOpenSignEditor packetplayoutopensigneditor);

    void a(PacketPlayOutTileEntityData packetplayouttileentitydata);

    void a(PacketPlayOutBlockAction packetplayoutblockaction);

    void a(PacketPlayOutBlockChange packetplayoutblockchange);

    void a(PacketPlayOutChat packetplayoutchat);

    void a(PacketPlayOutMultiBlockChange packetplayoutmultiblockchange);

    void a(PacketPlayOutMap packetplayoutmap);

    void a(PacketPlayOutTransaction packetplayouttransaction);

    void a(PacketPlayOutCloseWindow packetplayoutclosewindow);

    void a(PacketPlayOutWindowItems packetplayoutwindowitems);

    void a(PacketPlayOutOpenWindowHorse packetplayoutopenwindowhorse);

    void a(PacketPlayOutWindowData packetplayoutwindowdata);

    void a(PacketPlayOutSetSlot packetplayoutsetslot);

    void a(PacketPlayOutCustomPayload packetplayoutcustompayload);

    void a(PacketPlayOutKickDisconnect packetplayoutkickdisconnect);

    void a(PacketPlayOutEntityStatus packetplayoutentitystatus);

    void a(PacketPlayOutAttachEntity packetplayoutattachentity);

    void a(PacketPlayOutMount packetplayoutmount);

    void a(PacketPlayOutExplosion packetplayoutexplosion);

    void a(PacketPlayOutGameStateChange packetplayoutgamestatechange);

    void a(PacketPlayOutKeepAlive packetplayoutkeepalive);

    void a(PacketPlayOutMapChunk packetplayoutmapchunk);

    void a(PacketPlayOutUnloadChunk packetplayoutunloadchunk);

    void a(PacketPlayOutWorldEvent packetplayoutworldevent);

    void a(PacketPlayOutLogin packetplayoutlogin);

    void a(PacketPlayOutEntity packetplayoutentity);

    void a(PacketPlayOutPosition packetplayoutposition);

    void a(PacketPlayOutWorldParticles packetplayoutworldparticles);

    void a(PacketPlayOutAbilities packetplayoutabilities);

    void a(PacketPlayOutPlayerInfo packetplayoutplayerinfo);

    void a(PacketPlayOutEntityDestroy packetplayoutentitydestroy);

    void a(PacketPlayOutRemoveEntityEffect packetplayoutremoveentityeffect);

    void a(PacketPlayOutRespawn packetplayoutrespawn);

    void a(PacketPlayOutEntityHeadRotation packetplayoutentityheadrotation);

    void a(PacketPlayOutHeldItemSlot packetplayouthelditemslot);

    void a(PacketPlayOutScoreboardDisplayObjective packetplayoutscoreboarddisplayobjective);

    void a(PacketPlayOutEntityMetadata packetplayoutentitymetadata);

    void a(PacketPlayOutEntityVelocity packetplayoutentityvelocity);

    void a(PacketPlayOutEntityEquipment packetplayoutentityequipment);

    void a(PacketPlayOutExperience packetplayoutexperience);

    void a(PacketPlayOutUpdateHealth packetplayoutupdatehealth);

    void a(PacketPlayOutScoreboardTeam packetplayoutscoreboardteam);

    void a(PacketPlayOutScoreboardScore packetplayoutscoreboardscore);

    void a(PacketPlayOutSpawnPosition packetplayoutspawnposition);

    void a(PacketPlayOutUpdateTime packetplayoutupdatetime);

    void a(PacketPlayOutNamedSoundEffect packetplayoutnamedsoundeffect);

    void a(PacketPlayOutEntitySound packetplayoutentitysound);

    void a(PacketPlayOutCustomSoundEffect packetplayoutcustomsoundeffect);

    void a(PacketPlayOutCollect packetplayoutcollect);

    void a(PacketPlayOutEntityTeleport packetplayoutentityteleport);

    void a(PacketPlayOutUpdateAttributes packetplayoutupdateattributes);

    void a(PacketPlayOutEntityEffect packetplayoutentityeffect);

    void a(PacketPlayOutTags packetplayouttags);

    void a(PacketPlayOutCombatEvent packetplayoutcombatevent);

    void a(PacketPlayOutServerDifficulty packetplayoutserverdifficulty);

    void a(PacketPlayOutCamera packetplayoutcamera);

    void a(PacketPlayOutWorldBorder packetplayoutworldborder);

    void a(PacketPlayOutTitle packetplayouttitle);

    void a(PacketPlayOutPlayerListHeaderFooter packetplayoutplayerlistheaderfooter);

    void a(PacketPlayOutResourcePackSend packetplayoutresourcepacksend);

    void a(PacketPlayOutBoss packetplayoutboss);

    void a(PacketPlayOutSetCooldown packetplayoutsetcooldown);

    void a(PacketPlayOutVehicleMove packetplayoutvehiclemove);

    void a(PacketPlayOutAdvancements packetplayoutadvancements);

    void a(PacketPlayOutSelectAdvancementTab packetplayoutselectadvancementtab);

    void a(PacketPlayOutAutoRecipe packetplayoutautorecipe);

    void a(PacketPlayOutCommands packetplayoutcommands);

    void a(PacketPlayOutStopSound packetplayoutstopsound);

    void a(PacketPlayOutTabComplete packetplayouttabcomplete);

    void a(PacketPlayOutRecipeUpdate packetplayoutrecipeupdate);

    void a(PacketPlayOutLookAt packetplayoutlookat);

    void a(PacketPlayOutNBTQuery packetplayoutnbtquery);

    void a(PacketPlayOutLightUpdate packetplayoutlightupdate);

    void a(PacketPlayOutOpenBook packetplayoutopenbook);

    void a(PacketPlayOutOpenWindow packetplayoutopenwindow);

    void a(PacketPlayOutOpenWindowMerchant packetplayoutopenwindowmerchant);

    void a(PacketPlayOutViewDistance packetplayoutviewdistance);

    void a(PacketPlayOutViewCentre packetplayoutviewcentre);

    void a(PacketPlayOutBlockBreak packetplayoutblockbreak);
}
