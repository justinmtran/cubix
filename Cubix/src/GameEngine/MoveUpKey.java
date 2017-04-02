package GameEngine;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;

public class MoveUpKey extends AbstractInputAction {
	private SceneNode avatar;
	private float speed = 0.01f;
	
	public MoveUpKey(SceneNode n) {
		avatar = n;
	}

	@Override
	public void performAction(float time, net.java.games.input.Event e) {
		Matrix3D rot = avatar.getLocalRotation();
		Vector3D dir = new Vector3D(0, 0, 1);
		dir = dir.mult(rot);
		dir.scale((double) (speed * time));
		avatar.translate((float) -dir.getX(), (float) -dir.getY(), (float) -dir.getZ());
	}
}