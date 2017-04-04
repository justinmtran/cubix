package Game;

import java.util.UUID;

import graphicslib3D.Vector3D;
import sage.scene.shape.Cube;

public class GhostAvatar extends Cube{
	private UUID id;
	
	public GhostAvatar(Vector3D position, UUID id) {
		this.translate((float)position.getX(), (float)position.getY(), (float)position.getZ());
		this.id = id;
	}
	
	public UUID getID()
	{
		return id;
	}

}
