package Game;

import java.util.Random;

import sage.scene.Group;
import sage.scene.shape.Cube;
import sage.texture.Texture;
import sage.texture.TextureManager;

public class Tile extends Group{

	private String tileType; 
	private Texture texture; 
	private Cube tile;
	
	public Tile(){
		super(); 
		Random rand = new Random(); 
		
		tile = new Cube(); 
		flattenCube();
		addChild(tile);
		setTileType(rand.nextInt(6)+1);
	}
	
	public Tile(int tileType){
		super(); 
		if(tileType != 0){
			tile = new Cube(); 
		}
		setTileType(tileType);	
	}
	
	private void flattenCube(){
		tile.scale(1, (-.2f), 1);
	}
	
	public String getTileType(){
		return tileType; 
	}
	
	public void setTileType(int color) {
		switch(color){
			case 1:{
				texture = TextureManager.loadTexture2D("images/textures/objects/red-tile.png");
				texture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
				tile.setTexture(texture);
				break;
			}
			case 2:{
				texture = TextureManager.loadTexture2D("images/textures/objects/orange-tile.png");
				texture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
				tile.setTexture(texture);
				break;
			}
			case 3:{
				texture = TextureManager.loadTexture2D("images/textures/objects/blue-tile.png");
				texture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
				tile.setTexture(texture);
				break;
			}
			case 4:{
				texture = TextureManager.loadTexture2D("images/textures/objects/green-tile.png");
				texture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
				tile.setTexture(texture);
				break;
			}
			case 5:{
				texture = TextureManager.loadTexture2D("images/textures/objects/yellow-tile.png");
				texture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
				tile.setTexture(texture);
				break;
			}
			case 6:{
				texture = TextureManager.loadTexture2D("images/textures/objects/white-tile.png");
				texture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
				tile.setTexture(texture);
				break;
			}
		}
	}
}
