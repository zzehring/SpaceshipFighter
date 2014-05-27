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


public class SplashGameState implements GameState {

    private String splashFilename;
    private Image splash;
    private GameAction exitSplash;
    private GameAction exitGame;
    private MidiPlayer midiPlayer;
    private Sound selectSound;
    private SoundManager soundManager;
    private Sequence music;
    private boolean done;
    private boolean quitGame = false;

    public SplashGameState(String splashFilename, MidiPlayer midi, SoundManager soundManager) {
        exitSplash = new GameAction("exitSplash",
             GameAction.DETECT_INITAL_PRESS_ONLY);
        exitGame = new GameAction("exitSplash",
        	 GameAction.DETECT_INITAL_PRESS_ONLY);
        
        this.splashFilename = splashFilename;
        this.midiPlayer = midi;
        this.soundManager = soundManager;
        Pause.newState = !Pause.newState;
        
    }

    public String getName() {
        return "Splash";
    }

    public void loadResources(ResourceManager resourceManager) {
        splash = resourceManager.loadImage(splashFilename);
        music = resourceManager.loadSequence("sounds/ImperialAttack.mid");
        selectSound = resourceManager.loadSound("sounds/select.wav");
    }


    public String checkForStateChange() {
    	if (quitGame) {
    		return GameStateManager.EXIT_GAME;
    	}
        return done?"Instructions":null;
    }


    public void start(InputManager inputManager) {
        inputManager.mapToKey(exitSplash, KeyEvent.VK_ENTER);
        inputManager.mapToKey(exitGame, KeyEvent.VK_ESCAPE);
        midiPlayer.setPaused(false);
        midiPlayer.play(music, true);
        done = false;
    }

    public void stop() {
        midiPlayer.setPaused(true);
    }

    public void update(long elapsedTime) {
        if (exitSplash.isPressed()) {
    		soundManager.play(selectSound);
            midiPlayer.setPaused(true);
        	done = true;
        }
        if (exitGame.isPressed()) {
    		soundManager.play(selectSound);
        	stop();
        	quitGame = true;
        }
    }

    public void draw(Graphics2D g) {
        g.drawImage(splash, 0, 0, null);
    }
}