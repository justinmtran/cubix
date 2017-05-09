package Game;

import java.util.UUID;

import graphicslib3D.Vector3D;
import sage.terrain.TerrainBlock;

public class GhostAvatar extends PlayerAvatar{
	
	private UUID id;
	
	public GhostAvatar(String textureName, Vector3D position, UUID id, TerrainBlock t) {
		super(textureName, t, null);
		this.translate((float)position.getX(), (float)position.getY(), (float)position.getZ());
		this.id = id;
	}
	
	public UUID getID()
	{
		return id;
	}
	
	public void updateVerticalPosition(){
		 super.updateVerticalPosition();
	 }

}
