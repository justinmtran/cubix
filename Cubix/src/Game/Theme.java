package Game;

import sage.app.BaseGame;
import sage.scene.SkyBox;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.TextureManager;

public class Theme extends SkyBox{
	
	// skybox textures
	private Texture north, south, up, down, east, west;
	
	public Theme(String name, float xExtend, float yExtend, float zExtend){
		super(name,xExtend,yExtend,zExtend);
	}
	
	public void islandTheme(){
		north = TextureManager.loadTexture2D("textures/stage_island/island_north.jpg");
		south = TextureManager.loadTexture2D("textures/stage_island/island_south.jpg");
		up = TextureManager.loadTexture2D("textures/stage_island/island_up.jpg");
		down = TextureManager.loadTexture2D("textures/stage_island/island_down.jpg");
		east = TextureManager.loadTexture2D("textures/stage_island/island_east.jpg"); 
		west = TextureManager.loadTexture2D("textures/stage_island/island_west.jpg");
		setTextures();
	}
	
	public void snowTheme(BaseGame game){
		north = TextureManager.loadTexture2D("textures/stage_snow/snow_north.jpg");
		south = TextureManager.loadTexture2D("textures/stage_snow/snow_south.jpg");
		up = TextureManager.loadTexture2D("textures/stage_snow/snow_up.jpg");
		down = TextureManager.loadTexture2D("textures/stage_snow/snow_down.jpg");
		east = TextureManager.loadTexture2D("textures/stage_snow/snow_east.jpg"); 
		west = TextureManager.loadTexture2D("textures/stage_snow/snow_west.jpg");
		setTextures();
	}
	
	public void halloweenTheme(){
		north = TextureManager.loadTexture2D("textures/stage_halloween/halloween_north.jpg");
		south = TextureManager.loadTexture2D("textures/stage_halloween/halloween_south.jpg");
		up = TextureManager.loadTexture2D("textures/stage_halloween/halloween_up.jpg");
		down = TextureManager.loadTexture2D("textures/stage_halloween/halloween_down.jpg");
		east = TextureManager.loadTexture2D("textures/stage_halloween/halloween_east.jpg"); 
		west = TextureManager.loadTexture2D("textures/stage_halloween/halloween_west.jpg");
		setTextures();
	}
	
	public void factoryTheme(){
		
	}
	
	public TerrainBlock getIslandTerrain(){
		return null; 
	}
	
	public TerrainBlock getSnowTerrain(){
		return null; 
	}
	
	public TerrainBlock getHauntedTerrain(){
		return null; 
	}
	
	public TerrainBlock getFactoryTerrain(){
		return null; 
	}
	
	// set all 6 quadrants of skybox
	private void setTextures(){
		setTexture(SkyBox.Face.North, north);
		setTexture(SkyBox.Face.South, south);
		setTexture(SkyBox.Face.Up, up);
		setTexture(SkyBox.Face.Down, down);
		setTexture(SkyBox.Face.East, east);
		setTexture(SkyBox.Face.West, west);
	}
	

}
