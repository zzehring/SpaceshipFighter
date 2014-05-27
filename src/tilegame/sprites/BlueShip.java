package tilegame.sprites;

import graphics.Animation;

/**
    A Blue Ship is a Creature that zigzag.
*/
public class BlueShip extends Creature {

    public BlueShip(Animation alive, Animation dead)
    {
        super(alive, dead);
    }


    public float getMaxSpeed() {
        return 0.1f;
    }
}
