package net.minecraft.server;

public class Position implements IPosition {

    protected final double a;
    protected final double b;
    protected final double c;

    public Position(double d0, double d1, double d2) {
        this.a = d0;
        this.b = d1;
        this.c = d2;
    }

    @Override
    public double getX() {
        return this.a;
    }

    @Override
    public double getY() {
        return this.b;
    }

    @Override
    public double getZ() {
        return this.c;
    }
}
