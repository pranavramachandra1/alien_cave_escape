package core;

import tileengine.TETile;

public class SpeedPotion extends Item {

    private boolean used;
    private int stepCounter;
    private boolean potionActive;

    private boolean lostPotion;

    private static final int MAXSTEPS = 50;
    public SpeedPotion(TETile i, Coord loc) {
        super(i, loc);
        used = false;
        stepCounter = MAXSTEPS;
        potionActive = false;
        lostPotion = false;
    }

    public void usePotion() {
        this.used = true;
        this.potionActive = false;
    }

    public void togglePotionEffects() {
        potionActive = !potionActive;
    }

    public void printActivity() {
        if (hasEffect()) {
            System.out.println("s Potion is active!");
        } else {
            System.out.println("s Potion is not active.");
        }
    }

    public boolean hasEffect() {
        return this.used && stepCounter > 0 && potionActive;
    }

    public void updateStepCounter() {
        if (this.used && stepCounter > 0 && potionActive) {
            stepCounter--;
        }
    }
}
