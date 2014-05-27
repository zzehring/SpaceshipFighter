package tilegame;

import java.awt.*;
import java.util.Iterator;

import javax.swing.JFrame;

import graphics.ScreenManager;
import graphics.Sprite;
import tilegame.sprites.Creature;
import tilegame.sprites.Player;

/**
    The TileMapRenderer class draws a TileMap on the screen.
    It draws all tiles, sprites, and an optional background image
    centered around the position of the player.

    <p>If the width of background image is smaller the width of
    the tile map, the background image will appear to move
    slowly, creating a parallax background effect.

    <p>Also, three static methods are provided to convert pixels
    to tile positions, and vice-versa.

    <p>This TileMapRender uses a tile size of 64.
*/
public class TileMapRenderer {

    private static final int TILE_SIZE = 64;
    // the size in bits of the tile
    // Math.pow(2, TILE_SIZE_BITS) == TILE_SIZE
    private static final int TILE_SIZE_BITS = 6;
    private static final int TIME = 4000;
    
    private boolean gameStarted = false;
    private long timeElapsed = 0;
    private long initialTime = System.currentTimeMillis();

    private Image background;

    /**
        Converts a pixel position to a tile position.
    */
    public static int pixelsToTiles(float pixels) {
        return pixelsToTiles(Math.round(pixels));
    }


    /**
        Converts a pixel position to a tile position.
    */
    public static int pixelsToTiles(int pixels) {
        // use shifting to get correct values for negative pixels
        return pixels >> TILE_SIZE_BITS;

        // or, for tile sizes that aren't a power of two,
        // use the floor function:
        //return (int)Math.floor((float)pixels / TILE_SIZE);
    }


    /**
        Converts a tile position to a pixel position.
    */
    public static int tilesToPixels(int numTiles) {
        // no real reason to use shifting here.
        // it's slighty faster, but doesn't add up to much
        // on modern processors.
        return numTiles << TILE_SIZE_BITS;

        // use this if the tile size isn't a power of 2:
        //return numTiles * TILE_SIZE;
    }


    /**
        Sets the background to draw.
    */
    public void setBackground(Image background) {
        this.background = background;
    }


    /**
        Draws the specified TileMap.
    */
    public void draw(Graphics2D g, TileMap map,
        int screenWidth, int screenHeight)
    {
    	if (Pause.newGame) {
    		initialTime = System.currentTimeMillis();
    		timeElapsed = 0;
    		Pause.newGame = !Pause.newGame;
    	}
        Sprite player = map.getPlayer();
                
        int mapWidth = tilesToPixels(map.getWidth());
        int mapHeight = tilesToPixels(map.getHeight());

        // get the x offset to draw all sprites and tiles
        int offsetX = screenWidth - mapWidth;

        // get the y offset scroll with dude
        int offsetY = (int) (Math.round(screenHeight / 1.1) -
        	Math.round(player.getY()) - TILE_SIZE);

        // draw black background
        
        g.setColor(Color.black);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // draw parallax background image; Not Used
        if (background != null) {
            int y = offsetY *
                (screenHeight - background.getHeight(null)) /
                (screenHeight - mapHeight);
            int x = screenWidth - background.getWidth(null);

            g.drawImage(background, x, y, null);
        }

        // draw the visible tiles
        int firstTileX = pixelsToTiles(-offsetX);
        int lastTileX = firstTileX +
            pixelsToTiles(screenWidth) + 1;
        for (int y=0; y<map.getHeight(); y++) {
            for (int x=firstTileX; x <= lastTileX; x++) {
                Image image = map.getTile(x, y);
                if (image != null) {
                    g.drawImage(image,
                        tilesToPixels(x) + offsetX,
                        tilesToPixels(y) + offsetY,
                        null);
                }
            }
        }

        // draw player
        g.drawImage(player.getImage(),
            Math.round(player.getX()) + offsetX,
            Math.round(player.getY()) + offsetY,
            null);

        // draw sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite)i.next();
            int x = Math.round(sprite.getX()) + offsetX;
            int y = Math.round(sprite.getY()) + offsetY;
            g.drawImage(sprite.getImage(), x, y, null);
            

            // wake up the creature when it's on screen
            if (sprite instanceof Creature &&
                y >= -150 && y < (screenHeight))
            {
            	int temp = sprite.getHeight();
                ((Creature)sprite).wakeUp();
            }
        }
        Player play = (Player)player;
        g.setColor(Color.white);
        g.drawString("Kills: " + Pause.kills, 20, 100);
        g.drawString("Shield " + (play.hasShield() == true?"up" : "down"), 20, 120);
        g.drawString("Lives : " + Pause.lives, 20, 80);
        g.drawString("Level : " + Pause.level, 20, 40);
        if(Pause.pause == true) {
        	g.drawImage(Pause.getPauseScreen(), 100, 150, null);
        }
        if (!gameStarted || timeElapsed < TIME) {
        	Pause.superPause = true;
        	g.drawImage(Pause.getReadyScreen(), 270, 200, null);
        	timeElapsed = System.currentTimeMillis() - initialTime;
        	gameStarted = true;
        } else if (gameStarted && timeElapsed < (1.3 * TIME)) {
        	Pause.superPause = false;
        	g.drawImage(Pause.getGoScreen(), 210, 120, null);
        	timeElapsed = System.currentTimeMillis() - initialTime;
        } else if (Pause.gameOver == true) {
            g.drawImage(Pause.getDefeatedScreen(), 80, 120,null);
            g.setColor(Color.orange);
            g.drawString("Press n to start new game", 270, 350);
        }
    }
}
