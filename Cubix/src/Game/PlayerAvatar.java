package Game;

import java.awt.Color;

import GameEngine.GameClient;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.model.loader.OBJLoader;
import sage.scene.Group;
import sage.scene.TriMesh;
import sage.scene.bounding.BoundingVolume;
import sage.scene.shape.Line;
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
	private Line[] sides = new Line[6];
	private CubixGame game;
	private TriMesh cube;
	private Tile startTile;
	private int i,j;
	
	public PlayerAvatar(String textureName, CubixGame g, GameClient c, Tile t)
	{
		client = c;
		game = g;
		startTile = t;
		i = (int)(startTile.getLocalTranslation().getCol(3).getX()-1)/2;
		j = (int)(startTile.getLocalTranslation().getCol(3).getZ()-1)/2; 
		
		game.updateVerticalPosition(this);
		
		faces[3] = new Vector3D(0,0,-1); //front
		faces[2] = new Vector3D(0,0,1); //back
		faces[5] = new Vector3D(1,0,0); //right
		faces[4] = new Vector3D(-1,0,0); //left
		faces[1] = new Vector3D(0,1,0); //up
		faces[0] = new Vector3D(0,-1,0); //down
		
		sides[0] = new Line(new Point3D(), new Point3D(0,0,3), Color.GREEN, 5);
		sides[1] = new Line(new Point3D(), new Point3D(0,0,-3), Color.BLUE, 5);
		sides[2] = new Line(new Point3D(), new Point3D(-3,0,0), Color.WHITE, 5);
		sides[3] = new Line(new Point3D(), new Point3D(3,0,0), Color.YELLOW, 5);
		sides[4] = new Line(new Point3D(), new Point3D(0,3,0), Color.ORANGE, 5);
		sides[5] = new Line(new Point3D(), new Point3D(0,-3,0), Color.RED, 5);
		
		if(!(this instanceof GhostAvatar))
		{
			addChild(sides[0]);
			addChild(sides[1]);
			addChild(sides[2]);
			addChild(sides[3]);
			addChild(sides[4]);
			addChild(sides[5]);
		}

		
		OBJLoader loader = new OBJLoader();
		cube = loader.loadModel("objects/Cube.obj");

		Texture cubeTexture = TextureManager.loadTexture2D(textureName);
		cubeTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
		cube.setTexture(cubeTexture);
		cube.updateLocalBound();
		cube.updateWorldBound();
		//cube.setShowBound(true);
		this.addChild(cube);
	}
	
	public void move(Vector3D rotAxis, Vector3D trans)
	{
		if(!isMoving)
		{	
			if(rotAxis != null)
			{
				rot = rotAxis.mult(this.getLocalRotation().inverse());
			}
			else
			{
				rot = null;
			}
			translation = trans;
			
			Tile newTile = game.getTile(i + (int)translation.getX(), j + (int)translation.getZ());
			if( (this instanceof GhostAvatar) || newTile != null && newTile.getTileType() != 0)
			{
				isMoving = true;
				rotated = 0;
				if(client != null)
				{
					client.sendMoveMessage(rotAxis, trans);
				}
				
				if(rotAxis != null)
				{
					Matrix3D rotationMatrix = new Matrix3D();
					rotationMatrix.rotate(-90, rotAxis);
					
					for(int i = 0; i<6; i++)
					{
						faces[i] = faces[i].mult(rotationMatrix).normalize();
					}
					String bottomColor;
					switch(getBottomFace())
					{
					case 3:
						bottomColor = "GREEN";
						break;
					case 2:
						bottomColor = "BLUE";
						break;
					case 5: 
						bottomColor = "WHITE";
						break;
					case 4:
						bottomColor = "YELLOW";
						break;
					case 1:
						bottomColor = "ORANGE";
						break;
					case 0:
						bottomColor = "RED";
						break;
					default:
						bottomColor = "ERROR";
						break;			
					}
								
					System.out.println("Bottom Face: " + bottomColor);
				}

			}
			

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
			if(rot != null)
			{
				this.rotate(rotationAmt, rot);
			}
			
			game.updateVerticalPosition(this);
			
			if(!(this instanceof GhostAvatar))
			{
				if(!isMoving)
				{
					updateGeometricState(0,true);
					i += translation.getX();
					j += translation.getZ();
					System.out.println(i + ", " + j);
					game.checkTile(this, i, j);
				}
			}

		}
	}
	
	public void reset()
	{
		this.setLocalTranslation((Matrix3D)game.getStartTile().getLocalTranslation().clone());
		this.translate(0, 1, 0);
		faces[3] = new Vector3D(0,0,-1); //front
		faces[2] = new Vector3D(0,0,1); //back
		faces[5] = new Vector3D(1,0,0); //right
		faces[4] = new Vector3D(-1,0,0); //left
		faces[1] = new Vector3D(0,1,0); //up
		faces[0] = new Vector3D(0,-1,0); //down
		i = (int)(startTile.getLocalTranslation().getCol(3).getX()-1)/2;
		j = (int)(startTile.getLocalTranslation().getCol(3).getZ()-1)/2;
		
		game.updateVerticalPosition(this);
		this.setLocalRotation(new Matrix3D());
		if(client != null)
		{
			client.sendDieMessage();
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
	
	public BoundingVolume getWorldBound()
	{
		return cube.getWorldBound();
	}
	
	protected boolean getIsMoving()
	{
		return isMoving;
	}
	
	public void Slide()
	{
		this.move(null, translation);
	}
	
}

