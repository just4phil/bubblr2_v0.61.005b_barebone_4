package de.philweb.bubblr.screens;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.philweb.bubblr.Bubblr;

public abstract class Screen {

	protected Bubblr bubblr;
		
	SpriteBatch batcher;
	OrthographicCamera guiCam;


	public Screen (Bubblr bubblr) {

		this.bubblr = bubblr;
	
		batcher = new SpriteBatch();
		
		guiCam = new OrthographicCamera(800, 480);
		guiCam.position.set(800 / 2, 480 / 2, 0);
	}

	
	public abstract void update (float deltaTime);

	public abstract void present (float deltaTime);
		
	public abstract void pause ();	
	
	public Bubblr getGame() {
		return bubblr;
	}
		
	public abstract void resume ();

	public abstract void dispose ();
	
}

