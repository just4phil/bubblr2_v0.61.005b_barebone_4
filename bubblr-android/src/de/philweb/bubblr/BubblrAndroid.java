package de.philweb.bubblr;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.badlogic.gdx.backends.android.AndroidApplication;



public class BubblrAndroid extends AndroidApplication {


	public Bubblr bubblr;  

  
    
    //==============================================================
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate (Bundle savedInstanceState) {
			
		super.onCreate(savedInstanceState);
		
	
		bubblr = new Bubblr(true);
	
	
			
		//----- for ads -------------------
		
		// Create the layout
        RelativeLayout layout = new RelativeLayout(this);

        // Do the stuff that initialize() would do for you
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        // Create the libgdx View
        View gameView = initializeForView(bubblr, false); 		//--- openGL 1.0

        // Add the libgdx view
        layout.addView(gameView);
        
        setContentView(layout);
		//----------------------------------
		
	}
	

	public void onResume() {
	    super.onResume();
	}

	public void onPause() {
	    super.onPause();
	}
	
	public void onStop() {
	    super.onStop();
	}
	
	public void onDestroy() {
	    super.onDestroy();
	}
}