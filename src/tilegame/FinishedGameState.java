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


public class FinishedGameState implements GameState {

	private String splashFilename;
    private Image splash;
    private GameAction exitSplash;
    private GameAction exitGame;
    private MidiPlayer midiPlayer;
    private SoundManager soundManager;
    private Sound selectSound;
    private Sequence music;
    private boolean done;
    private boolean quitGame = false;

    public FinishedGameState(String splashFilename, MidiPlayer midiPlayer, SoundManager soundManager) {
        exitSplash = new GameAction("exitSplash",
             GameAction.DETECT_INITAL_PRESS_ONLY);
        exitGame = new GameAction("exitSplash",
           	 GameAction.DETECT_INITAL_PRESS_ONLY);
        
        this.splashFilename = splashFilename;
        this.midiPlayer = midiPlayer;
        this.soundManager = soundManager;
    }

    public String getName() {
        return "Finished";
    }

    public void loadResources(ResourceManager resourceManager) {
        splash = resourceManager.loadImage(splashFilename);
        music = resourceManager.loadSequence("sounds/ReturnOfTheJediFinale.mid");
        selectSound = resourceManager.loadSound("sounds/select.wav");
    }


    public String checkForStateChange() {
    	if (quitGame) {
    		return GameStateManager.EXIT_GAME;
    	}
        return done?"Main":null;
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
    		Pause.brandNewGame = !Pause.brandNewGame;
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
    	g.drawString("You defeated " + Pause.totalKills + " out of 77 Enemies", 250, 450);
    	g.drawString("Escape to quit game", 300, 500);
    	g.drawString("Enter to start new game", 290, 550);
    }
}