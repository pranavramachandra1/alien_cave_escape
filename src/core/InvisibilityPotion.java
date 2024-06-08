package core;

import tileengine.TETile;

public class InvisibilityPotion extends Item {

    private boolean used;
    private int stepCounter;
    private boolean potionActive;

    private static final int MAXSTEPS = 50;

    private boolean lostPotion;

    private boolean stopEffects;

    public InvisibilityPotion(TETile i, Coord loc) {
        super(i, loc);
        used = false;
        stepCounter = MAXSTEPS;
        potionActive = false;
        lostPotion = false;
        stopEffects = false;
    }


    public void usePotion() {
        this.used = true;
        this.potionActive = false;
    }

    public void togglePotionEffects() {
        potionActive = !potionActive;
    }

    public void printActivity() {
        if (potionActive && hasEffect()) {
            System.out.println("in Potion is active!");
        } else {
            System.out.println("in Potion is not active.");
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
