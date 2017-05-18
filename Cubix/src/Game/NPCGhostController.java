package Game;

import java.util.Iterator;

import GameEngine.ChasePlayer;
import GameEngine.GhostDefault;
import GameEngine.PlayerNear;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import GameEngine.CollisionEvent;
import sage.ai.behaviortrees.BTCompositeType;
import sage.ai.behaviortrees.BTCondition;
import sage.ai.behaviortrees.BTSequence;
import sage.ai.behaviortrees.BehaviorTree;
import sage.event.IEventListener;
import sage.event.IGameEvent;
import sage.scene.Group;
import sage.scene.Model3DTriMesh;
import sage.scene.SceneNode;
import sage.scene.bounding.BoundingSphere;
import sage.scene.bounding.BoundingVolume;
import sage.texture.Texture;
import sage.texture.TextureManager;

public class NPCGhostController extends Group implements IEventListener{
	private PlayerAvatar player;
	private Group ghost;
	
	BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR);
	private boolean chase;
	private float lastThinkUpdateTime;
	private float lastTickUpdateTime;
	private int speed = 2;
	private String currentAnimation = "";
	Texture defaultTexture, chaseTexture;
	
	public NPCGhostController(PlayerAvatar p, CubixGame game)
	{
		player = p;
		
		defaultTexture = TextureManager.loadTexture2D("textures/objects/ghost-texture-default.png");
		defaultTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);

		chaseTexture = TextureManager.loadTexture2D("textures/objects/ghost-texture-chase.png");
		chaseTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
		
		ghost = game.getGhost();
		ghost.updateGeometricState(0, true);



		Iterator<SceneNode> itr = ghost.getChildren();
		while(itr.hasNext())
		{
			Model3DTriMesh mesh = ((Model3DTriMesh)itr.next());
			mesh.setTexture(defaultTexture);
			mesh.setLocalBound(new BoundingSphere(new Point3D(mesh.getLocalTranslation().getCol(3).getX(), mesh.getLocalTranslation().getCol(3).getY(), mesh.getLocalTranslation().getCol(3).getZ()), 2));
			//mesh.setShowBound(true);
		}
		this.addChild(ghost);
		
		setupBehaviorTree();
	}
	
	
	public void setupBehaviorTree()
	{
		bt.insertAtRoot(new BTSequence(10));
		bt.insert(10, new PlayerNear(ghost, player, false));
		bt.insert(10, new ChasePlayer(this));
		bt.insertAtRoot(new BTSequence(20));
		bt.insert(20, new GhostDefault(this));
		
	}
	
	public void setChase(boolean b)
	{
		chase = b;
	}
	
	public void update(float time)
	{
		if(chase)
		{
			Vector3D ghostLocation = ghost.getWorldTranslation().getCol(3);
			Vector3D playerLocation = player.getWorldTranslation().getCol(3);
			Vector3D direction = playerLocation.minus(ghostLocation).normalize();
			this.translate(speed*(float)direction.getX()*time/1000, 0, speed*(float)direction.getZ()*time/1000);
			Matrix3D test = new Matrix3D();
			test.rotateY(Math.toDegrees(Math.atan2(direction.getX(), direction.getZ())));
			this.setLocalRotation(test);
		}
		
		Iterator<SceneNode> itr = ghost.getChildren();
		while(itr.hasNext())
		{
			Model3DTriMesh submesh = ((Model3DTriMesh)itr.next());
			submesh.updateAnimation(time);
		}
		
	}
	
	public void npcLoop(float time)
	{
		lastThinkUpdateTime += time;
		lastTickUpdateTime += time;
		
		if(lastTickUpdateTime > 50f)
		{
			this.update(lastTickUpdateTime);
			lastTickUpdateTime = 0;
		}
		
		if(lastThinkUpdateTime > 500f)
		{
			chase = false;
			bt.update(lastThinkUpdateTime);
			lastThinkUpdateTime = 0;
		}

	}
	
	public void setAnimation(String name)
	{
		if(!currentAnimation.equals(name))
		{
			Iterator<SceneNode> itr = ghost.getChildren();
			while(itr.hasNext())
			{
				Model3DTriMesh mesh = ((Model3DTriMesh)itr.next());
				mesh.startAnimation(name);
				
				if(name.equals("Default"))
				{
					mesh.setTexture(defaultTexture);
				}
				else if (name.equals("Move"))
				{
					mesh.setTexture(chaseTexture);
				}
			}
			currentAnimation = name;
		
		}

	}
	
	public BoundingVolume getWorldBound()
	{
		
		return ghost.getWorldBound();
		
	}
	
	public boolean handleEvent(IGameEvent event)
	 {
		CollisionEvent collision = (CollisionEvent)event;
		collision.getObject().reset();
		return true;
	 }
	
}

