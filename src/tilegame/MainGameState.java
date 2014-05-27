package tilegame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;
import javax.sound.*;

import graphics.*;
import sound.*;
import input.*;
import tilegame.sprites.*;
import state.*;

/**
    GameManager manages all parts of the game.
*/
public class MainGameState implements GameState {

    public static void main(String[] args) {
        new GameManager().run();
    }
    
    private final int MAX_LEVELS = 1;

    private Point pointCache = new Point();
    private TileMap map;
    private MidiPlayer midiPlayer;
    private SoundManager soundManager;
    private TileGameResourceManager resourceManager;
    private Sound prizeSound;
    private Sound explosionSound;
    private Sound selectSound;
    private Sequence music;
    private TileMapRenderer renderer;
    
    private String stateChange;

    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction moveUp;
    private GameAction moveDown;
    private GameAction shoot;
    private GameAction exit;
    private GameAction pause;
    private GameAction newGame;
    

	private int width;
	private int height;
	private long timeElapsed;


    public MainGameState(SoundManager soundManager,
            MidiPlayer midiPlayer, int width, int height)
    {
        this.soundManager = soundManager;
        this.midiPlayer = midiPlayer;
        this.width = width;
        this.height = height;
        moveLeft = new GameAction("moveLeft");
        moveRight = new GameAction("moveRight");
        moveUp = new GameAction("moveUp");
        moveDown = new GameAction("moveDown");
        shoot = new GameAction("shoot",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        pause = new GameAction("pause", 
       		GameAction.DETECT_INITAL_PRESS_ONLY);
        exit = new GameAction("exit",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        newGame = new GameAction("newGame",
        	GameAction.DETECT_INITAL_PRESS_ONLY);

        renderer = new TileMapRenderer();
    }


    /**
        Closes any resources used by the GameManager.
    */
    public void stop() {
    	soundManager.setPaused(true);
        midiPlayer.setPaused(true);
        stateChange = GameStateManager.EXIT_GAME;
    }

    private void checkInput(long elapsedTime) {

        if (exit.isPressed()) {
            soundManager.play(selectSound);
        	stop();
        }
        if (pause.isPressed()) {
            soundManager.play(selectSound);
        	Pause.pause = !Pause.pause;
        }
        if (newGame.isPressed()) {
            soundManager.play(selectSound);
        	newGame();
        }
        

        Player player = (Player)map.getPlayer();
        if (player.isAlive()) {
            float velocityX = 0;
            float velocityY = player.getConstantSpeed();
            if (moveLeft.isPressed()) {
                velocityX-=player.getMaxSpeed();
            }
            if (moveRight.isPressed()) {
                velocityX+=player.getMaxSpeed();
            }
            if (moveUp.isPressed()) {
            	velocityY -= player.getMaxVerticalSpeed();
            }
            if (moveDown.isPressed()) {
            	velocityY += player.getMaxVerticalSpeed();
            }
            if (shoot.isPressed()) {
            	if (!player.fireCooldown()) {
            	 float misslex = player.getX();
                 float missley = player.getY();
                 TileGameResourceManager.addProjectile(map, (int)misslex, (int)missley, player);
            	}
                 
            }
            player.setVelocityX(velocityX);
            player.setVelocityY(velocityY);
        }

    }

    public void draw(Graphics2D g) {
        renderer.draw(g, map, width, height);
    }


    /**
        Gets the current map.
    */
    public TileMap getMap() {
        return map;
    }

    /**
        Gets the tile that a Sprites collides with. Only the
        Sprite's X or Y should be changed, not both. Returns null
        if no collision is detected.
    */
    public Point getTileCollision(Sprite sprite,
        float newX, float newY)
    {
        float fromX = Math.min(sprite.getX(), newX);
        float fromY = Math.min(sprite.getY(), newY);
        float toX = Math.max(sprite.getX(), newX);
        float toY = Math.max(sprite.getY(), newY);

        // get the tile locations
        int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
        int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
        int toTileX = TileMapRenderer.pixelsToTiles(
            toX + sprite.getWidth() - 1);
        int toTileY = TileMapRenderer.pixelsToTiles(
            toY + sprite.getHeight() - 1);

        // check each tile for a collision
        for (int x=fromTileX; x<=toTileX; x++) {
            for (int y=fromTileY; y<=toTileY; y++) {
                if (x < 0 || x >= map.getWidth() ||
                    map.getTile(x, y) != null)
                {
                    // collision found, return the tile
                	if (sprite instanceof Projectile) {
                		Projectile missle = (Projectile) sprite;
                		missle.setState(Creature.STATE_DYING);
                	}
                	if (sprite instanceof SmallLaser) {
                		SmallLaser laser = (SmallLaser) sprite;
                		laser.setState(Creature.STATE_DYING);
                	}
                    pointCache.setLocation(x, y);
                    return pointCache;
                }
            }
        }

        // no collision found
        return null;
    }


    /**
        Checks if two Sprites collide with one another. Returns
        false if the two Sprites are the same. Returns false if
        one of the Sprites is a Creature that is not alive.
    */
    public boolean isCollision(Sprite s1, Sprite s2) {
        // if the Sprites are the same, return false
        if (s1 == s2 || (s1 instanceof Player && s2 instanceof Projectile) || (s2 instanceof Player && s1 instanceof Projectile)) {
            return false;
        }

        // if one of the Sprites is a dead Creature, return false
        if (s1 instanceof Creature && !((Creature)s1).isAlive()) {
            return false;
        }
        if (s2 instanceof Creature && !((Creature)s2).isAlive()) {
            return false;
        }

        // get the pixel location of the Sprites
        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());

        // check if the two sprites' boundaries intersect
        return (s1x < s2x + s2.getWidth() &&
            s2x < s1x + s1.getWidth() &&
            s1y < s2y + s2.getHeight() &&
            s2y < s1y + s1.getHeight());
    }


    /**
        Gets the Sprite that collides with the specified Sprite,
        or null if no Sprite collides with the specified Sprite.
    */
    public Sprite getSpriteCollision(Sprite sprite) {

        // run through the list of Sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite otherSprite = (Sprite)i.next();
            if (isCollision(sprite, otherSprite)) {
                // collision found, return the Sprite
                return otherSprite;
            }
        }

        // no collision found
        return null;
    }
    
