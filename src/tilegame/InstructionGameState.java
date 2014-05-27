package tilegame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;

import javax.sound.midi.Sequence;

import input.*;
import sound.MidiPlayer;
import sound.Sound;
import sound.SoundManager;
import state.*;


public class InstructionGameState implements GameState {

    private String instructionsFileName;
    private Image splash;
    private GameAction exitSplash;
    private GameAction exitGame;
    private Sound selectSound;
    private SoundManager soundManager;
    private long totalElapsedTime;
    private boolean done;
    private boolean quitGame = false;

    public InstructionGameState(String instructionsFileName, SoundManager soundManager) {
    	exitSplash = new GameAction("exitSplash",
                GameAction.DETECT_INITAL_PRESS_ONLY);
    	exitGame = new GameAction("exitSplash",
           	 GameAction.DETECT_INITAL_PRESS_ONLY);
        this.instructionsFileName = instructionsFileName;
        this.soundManager = soundManager;
    }

    public String getName() {
        return "Instructions";
    }

    public void loadResources(ResourceManager resourceManager) {
        splash = resourceManager.loadImage(instructionsFileName);
        selectSound = resourceManager.loadSound("sounds/select.wav");
    }


    public String checkForStateChange() {
    	if (quitGame) {
    		return "EXIT_GAME";
    	}
        return done?"Main":null;
    }


    public void start(InputManager inputManager) {
        inputManager.mapToKey(exitSplash, KeyEvent.VK_ENTER);
        inputManager.mapToKey(exitGame, KeyEvent.VK_ESCAPE);
        totalElapsedTime = 0;
        done = false;
    }

    public void stop() {
    	//do nothing
    }

    public void update(long elapsedTime) {
    	totalElapsedTime+=elapsedTime;
    	if (exitSplash.isPressed()) {
    		soundManager.play(selectSound);
            done = true;
        }
    	if (exitGame.isPressed()) {
    		soundManager.play(selectSound);
        	stop();
        	quitGame = true;
        }
        if (totalElapsedTime > 10000) {
    		soundManager.play(selectSound);
            done = true;
        }
    }

    public void draw(Graphics2D g) {
        g.drawImage(splash, 0, 0, null);
    }
}