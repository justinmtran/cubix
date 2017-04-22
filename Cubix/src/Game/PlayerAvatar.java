package Game;

import GameEngine.GameClient;
import graphicslib3D.Matrix3D;
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
	private Vector3D[] faces = new Vector3D[6];
	
	public PlayerAvatar(TerrainBlock t, GameClient c)
	{
		terrain = t;
		client = c;
		updateVerticalPosition();
		
		faces[0] = new Vector3D(0,0,-1); //front
		faces[1] = new Vector3D(0,0,1); //back
		faces[2] = new Vector3D(-1,0,0); //left
		faces[3] = new Vector3D(1,0,0); //right
		faces[4] = new Vector3D(0,1,0); //up
		faces[5] = new Vector3D(0,-1,0); //down
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
			Matrix3D rotationMatrix = new Matrix3D();
			rotationMatrix.rotate(-90, rotAxis);
			
			for(int i = 0; i<6; i++)
			{
				faces[i] = faces[i].mult(rotationMatrix).normalize();
			}
			String bottomColor;
			switch(getBottomFace())
			{
			case 0:
				bottomColor = "BLUE";
				break;
			case 1:
				bottomColor = "RED";
				break;
			case 2: 
				bottomColor = "LIGHTBLUE";
				break;
			case 3:
				bottomColor = "GREEN";
				break;
			case 4:
				bottomColor = "PURPLE";
				break;
			case 5:
				bottomColor = "YELLOW";
				break;
			default:
				bottomColor = "ERROR";
				break;			
			}
						
			System.out.println("Bottom Face: " + bottomColor);
		}
	}
	
	public void update(float time)
	{
		float rotSpeed = 0.5f;
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
	
	public int getBottomFace()
	{
		Point3D center = new Point3D(0,0,0);
		for(int i = 0; i < 6; i++)
		{
			faces[i] = new Vector3D(Math.round(faces[i].getX()), Math.round(faces[i].getY()), Math.round(faces[i].getZ()));
			if(faces[i].minus(new Vector3D(0,-1,0)).equals(new Vector3D(center)))
			{
				return i;
			}
		}

		return -1;
	}
}

