package Game;

import GameEngine.GameClient;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.model.loader.OBJLoader;
import sage.scene.Group;
import sage.scene.TriMesh;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.TextureManager;

public class PlayerAvatar extends Group{
	private boolean isMoving;
	private Vector3D rot, translation;
	private float rotated;
	private TerrainBlock terrain; 
	GameClient client;
	private Vector3D[] faces = new Vector3D[6];
	private CubixGame game;
	
	public PlayerAvatar(String textureName, CubixGame g, GameClient c)
	{
		client = c;
		game = g;
		
		game.updateVerticalPosition(this);
		
		faces[0] = new Vector3D(0,0,-1); //front
		faces[1] = new Vector3D(0,0,1); //back
		faces[2] = new Vector3D(-1,0,0); //left
		faces[3] = new Vector3D(1,0,0); //right
		faces[4] = new Vector3D(0,1,0); //up
		faces[5] = new Vector3D(0,-1,0); //down
		
		OBJLoader loader = new OBJLoader();
		TriMesh cube = loader.loadModel("objects/Cube.obj");

		Texture cubeTexture = TextureManager.loadTexture2D(textureName);
		cubeTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
		cube.setTexture(cubeTexture);
		cube.updateLocalBound();
		this.addChild(cube);
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
				bottomColor = "GREEN";
				break;
			case 1:
				bottomColor = "BLUE";
				break;
			case 2: 
				bottomColor = "WHITE";
				break;
			case 3:
				bottomColor = "YELLOW";
				break;
			case 4:
				bottomColor = "ORANGE";
				break;
			case 5:
				bottomColor = "RED";
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
			
			game.updateVerticalPosition(this);

		}
	}
	

	
	public int getBottomFace()
	{
		for(int i = 0; i < 6; i++)
		{
			faces[i] = new Vector3D(Math.round(faces[i].getX()), Math.round(faces[i].getY()), Math.round(faces[i].getZ()));
			if(faces[i].equals(new Vector3D(0,-1,0)))
			{
				return i;
			}
		}

		return -1;
	}
}

