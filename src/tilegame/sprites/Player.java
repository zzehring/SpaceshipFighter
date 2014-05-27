package tilegame.sprites;

import java.lang.reflect.Constructor;

import graphics.Animation;
import graphics.Sprite;

/**
    The Player.
*/
public class Player extends Creature {
	
	private final int MAX_LIVES = 3;
	private final int FIRE_COOLDOWN = 200;
	
	private int kills = 0;
	private boolean shield = false;
	private int lives = MAX_LIVES;
	private long startTime = 0;
	private long elapsedTime;
	
    private Animation shielded;
    
    private static final int DIE_TIME = 500;

    public static final int STATE_NORMAL = 0;
    public static final int STATE_DYING = 1;
    public static final int STATE_DEAD = 2;
    public static final int SHIELDED = 3;
    
    public boolean stopped = false;
    public boolean fired = false;

    public Player(Animation alive, Animation dead, Animation shield) {
        super(alive, dead);
        shielded = shield;
    }


    public void collideHorizontal() {
        setVelocityX(0);
    }


    public void collideVertical() {
    	if (stopped) {
    		setVelocityY(0);
    		return;
    	}
    	state = STATE_DYING;
    }


    public void setY(float y) {
        super.setY(y);
    }


    public void wakeUp() {
        // do nothing
    }

    public float getMaxSpeed() {
        return 0.5f;
    }


	public float getConstantSpeed() {
		if (stopped) {
			return 0;
		}
		return -0.2f;
	}


	public float getMaxVerticalSpeed() {
		if (stopped) {
			return 0;
		}
		return .05f;
	}
	
	public int getLives() {
        return lives;
    }
	
	public void enemyKill() {
		kills++;
	}
	
	public int killCount() {
		return kills;
	}
	
	public boolean hasShield() {
		return shield;
	}
	
	public void gainShield() {
		shield = true;
	}
	
	public void lostShield() {
		shield = false;
	}
	
	public boolean fireCooldown() {
		elapsedTime = System.currentTimeMillis() - startTime;
		if (elapsedTime <= FIRE_COOLDOWN) {
			return true;
		}
		startTime = System.currentTimeMillis();
		return false;
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
		if (shield) {
			newAnim = shielded;
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
			lives--;
			setState(STATE_DEAD);
		}
	}

	public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(new Object[] {
                (Animation)alive.clone(),
                (Animation)dead.clone(),
                (Animation)shielded.clone(),
            });
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }
	
	public void newGame() {
		lives = 3;
	}


	public void setLives(int lives) {
		this.lives = lives;
	}


	public void stop() {
		stopped = true;
	}
	
	public void unStop() {
		stopped = false;
	}
}
