package net.minecraft.server;

public enum GenLayerRiver implements AreaTransformer7 {

    INSTANCE;

    private GenLayerRiver() {}

    @Override
    public int a(WorldGenContext worldgencontext, int i, int j, int k, int l, int i1) {
        int j1 = c(i1);

        return j1 == c(l) && j1 == c(i) && j1 == c(j) && j1 == c(k) ? -1 : 7;
    }

    private static int c(int i) {
        return i >= 2 ? 2 + (i & 1) : i;
    }
}
