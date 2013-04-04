package de.philweb.bubblr;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;


/**
 * A TiledMap Loader which loads tiles from a TextureAtlas instead of separate images.
 * 
 * It requires a map-level property called 'atlas' with its value being the relative path to the TextureAtlas.
 * The atlas must have in it indexed regions named after the tilesets used in the map. The indexes shall be
 * local to the tileset (not the  global id). Strip whitespace and rotation should not be used when creating the atlas.
 * 
 * @author Justin Shapcott
 */
public class AtlasTiledMapLoader extends AsynchronousAssetLoader<AtlasTiledMap, AtlasTiledMapLoader.AtlasTiledMapLoaderParameters> {
	
	protected static final int FLAG_FLIP_HORIZONTALLY = 0x80000000;
	protected static final int FLAG_FLIP_VERTICALLY = 0x40000000;
	protected static final int FLAG_FLIP_DIAGONALLY = 0x20000000;
	protected static final int MASK_CLEAR  = 0xE0000000;
	
	protected XmlReader xmlReader = new XmlReader();
	protected Element rootElement;
	
	protected TextureAtlas atlas;
	
	protected AtlasTiledMap map;
	
	protected boolean yUp;

	protected int mapWidthInPixels;
	protected int mapHeightInPixels;
	
	private interface AtlasResolver {
		
		public TextureAtlas getAtlas(String name);
		
		public static class DirectAtlasResolver implements AtlasResolver {
			
			private final ObjectMap<String, TextureAtlas> atlases;
			
			public DirectAtlasResolver(ObjectMap<String, TextureAtlas> atlases) {
				this.atlases = atlases;
			}
			
			@Override
			public TextureAtlas getAtlas (String name) {
				return atlases.get(name);
			}
			
		}
		
		public static class AssetManagerAtlasResolver implements AtlasResolver {
			private final AssetManager assetManager;
			
			public AssetManagerAtlasResolver(AssetManager assetManager) {
				this.assetManager = assetManager;
			}
			
			@Override
			public TextureAtlas getAtlas (String name) {
				return assetManager.get(name, TextureAtlas.class);
			}
		}
		
	}
	
	public static class AtlasTiledMapLoaderParameters extends AssetLoaderParameters<AtlasTiledMap> {
		/** Whether to load the map for a y-up coordinate system */
		public boolean yUp = true;
		/** generate mipmaps? **/
		public boolean generateMipMaps = false;
	}
	
