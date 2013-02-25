package de.philweb.bubblr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class WorldRenderer  {
	
	Bubblr bubblr;
	Welt world;
	
	OrthographicCamera worldCamera;
	SpriteBatch batch;
	
	private TextureRegion keyFrame;

		

	public WorldRenderer (Level level, int levelNumber, Bubblr bubblr) {
		
		this.bubblr = bubblr;
		this.batch = bubblr.getSpritebatch();
		this.world = level.world;

		worldCamera = bubblr.getWorldCamera();

	}

	
//===========================================================================================	
	
	
	public void render (Level level) {
				
		GLCommon gl = Gdx.gl;
		gl.glClearColor(0, 0, 0, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	    
	    Gdx.gl.glViewport((int) bubblr._viewport.x, (int) bubblr._viewport.y,
	    		  (int) bubblr._viewport.width, (int) bubblr._viewport.height);
	    	
		
		worldCamera.update();
		worldCamera.apply(Gdx.gl10);

	
		batch.setProjectionMatrix(worldCamera.combined);
		batch.enableBlending();
		
	    batch.begin();	
	    
	    //---------- render gameobjects ------------
	    
		keyFrame = Assets.elfe; 
		batch.draw(keyFrame, world.player1.startpoint_x - (world.player1.getWidth_mtr() / 2), world.player1.startpoint_y - (world.player1.getHeight_mtr() / 2), world.player1.getWidth_mtr(), world.player1.getHeight_mtr());
//	    
//		Gdx.app.log("player.body.getPosition().x: " + world.player1.body.getPosition().x, "player.body.getPosition().y: " + world.player1.body.getPosition().y);
	    //------------------------------------

		 
		batch.end();

	}
}





