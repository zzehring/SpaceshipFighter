package tilegame.sprites;

import graphics.Animation;

/**
A Missle fired from ship. Goes straight and kills any ships.
*/
public class Projectile extends Creature {
	
	private static final float SPEED = 1.5f;
    private static final int LIFESPAN = 350;
    private static final int DIE_TIME = 500;

    
	public Projectile(Animation alive, Animation dead)
	{
		super(alive, dead);
	}
	
	public float getMaxVerticalSpeed() {
		return -SPEED;
	}
	
	public void update(long elapsedTime) {
        // select the correct Animation
        Animation newAnim = anim;
        newAnim = alive;
        if (state == STATE_DYING) {
        	newAnim = dead;
        }

        // update the Animation
        if (anim != newAnim) {
            anim = newAnim;
            anim.start();
        }
        else {
            anim.update(elapsedTime);
        }

        // update to "dead" state
        stateTime += elapsedTime;
        if ((state == STATE_DYING && stateTime >= DIE_TIME) || stateTime >= LIFESPAN) {
            setState(STATE_DEAD);
        }
    }
    
}
