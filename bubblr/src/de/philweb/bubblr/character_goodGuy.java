package de.philweb.bubblr;


public class character_goodGuy {

	public Bubblr bubblr;
	Welt world;
	
	public static final int IDLE = 0;
	public int state;
	
	public float startpoint_x;
	public float startpoint_y;
		
	static final float width_mtr = 3.05f; //0.733f; //1.38f;
	static final float height_mtr = 3.33333f; //0.833f; //1.63f;
	
	public float stateTime;
	public int State_Animation;
				
		
	
    
	public character_goodGuy(String characterName, float x, float y, boolean facingRight, Welt world, Bubblr bubblr) {
		
		this.bubblr = bubblr;
		this.world = world;
		
		startpoint_x = x;
		startpoint_y = y;
		
		state = IDLE;
		stateTime = 0;
	}



	
	public void update (float deltaTime) {
		
		State_Animation = IDLE;
		
		stateTime += deltaTime;
	}

	
	public float getWidth_mtr() {
		return width_mtr;
	}

	public float getHeight_mtr() {
		return height_mtr;
	}
	
	
	public float getWidth_px() {
		return world.bubblr.meterToPixels(width_mtr);
	}

	public float getHeight_px() {
		return world.bubblr.meterToPixels(height_mtr);
	}
	
}


