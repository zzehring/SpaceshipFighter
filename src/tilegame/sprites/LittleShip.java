package tilegame.sprites;

import graphics.Animation;

/**
    A Little Ship is a Creature that zig zags and shoots laser projectile.
*/
public class LittleShip extends Creature {
	
	private static final int DIE_TIME = 500;
	private static final long FIRE_COOLDOWN = 400;
	private boolean fire = false;
	private boolean canFire = false;


    public LittleShip(Animation alive, Animation dead)
    {
        super(alive, dead);
    }

    public float getMaxSpeed() {
        return .1f;
    }
    
    public float getMaxVerticalSpeed() {
    	return .04f;
    }
    
    public void setFire() {
    	fire = !fire;
    }
    
    public boolean getFire() {
    	return fire;
    }
    /**
    Updates the animaton for this creature.
	 */
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
		if (state == STATE_DYING && stateTime >= DIE_TIME) {
			setState(STATE_DEAD);
		}
		
		if (stateTime % FIRE_COOLDOWN <= 8 && canFire) {
			setFire();
		}
		
	}
	
	/**
    Sets the state of this Creature to STATE_NORMAL,
    STATE_DYING, or STATE_DEAD.
	 */
	public void setState(int state) {
		if (this.state != state) {
			this.state = state;
			stateTime = 0;
			if (state == STATE_DYING) {
				setVelocityX(0);
				setVelocityY(0);
			}
		}
	}
	
	public void wakeUp() {
		if (getState() == STATE_NORMAL && getVelocityX() == 0) {
            setVelocityX(-getMaxSpeed());
            setVelocityY(getMaxVerticalSpeed());
            canFire = true;
        }
	}
}
