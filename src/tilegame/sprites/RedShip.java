package tilegame.sprites;

import graphics.Animation;

/**
    A Red Ship is a Creature that zig zags.
*/
public class RedShip extends Creature {

    public RedShip(Animation alive, Animation dead)
    {
        super(alive, dead);
    }


    public float getMaxSpeed() {
        return 0.2f;
    }
    
    public float getMaxVerticalSpeed() {
    	return 0.05f;
    }
}
