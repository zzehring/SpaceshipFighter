package menu;

import javax.swing.ImageIcon;
import java.awt.Image;

public class Menu {
	
	private static ImageIcon menu = new ImageIcon(Menu.class.getResource("Menu/MainMenu.jpg"));
	private static ImageIcon pauseImage = new ImageIcon(Menu.class.getResource("Menu/PauseScreen.jpg"));

	
	public static Image getMenuSplash() {
		return menu.getImage();
	}
	
	public static Image getPauseScreen() {
		return pauseImage.getImage();
	}

	public static boolean mainMenu = true;
	public static boolean pause = false;
}