	public AtlasTiledMapLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, AtlasTiledMapLoaderParameters parameter) {
		Array<AssetDescriptor> dependencies = new Array<AssetDescriptor>();
		FileHandle tmxFile = resolve(fileName);
		try {
			rootElement = xmlReader.parse(tmxFile);
			
			Element properties = rootElement.getChildByName("properties");
			if (properties != null) {				
				for (Element property : properties.getChildrenByName("property")) {
					String name = property.getAttribute("name");
					String value = property.getAttribute("value");
					if (name.startsWith("atlas")) {
						FileHandle atlasHandle = getRelativeFileHandle(tmxFile, value);
						atlasHandle = resolve(atlasHandle.path());
						dependencies.add(new AssetDescriptor(atlasHandle.path(), TextureAtlas.class));
					}
				}
			}
		} catch (IOException e) {
			throw new GdxRuntimeException("Unable to parse .tmx file.");
		}
		return dependencies;
	}
	
	@Override
	public void loadAsync(AssetManager manager, String fileName, AtlasTiledMapLoaderParameters parameter) {
		map = null;
		FileHandle tmxFile = resolve(fileName);
		yUp = parameter.yUp;
		map = loadMap(rootElement, tmxFile, new AtlasResolver.AssetManagerAtlasResolver(manager));
	}

	@Override
	public AtlasTiledMap loadSync(AssetManager manager, String fileName, AtlasTiledMapLoaderParameters parameter) {
		return map;
	}

	protected AtlasTiledMap loadMap(Element rootElement, FileHandle tmxFile, AtlasResolver atlasResolver) {
		AtlasTiledMap map = new AtlasTiledMap();
		for (int i = 0, j = rootElement.getChildCount(); i < j; i++) {
			Element element = rootElement.getChild(i);
			String elementName = element.getName();
			if (elementName.equals("properties")) {
				loadProperties(map.getProperties(), element);
				if (map.getProperties().containsKey("atlas")) {
					FileHandle atlasHandle = getRelativeFileHandle(tmxFile, map.getProperties().get("atlas", String.class));
					atlasHandle = resolve(atlasHandle.path());
					atlas = atlasResolver.getAtlas(atlasHandle.path());
				}
			}
			else
			if (elementName.equals("tileset")) {
				loadTileset(map, element, tmxFile, atlas);
			}
			else
			if (elementName.equals("layer")) {
				loadTileLayer(map, element);
			}
			else
			if (elementName.equals("objectgroup")) {
				loadObjectGroup(map, element);
			}			
		}
		return map;
	}
	
	protected void loadTileset(AtlasTiledMap map, Element element, FileHandle tmxFile, TextureAtlas atlas) {
		String name = element.get("name", null);
		int firstgid = element.getIntAttribute("firstgid", 1);
		int tilewidth = element.getIntAttribute("tilewidth", 0);
		int tileheight = element.getIntAttribute("tileheight", 0);
		int spacing = element.getIntAttribute("spacing", 0);
		int margin = element.getIntAttribute("margin", 0);			
		String source = element.getAttribute("source", null);

		String imageSource = "";
		int imageWidth = 0, imageHeight = 0;

		FileHandle image = null;
		if (source != null) {
			FileHandle tsx = getRelativeFileHandle(tmxFile, source);
			try {
				element = xmlReader.parse(tsx);
				name = element.get("name", null);
				tilewidth = element.getIntAttribute("tilewidth", 0);
				tileheight = element.getIntAttribute("tileheight", 0);
				spacing = element.getIntAttribute("spacing", 0);
				margin = element.getIntAttribute("margin", 0);
				imageSource = element.getChildByName("image").getAttribute("source");
				imageWidth = element.getChildByName("image").getIntAttribute("width", 0);
				imageHeight = element.getChildByName("image").getIntAttribute("height", 0);
			} catch (IOException e) {
				throw new GdxRuntimeException("Error parsing external tileset.");
			}
		} else {
			imageSource = element.getChildByName("image").getAttribute("source");
			imageWidth = element.getChildByName("image").getIntAttribute("width", 0);
			imageHeight = element.getChildByName("image").getIntAttribute("height", 0);
		}
		
		Array<AtlasRegion> regions = atlas.findRegions(name);
		TiledMapTileSet tileset = new TiledMapTileSet();
		for (AtlasRegion region : regions) {
			StaticTiledMapTile tile = new StaticTiledMapTile(region);
			if (region.originalWidth != tilewidth) {
				tile.getProperties().put("imageScale", (float) tilewidth / (float) region.originalWidth);	
			} else {
				tile.getProperties().put("imageScale", 1.0f);
			}
			tileset.putTile(firstgid + region.index, tile);
		}
		map.getTileSets().addTileSet(tileset);
		Array<Element> tileElements = element.getChildrenByName("tile");
		for (Element tileElement : tileElements) {
			int localtid = tileElement.getIntAttribute("id", 0);
			TiledMapTile tile = tileset.getTile(firstgid + localtid);
			if (tile!= null) {
				Element properties = tileElement.getChildByName("properties");
				if (properties != null) {
					loadProperties(tile.getProperties(), properties);
				}
			}
		}
		
		Element properties = element.getChildByName("properties");
		if (properties != null) {
			loadProperties(tileset.getProperties(), properties);	
		}
	}
	
	protected void loadTileLayer(TiledMap map, Element element) {
		if (element.getName().equals("layer")) {
			String name = element.getAttribute("name", null);
			int width = element.getIntAttribute("width", 0);
			int height = element.getIntAttribute("height", 0);
			int tileWidth = element.getParent().getIntAttribute("tilewidth", 0);
			int tileHeight = element.getParent().getIntAttribute("tileheight", 0);
			boolean visible = element.getIntAttribute("visible", 1) == 1;
			float opacity = element.getFloatAttribute("opacity", 1.0f);
			TiledMapTileLayer layer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
			layer.setVisible(visible);
			layer.setOpacity(opacity);
			layer.setName(name);
			
			TiledMapTileSets tilesets = map.getTileSets();
			
			Element data = element.getChildByName("data");
			String encoding = data.getAttribute("encoding", null);
			String compression = data.getAttribute("compression", null);
			if (encoding.equals("csv")) {
				String[] array = data.getText().split(",");
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int id = (int) Long.parseLong(array[y * width + x].trim());
						
						final boolean flipHorizontally = ((id & FLAG_FLIP_HORIZONTALLY) != 0);
						final boolean flipVertically = ((id & FLAG_FLIP_VERTICALLY) != 0);
						final boolean flipDiagonally = ((id & FLAG_FLIP_DIAGONALLY) != 0);

						id = id & ~MASK_CLEAR;
						
						tilesets.getTile(id);
						TiledMapTile tile = tilesets.getTile(id);
						if (tile != null) {
							Cell cell = new Cell();
							if (flipDiagonally) {
								if (flipHorizontally && flipVertically) {
									cell.setFlipHorizontally(true);
									cell.setRotation(Cell.ROTATE_270);
								} else if (flipHorizontally) {
									cell.setRotation(Cell.ROTATE_270);
								} else if (flipVertically) {
									cell.setRotation(Cell.ROTATE_90);
								} else {
									cell.setFlipVertically(true);
									cell.setRotation(Cell.ROTATE_270);
								}
							} else {
								cell.setFlipHorizontally(flipHorizontally);
								cell.setFlipVertically(flipVertically);
							}
							cell.setTile(tile);
							layer.setCell(x, yUp ? height - 1 - y : y, cell);
						}
					}
				}
			} else {
				if(encoding.equals("base64")) {
					byte[] bytes = Base64Coder.decode(data.getText());
					if (compression == null) {
						int read = 0;
						for (int y = 0; y < height; y++) {
							for (int x = 0; x < width; x++) {
								
								int id =
								unsignedByteToInt(bytes[read++]) |
								unsignedByteToInt(bytes[read++]) << 8 |
								unsignedByteToInt(bytes[read++]) << 16 |
								unsignedByteToInt(bytes[read++]) << 24;
								
								final boolean flipHorizontally = ((id & FLAG_FLIP_HORIZONTALLY) != 0);
								final boolean flipVertically = ((id & FLAG_FLIP_VERTICALLY) != 0);
								final boolean flipDiagonally = ((id & FLAG_FLIP_DIAGONALLY) != 0);

								id = id & ~MASK_CLEAR;
								
								tilesets.getTile(id);
								TiledMapTile tile = tilesets.getTile(id);
								if (tile != null) {
									Cell cell = new Cell();
									if (flipDiagonally) {
										if (flipHorizontally && flipVertically) {
											cell.setFlipHorizontally(true);
											cell.setRotation(Cell.ROTATE_270);
										} else if (flipHorizontally) {
											cell.setRotation(Cell.ROTATE_270);
										} else if (flipVertically) {
											cell.setRotation(Cell.ROTATE_90);
										} else {
											cell.setFlipVertically(true);
											cell.setRotation(Cell.ROTATE_270);
										}
									} else {
										cell.setFlipHorizontally(flipHorizontally);
										cell.setFlipVertically(flipVertically);
									}
									cell.setTile(tile);
									layer.setCell(x, yUp ? height - 1 - y : y, cell);
								}
							}
						}
					} else if (compression.equals("gzip")) {
						GZIPInputStream GZIS = null;
						try {
							GZIS = new GZIPInputStream(new ByteArrayInputStream(bytes), bytes.length);
						} catch (IOException e) {
							throw new GdxRuntimeException("Error Reading TMX Layer Data - IOException: " + e.getMessage());
						}

						byte[] temp = new byte[4];
						for (int y = 0; y < height; y++) {
							for (int x = 0; x < width; x++) {
								try {
									GZIS.read(temp, 0, 4);
									int id =
									unsignedByteToInt(temp[0]) |
									unsignedByteToInt(temp[1]) << 8 |
									unsignedByteToInt(temp[2]) << 16 |
									unsignedByteToInt(temp[3]) << 24;

									final boolean flipHorizontally = ((id & FLAG_FLIP_HORIZONTALLY) != 0);
									final boolean flipVertically = ((id & FLAG_FLIP_VERTICALLY) != 0);
									final boolean flipDiagonally = ((id & FLAG_FLIP_DIAGONALLY) != 0);

									id = id & ~MASK_CLEAR;
									
									tilesets.getTile(id);
									TiledMapTile tile = tilesets.getTile(id);
									if (tile != null) {
										Cell cell = new Cell();
										if (flipDiagonally) {
											if (flipHorizontally && flipVertically) {
												cell.setFlipHorizontally(true);
												cell.setRotation(Cell.ROTATE_270);
											} else if (flipHorizontally) {
												cell.setRotation(Cell.ROTATE_270);
											} else if (flipVertically) {
												cell.setRotation(Cell.ROTATE_90);
											} else {
												cell.setFlipVertically(true);
												cell.setRotation(Cell.ROTATE_270);
											}
										} else {
											cell.setFlipHorizontally(flipHorizontally);
											cell.setFlipVertically(flipVertically);
										}
										cell.setTile(tile);
										layer.setCell(x, yUp ? height - 1 - y : y, cell);
									}
								} catch (IOException e) {
									throw new GdxRuntimeException("Error Reading TMX Layer Data.", e);
								}
							}
						}
					} else if (compression.equals("zlib")) {
						Inflater zlib = new Inflater();
						
						byte[] temp = new byte[4];

						zlib.setInput(bytes, 0, bytes.length);

						for (int y = 0; y < height; y++) {
							for (int x = 0; x < width; x++) {
								try {
									zlib.inflate(temp, 0, 4);
									int id =
									unsignedByteToInt(temp[0]) |
									unsignedByteToInt(temp[1]) << 8 |
									unsignedByteToInt(temp[2]) << 16 |
									unsignedByteToInt(temp[3]) << 24;
									
									final boolean flipHorizontally = ((id & FLAG_FLIP_HORIZONTALLY) != 0);
									final boolean flipVertically = ((id & FLAG_FLIP_VERTICALLY) != 0);
									final boolean flipDiagonally = ((id & FLAG_FLIP_DIAGONALLY) != 0);

									id = id & ~MASK_CLEAR;
									
									tilesets.getTile(id);
									TiledMapTile tile = tilesets.getTile(id);
									if (tile != null) {
										Cell cell = new Cell();
										if (flipDiagonally) {
											if (flipHorizontally && flipVertically) {
												cell.setFlipHorizontally(true);
												cell.setRotation(Cell.ROTATE_270);
											} else if (flipHorizontally) {
												cell.setRotation(Cell.ROTATE_270);
											} else if (flipVertically) {
												cell.setRotation(Cell.ROTATE_90);
											} else {
												cell.setFlipVertically(true);
												cell.setRotation(Cell.ROTATE_270);
											}
										} else {
											cell.setFlipHorizontally(flipHorizontally);
											cell.setFlipVertically(flipVertically);
										}
										cell.setTile(tile);
										layer.setCell(x, yUp ? height - 1 - y : y, cell);
									}
			
								} catch (DataFormatException e) {
									throw new GdxRuntimeException("Error Reading TMX Layer Data.", e);
								}
							}
						}
					}
				}
			}
			Element properties = element.getChildByName("properties");
			if (properties != null) {
				loadProperties(layer.getProperties(), properties);
			}
			map.getLayers().add(layer);
		}		
	}
	
	protected void loadObjectGroup(TiledMap map, Element element) {
		if (element.getName().equals("objectgroup")) {
			String name = element.getAttribute("name", null);
			MapLayer layer = new MapLayer();
			layer.setName(name);
			Element properties = element.getChildByName("properties");
			if (properties != null) {
				loadProperties(layer.getProperties(), properties);
			}
			
			for (Element objectElement : element.getChildrenByName("object")) {
				loadObject(layer, objectElement);
			}

			map.getLayers().add(layer);
		}
	}
	
	protected void loadObject(MapLayer layer, Element element) {
		if (element.getName().equals("object")) {
			MapObject object = null;
			
			int x = element.getIntAttribute("x", 0);
			int y = (yUp ? mapHeightInPixels - element.getIntAttribute("y", 0) : element.getIntAttribute("y", 0));

			int width = element.getIntAttribute("width", 0);
			int height = element.getIntAttribute("height", 0);
			
			if (element.getChildCount() > 0) {
				Element child = null;
				if ((child = element.getChildByName("polygon")) != null) {
					String[] points = child.getAttribute("points").split(" ");
					float[] vertices = new float[points.length * 2];
					for (int i = 0; i < points.length; i++) {
						String[] point = points[i].split(",");
						vertices[i * 2] = Integer.parseInt(point[0]);
						vertices[i * 2 + 1] = Integer.parseInt(point[1]);
						if (yUp) {
							vertices[i * 2 + 1] *= -1;
						}
					}
					Polygon polygon = new Polygon(vertices);
					polygon.setPosition(x, y);
					object = new PolygonMapObject(polygon);
				} else if ((child = element.getChildByName("polyline")) != null) {
					String[] points = child.getAttribute("points").split(" ");
					float[] vertices = new float[points.length * 2];
					for (int i = 0; i < points.length; i++) {
						String[] point = points[i].split(",");
						vertices[i * 2] = Integer.parseInt(point[0]);
						vertices[i * 2 + 1] = Integer.parseInt(point[1]);
						if (yUp) {
							vertices[i * 2 + 1] *= -1;
						}
					}
					Polyline polyline = new Polyline(vertices);
					polyline.setPosition(x, y);
					object = new PolylineMapObject(polyline);
				} else if ((child = element.getChildByName("ellipse")) != null) {
					object = new EllipseMapObject(x, yUp ? y - height : y, width, height);
				}
			}
			if (object == null) {
				object = new RectangleMapObject(x, yUp ? y - height : y, width, height);
			}
			object.setName(element.getAttribute("name", null));
			String type = element.getAttribute("type", null);
			if (type != null) {
				object.getProperties().put("type", type);
			}
			object.getProperties().put("x", x);
			object.getProperties().put("y", yUp ? y - height : y);
			object.setVisible(element.getIntAttribute("visible", 1) == 1);
			Element properties = element.getChildByName("properties");
			if (properties != null) {
				loadProperties(object.getProperties(), properties);
			}
			layer.getObjects().add(object);
		}
	}
	
	protected void loadProperties(MapProperties properties, Element element) {
		if (element.getName().equals("properties")) {
			for (Element property : element.getChildrenByName("property")) {
				String name = property.getAttribute("name", null);
				String value = property.getAttribute("value", null);
				if (value == null) {
					value = property.getText();
				}
				properties.put(name, value);
			}
		}
	}	

	public static FileHandle getRelativeFileHandle(FileHandle file, String path) {
		StringTokenizer tokenizer = new StringTokenizer(path, "\\/");
		FileHandle result = file.parent();
		while (tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken();
			if (token.equals(".."))
				result = result.parent();
			else {
				result = result.child(token);
			}
		}
		return result;		
	}
	
	protected static int unsignedByteToInt (byte b) {
		return (int) b & 0xFF;
	}
	
}
