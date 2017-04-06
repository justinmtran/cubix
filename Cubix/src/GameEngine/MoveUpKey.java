package GameEngine;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;
import sage.terrain.TerrainBlock;

public class MoveUpKey extends AbstractInputAction {
	private SceneNode avatar;
	private GameClient client;
	private float speed = 0.01f;
	private TerrainBlock terrain; 
	
	public MoveUpKey(SceneNode n, GameClient c, TerrainBlock t) {
		avatar = n;
		client = c;
		terrain = t; 
	}

	@Override
	public void performAction(float time, net.java.games.input.Event e) {
		Matrix3D rot = avatar.getLocalRotation();
		Vector3D dir = new Vector3D(0, 0, 1);
		dir = dir.mult(rot);
		dir.scale(-(double) (speed * time));
		avatar.translate((float) dir.getX(), (float) dir.getY(), (float) dir.getZ());
		
		client.sendMoveMessage(dir);
		
		// update avatar's Y position with respect with terrain. 
		updateVerticalPosition(); 
	}
	

	 private void updateVerticalPosition(){
		 // get avatar's X and Y coord.
		 Point3D avLoc = new Point3D(avatar.getLocalTranslation().getCol(3)); // get local XYZ coord
		 float x = (float) avLoc.getX();
		 float z = (float) avLoc.getZ();
		 
		 // get Y coord based of terrain's local X,Y
		 float terHeight = terrain.getHeight(x,z);
		 
		 // calculate new Y for avatar 
		 float desiredHeight = terHeight + (float)terrain.getOrigin().getY() + 0.5f;
		 
		 // apply Y translation 
		 avatar.getLocalTranslation().setElementAt(1, 3, desiredHeight);
	 }
}