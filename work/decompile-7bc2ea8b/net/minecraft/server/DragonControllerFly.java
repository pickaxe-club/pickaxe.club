package net.minecraft.server;

import javax.annotation.Nullable;

public class DragonControllerFly extends AbstractDragonController {

    private boolean b;
    private PathEntity c;
    private Vec3D d;

    public DragonControllerFly(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public void c() {
        if (!this.b && this.c != null) {
            BlockPosition blockposition = this.a.world.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, WorldGenEndTrophy.a);

            if (!blockposition.a((IPosition) this.a.getPositionVector(), 10.0D)) {
                this.a.getDragonControllerManager().setControllerPhase(DragonControllerPhase.HOLDING_PATTERN);
            }
        } else {
            this.b = false;
            this.j();
        }

    }

    @Override
    public void d() {
        this.b = true;
        this.c = null;
        this.d = null;
    }

    private void j() {
        int i = this.a.eJ();
        Vec3D vec3d = this.a.t(1.0F);
        int j = this.a.o(-vec3d.x * 40.0D, 105.0D, -vec3d.z * 40.0D);

        if (this.a.getEnderDragonBattle() != null && this.a.getEnderDragonBattle().c() > 0) {
            j %= 12;
            if (j < 0) {
                j += 12;
            }
        } else {
            j -= 12;
            j &= 7;
            j += 12;
        }

        this.c = this.a.a(i, j, (PathPoint) null);
        this.k();
    }

    private void k() {
        if (this.c != null) {
            this.c.a();
            if (!this.c.b()) {
                BaseBlockPosition baseblockposition = this.c.g();

                this.c.a();

                double d0;

                do {
                    d0 = (double) ((float) baseblockposition.getY() + this.a.getRandom().nextFloat() * 20.0F);
                } while (d0 < (double) baseblockposition.getY());

                this.d = new Vec3D((double) baseblockposition.getX(), d0, (double) baseblockposition.getZ());
            }
        }

    }

    @Nullable
    @Override
    public Vec3D g() {
        return this.d;
    }

    @Override
    public DragonControllerPhase<DragonControllerFly> getControllerPhase() {
        return DragonControllerPhase.TAKEOFF;
    }
}
