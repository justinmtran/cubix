package Game;

import java.util.UUID;

import graphicslib3D.Vector3D;
import sage.scene.shape.Sphere;

public class GhostAvatar extends Sphere{
	
	private UUID id;
	
	public GhostAvatar(Vector3D position, UUID id) {
		this.translate((float)position.getX(), (float)position.getY(), (float)position.getZ());
		this.id = id;
		this.scale(2,2,2);
	}
	
	public UUID getID()
	{
		return id;
	}

}
