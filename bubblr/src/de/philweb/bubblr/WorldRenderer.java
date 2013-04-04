package de.philweb.bubblr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;


public class WorldRenderer  {
	
//	private final static String MAP_LEVEL1 = "data/maps/level1.tmx";
	
	//--------------------------------------------------------------
	public TiledMap map;
	public ResolutionTiledMapRenderer TMXrenderer;
	private final static String MultiMAP = "data/maps/multi.tmx";
	//--------------------------------------------------------------	
	
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
		
		map = Bubblr.getAssetManager().get(MultiMAP, AtlasTiledMap.class);
		TMXrenderer = new ResolutionTiledMapRenderer(map, 1f / 32f);	
//		TMXrenderer = new ResolutionTiledMapRenderer(map, bubblr.getPixelPerMeter());	
		
		
		
	}

	
//===========================================================================================	
	
	
	public void render (Level level) {
				
		GLCommon gl = Gdx.gl;
		gl.glClearColor(0, 0, 0, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	    
	    Gdx.gl.glViewport((int) bubblr._viewport.x, (int) bubblr._viewport.y,
	    		  (int) bubblr._viewport.width, (int) bubblr._viewport.height);
	    	
	
		worldCamera.position.set(4.8f, 3.9f, 0.0f);

		worldCamera.update();
		worldCamera.apply(Gdx.gl10);

		
		TMXrenderer.setView(worldCamera);
		TMXrenderer.render();
				
		
		worldCamera.position.set(Bubblr.VIRTUAL_WIDTH / 2.0f, Bubblr.VIRTUAL_HEIGHT / 2.0f, 0.0f);
		worldCamera.update();
		
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





