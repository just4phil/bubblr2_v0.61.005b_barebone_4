package de.philweb.bubblr;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class BubblrDesktop {
		
	public static void main (String[] argv) {
		       
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "bubblr";
		cfg.useGL20 = false;
//		cfg.width = 480;
//		cfg.height = 320;
		cfg.width = 800;
		cfg.height = 480;        
//		cfg.width = 1280;
//		cfg.height = 800;  
//		cfg.width = 1920;
//		cfg.height = 1152; 
		new LwjglApplication(new Bubblr(false), cfg);
		        
	}
}
