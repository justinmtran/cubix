package Game;

import graphicslib3D.Vector3D;
import sage.scene.shape.Cube;

public class GhostAvatar extends Cube{

	public GhostAvatar(Vector3D position) {
		this.translate((float)position.getX(), (float)position.getY(), (float)position.getZ());
	}

}
