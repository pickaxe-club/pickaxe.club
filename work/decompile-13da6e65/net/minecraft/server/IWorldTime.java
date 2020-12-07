package net.minecraft.server;

public interface IWorldTime extends IWorldReader {

    long ac();

    default float af() {
        return DimensionManager.e[this.getDimensionManager().b(this.ac())];
    }

    default float f(float f) {
        return this.getDimensionManager().a(this.ac());
    }
}
