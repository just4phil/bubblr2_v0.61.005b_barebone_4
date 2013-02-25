package de.philweb.bubblr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver.Resolution;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;


public class WorldRenderer  {
	
	private final static String MAP_LEVEL1 = "data/maps/level1.tmx";
	
	Bubblr bubblr;
	Welt world;
	OrthographicCamera worldCamera;
	SpriteBatch batch;
	private TextureRegion keyFrame;

	private TmxMapLoader tmxMapLoader;
	
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject () {
			return new Rectangle();
		}
	};
	private Array<Rectangle> tiles = new Array<Rectangle>();
		

	public WorldRenderer (Level level, int levelNumber, Bubblr bubblr) {
		
		this.bubblr = bubblr;
		this.batch = bubblr.getSpritebatch();
		this.world = level.world;
		worldCamera = bubblr.getWorldCamera();

		
		//----- ITEMS (ImageAtlas) -------------------------------
		Resolution[] resolutionsGame = {
				new Resolution(480, 800, "800x480")
				, new Resolution(768, 1280, "1280x768")
				, new Resolution(1152, 1920, "1920x1152")
				};
		ResolutionFileResolver resolver = new ResolutionFileResolver(new InternalFileHandleResolver(), resolutionsGame);
        

		
		// load the map, set the unit scale to 1/16 (1 unit == 16 pixels)
//		Note if you load your TMX map directly, you are responsible for calling TiledMap#dispose() once you no longer need it. This call will dispose of any textures loaded for the map.
		tmxMapLoader = new TmxMapLoader(resolver);
		
		map = tmxMapLoader.load(MAP_LEVEL1);
			
		renderer = new OrthogonalTiledMapRenderer(map, 1 / bubblr.getPixelPerMeter());
		
		
//		Only use tiles from a single tile set in a layer. This will reduce texture binding.
//		Mark tiles that do not need blending as opaque. At the moment you can only do this programmatically, we will provide ways to do it in the editor or automatically.
//		Do not go overboard with the number of layers.
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

	
		// set the tile map rendere view based on what the
		// camera sees and render the map

		renderer.setView(worldCamera);
		renderer.render();
		
		
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
	
	
	
	private void getTiles(int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {
		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().getLayer(1);
		rectPool.freeAll(tiles);
		tiles.clear();
		for(int y = startY; y <= endY; y++) {
			for(int x = startX; x <= endX; x++) {
				Cell cell = layer.getCell(x, y);
				if(cell != null) {
					Rectangle rect = rectPool.obtain();
					rect.set(x, y, 1, 1);
					tiles.add(rect);
				}
			}
		}
	}
}





