package tilegame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import sound.MidiPlayer;
import sound.SoundManager;
import state.ResourceManager;
import graphics.*;
import tilegame.sprites.*;


/**
    The ResourceManager class loads and manages tile Images and
    "host" Sprites used in the game. Game Sprites are cloned from
    "host" Sprites.
*/
public class TileGameResourceManager extends ResourceManager {

    private ArrayList tiles;
    private int currentMap;
    private GraphicsConfiguration gc;

    // host sprites used for cloning
    private Sprite playerSprite;
    private Sprite shieldSprite;
    private Sprite goalSprite;
    private Sprite oneUpSprite;
    private Sprite blueSprite;
    private Sprite redSprite;
    private Sprite littleShipSprite;
    private Sprite bossSprite;
    private Sprite stopSprite;
    private static Sprite missleSprite;
    private static Sprite laserSprite;
    private static Sprite bossMissleSpriteRed;
    private static Sprite bossMissleSpriteBlue;


    /**
        Creates a new ResourceManager with the specified
        GraphicsConfiguration.
    */
    public TileGameResourceManager(GraphicsConfiguration gc,
            SoundManager soundManager, MidiPlayer midiPlayer)
        {
            super(gc, soundManager, midiPlayer);
        }
    
    public void loadResources() {
        loadTileImages();
        loadCreatureSprites();
        loadPowerUpSprites();
    }

    /**
        Gets an image from the images/ directory.
    */
    public Image loadImage(String name) {
        String filename = "images/" + name;
        return new ImageIcon(filename).getImage();
    }

    public TileMap loadNextMap() {
        TileMap map = null;
        while (map == null) {
            currentMap++;
            try {
                map = loadMap(
                    "maps/map" + currentMap + ".txt");
            }
            catch (IOException ex) {
                if (currentMap == 1) {
                    // no maps to load!
                    return null;
                }
                currentMap = 0;
                map = null;
            }
        }

        return map;
    }


