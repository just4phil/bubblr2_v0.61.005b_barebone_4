package de.philweb.bubblr.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.philweb.bubblr.Assets;
import de.philweb.bubblr.Bubblr;


public class LoadingAsyncScreen extends Screen {
	
	Texture background;
	TextureRegion backgroundRegion;
	TextureRegion whitePixel;
	
	TextureRegion musicOn;
	TextureRegion musicOff;
	TextureRegion quit;
	
	Color color;
	float oldRed;
	float oldGreen;
	float oldBlue;
	float oldAlpha;
	
	boolean doOnlyOnce = false;
	boolean assetsLoaded = false;	
	public boolean areImagesReady = false;
	
	float progress = 0f;
	

	 
	public LoadingAsyncScreen (Bubblr bubblr, boolean loadnew) {
		super(bubblr);
	
		background = new Texture(Gdx.files.internal("data/splash.png"));
		background.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		backgroundRegion = new TextureRegion(background, 0, 0, 800, 480);		//------ TODO: diese texturen auch in assetmanager aufnehmen! ?
		whitePixel = new TextureRegion(background, 0, 600, 1, 1);
		musicOn  = new TextureRegion(background, 700, 600, 64, 64);
		musicOff  = new TextureRegion(background, 800, 600, 64, 64);
		quit  = new TextureRegion(background, 600, 600, 64, 64);
		
		
		Gdx.input.setCatchBackKey(true);	// back key abfangen
	
		if (loadnew == true) Bubblr.getAssetManager().clear();
		
		//--------- setup Assetmanager --------------------------
		Assets.loadRessources(Bubblr.getAssetManager());
		
	}


    
	
	@Override
	public void update (float deltaTime) {		
			
				
		//----- get assetmanager progress ------------------------
		
	      if(Bubblr.getAssetManager().update() == true) {
	          
	    	  //--------------------------------------- we are done loading -> assign ressources
	    	  
	    	  if (areImagesReady == false) {
	    		  
	    		  Assets.assignRessources(Bubblr.getAssetManager());
	    		  
	    		  areImagesReady = true;
	    	  }
	    	  
	    	  else {	//--------------------------------------- we are done loading and have assigned ressources... game can start

   		  		bubblr.setScreen(new GameScreen(bubblr, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false));
	    	  	}
	       }
	      
    	  progress = Bubblr.getAssetManager().getProgress();	
	}

	
	
	@Override
	public void present (float deltaTime) {
		
		GLCommon gl = Gdx.gl;
		gl.glClearColor(0, 0, 0, 0);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		guiCam.update();
		batcher.setProjectionMatrix(guiCam.combined);
		
		batcher.enableBlending();
		batcher.begin();
		
		batcher.draw(backgroundRegion, 0, 0, 800, 480);


    	//======== progress bar zeichnen ===========================
    	  
		//--- zuerst originalfarben speichern
		color = batcher.getColor();//get current Color, you can't modify directly
		oldAlpha = color.a;
		oldRed = color.r;
		oldGreen = color.g; //save its green
		oldBlue = color.b; //save its blue
		
    	//---- color auf gr�n
		color.r = 0.0f; 
		color.g = 1.0f; 
		color.b = 0.0f;
		color.a = 1.0f;
		batcher.setColor(color); //set it
		
	  
		batcher.draw(whitePixel, 400f - (390f / 2) +2f, 146f, 392f * progress, 20f);	//---- gr�nen balken in abh�ngigkeit von progress stretchen
		
	  
	  	//---- farbe wieder auf original (weiss)
		color.r = oldRed;
		color.g = oldGreen;
		color.b = oldBlue;
		color.a = oldAlpha;
			
		batcher.setColor(color); //set it
	    
		
		//---- draw controls -----------------------
		batcher.draw(musicOff, 0, 480 - 64, 64, 64);

		batcher.draw(quit, 714, 32, 64, 64);
		//------------------------------------------------
		
		
	    batcher.end();
	}

	
	
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

