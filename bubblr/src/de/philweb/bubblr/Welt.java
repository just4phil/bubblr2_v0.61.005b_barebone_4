package de.philweb.bubblr;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;

public class Welt {
	
	Bubblr bubblr;
	
	//===============================================
	public static final float WORLD_WIDTH = 13.333333f; // 10;
	public static final float WORLD_HEIGHT = 8.0f; // 15 * 20;
	
	public static final float mapgrenze_rechts = 13.333333f;	// in metern (60 px pro meter)
	public static final float mapgrenze_links = 0.0f;
	public static final float mapgrenze_oben = 8.0f;
	public static final float mapgrenze_unten = 0.0f;

	//================= DEFAULTS ====================
	public static final int PLAYERLIFES = 5;
	public static final float PLAYER_JUMP_VELOCITY = 5.7f;
	public static final float PLAYER_MOVE_VELOCITY = 4; // 3.5f; 

	//--------------- touchkoordinaten Gamescreen UI -----------------------

	public static final float X_leftClickSpace = 0.5f;
	public static final float X_rightClickSpace = 0.5f;
	
	public static final float Y_UI_above_high = 0f;
	public static final float Y_UI_above_low = 0.146f;
	
	public static final float Y_Fire_high = 0.146f;
	public static final float Y_Fire_low = 0.896f;
	public static final float X_Fire_left = 0f;
	public static final float X_Fire_right = 0.5f;

	public static final float Y_WeaponDown_high = 0.896f;
	public static final float Y_WeaponDown_low = 1.0f;
	public static final float X_WeaponDown_left = 0f;
	public static final float X_WeaponDown_right = 0.144f;

	public static final float X_Laser_left = 0.1875f;
	public static final float X_Laser_right = 0.275f;
	public static final float X_Suicide_left = 0.2875f;
	public static final float  X_Suicide_right = 0.3625f;

	public static final float Y_JumpHigh_high = 0.146f;
	public static final float Y_JumpHigh_low = 0.583f;
	public static final float X_JumpHigh_left = 0.5f;
	public static final float X_JumpHigh_right = 1.0f;
	
	public static final float Y_Jumplow_high = 0.585f;
	public static final float Y_Jumplow_low = 1.0f;
	public static final float X_Jumplow_left = 0.5f;
	public static final float X_Jumplow_right = 1.0f;
	
	public static final Rectangle soundBounds = new Rectangle(50, 480 - 64, 60, 64);
	public static final Rectangle musicBounds = new Rectangle(0, 480 - 64, 60, 64);
	public static final Rectangle pauseBounds = new Rectangle(800 - 86, 480 - 64, 64, 64);
	public static final Rectangle resumeBounds = new Rectangle(800 - 86, 480 - 64, 64, 64);  // 400 - 96, 240, 192, 36);
	public static final Rectangle quitBounds = new Rectangle(800 - 86, 32, 64, 64); //400 - 96, 240 - 36, 192, 36);
	public static final Rectangle nextBounds = new Rectangle(240, 145, 120, 55);
	public static final Rectangle retryBounds = new Rectangle(430, 145,120, 55);
	public static final Rectangle restartBounds = new Rectangle(714 - 150, 480 - 64, 64, 64);	// (714 - 75, 480 - 64, 64, 64);
	
	//---------------------------------------------------------------
	
	public static final int WORLD_STATE_RUNNING = 0;
	public static final int WORLD_STATE_NEXT_LEVEL = 1;
	public static final int WORLD_STATE_GAME_OVER = 2;

	public character_goodGuy player1;
	
	OrthographicCamera guiCam;
	
	
//----------------------------------------------------------------

	
	public Welt (Bubblr bubblr, float monsterSpeed) {
	
		this.guiCam = null;	// results in crash when player touches screen in game, because of touchpoint unproject, but nevermind for testing :)
		
		this.bubblr = bubblr;
	}

	
	
//====================================================================================================
	
	
	
	public void characterSetup() {
		
		//--- setup Player
		player1 = new character_goodGuy("player1", 6.67f, 4f, true, this, bubblr);	// 4. parameter = blickrichtung

	}
	

	
	
//==============================================================================================
	
	public void update (float deltaTime, long timeDiff, float accelY, int touchState) {
				
		player1.update(deltaTime);				
	}
	
	

}