    public TileMap reloadMap() {
    	if (Pause.lives == 0) {
    		try {
                return loadMap(
                    "maps/map1.txt");
            }
            catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
    	}
    	try {
            return loadMap(
                "maps/map" + currentMap + ".txt");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private TileMap loadMap(String filename)
        throws IOException
    {
        ArrayList lines = new ArrayList();
        int width = 0;
        int height = 0;

        // read every line in the text file into the list
        BufferedReader reader = new BufferedReader(
            new FileReader(filename));
        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            // add every line except for comments
            if (!line.startsWith("#")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }

        // parse the lines to create a TileEngine
        height = lines.size();
        TileMap newMap = new TileMap(width, height);
        int y;
        for (y=0; y<height; y++) {
            String line = (String)lines.get(y);
            for (int x=0; x<line.length(); x++) {
                char ch = line.charAt(x);

                // check if the char represents tile A, B, C etc.
                int tile = ch - 'A';
                if (tile >= 0 && tile < tiles.size()) {
                    newMap.setTile(x, y, (Image)tiles.get(tile));
                }

                // check if the char represents a sprite
                else if (ch == 'o') {
                    addSprite(newMap, shieldSprite, x, y);
                }
                else if (ch == '*') {
                    addSprite(newMap, goalSprite, x, y);
                }
                else if (ch == '0') {
                	addSprite(newMap, stopSprite, x, y);
                }
                else if (ch == '~') {
                	addSprite(newMap, oneUpSprite, x, y);
                }
                else if (ch == '1') {
                    addSprite(newMap, blueSprite, x, y);
                }
                else if (ch == '2') {
                    addSprite(newMap, redSprite, x, y);
                }
                else if (ch == '3') {
                	addSprite(newMap, littleShipSprite, x, y);
                }
                else if (ch == '4') {
                	addSprite(newMap, bossSprite, x, y);
                }
            }
        }

        // add the player to the map
        Sprite player = (Sprite)playerSprite.clone();
        player.setX(TileMapRenderer.tilesToPixels(5));
        player.setY(TileMapRenderer.tilesToPixels(y - 2));
        newMap.setPlayer(player);

        return newMap;
    }


    static void addSprite(TileMap map,
        Sprite hostSprite, int tileX, int tileY)
    {
        if (hostSprite != null) {
            // clone the sprite from the "host"
            Sprite sprite = (Sprite)hostSprite.clone();

            // center the sprite
            sprite.setX(
                TileMapRenderer.tilesToPixels(tileX) +
                (TileMapRenderer.tilesToPixels(1) -
                sprite.getWidth()) / 2);

            // bottom-justify the sprite
            sprite.setY(
                TileMapRenderer.tilesToPixels(tileY + 1) -
                sprite.getHeight());

            // add it to the map
            map.addSprite(sprite);
        }
    }


    // -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------


    public void loadTileImages() {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ directory
        tiles = new ArrayList();
        char ch = 'A';
        while (true) {
            String name = "tile_" + ch + ".png";
            File file = new File("images/" + name);
            if (!file.exists()) {
                break;
            }
            tiles.add(loadImage(name));
            ch++;
        }
    }


    public void loadCreatureSprites() {

        Image[] images = new Image[100];

        // load images
        images = new Image[] {
            loadImage("player1.png"),
            loadImage("player2.png"),
            loadImage("player3.png"),
            loadImage("player4.png"),
            loadImage("playerdeath1.png"),
            loadImage("playerdeath2.png"),
            loadImage("playerdeath3.png"),
            loadImage("playerdeath4.png"),
            loadImage("playerdeath5.png"),
            loadImage("shieldShip1.png"),
            loadImage("shieldShip2.png"),
            loadImage("shieldShip3.png"),
            loadImage("shieldShip4.png"),
            loadImage("redShip1.png"),
            loadImage("redShip2.png"),
            loadImage("redShip3.png"),
            loadImage("redShip4.png"),
            loadImage("redShipdead1.png"),
            loadImage("redShipdead2.png"),
            loadImage("redShipdead3.png"),
            loadImage("redShipdead4.png"),
            loadImage("redShipdead5.png"),
            loadImage("blueShip1.png"),
            loadImage("blueShip2.png"),
            loadImage("blueShip3.png"),
            loadImage("blueShip4.png"),
            loadImage("blueShipdeath1.png"),
            loadImage("blueShipdeath2.png"),
            loadImage("blueShipdeath3.png"),
            loadImage("blueShipdeath4.png"),
            loadImage("blueShipdeath5.png"),
            loadImage("littleShip1.png"),
            loadImage("littleShip2.png"),
            loadImage("littleShip3.png"),
            loadImage("littleShip4.png"),
            loadImage("littleShipdeath1.png"),
            loadImage("littleShipdeath2.png"),
            loadImage("littleShipdeath3.png"),
            loadImage("littleShipdeath4.png"),
            loadImage("littleShipdeath5.png"),
            loadImage("bossShip1.png"),
            loadImage("bossShipInjured1.png"),
            loadImage("bossShipInjured2.png"),
            loadImage("bossShipInjured3.png"),
            loadImage("bossShipDead1.png"),
            loadImage("bossShipDead2.png"),
            loadImage("bossShipDead3.png"),
            loadImage("bossShipDead4.png"),
            loadImage("bossShipDead5.png"),
            loadImage("bossShipDead6.png"),
            loadImage("missle1.png"),
            loadImage("missle2.png"),
            loadImage("missle3.png"),
            loadImage("missle4.png"),
            loadImage("bossMissle.png"),
            loadImage("bossMissle2.png"),
            loadImage("bossMissle3.png"),
            loadImage("bossMissle4.png"),
            loadImage("bossMissleBlue1.png"),
            loadImage("bossMissleBlue2.png"),
            loadImage("bossMissleBlue3.png"),
            loadImage("bossMissleBlue4.png"),
            loadImage("enemyLaser.png"),


        };
        
        // create creature animations
        Animation[] playerAnim = new Animation[3];
        Animation[] redAnim = new Animation[2];
        Animation[] blueAnim = new Animation[2];
        Animation[] littleShipAnim = new Animation[2];
        Animation[] bossAnim = new Animation[3];
        Animation[] missleAnim = new Animation[2];
        Animation[] bossMissleRedAnim = new Animation[2];
        Animation[] bossMissleBlueAnim = new Animation[2];
        Animation[] enemyLaserAnim = new Animation[2];
        
        int i = 0;
        /*
        for (int i=0; i<4; i++) {
            playerAnim[i] = createPlayerAnim(
                images[0], images[1], images[2], images[3]);
            redAnim[i] = createredAnim(
                images[4], images[5], images[6]);
            blueAnim[i] = createblueAnim(
                images[7], images[8], images[9]);
            greenAnim[i] = creategreenAnim(
                    images[6], images[7]);
            bossAnim[i] = createbossAnim(
                    images[6], images[7]);
            asteroidAnim[i] = createasteroidAnim(
                    images[6], images[7]);
        }
        */
        playerAnim[0] = createPlayerAnim(images[i++], images[i++], images[i++], images[i++]);
        
        playerAnim[1] = createShipDeathAnim(images[i++], images[i++], images[i++], images[i++], images[i++]);
        
        playerAnim[2] = createPlayerAnim(images[i++], 
        		images[i++], images[i++], images[i++]);
        
        redAnim[0] = createShipAnim(images[i++], images[i++], images[i++], images[i++]);
        
        redAnim[1] =  createShipDeathAnim(images[i++], images[i++], 
        		images[i++], images[i++], images[i++]);
        
        blueAnim[0] = createShipAnim(images[i++], images[i++], images[i++], images[i++]);
        
        blueAnim[1] = createShipDeathAnim(images[i++], images[i++], 
        		images[i++], images[i++], images[i++]);
        
        littleShipAnim[0] = createShipAnim(images[i++], images[i++], images[i++], images[i++]);
        
        littleShipAnim[1] = createShipDeathAnim(images[i++], images[i++], 
        		images[i++], images[i++], images[i++]);
        
        bossAnim[0] = createbossAnim(images[i++]);
                
        bossAnim[2] = createBossHitAnim(images[i++], images[i++], images[i++]);
        
        bossAnim[1] = createBossDeathAnim(images[i++], images[i++], images[i++], 
        		images[i++], images[i++], images[i++]);
        		
        missleAnim[0] = createmissleAnim(images[i++], images[i++], images[i++], images[i++]);
                
        missleAnim[1] = createmissleAnim(loadImage("nothing.jpg"), 
             	loadImage("nothing.jpg"), loadImage("nothing.jpg"),loadImage("nothing.jpg"));
        
        bossMissleRedAnim[0] = createmissleAnim(images[i++], images[i++], images[i++], images[i++]);
        
        bossMissleRedAnim[1] = createmissleAnim(loadImage("nothing.jpg"), 
             	loadImage("nothing.jpg"), loadImage("nothing.jpg"),loadImage("nothing.jpg"));
        
        bossMissleBlueAnim[0] = createmissleAnim(images[i++], images[i++], images[i++], images[i++]);
        
        bossMissleBlueAnim[1] = createmissleAnim(loadImage("nothing.jpg"), 
             	loadImage("nothing.jpg"), loadImage("nothing.jpg"),loadImage("nothing.jpg"));
        
        enemyLaserAnim[0] = createbossAnim(loadImage("enemyLaser.png"));
        
        enemyLaserAnim[1] = createmissleAnim(loadImage("nothing.jpg"), 
             	loadImage("nothing.jpg"), loadImage("nothing.jpg"),loadImage("nothing.jpg"));
        
        // create creature sprites
        playerSprite = new Player(playerAnim[0],
            playerAnim[1], playerAnim[2]);
        redSprite = new RedShip(redAnim[0],
            redAnim[1]);
        blueSprite = new BlueShip(blueAnim[0],
            blueAnim[1]);
        littleShipSprite = new LittleShip(littleShipAnim[0],
        	littleShipAnim[1]);
        bossSprite = new BossShip(bossAnim[0], bossAnim[1], bossAnim[2]);
        missleSprite = new Projectile(missleAnim[0], missleAnim[1]);
        laserSprite = new SmallLaser(enemyLaserAnim[0], enemyLaserAnim[1]);
        bossMissleSpriteRed = new BossMissleRed(bossMissleRedAnim[0], bossMissleRedAnim[1]);
        bossMissleSpriteBlue = new BossMissleBlue(bossMissleBlueAnim[0], bossMissleBlueAnim[1]);

    }

	private Animation createBossDeathAnim(Image image, Image image2,
			Image image3, Image image4, Image image5, Image image6) {
		 Animation anim = new Animation();
	        anim.addFrame(image, 200);
	        anim.addFrame(image2, 200);
	        anim.addFrame(image3, 200);
	        anim.addFrame(image4, 200);
	        anim.addFrame(image5, 200);
	        anim.addFrame(image6, 200);

	        return anim;
	}

	private Animation createPlayerAnim(Image player1,
        Image player2, Image player3, Image player4)
    {
        Animation anim = new Animation();
        anim.addFrame(player1, 100);
        anim.addFrame(player2, 100);
        anim.addFrame(player3, 100);
        anim.addFrame(player4, 100);
        anim.addFrame(player3, 100);
        anim.addFrame(player2, 100);

        return anim;
    }
	
	private Animation createBossHitAnim(Image img1, Image img2, Image img3) {
		 Animation anim = new Animation();
	        anim.addFrame(img1, 50);
	        anim.addFrame(img2, 50);
	        anim.addFrame(img3, 50);
	        anim.addFrame(img2, 50);
	        
	        return anim;
	}


    private Animation createShipAnim(Image img1, Image img2,
        Image img3, Image img4)
    {
        Animation anim = new Animation();
        anim.addFrame(img1, 50);
        anim.addFrame(img2, 50);
        anim.addFrame(img3, 50);
        anim.addFrame(img4, 50);
        anim.addFrame(img3, 50);
        anim.addFrame(img2, 50);

        return anim;
    }
    
    private Animation createShipDeathAnim(Image img1, Image img2, 
    		Image img3, Image img4, Image img5) {
    	Animation anim = new Animation();
        anim.addFrame(img1, 100);
        anim.addFrame(img2, 100);
        anim.addFrame(img3, 100);
        anim.addFrame(img4, 100);
        anim.addFrame(img5, 100);
    	return anim;
    }
    
    private Animation createbossAnim(Image img1) {
        Animation anim = new Animation();
        anim.addFrame(img1, 250);
        return anim;
    }
    
    private Animation createmissleAnim(Image img1, Image img2, Image img3, Image img4) {
        Animation anim = new Animation();
        anim.addFrame(img1, 50);
        anim.addFrame(img2, 50);
        anim.addFrame(img3, 50);
        anim.addFrame(img4, 50);
        return anim;
    }

    private void loadPowerUpSprites() {
        // create "goal" sprite
        Animation anim = new Animation();
        anim.addFrame(loadImage("goal.png"), 150);
        goalSprite = new PowerUp.Goal(anim);

        // create "shield" sprite
        anim = new Animation();
        anim.addFrame(loadImage("shield1.png"), 100);
        anim.addFrame(loadImage("shield2.png"), 100);
        anim.addFrame(loadImage("shield3.png"), 100);
        anim.addFrame(loadImage("shield2.png"), 100);
        shieldSprite = new PowerUp.Shield(anim);
        
        // create stop sprite
        anim = new Animation();
        anim.addFrame(loadImage("stop.png"), 100);
        stopSprite = new PowerUp.Stop(anim);
        
        anim = new Animation();
        anim.addFrame(loadImage("oneUp1.png"), 100);
        anim.addFrame(loadImage("oneUp2.png"), 100);
        anim.addFrame(loadImage("oneUp3.png"), 100);
        anim.addFrame(loadImage("oneUp2.png"), 100);
        oneUpSprite = new PowerUp.OneUp(anim);
        
    }
    static void addProjectile(TileMap map,
            int tileX, int tileY, Creature creature)
        {
    		 Sprite sprite = null;
             // clone the sprite from the "host"             
             if (creature instanceof Player) {
            	 sprite = (Projectile)missleSprite.clone();
            	 sprite.setX(
                     (float) (tileX +
                     (TileMapRenderer.tilesToPixels(1) -
                     sprite.getWidth()) / 3)); 
            	 sprite.setY(
            		  tileY -
            		  sprite.getHeight());
             } else if (creature instanceof LittleShip) {
            	 sprite = (SmallLaser)laserSprite.clone();
            	 sprite.setX(
                         (float) (tileX +
                         (TileMapRenderer.tilesToPixels(1) -
                         sprite.getWidth()) / 1.3)); 
                	 sprite.setY(
                		  tileY +
                		  sprite.getHeight() * 3);
             } else if (creature instanceof BossShip && ((BossShip)creature).getFireRed()) {
            	 sprite = (BossMissleRed)bossMissleSpriteRed.clone();
            	 sprite.setX(
                         (float) (tileX +
                         (TileMapRenderer.tilesToPixels(1) -
                         sprite.getWidth()) / .31)); 
                	 sprite.setY(
                	     (float) (tileY +
                         sprite.getHeight() * 3.15));
             } else if (creature instanceof BossShip && ((BossShip)creature).getFireBlue()) {
            	 sprite = (BossMissleBlue)bossMissleSpriteBlue.clone();
            	 sprite.setX(
                         (float) (tileX +
                         (TileMapRenderer.tilesToPixels(1) -
                         sprite.getWidth()) / 7.0)); 
                	 sprite.setY(
                		  (float) (tileY +
                		  sprite.getHeight() * 3.15));
             }

             // add it to the map
             map.addSprite(sprite);
            
        }
}