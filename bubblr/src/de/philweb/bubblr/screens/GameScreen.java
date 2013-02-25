package de.philweb.bubblr.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import de.philweb.bubblr.Bubblr;
import de.philweb.bubblr.Level;
import de.philweb.bubblr.WorldRenderer;



public class GameScreen extends Screen {
	
	static final int GAME_READY = 0;
	static final int GAME_RUNNING = 1;
		
	public Level level;
	SpriteBatch batcher;
	OrthographicCamera guiCam;
	Vector3 touchPoint;
	
	int state;
	
	StringBuffer textPuffer;
	
   

	//------------------------------------------------------------------------------------------------
	
	
	public GameScreen (Bubblr bubblr, int levelNumber, int lastLifes, long lastPoints, int lastMonsterKisses, int lastHattrickTwo, int lastHattrickThree, int lastHattrickFour, int lastHattrickFive, int lastMonsterKissesForExtraLifeCounter, int lastlaserCounter, boolean cameFromLevelSelector) {
		
		super(bubblr);

		state = GAME_RUNNING;
		
		touchPoint = new Vector3();
		batcher = bubblr.getSpritebatch();		//		batcher = new SpriteBatch();
		textPuffer = new StringBuffer();
	

		//-------------- setup the level ----------------
		
		level = new Level(bubblr, 0.0f);
		level.setRenderer(new WorldRenderer(level, levelNumber, bubblr));
		level.setupLevel();

	}

	
	//--------------------------------------------------------------------------------------------------
	
	@Override
	public void update (float deltaTime) {
					
		level.simulate(deltaTime, 0, 0f, 0);
	}

	//=================================================================================
	
	
	
	@Override
	public void present (float deltaTime) {
		
	
		level.render();				// -------------- render --------------------

        
	}
	
//---------------------------------------------------------------
	
	
	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
	
	
	}

}

