package tilegame.sprites;

import java.lang.reflect.Constructor;

import tilegame.Pause;
import graphics.Animation;

/**
    A Blue Ship is a Creature that zigzag.
*/
public class BossShip extends Creature {
	private static final int DIE_TIME = 2400;
	private static final int HIT_TIME = 200;
	private static final long FIRE_COOLDOWN = 400;
	
	public static final int STATE_NORMAL = 0;
    public static final int STATE_DYING = 1;
    public static final int STATE_DEAD = 2;
    public static final int STATE_HIT = 3;
    public static final int STATE_INJURED = 4;
    
    private int health = 60;
	
	private boolean fireRed = false;
	private boolean fireBlue = false;
	private boolean altFire = true;
	private boolean canFire = false;
	private boolean justHit = false;
	
	private Animation hit;

    public BossShip(Animation alive, Animation dead, Animation hit)
    {
        super(alive, dead);
        this.hit = hit;
    }

    public float getMaxSpeed() {
        return .17f;
    }
    
    public void setFireBlue() {
    	fireBlue = !fireBlue;
    }
    
    public boolean getFireBlue() {
    	return fireBlue;
    }
    
    public void setFireRed() {
    	fireRed = !fireRed;
    }
    
    public boolean getFireRed() {
    	return fireRed;
    }
    
    public void gotHit() {
    	health -= 2;
    	justHit = true;
    	if (health <= 0) {
    		setState(STATE_DYING);
    	}
    }
    
    public void update(long elapsedTime) {
		// select the correct Animation
		Animation newAnim = anim;
		newAnim = alive;
		if (state == STATE_DYING) {
			newAnim = dead;
		} else if (justHit && health > 0) {
			newAnim = hit;
			setState(STATE_HIT);
		} else if (state == STATE_NORMAL) {
			newAnim = alive;
		}
		// update the Animation
		if (anim != newAnim) {
			anim = newAnim;
			anim.start();
		} else {
			anim.update(elapsedTime);
		}
		
		// update to "dead" state
		stateTime += elapsedTime;
		long i = 0;
		if (state == STATE_HIT && stateTime >= HIT_TIME) {
			i = stateTime;
			justHit = false;
			setState(STATE_NORMAL);
		}
		if (state == STATE_DYING && stateTime >= DIE_TIME) {
			setState(STATE_DEAD);
		}
		
		if (stateTime % FIRE_COOLDOWN <= 14 && canFire && altFire && state != STATE_DYING) {
			setFireRed();
			altFire = !altFire;
		} else if (stateTime % FIRE_COOLDOWN <= 12 && canFire && state != STATE_DYING) {
			altFire = !altFire;
			setFireBlue();
		}
	}
    
    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(new Object[] {
                (Animation)alive.clone(),
                (Animation)dead.clone(),
                (Animation)hit.clone(),
            });
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }
   
    /**
    Gets the state of this Creature. The state is either
    STATE_NORMAL, STATE_DYING, or STATE_DEAD.
     */
    public int getState() {
    	if (state == STATE_DEAD) {
    		Pause.bossDefeated = true;
    	}
    	return state;
    }
    
    public void wakeUp() {
		if (getState() == STATE_NORMAL && getVelocityX() == 0) {
            setVelocityX(-getMaxSpeed());
            setVelocityY(getMaxVerticalSpeed());
            canFire = true;
        }
	}
    
    
}
