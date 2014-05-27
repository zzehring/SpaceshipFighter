package tilegame;

import javax.swing.ImageIcon;

import java.awt.Image;

public class Pause {	
	public static boolean pause = false;
	public static boolean superPause = false;
	public static boolean gameOver = false;
	public static boolean newGame = true;
	public static boolean bossDefeated = false;
	public static int lives = 3;
	public static int level = 0;
	public static boolean brandNewGame = false;
	public static boolean newState = true;
	public static int kills = 0;
	public static int totalKills = 0;

	public static Image getPauseScreen() {
		return new ImageIcon("images/pauseScreen.png").getImage();
	}
	
	public static Image getReadyScreen() {
		return new ImageIcon("images/ready.png").getImage();
	}
	
	public static Image getGoScreen() {
		return new ImageIcon("images/go.png").getImage();
	}

	public static Image getDefeatedScreen() {
		return new ImageIcon("images/defeated.png").getImage();
	}
}
