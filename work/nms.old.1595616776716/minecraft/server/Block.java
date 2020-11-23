package net.minecraft.server;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Block implements IMaterial {

    protected static final Logger LOGGER = LogManager.getLogger();
    public static final RegistryBlockID<IBlockData> REGISTRY_ID = new RegistryBlockID<>();
    private static final EnumDirection[] a = new EnumDirection[]{EnumDirection.WEST, EnumDirection.EAST, EnumDirection.NORTH, EnumDirection.SOUTH, EnumDirection.DOWN, EnumDirection.UP};
    private static final LoadingCache<VoxelShape, Boolean> b = CacheBuilder.newBuilder().maximumSize(512L).weakKeys().build(new CacheLoader<VoxelShape, Boolean>() {
        public Boolean load(VoxelShape voxelshape) {
            return !VoxelShapes.c(VoxelShapes.b(), voxelshape, OperatorBoolean.NOT_SAME);
        }
    });
    private static final VoxelShape c = VoxelShapes.a(VoxelShapes.b(), a(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D), OperatorBoolean.ONLY_FIRST);
    private static final VoxelShape d = a(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D);
    protected final int n;
    public final float strength;
    protected final float durability;
    protected final boolean q;
    protected final SoundEffectType stepSound;
    protected final Material material;
    protected final MaterialMapColor t;
    private final float frictionFactor;
    private final float f;
    private final float g;
    protected final BlockStateList<Block, IBlockData> blockStateList;
    private IBlockData blockData;
    protected final boolean v;
    private final boolean i;
    private final boolean j;
    @Nullable
    private MinecraftKey k;
    @Nullable
    private String name;
    @Nullable
    private Item x;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.a>> y = ThreadLocal.withInitial(() -> {
        Object2ByteLinkedOpenHashMap<Block.a> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.a>(2048, 0.25F) {
            protected void rehash(int i) {}
        };

        object2bytelinkedopenhashmap.defaultReturnValue((byte) 127);
        return object2bytelinkedopenhashmap;
    });

    public static int getCombinedId(@Nullable IBlockData iblockdata) {
        if (iblockdata == null) {
            return 0;
        } else {
            int i = Block.REGISTRY_ID.getId(iblockdata);

            return i == -1 ? 0 : i;
        }
    }

    public static IBlockData getByCombinedId(int i) {
        IBlockData iblockdata = (IBlockData) Block.REGISTRY_ID.fromId(i);

        return iblockdata == null ? Blocks.AIR.getBlockData() : iblockdata;
    }

    public static Block asBlock(@Nullable Item item) {
        return item instanceof ItemBlock ? ((ItemBlock) item).getBlock() : Blocks.AIR;
    }

    public static IBlockData a(IBlockData iblockdata, IBlockData iblockdata1, World world, BlockPosition blockposition) {
        VoxelShape voxelshape = VoxelShapes.b(iblockdata.getCollisionShape(world, blockposition), iblockdata1.getCollisionShape(world, blockposition), OperatorBoolean.ONLY_SECOND).a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
        List<Entity> list = world.getEntities((Entity) null, voxelshape.getBoundingBox());
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();
            double d0 = VoxelShapes.a(EnumDirection.EnumAxis.Y, entity.getBoundingBox().d(0.0D, 1.0D, 0.0D), Stream.of(voxelshape), -1.0D);

            entity.enderTeleportTo(entity.locX(), entity.locY() + 1.0D + d0, entity.locZ());
        }

        return iblockdata1;
    }

    public static VoxelShape a(double d0, double d1, double d2, double d3, double d4, double d5) {
        return VoxelShapes.create(d0 / 16.0D, d1 / 16.0D, d2 / 16.0D, d3 / 16.0D, d4 / 16.0D, d5 / 16.0D);
    }

    @Deprecated
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EntityTypes<?> entitytypes) {
        return iblockdata.d(iblockaccess, blockposition, EnumDirection.UP) && this.n < 14;
    }

    @Deprecated
    public boolean d(IBlockData iblockdata) {
        return false;
    }

    @Deprecated
    public int a(IBlockData iblockdata) {
        return this.n;
    }

    @Deprecated
    public Material k(IBlockData iblockdata) {
        return this.material;
    }

    @Deprecated
    public MaterialMapColor e(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.t;
    }

    @Deprecated
    public void a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
        Throwable throwable = null;

        try {
            EnumDirection[] aenumdirection = Block.a;
            int j = aenumdirection.length;

            for (int k = 0; k < j; ++k) {
                EnumDirection enumdirection = aenumdirection[k];

                blockposition_pooledblockposition.g(blockposition).c(enumdirection);
                IBlockData iblockdata1 = generatoraccess.getType(blockposition_pooledblockposition);
                IBlockData iblockdata2 = iblockdata1.updateState(enumdirection.opposite(), iblockdata, generatoraccess, blockposition_pooledblockposition, blockposition);

                a(iblockdata1, iblockdata2, generatoraccess, blockposition_pooledblockposition, i);
            }
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_pooledblockposition != null) {
                if (throwable != null) {
                    try {
                        blockposition_pooledblockposition.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_pooledblockposition.close();
                }
            }

        }

    }

    public boolean a(Tag<Block> tag) {
        return tag.isTagged(this);
    }

    public static IBlockData b(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        IBlockData iblockdata1 = iblockdata;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        EnumDirection[] aenumdirection = Block.a;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            blockposition_mutableblockposition.g(blockposition).c(enumdirection);
            iblockdata1 = iblockdata1.updateState(enumdirection, generatoraccess.getType(blockposition_mutableblockposition), generatoraccess, blockposition, blockposition_mutableblockposition);
        }

        return iblockdata1;
    }

    public static void a(IBlockData iblockdata, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        if (iblockdata1 != iblockdata) {
            if (iblockdata1.isAir()) {
                if (!generatoraccess.p_()) {
                    generatoraccess.b(blockposition, (i & 32) == 0);
                }
            } else {
                generatoraccess.setTypeAndData(blockposition, iblockdata1, i & -33);
            }
        }

    }

    @Deprecated
    public void b(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {}

    @Deprecated
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return iblockdata;
    }

    @Deprecated
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata;
    }

    @Deprecated
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata;
    }

    public Block(Block.Info block_info) {
        BlockStateList.a<Block, IBlockData> blockstatelist_a = new BlockStateList.a<>(this);

        this.a(blockstatelist_a);
        this.material = block_info.a;
        this.t = block_info.b;
        this.v = block_info.c;
        this.stepSound = block_info.d;
        this.n = block_info.e;
        this.durability = block_info.f;
        this.strength = block_info.g;
        this.q = block_info.h;
        this.frictionFactor = block_info.i;
        this.f = block_info.j;
        this.g = block_info.k;
        this.i = block_info.n;
        this.k = block_info.l;
        this.j = block_info.m;
        this.blockStateList = blockstatelist_a.a(IBlockData::new);
        this.p((IBlockData) this.blockStateList.getBlockData());
    }

    public static boolean a(Block block) {
        return block instanceof BlockLeaves || block == Blocks.BARRIER || block == Blocks.CARVED_PUMPKIN || block == Blocks.JACK_O_LANTERN || block == Blocks.MELON || block == Blocks.PUMPKIN || block.a(TagsBlock.SHULKER_BOXES);
    }

    @Deprecated
    public boolean isOccluding(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.getMaterial().f() && iblockdata.p(iblockaccess, blockposition) && !iblockdata.isPowerSource();
    }

    @Deprecated
    public boolean c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.material.isSolid() && iblockdata.p(iblockaccess, blockposition);
    }

    @Deprecated
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        switch (pathmode) {
            case LAND:
                return !iblockdata.p(iblockaccess, blockposition);
            case WATER:
                return iblockaccess.getFluid(blockposition).a(TagsFluid.WATER);
            case AIR:
                return !iblockdata.p(iblockaccess, blockposition);
            default:
                return false;
        }
    }

    @Deprecated
    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Deprecated
    public boolean a(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        return this.material.isReplaceable() && (blockactioncontext.getItemStack().isEmpty() || blockactioncontext.getItemStack().getItem() != this.getItem());
    }

    @Deprecated
    public boolean a(IBlockData iblockdata, FluidType fluidtype) {
        return this.material.isReplaceable() || !this.material.isBuildable();
    }

    @Deprecated
    public float g(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.strength;
    }

    public boolean isTicking(IBlockData iblockdata) {
        return this.q;
    }

    public boolean isTileEntity() {
        return this instanceof ITileEntity;
    }

    @Deprecated
    public boolean h(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return false;
    }

    @Deprecated
    public final boolean n(IBlockData iblockdata) {
        return this.j;
    }

    @Deprecated
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return VoxelShapes.b();
    }

    @Deprecated
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.v ? iblockdata.getShape(iblockaccess, blockposition) : VoxelShapes.a();
    }

    @Deprecated
    public VoxelShape i(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.getShape(iblockaccess, blockposition);
    }

    @Deprecated
    public VoxelShape j(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return VoxelShapes.a();
    }

    public static boolean c(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);

        return !iblockdata.a(TagsBlock.LEAVES) && !VoxelShapes.c(iblockdata.getCollisionShape(iblockaccess, blockposition).a(EnumDirection.UP), Block.c, OperatorBoolean.ONLY_SECOND);
    }

    public static boolean a(IWorldReader iworldreader, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = iworldreader.getType(blockposition);

        return !iblockdata.a(TagsBlock.LEAVES) && !VoxelShapes.c(iblockdata.getCollisionShape(iworldreader, blockposition).a(enumdirection), Block.d, OperatorBoolean.ONLY_SECOND);
    }

    public static boolean d(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return !iblockdata.a(TagsBlock.LEAVES) && a(iblockdata.getCollisionShape(iblockaccess, blockposition), enumdirection);
    }

    public static boolean a(VoxelShape voxelshape, EnumDirection enumdirection) {
        VoxelShape voxelshape1 = voxelshape.a(enumdirection);

        return a(voxelshape1);
    }

    public static boolean a(VoxelShape voxelshape) {
        return (Boolean) Block.b.getUnchecked(voxelshape);
    }

    @Deprecated
    public final boolean k(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.o() ? a(iblockdata.j(iblockaccess, blockposition)) : false;
    }

    public boolean b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return !a(iblockdata.getShape(iblockaccess, blockposition)) && iblockdata.getFluid().isEmpty();
    }

    @Deprecated
    public int l(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.g(iblockaccess, blockposition) ? iblockaccess.H() : (iblockdata.a(iblockaccess, blockposition) ? 0 : 1);
    }

    @Deprecated
    public boolean o(IBlockData iblockdata) {
        return false;
    }

    @Deprecated
    public void b(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        this.tick(iblockdata, worldserver, blockposition, random);
    }

    @Deprecated
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {}

    public void postBreak(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {}

    @Deprecated
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        PacketDebug.a(world, blockposition);
    }

    public int a(IWorldReader iworldreader) {
        return 10;
    }

    @Nullable
    @Deprecated
    public ITileInventory getInventory(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return null;
    }

    @Deprecated
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {}

    @Deprecated
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (this.isTileEntity() && iblockdata.getBlock() != iblockdata1.getBlock()) {
            world.removeTileEntity(blockposition);
        }

    }

    @Deprecated
    public float getDamage(IBlockData iblockdata, EntityHuman entityhuman, IBlockAccess iblockaccess, BlockPosition blockposition) {
        float f = iblockdata.f(iblockaccess, blockposition);

        if (f == -1.0F) {
            return 0.0F;
        } else {
            int i = entityhuman.hasBlock(iblockdata) ? 30 : 100;

            return entityhuman.b(iblockdata) / f / (float) i;
        }
    }

    @Deprecated
    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, ItemStack itemstack) {}

    public MinecraftKey g() {
        if (this.k == null) {
            MinecraftKey minecraftkey = IRegistry.BLOCK.getKey(this);

            this.k = new MinecraftKey(minecraftkey.getNamespace(), "blocks/" + minecraftkey.getKey());
        }

        return this.k;
    }

    @Deprecated
    public List<ItemStack> a(IBlockData iblockdata, LootTableInfo.Builder loottableinfo_builder) {
        MinecraftKey minecraftkey = this.g();

        if (minecraftkey == LootTables.a) {
            return Collections.emptyList();
        } else {
            LootTableInfo loottableinfo = loottableinfo_builder.set(LootContextParameters.BLOCK_STATE, iblockdata).build(LootContextParameterSets.BLOCK);
            WorldServer worldserver = loottableinfo.c();
            LootTable loottable = worldserver.getMinecraftServer().getLootTableRegistry().getLootTable(minecraftkey);

            return loottable.populateLoot(loottableinfo);
        }
    }

    public static List<ItemStack> a(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, @Nullable TileEntity tileentity) {
        LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder(worldserver)).a(worldserver.random).set(LootContextParameters.POSITION, blockposition).set(LootContextParameters.TOOL, ItemStack.a).setOptional(LootContextParameters.BLOCK_ENTITY, tileentity);

        return iblockdata.a(loottableinfo_builder);
    }

    public static List<ItemStack> getDrops(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, @Nullable TileEntity tileentity, @Nullable Entity entity, ItemStack itemstack) {
        LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder(worldserver)).a(worldserver.random).set(LootContextParameters.POSITION, blockposition).set(LootContextParameters.TOOL, itemstack).setOptional(LootContextParameters.THIS_ENTITY, entity).setOptional(LootContextParameters.BLOCK_ENTITY, tileentity);

        return iblockdata.a(loottableinfo_builder);
    }

    public static void c(IBlockData iblockdata, World world, BlockPosition blockposition) {
        if (world instanceof WorldServer) {
            a(iblockdata, (WorldServer) world, blockposition, (TileEntity) null).forEach((itemstack) -> {
                a(world, blockposition, itemstack);
            });
        }

        iblockdata.dropNaturally(world, blockposition, ItemStack.a);
    }

    public static void a(IBlockData iblockdata, World world, BlockPosition blockposition, @Nullable TileEntity tileentity) {
        if (world instanceof WorldServer) {
            a(iblockdata, (WorldServer) world, blockposition, tileentity).forEach((itemstack) -> {
                a(world, blockposition, itemstack);
            });
        }

        iblockdata.dropNaturally(world, blockposition, ItemStack.a);
    }

    public static void dropItems(IBlockData iblockdata, World world, BlockPosition blockposition, @Nullable TileEntity tileentity, Entity entity, ItemStack itemstack) {
        if (world instanceof WorldServer) {
            getDrops(iblockdata, (WorldServer) world, blockposition, tileentity, entity, itemstack).forEach((itemstack1) -> {
                a(world, blockposition, itemstack1);
            });
        }

        iblockdata.dropNaturally(world, blockposition, itemstack);
    }

    public static void a(World world, BlockPosition blockposition, ItemStack itemstack) {
        if (!world.isClientSide && !itemstack.isEmpty() && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            float f = 0.5F;
            double d0 = (double) (world.random.nextFloat() * 0.5F) + 0.25D;
            double d1 = (double) (world.random.nextFloat() * 0.5F) + 0.25D;
            double d2 = (double) (world.random.nextFloat() * 0.5F) + 0.25D;
            EntityItem entityitem = new EntityItem(world, (double) blockposition.getX() + d0, (double) blockposition.getY() + d1, (double) blockposition.getZ() + d2, itemstack);

            entityitem.defaultPickupDelay();
            // CraftBukkit start
            if (world.captureDrops != null) {
                world.captureDrops.add(entityitem);
            } else {
                world.addEntity(entityitem);
            }
            // CraftBukkit end
        }
    }

    protected void dropExperience(World world, BlockPosition blockposition, int i) {
        if (!world.isClientSide && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            while (i > 0) {
                int j = EntityExperienceOrb.getOrbValue(i);

                i -= j;
                world.addEntity(new EntityExperienceOrb(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, j));
            }
        }

    }

    public float getDurability() {
        return this.durability;
    }

    public void wasExploded(World world, BlockPosition blockposition, Explosion explosion) {}

    @Deprecated
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return true;
    }

    @Deprecated
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        return EnumInteractionResult.PASS;
    }

    public void stepOn(World world, BlockPosition blockposition, Entity entity) {}

    @Nullable
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return this.getBlockData();
    }

    @Deprecated
    public void attack(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {}

    @Deprecated
    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return 0;
    }

    @Deprecated
    public boolean isPowerSource(IBlockData iblockdata) {
        return false;
    }

    @Deprecated
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {}

    @Deprecated
    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return 0;
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        entityhuman.b(StatisticList.BLOCK_MINED.b(this));
        entityhuman.applyExhaustion(0.005F);
        dropItems(iblockdata, world, blockposition, tileentity, entityhuman, itemstack);
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {}

    public boolean Y_() {
        return !this.material.isBuildable() && !this.material.isLiquid();
    }

    public String k() {
        if (this.name == null) {
            this.name = SystemUtils.a("block", IRegistry.BLOCK.getKey(this));
        }

        return this.name;
    }

    @Deprecated
    public boolean a(IBlockData iblockdata, World world, BlockPosition blockposition, int i, int j) {
        return false;
    }

    @Deprecated
    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return this.material.getPushReaction();
    }

    public void fallOn(World world, BlockPosition blockposition, Entity entity, float f) {
        entity.b(f, 1.0F);
    }

    public void a(IBlockAccess iblockaccess, Entity entity) {
        entity.setMot(entity.getMot().d(1.0D, 0.0D, 1.0D));
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        nonnulllist.add(new ItemStack(this));
    }

    @Deprecated
    public Fluid a_(IBlockData iblockdata) {
        return FluidTypes.EMPTY.h();
    }

    public float l() {
        return this.frictionFactor;
    }

    public float m() {
        return this.f;
    }

    public float n() {
        return this.g;
    }

    public void a(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, Entity entity) {}

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        world.a(entityhuman, 2001, blockposition, getCombinedId(iblockdata));
    }

    public void c(World world, BlockPosition blockposition) {}

    public boolean a(Explosion explosion) {
        return true;
    }

    @Deprecated
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return false;
    }

    @Deprecated
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return 0;
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {}

    public BlockStateList<Block, IBlockData> getStates() {
        return this.blockStateList;
    }

    protected final void p(IBlockData iblockdata) {
        this.blockData = iblockdata;
    }

    public final IBlockData getBlockData() {
        return this.blockData;
    }

    public Block.EnumRandomOffset X_() {
        return Block.EnumRandomOffset.NONE;
    }

    @Deprecated
    public Vec3D m(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        Block.EnumRandomOffset block_enumrandomoffset = this.X_();

        if (block_enumrandomoffset == Block.EnumRandomOffset.NONE) {
            return Vec3D.a;
        } else {
            long i = MathHelper.c(blockposition.getX(), 0, blockposition.getZ());

            return new Vec3D(((double) ((float) (i & 15L) / 15.0F) - 0.5D) * 0.5D, block_enumrandomoffset == Block.EnumRandomOffset.XYZ ? ((double) ((float) (i >> 4 & 15L) / 15.0F) - 1.0D) * 0.2D : 0.0D, ((double) ((float) (i >> 8 & 15L) / 15.0F) - 0.5D) * 0.5D);
        }
    }

    public SoundEffectType getStepSound(IBlockData iblockdata) {
        return this.stepSound;
    }

    @Override
    public Item getItem() {
        if (this.x == null) {
            this.x = Item.getItemOf(this);
        }

        return this.x;
    }

    public boolean q() {
        return this.i;
    }

    public String toString() {
        return "Block{" + IRegistry.BLOCK.getKey(this) + "}";
    }

    // CraftBukkit start
    public int getExpDrop(IBlockData iblockdata, World world, BlockPosition blockposition, ItemStack itemstack) {
        return 0;
    }
    // CraftBukkit end

    public static enum EnumRandomOffset {

        NONE, XZ, XYZ;

        private EnumRandomOffset() {}
    }

    public static class Info {

        private Material a;
        private MaterialMapColor b;
        private boolean c = true;
        private SoundEffectType d;
        private int e;
        private float f;
        private float g;
        private boolean h;
        private float i;
        private float j;
        private float k;
        private MinecraftKey l;
        private boolean m;
        private boolean n;

        private Info(Material material, MaterialMapColor materialmapcolor) {
            this.d = SoundEffectType.d;
            this.i = 0.6F;
            this.j = 1.0F;
            this.k = 1.0F;
            this.m = true;
            this.a = material;
            this.b = materialmapcolor;
        }

        public static Block.Info a(Material material) {
            return a(material, material.i());
        }

        public static Block.Info a(Material material, EnumColor enumcolor) {
            return a(material, enumcolor.e());
        }

        public static Block.Info a(Material material, MaterialMapColor materialmapcolor) {
            return new Block.Info(material, materialmapcolor);
        }

        public static Block.Info a(Block block) {
            Block.Info block_info = new Block.Info(block.material, block.t);

            block_info.a = block.material;
            block_info.g = block.strength;
            block_info.f = block.durability;
            block_info.c = block.v;
            block_info.h = block.q;
            block_info.e = block.n;
            block_info.b = block.t;
            block_info.d = block.stepSound;
            block_info.i = block.l();
            block_info.j = block.m();
            block_info.n = block.i;
            block_info.m = block.j;
            return block_info;
        }

        public Block.Info a() {
            this.c = false;
            this.m = false;
            return this;
        }

        public Block.Info b() {
            this.m = false;
            return this;
        }

        public Block.Info a(float f) {
            this.i = f;
            return this;
        }

        public Block.Info b(float f) {
            this.j = f;
            return this;
        }

        public Block.Info c(float f) {
            this.k = f;
            return this;
        }

        protected Block.Info a(SoundEffectType soundeffecttype) {
            this.d = soundeffecttype;
            return this;
        }

        protected Block.Info a(int i) {
            this.e = i;
            return this;
        }

        public Block.Info a(float f, float f1) {
            this.g = f;
            this.f = Math.max(0.0F, f1);
            return this;
        }

        protected Block.Info c() {
            return this.d(0.0F);
        }

        protected Block.Info d(float f) {
            this.a(f, f);
            return this;
        }

        protected Block.Info d() {
            this.h = true;
            return this;
        }

        protected Block.Info e() {
            this.n = true;
            return this;
        }

        protected Block.Info f() {
            this.l = LootTables.a;
            return this;
        }

        public Block.Info b(Block block) {
            this.l = block.g();
            return this;
        }
    }

    public static final class a {

        private final IBlockData a;
        private final IBlockData b;
        private final EnumDirection c;

        public a(IBlockData iblockdata, IBlockData iblockdata1, EnumDirection enumdirection) {
            this.a = iblockdata;
            this.b = iblockdata1;
            this.c = enumdirection;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (!(object instanceof Block.a)) {
                return false;
            } else {
                Block.a block_a = (Block.a) object;

                return this.a == block_a.a && this.b == block_a.b && this.c == block_a.c;
            }
        }

        public int hashCode() {
            int i = this.a.hashCode();

            i = 31 * i + this.b.hashCode();
            i = 31 * i + this.c.hashCode();
            return i;
        }
    }
}
