package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;

public class PathfinderGoalMoveThroughVillage extends PathfinderGoal {

    protected final EntityCreature a;
    private final double b;
    private PathEntity c;
    private BlockPosition d;
    private final boolean e;
    private final List<BlockPosition> f = Lists.newArrayList();
    private final int g;
    private final BooleanSupplier h;

    public PathfinderGoalMoveThroughVillage(EntityCreature entitycreature, double d0, boolean flag, int i, BooleanSupplier booleansupplier) {
        this.a = entitycreature;
        this.b = d0;
        this.e = flag;
        this.g = i;
        this.h = booleansupplier;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        if (!this.h()) {
            throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
        }
    }

    @Override
    public boolean a() {
        if (!this.h()) {
            return false;
        } else {
            this.g();
            if (this.e && this.a.world.isDay()) {
                return false;
            } else {
                WorldServer worldserver = (WorldServer) this.a.world;
                BlockPosition blockposition = this.a.getChunkCoordinates();

                if (!worldserver.a(blockposition, 6)) {
                    return false;
                } else {
                    Vec3D vec3d = RandomPositionGenerator.a(this.a, 15, 7, (blockposition1) -> {
                        if (!worldserver.b_(blockposition1)) {
                            return Double.NEGATIVE_INFINITY;
                        } else {
                            Optional<BlockPosition> optional = worldserver.x().b(VillagePlaceType.b, this::a, blockposition1, 10, VillagePlace.Occupancy.IS_OCCUPIED);

                            return !optional.isPresent() ? Double.NEGATIVE_INFINITY : -((BlockPosition) optional.get()).j(blockposition);
                        }
                    });

                    if (vec3d == null) {
                        return false;
                    } else {
                        Optional<BlockPosition> optional = worldserver.x().b(VillagePlaceType.b, this::a, new BlockPosition(vec3d), 10, VillagePlace.Occupancy.IS_OCCUPIED);

                        if (!optional.isPresent()) {
                            return false;
                        } else {
                            this.d = ((BlockPosition) optional.get()).immutableCopy();
                            Navigation navigation = (Navigation) this.a.getNavigation();
                            boolean flag = navigation.f();

                            navigation.a(this.h.getAsBoolean());
                            this.c = navigation.a(this.d, 0);
                            navigation.a(flag);
                            if (this.c == null) {
                                Vec3D vec3d1 = RandomPositionGenerator.b(this.a, 10, 7, Vec3D.c((BaseBlockPosition) this.d));

                                if (vec3d1 == null) {
                                    return false;
                                }

                                navigation.a(this.h.getAsBoolean());
                                this.c = this.a.getNavigation().a(vec3d1.x, vec3d1.y, vec3d1.z, 0);
                                navigation.a(flag);
                                if (this.c == null) {
                                    return false;
                                }
                            }

                            for (int i = 0; i < this.c.e(); ++i) {
                                PathPoint pathpoint = this.c.a(i);
                                BlockPosition blockposition1 = new BlockPosition(pathpoint.a, pathpoint.b + 1, pathpoint.c);

                                if (BlockDoor.a(this.a.world, blockposition1)) {
                                    this.c = this.a.getNavigation().a((double) pathpoint.a, (double) pathpoint.b, (double) pathpoint.c, 0);
                                    break;
                                }
                            }

                            return this.c != null;
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean b() {
        return this.a.getNavigation().m() ? false : !this.d.a((IPosition) this.a.getPositionVector(), (double) (this.a.getWidth() + (float) this.g));
    }

    @Override
    public void c() {
        this.a.getNavigation().a(this.c, this.b);
    }

    @Override
    public void d() {
        if (this.a.getNavigation().m() || this.d.a((IPosition) this.a.getPositionVector(), (double) this.g)) {
            this.f.add(this.d);
        }

    }

    private boolean a(BlockPosition blockposition) {
        Iterator iterator = this.f.iterator();

        BlockPosition blockposition1;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            blockposition1 = (BlockPosition) iterator.next();
        } while (!Objects.equals(blockposition, blockposition1));

        return false;
    }

    private void g() {
        if (this.f.size() > 15) {
            this.f.remove(0);
        }

    }

    private boolean h() {
        return this.a.getNavigation() instanceof Navigation;
    }
}
