package de.philweb.bubblr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

import de.philweb.bubblr.screens.GameScreen;
import de.philweb.bubblr.screens.LoadingAsyncScreen;
import de.philweb.bubblr.screens.Screen;


public class Bubblr extends Game {
	

	
	//============= Admin-Functions ============================================================
	
	public boolean adminMode = false;	// set to false!!
	public final boolean debugMode = false;	// for log output => set to false!!
	
	
	//==========================================================================================
	
	public boolean isDemoVersion;
	
    public boolean activateMenuSwitches = false;		//---- set if menu shall be switchable by controller (red button text if button active) -----------
          
    protected static AssetManager m_assetManager;
    
   
    public boolean geist_lassdassein_hasBeenPlayed;
       
    
    
	//--------- handler f�r ads etc an superklasse �bergeben -------
	public Bubblr(boolean isDemoVersion) {
	
		m_assetManager = new AssetManager(); 	// must be initialized before super.create (else: = null)	
		
	}
	//---------------------------------------------------
	
	@Override
	public Screen getStartScreen () {
		
		return new LoadingAsyncScreen(this, false);
		
	}
	
	
	@Override
	public void create () {
		Gdx.input.setInputProcessor(this);
		Gdx.input.setCatchBackKey(true);
		
		super.create();
		
	}
	  
	@Override
	public void resize (int arg0, int arg1) {
		super.resize (arg0, arg1);

		setScreen(new LoadingAsyncScreen(this, true));
	}
	
	  
	/**
	 * @return resource management system
	 */
	public static AssetManager getAssetManager() {
		return m_assetManager;
	}
		
		
		
	
	//----- inputProcessor ---------------
	
	@Override
	public boolean keyDown(int keycode) {

//	       if(keycode == Keys.BACK){
//	    	   Gdx.app.log("TEST: back button pressed","");
//	             Gdx.app.exit(); //This will exit the app but you can add other options here as well
//	       }
	      return false;
	 }
	
	@Override
	public boolean keyTyped(char arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {

//	       if(keycode == Keys.BACK){
//	    	   Gdx.app.log("TEST: back button released","");
//	             Gdx.app.exit(); //This will exit the app but you can add other options here as well
//	       }
	       
		return false;
	}

	@Override
	public boolean scrolled(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

//	@Override
//	public boolean touchMoved(int arg0, int arg1) {
//		// TODO Auto-generated method stub
//		return false;
//	}

	@Override
	public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}
	
	


	//-------------------------

}

