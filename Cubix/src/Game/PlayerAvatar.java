package Game;

import GameEngine.GameClient;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.scene.shape.Cube;
import sage.terrain.TerrainBlock;

public class PlayerAvatar extends Cube{
	private boolean isMoving;
	private Vector3D rot, translation;
	private float rotated;
	private TerrainBlock terrain; 
	GameClient client;
	
	public PlayerAvatar(TerrainBlock t, GameClient c)
	{
		terrain = t;
		client = c;
		updateVerticalPosition();
	}
	
	public void move(Vector3D rotAxis, Vector3D trans)
	{
		if(!isMoving)
		{
			rot = rotAxis.mult(this.getLocalRotation().inverse());
			translation = trans;
			isMoving = true;
			rotated = 0;
			if(client != null)
			{
				client.sendMoveMessage(rotAxis, trans);
			}
			
		}

	}
	
	public void update(float time)
	{
		float rotSpeed = 0.50f;
		if(isMoving)
		{
			float rotationAmt;
			if(rotated + time*rotSpeed >= 90)
			{
				rotationAmt = -rotated+90;
				isMoving = false;
			}
			else
			{
				rotationAmt = time*rotSpeed;
			}
			rotated += rotationAmt;

			Vector3D translationAmt = translation.mult(rotationAmt/45);
			
			this.translate((float)translationAmt.getX(), (float)translationAmt.getY(), (float)translationAmt.getZ());
			this.rotate(rotationAmt, rot);
			
			updateVerticalPosition();

		}
	}
	
	protected void updateVerticalPosition(){
		 // get avatar's X and Y coord.
		 Point3D avLoc = new Point3D(this.getLocalTranslation().getCol(3)); // get local XYZ coord
		 float x = (float) avLoc.getX();
		 float z = (float) avLoc.getZ();
		 
		 // get Y coord based of terrain's local X,Y
		 float terHeight = terrain.getHeight(x,z);
		 
		 // calculate new Y for avatar 
		 float desiredHeight = terHeight + (float)terrain.getOrigin().getY() + 0.5f;
		 
		 // apply Y translation 
		 if(desiredHeight >= -2)
		 {
			 this.getLocalTranslation().setElementAt(1, 3, desiredHeight+0.6);
		 }
		 
	 }
}

