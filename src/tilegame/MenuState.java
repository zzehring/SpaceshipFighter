package tilegame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import input.*;
import state.*;


public class MenuState implements GameState {

    private String splashFilename;
    private Image splash;
    private GameAction exitSplash;
    private long totalElapsedTime;
    private boolean done;

    public MenuState(String splashFilename) {
        exitSplash = new GameAction("exitSplash",
             GameAction.DETECT_INITAL_PRESS_ONLY);
        this.splashFilename = splashFilename;
    }

    public String getName() {
        return "Menu";
    }

    public void loadResources(ResourceManager resourceManager) {
        splash = resourceManager.loadImage(splashFilename);
    }


    public String checkForStateChange() {
        return done?"Main":null;
    }


    public void start(InputManager inputManager) {
        inputManager.mapToKey(exitSplash, KeyEvent.VK_P);
        totalElapsedTime = 0;
        done = false;
    }

    public void stop() {
        // do nothing
    }

    public void update(long elapsedTime) {
        if (exitSplash.isPressed()) {
            done = true;
        }
    }

    public void draw(Graphics2D g) {
        g.drawImage(splash, 0, 0, null);
    }
}