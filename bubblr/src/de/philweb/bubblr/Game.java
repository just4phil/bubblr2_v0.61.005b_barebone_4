package de.philweb.bubblr;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import de.philweb.bubblr.screens.Screen;



public abstract class Game implements ApplicationListener, InputProcessor {
	   
	Screen screen;
	int renderID = 1;
	      
  //---------------------------------------
    
    protected SpriteBatch _batch;
    
	protected OrthographicCamera _worldCamera;		//our camera
    private float pixelPerMeter;
    
	//------- world Cam -----------
	public Rectangle _viewport = null;
	public static final float VIRTUAL_WIDTH = (float)800.0f/60.0f; 						// 800 px / 60 ppm = 13.333333333333 meters
	public static final float VIRTUAL_HEIGHT = 8.0f;									// in meters
	private static final float ASPECT_RATIO = (float)VIRTUAL_WIDTH / VIRTUAL_HEIGHT;	// = 1,666666666667
	public Vector2 crop;
	public float w;
	public float h;
	float aspectRatio_GAME;
    float scale;

    //---------------------------------------

	protected float aspectRatio = 0.0f;		//the aspect ratio
	protected int screenWidth = 0;			//screen width in pixel size
	protected int screenHeight = 0;			//screen height in pixel size
    
    //==================================================
    
	
	public void setScreen (Screen helpScreen2) {
		screen = helpScreen2;
		renderID = 1;
	}
	
	
	public abstract Screen getStartScreen ();

	
	
	@Override
	public void create () {
			
		pixelPerMeter = 0.0f;		//---- todo: this shouldnt be fixed
		
		_batch = new SpriteBatch();
		
		screen = getStartScreen();
	}

	  
	
	@Override
	public void resume () {
		screen.resume();
	}

	@Override
	public void render () {
		
		if (renderID == 1) {
			screen.update(Gdx.graphics.getDeltaTime());
			screen.present(Gdx.graphics.getDeltaTime());
		}
	}

	@Override
	public void resize (int arg0, int arg1) {
		
		pixelPerMeter = 0.0f;		//---- todo: this shouldnt be fixed
		
		aspectRatio = (float) arg0 / (float) arg1;
		
		//----- world camera ---------------------------------------------		
		_worldCamera = new OrthographicCamera();
		_worldCamera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		_worldCamera.position.set(VIRTUAL_WIDTH / 2.0f, VIRTUAL_HEIGHT / 2.0f, 0.0f);
		_worldCamera.update();
		
		
		// calculate new viewport for World Cam -------------
        float scale = 1f;
        
        crop = new Vector2(0f, 0f);
        
        if(aspectRatio > ASPECT_RATIO)
        {
            scale = (float)arg1 / (float)VIRTUAL_HEIGHT;
            crop.x = (arg0 - VIRTUAL_WIDTH * scale) / 2.0f;
        }
        else if(aspectRatio < ASPECT_RATIO)
        {
            scale = (float)arg0 / (float)VIRTUAL_WIDTH;
            crop.y = (arg1 - VIRTUAL_HEIGHT * scale) / 2.0f;
        }
        else
        {
            scale = (float)arg0 / (float)VIRTUAL_WIDTH;
        }

        w = (float)VIRTUAL_WIDTH * scale;
        h = (float)VIRTUAL_HEIGHT * scale;
          
        _viewport = new Rectangle(crop.x, crop.y, w, h);

        Gdx.app.log("viewport.getX(): " +_viewport.getX(), "viewport.getY(): " + _viewport.getY());
        Gdx.app.log("viewport.getWidth(): " +_viewport.getWidth(), "viewport.getHeight(): " + _viewport.getHeight());
        
        pixelPerMeter = (float)w / (800.0f / 60.0f);
        
        Gdx.app.log("ppm", "" + pixelPerMeter);
		
       
	}

	
	@Override
	public void pause () {
		screen.pause();
	}

	@Override
	public void dispose () {
		
//		m_batch.dispose();		
//		m_HUDBatch.dispose();
		
		screen.dispose();
	}
	
	//--------------------------------

	
	public SpriteBatch getSpritebatch() {
		return _batch;
	}
	
	

	public OrthographicCamera getWorldCamera() {
		return _worldCamera;
	}
	
	
	public float getPixelPerMeter() {
		return pixelPerMeter;
	}
	
	public float meterToPixels(float meter) {
		return (float)meter * pixelPerMeter;
	}
	
	
	public float pixelsToMeter(float pixels) {
		return (float)pixels / pixelPerMeter;		
	}
	
	
}
