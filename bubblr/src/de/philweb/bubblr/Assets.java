package de.philweb.bubblr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver.Resolution;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


public class Assets {
	
	private final static String PATH_IMAGE_ATLAS = "data/imageatlas";
	private final static String FILE_IMAGE_ATLAS = "data/imageatlas/pages.atlas";
	public static TextureAtlas imageAtlasGame;
	
	private final static String FILE_UI_SKIN = "skin/uiskin.json";
	public static Skin skin;
	
	public static TextureRegion backgroundRegion;

	public static TextureRegion elfe;
	public static Texture items;


	
	public static Texture loadTexture (String file) {
		return new Texture(Gdx.files.internal(file));
	}
	
	
    public static Skin getSkin(){
        if( skin == null ) {
            FileHandle skinFile = Gdx.files.internal(FILE_UI_SKIN);
            skin = new Skin( skinFile );
        }
        return skin;
    }
	
 
	
    
	
	private static void releaseResources() {
		skin = null;
		
		// TODO: unload assetmgr. ?
	}
	
	
	public static void loadRessources (AssetManager m_assetManager) {
		
		//----- BACKGROUND -------------------------------
		TextureParameter param;
		param = new TextureParameter();
		param.minFilter = TextureFilter.Linear;
//		param.genMipMaps = true;
		m_assetManager.load("data/background.png", Texture.class, param);
		
	    
		//----- ITEMS (ImageAtlas) -------------------------------
		Resolution[] resolutionsGame = {
				new Resolution(480, 800, "800x480")
				, new Resolution(768, 1280, "1280x768")
				, new Resolution(1152, 1920, "1920x1152")
				};
		ResolutionFileResolver resolver = new ResolutionFileResolver(new InternalFileHandleResolver(), resolutionsGame);
        
		System.out.println("Asset path: "
                + resolver.resolve(FILE_IMAGE_ATLAS).path());
        
        m_assetManager.setLoader(TextureAtlas.class, new TextureAtlasLoader(resolver));
        m_assetManager.load(FILE_IMAGE_ATLAS, TextureAtlas.class);
//        Texture.setAssetManager(m_assetManager);		// this caused the bug of the white screen after resuming from swarm screen!!!
		
		
        //-------------- load resolution dependent TMX map ----------------------------
        
        
		Resolution[] resolutions = new Resolution[] {
				new Resolution(320, 480, "320"),
				new Resolution(480, 800, "480"),
			};
				
			resolver = new ResolutionFileResolver(new InternalFileHandleResolver(), resolutions);
			m_assetManager.setLoader(AtlasTiledMap.class, new AtlasTiledMapLoader(resolver));		
			m_assetManager.load("data/maps/multi.tmx", AtlasTiledMap.class, new AtlasTiledMapLoader.AtlasTiledMapLoaderParameters());
	}	
	
	
	
	
	public static void assignRessources (AssetManager m_assetManager) {
				

		if (m_assetManager.isLoaded("data/background.png")) {
			
			backgroundRegion = new TextureRegion(m_assetManager.get("data/background.png", Texture.class), 0, 0, 800, 480);	
		}
		
		
		if (m_assetManager.isLoaded(FILE_IMAGE_ATLAS)) {
					
			imageAtlasGame = m_assetManager.get(FILE_IMAGE_ATLAS, TextureAtlas.class);
			
			
			elfe = imageAtlasGame.findRegion("elfe");
			
		}		
		
	}	
	
}