    /**
    Checks input from GameActions that can be pressed
    regardless of whether the game is paused or not.
     */
    public void checkSystemInput() {
    	if (pause.isPressed()) {
            soundManager.play(selectSound);
    		Pause.pause = !Pause.pause;
    	}
    	if (exit.isPressed()) {
            soundManager.play(selectSound);
    		stop();
    	}
    	if (newGame.isPressed()) {
            soundManager.play(selectSound);
    		newGame();
    	}
    }


    /**
        Updates Animation, position, and velocity of all Sprites
        in the current map.
    */
    public void update(long elapsedTime) {
    	
    	timeElapsed = elapsedTime;
    	if (Pause.brandNewGame) {
			Pause.brandNewGame = !Pause.brandNewGame;
    		newGame();
    	}
    	
    	checkGameOverSystemInput();
    	
    	if(Pause.superPause) {
    		return;
    	}
    	
    	if(Pause.gameOver) {
    		return;
    	}
    	
    	checkSystemInput();
    	
    	if (Pause.pause) {
    		return;
    	}
    	
        Creature player = (Creature)map.getPlayer();
    	Player actualPlayer = (Player)player;
    	actualPlayer.setLives(Pause.lives);

        // player is dead! start map over
        if (player.getState() == Creature.STATE_DEAD) {
        	actualPlayer = (Player)player;
        	if (Pause.lives == 0) {
        		gameOver();
        		return;
        	}
        	map = resourceManager.reloadMap();
            return;
        }
        
        // get keyboard/mouse input
        checkInput(elapsedTime);

        // update player
        updateCreature(player, elapsedTime, (Player) player);
        player.update(elapsedTime);

        // update other sprites
        Iterator i = map.getSprites();
        int iteratorPosition = 0;
        while (i.hasNext()) {
            Sprite sprite = (Sprite)i.next();
            iteratorPosition++;
            if (sprite instanceof Creature) {
                Creature creature = (Creature)sprite;
                if (creature.getState() == Creature.STATE_DEAD) {
                    i.remove();
                    if (creature instanceof BossShip) {
                    	if (++Pause.level > MAX_LEVELS) {
                    		Pause.totalKills = Pause.kills;
                        	stateChange = "Finished";
                        	return;
                    	}
                        map = resourceManager.loadNextMap();
                    }
                }
                else {
                    updateCreature(creature, elapsedTime, (Player) player);
                    if (creature instanceof LittleShip) {
                    	LittleShip ship = (LittleShip)creature;
                    	if (ship.getFire()) {
                    		ship.setFire();
                    		i = map.getSprites();
                    		for (int index = 0; index <= iteratorPosition; index++) {
                    			i.next();
                    		}
                    	}
                    }
                    if (creature instanceof BossShip) {
                    	BossShip ship = (BossShip)creature;
                    	if (ship.getFireBlue()) {
                    		ship.setFireBlue();
                    		i = map.getSprites();
                    		for (int index = 0; index <= iteratorPosition; index++) {
                    			i.next();
                    		}
                    	} else if (ship.getFireRed()) {
                    		ship.setFireRed();
                    		i = map.getSprites();
                    		for (int index = 0; index <= iteratorPosition; index++) {
                    			i.next();
                    		}
                    	}
                    }
                }
            }
            // normal update
            sprite.update(elapsedTime);
        }
    }


