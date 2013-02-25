package de.philweb.bubblr;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Level {

	public Bubblr bubblr;
	public Welt world;
	public WorldRenderer renderer;
	
	
	public Level(Bubblr bubblr, float game_monsterSpeed) {	
		
		this.bubblr = bubblr;
		
		world = new Welt(bubblr, game_monsterSpeed);
	}
	
	
		
	public void setupLevel() {
		
		world.characterSetup();
	}
	
	
	
	public void simulate(float deltaTime, long timeDiff, float accel, int touchState) {
		
		world.update(deltaTime, timeDiff, accel, touchState);
	}
	
	
	
	
	public void render() {
		
		renderer.render(this);
	}
	
		
	
	//--------------- getters ------------------------------------------
	

	public Welt getWorld() {
		return world;
	}
	
	public Bubblr getBubblr() {	
		return bubblr;
	}
	
	public WorldRenderer getRenderer() {
		return renderer;
	}
	//--------------- setters  ------------------------------------------
	
	
	public void setRenderer(WorldRenderer render) {
		renderer = render;
	}
	
	
}
