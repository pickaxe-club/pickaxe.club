package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;

public class TileEntityJigsaw extends TileEntity {

    private MinecraftKey a;
    private MinecraftKey b;
    private MinecraftKey c;
    private TileEntityJigsaw.JointType g;
    private String h;

    public TileEntityJigsaw(TileEntityTypes<?> tileentitytypes) {
        super(tileentitytypes);
        this.a = new MinecraftKey("empty");
        this.b = new MinecraftKey("empty");
        this.c = new MinecraftKey("empty");
        this.g = TileEntityJigsaw.JointType.ROLLABLE;
        this.h = "minecraft:air";
    }

    public TileEntityJigsaw() {
        this(TileEntityTypes.JIGSAW);
    }

    public void a(MinecraftKey minecraftkey) {
        this.a = minecraftkey;
    }

    public void b(MinecraftKey minecraftkey) {
        this.b = minecraftkey;
    }

    public void c(MinecraftKey minecraftkey) {
        this.c = minecraftkey;
    }

    public void a(String s) {
        this.h = s;
    }

    public void a(TileEntityJigsaw.JointType tileentityjigsaw_jointtype) {
        this.g = tileentityjigsaw_jointtype;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.setString("name", this.a.toString());
        nbttagcompound.setString("target", this.b.toString());
        nbttagcompound.setString("pool", this.c.toString());
        nbttagcompound.setString("final_state", this.h);
        nbttagcompound.setString("joint", this.g.getName());
        return nbttagcompound;
    }

    @Override
    public void load(IBlockData iblockdata, NBTTagCompound nbttagcompound) {
        super.load(iblockdata, nbttagcompound);
        this.a = new MinecraftKey(nbttagcompound.getString("name"));
        this.b = new MinecraftKey(nbttagcompound.getString("target"));
        this.c = new MinecraftKey(nbttagcompound.getString("pool"));
        this.h = nbttagcompound.getString("final_state");
        this.g = (TileEntityJigsaw.JointType) TileEntityJigsaw.JointType.a(nbttagcompound.getString("joint")).orElseGet(() -> {
            return BlockJigsaw.h(iblockdata).n().d() ? TileEntityJigsaw.JointType.ALIGNED : TileEntityJigsaw.JointType.ROLLABLE;
        });
    }

    @Nullable
    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.position, 12, this.b());
    }

    @Override
    public NBTTagCompound b() {
        return this.save(new NBTTagCompound());
    }

    public void a(WorldServer worldserver, int i, boolean flag) {
        ChunkGenerator chunkgenerator = worldserver.getChunkProvider().getChunkGenerator();
        DefinedStructureManager definedstructuremanager = worldserver.r_();
        StructureManager structuremanager = worldserver.getStructureManager();
        Random random = worldserver.getRandom();
        BlockPosition blockposition = this.getPosition();
        List<WorldGenFeaturePillagerOutpostPoolPiece> list = Lists.newArrayList();
        DefinedStructure definedstructure = new DefinedStructure();

        definedstructure.a(worldserver, blockposition, new BlockPosition(1, 1, 1), false, (Block) null);
        WorldGenFeatureDefinedStructurePoolSingle worldgenfeaturedefinedstructurepoolsingle = new WorldGenFeatureDefinedStructurePoolSingle(definedstructure, ImmutableList.of(), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID);
        TileEntityJigsaw.b tileentityjigsaw_b = new TileEntityJigsaw.b(definedstructuremanager, worldgenfeaturedefinedstructurepoolsingle, blockposition, 1, EnumBlockRotation.NONE, new StructureBoundingBox(blockposition, blockposition));

        WorldGenFeatureDefinedStructureJigsawPlacement.a(tileentityjigsaw_b, i, TileEntityJigsaw.b::new, chunkgenerator, definedstructuremanager, list, random);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece = (WorldGenFeaturePillagerOutpostPoolPiece) iterator.next();

            worldgenfeaturepillageroutpostpoolpiece.a(worldserver, structuremanager, chunkgenerator, random, StructureBoundingBox.b(), blockposition, flag);
        }

    }

    public static final class b extends WorldGenFeaturePillagerOutpostPoolPiece {

        public b(DefinedStructureManager definedstructuremanager, WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure, BlockPosition blockposition, int i, EnumBlockRotation enumblockrotation, StructureBoundingBox structureboundingbox) {
            super(WorldGenFeatureStructurePieceType.ag, definedstructuremanager, worldgenfeaturedefinedstructurepoolstructure, blockposition, i, enumblockrotation, structureboundingbox);
        }

        public b(DefinedStructureManager definedstructuremanager, NBTTagCompound nbttagcompound) {
            super(definedstructuremanager, nbttagcompound, WorldGenFeatureStructurePieceType.ag);
        }
    }

    public static enum JointType implements INamable {

        ROLLABLE("rollable"), ALIGNED("aligned");

        private final String c;

        private JointType(String s) {
            this.c = s;
        }

        @Override
        public String getName() {
            return this.c;
        }

        public static Optional<TileEntityJigsaw.JointType> a(String s) {
            return Arrays.stream(values()).filter((tileentityjigsaw_jointtype) -> {
                return tileentityjigsaw_jointtype.getName().equals(s);
            }).findFirst();
        }
    }
}