    private void checkGameOverSystemInput() {
    	if (exit.isPressed()) {
            soundManager.play(selectSound);
    		stop();
    	}
    	if (newGame.isPressed()) {
            soundManager.play(selectSound);
    		newGame();
    	}
	}


	/**
        Updates the creature, applying gravity for creatures that
        aren't flying, and checks collisions.
    */
    private void updateCreature(Creature creature,
        long elapsedTime, Player player)
    {
        // change x
    	
    	if (creature instanceof LittleShip && ((LittleShip)creature).getFire()) {
    		float misslex = creature.getX();
            float missley = creature.getY();
    		TileGameResourceManager.addProjectile(map, (int)misslex, (int)missley, creature);
    	}
    	if (creature instanceof BossShip &&((BossShip)creature).getFireRed()) {
    		float misslex = creature.getX();
            float missley = creature.getY();
    		TileGameResourceManager.addProjectile(map, (int)misslex, (int)missley, creature);
    	} else if (creature instanceof BossShip &&((BossShip)creature).getFireBlue()) {
    		float misslex = creature.getX();
            float missley = creature.getY();
    		TileGameResourceManager.addProjectile(map, (int)misslex, (int)missley, creature);
    	}
        float dx = creature.getVelocityX();
        float oldX = creature.getX();
        float newX = oldX + dx * elapsedTime;
        Point tile =
            getTileCollision(creature, newX, creature.getY());
        if (tile == null) {
            creature.setX(newX);
        }
        else {
            // line up with the tile boundary
            if (dx > 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x) -
                    creature.getWidth());
            }
            else if (dx < 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x + 1));
            }
            creature.collideHorizontal();
        }
        if (creature instanceof Player) {
            checkPlayerCollision((Player)creature, false);
        }

        // change y
        float dy = creature.getVelocityY();
        float oldY = creature.getY();
        float newY = oldY + dy * elapsedTime;
        tile = getTileCollision(creature, creature.getX(), newY);
        if (tile == null) {
            creature.setY(newY);
        }
        else {
            // line up with the tile boundary
            if (dy > 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y) -
                    creature.getHeight());
            }
            else if (dy < 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y + 1));
            }
            if (creature instanceof Player) {
            	Player player1 = (Player)creature;
            	if (player.stopped) {
            		if (Pause.bossDefeated) {
                        map = resourceManager.loadNextMap();
            			return;
            		}
            		player.collideVertical();
            		return;
            	}
            	Pause.lives--;
            	if (player.hasShield()) {
            		player1.setState(Player.STATE_NORMAL);
            	}
            	creature.setState(Creature.STATE_DYING);
            	soundManager.play(explosionSound);
            }
            //creature.collideVertical();
            
        }
        if (creature instanceof Projectile) {
        	boolean canKill = true;
        	checkProjectileCollision((Projectile)creature,canKill, player);
        }

    }


    /**
        Checks for Player collision with other Sprites. If
        canKill is true, collisions with Creatures will kill
        them.
    */
    public void checkPlayerCollision(Player player,
        boolean canKill)
    {
        if (!player.isAlive()) {
            return;
        }

        // check for player collision with other sprites
        Sprite collisionSprite = getSpriteCollision(player);
        if (collisionSprite instanceof PowerUp) {
            acquirePowerUp((PowerUp)collisionSprite);
        }
        else if (collisionSprite instanceof Creature) {
            Creature badguy = (Creature)collisionSprite;
            if (canKill) {
                // kill the badguy and make enemy explode
                soundManager.play(explosionSound);
                badguy.setState(Creature.STATE_DYING);
                player.setY(badguy.getY() - player.getHeight());
            }
            else {
            	if (player.hasShield()) {
            		soundManager.play(explosionSound);
                    badguy.setState(Creature.STATE_DYING);
                    player.lostShield();
            	} else {
            		// player dies!
            		player.setState(Player.STATE_DYING);
            		Pause.kills = 0;
            		Pause.lives--;
            		player.setLives(Pause.lives);
            		soundManager.play(explosionSound);
            	}
            }
        } else if (collisionSprite instanceof Creature) {
        	
        }
    }
    
    private void gameOver() {
        midiPlayer.setPaused(true);
		Pause.gameOver = true;		
	}


	public void checkProjectileCollision(Projectile missle,
            boolean canKill, Player player)
        {
            if (!missle.isAlive()) {
                return;
            }

            // check for player collision with other sprites
            Sprite collisionSprite = getSpriteCollision(missle);
            if (collisionSprite instanceof BossShip) {
            	BossShip badguy = (BossShip)collisionSprite;
            	if (canKill) {
                	soundManager.play(explosionSound);
                	badguy.gotHit();
                    missle.setState(Creature.STATE_DYING);
                	return;
            	}
            }
            if (collisionSprite instanceof Creature && !(collisionSprite instanceof SmallLaser) 
            		&& !(collisionSprite instanceof BossMissleBlue) && !(collisionSprite instanceof BossMissleRed)) {
                Creature badguy = (Creature)collisionSprite;
                if (canKill) {
                    // kill the bad guy and make enemy explode
                	soundManager.play(explosionSound);
                    badguy.setState(Creature.STATE_DYING);
                    missle.setState(Creature.STATE_DYING);
                    Pause.kills++;
                }
            }
        }


    /**
        Gives the player the specified power up and removes it
        from the map.
    */
    public void acquirePowerUp(PowerUp powerUp) {
        // remove it from the map
    	if (!(powerUp instanceof PowerUp.Stop)) {
    		map.removeSprite(powerUp);
    	}

        if (powerUp instanceof PowerUp.Shield) {
            // do something here, like give the player points
            soundManager.play(prizeSound);
            ((Player) map.getPlayer()).gainShield();
        } else if (powerUp instanceof PowerUp.Goal) {
        	Pause.totalKills += Pause.kills;
        	Pause.kills = 0;
            // advance to next map
            soundManager.play(prizeSound,
                new EchoFilter(2000, .7f), false);
            if (++Pause.level > MAX_LEVELS) {
            	stateChange = "Finished";
            	return;
            }
            map = resourceManager.loadNextMap();
        } else if (powerUp instanceof PowerUp.Stop) {
        	((Player) map.getPlayer()).stop();
        } else if (powerUp instanceof PowerUp.OneUp) {
            soundManager.play(prizeSound);
        	Pause.lives++;
        }
    }


	public String getName() {
		return "Main";
	}


	public String checkForStateChange() {
		if (Pause.brandNewGame) {
			stateChange = null;
		}
		return stateChange;	
	}


    public void loadResources(ResourceManager resManager) {

        resourceManager = (TileGameResourceManager)resManager;

        resourceManager.loadResources();

        renderer.setBackground(
            resourceManager.loadImage("background.png"));

        // load first map
        map = resourceManager.loadNextMap();
        Pause.level++;

        // load sounds
        prizeSound = resourceManager.loadSound("sounds/prize.wav");
        explosionSound = resourceManager.loadSound("sounds/explosion5.wav");
        selectSound = resourceManager.loadSound("sounds/select.wav");
        music = resourceManager.loadSequence("sounds/terminat.mid");
    }

	public void start(InputManager inputManager) {
		inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(moveUp, KeyEvent.VK_UP);
        inputManager.mapToKey(moveDown, KeyEvent.VK_DOWN);
        inputManager.mapToKey(shoot, KeyEvent.VK_SPACE);
        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
        inputManager.mapToKey(pause, KeyEvent.VK_P);
        inputManager.mapToKey(newGame, KeyEvent.VK_N);

        soundManager.setPaused(false);
        midiPlayer.setPaused(false);
        midiPlayer.play(music, true);
	}
	
	public void newGame() {
        midiPlayer.setPaused(false);
        midiPlayer.play(music, true);
        Player player = (Player)map.getPlayer();
		player.setState(Creature.STATE_DEAD);
        Pause.gameOver = false;
        Pause.newGame = !Pause.newGame;
        Pause.pause = false;
        Pause.lives = 3;
        Pause.level = 0;
        Pause.kills = 0;
        Pause.totalKills = 0;
	}

}
