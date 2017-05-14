package Game;

import java.util.Random;

import sage.scene.Group;
import sage.scene.shape.Cube;
import sage.texture.Texture;
import sage.texture.TextureManager;

/*
	0 - Terrain Tile
	1 - Red Tile
	2 - Orange Tile
	3 - Blue Tile
	4 - Green Tile
	5 - Yellow Tile
	6 - White Tile
	7 - Start Tile
	8 - End Tile
	9 - Ice Tile
*/

public class Tile extends Group{

	private int tileType; 
	private Texture texture; 
	private Cube tile;
	
	/**
	 * Default Constructor, which will create a random
	 * colored tile. 
	 */
	public Tile(){
		super(); 	
		tile = new Cube(); 
		flattenCube();
		addChild(tile);
		setTileType(new Random().nextInt(6)+1);
	}
	
	/**
	 * Param Constructor, takes a number and map the tile type to the
	 * cooresponding tile
	 * @param tileType
	 */
	public Tile(int tileType){
		super(); 
		if(tileType != 0){
			tile = new Cube(); 
			addChild(tile);
		}
		setTileType(tileType);	
	}
	
	private void flattenCube(){
		tile.scale(1, (-.2f), 1);
	}
	
	public int getTileType(){
		return tileType; 
	}
	
	public void setTileType(int tileType) {
		switch(tileType){
			case 0:{ // terrain tile 
				tile = null; 
				this.tileType = tileType; 
				break; 
			}
			case 1:{
				texture = TextureManager.loadTexture2D("images/textures/objects/red-tile.png");
				texture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
				tile.setTexture(texture);
				this.tileType = tileType; 
				break;
			}
			case 2:{
				texture = TextureManager.loadTexture2D("images/textures/objects/orange-tile.png");
				texture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
				tile.setTexture(texture);
				this.tileType = tileType; 
				break;
			}
			case 3:{
				texture = TextureManager.loadTexture2D("images/textures/objects/blue-tile.png");
				texture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
				tile.setTexture(texture);
				this.tileType = tileType; 
				break;
			}
			case 4:{
				texture = TextureManager.loadTexture2D("images/textures/objects/green-tile.png");
				texture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
				tile.setTexture(texture);
				this.tileType = tileType; 
				break;
			}
			case 5:{
				texture = TextureManager.loadTexture2D("images/textures/objects/yellow-tile.png");
				texture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
				tile.setTexture(texture);
				this.tileType = tileType; 
				break;
			}
			case 6:{
				texture = TextureManager.loadTexture2D("images/textures/objects/white-tile.png");
				texture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
				tile.setTexture(texture);
				this.tileType = tileType; 
				break;
			}
			case 7:{
				texture = TextureManager.loadTexture2D("images/textures/objects/start-tile.png");
				texture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
				tile.setTexture(texture);
				this.tileType = tileType; 
				break;
			}
			case 8:{
				texture = TextureManager.loadTexture2D("images/textures/objects/end-tile.png");
				texture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
				tile.setTexture(texture);
				this.tileType = tileType; 
				break;
			}
		}
	}
}
