package core;

import tileengine.TETile;

public class LaserGun extends Item {
    private boolean used;
    private int bastradius = 6;

    private static final int MAXSTEPS = 100;
    public LaserGun(TETile i, Coord loc) {
        super(i, loc);
        used = false;
    }

    public void acquireLaserGun() {
        this.used = true;
    }

    public boolean hasEffect() {
        return this.used;
    }

    public int getBlastRadius() {
        return bastradius;
    }
}
