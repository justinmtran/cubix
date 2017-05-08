package Game;

import java.util.Iterator;

import GameEngine.ChasePlayer;
import GameEngine.GhostDefault;
import GameEngine.PlayerNear;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.ai.behaviortrees.BTCompositeType;
import sage.ai.behaviortrees.BTCondition;
import sage.ai.behaviortrees.BTSequence;
import sage.ai.behaviortrees.BehaviorTree;
import sage.scene.Group;
import sage.scene.Model3DTriMesh;
import sage.scene.SceneNode;

public class NPCGhostController {
	private PlayerAvatar player;
	private Group ghost;
	
	BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR);
	private boolean chase;
	private float lastThinkUpdateTime;
	private float lastTickUpdateTime;
	private int speed = 2;
	private String currentAnimation = "";
	
	public NPCGhostController(Group g, PlayerAvatar p)
	{
		ghost = g;
		player = p;
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
			Vector3D ghostLocation = ghost.getLocalTranslation().getCol(3);
			Vector3D playerLocation = player.getLocalTranslation().getCol(3);
			Vector3D direction = playerLocation.minus(ghostLocation).normalize();
			ghost.translate(speed*(float)direction.getX()*time/1000, 0, speed*(float)direction.getZ()*time/1000);
			Matrix3D test = new Matrix3D();
			test.rotateY(Math.toDegrees(Math.atan2(direction.getX(), direction.getZ())));
			ghost.setLocalRotation(test);
		}
		
	}
	
	public void npcLoop(float time)
	{
		//while(true)
		{
			//long currentTime = System.nanoTime();
			//float elapsedThinkMilliSecs = (currentTime - lastThinkUpdateTime)/(1000000.0f);
			//float elapsedTickMilliSecs = (currentTime - lastTickUpdateTime)/(1000000.0f);
			lastThinkUpdateTime += time;
			lastTickUpdateTime += time;
			
			if(lastTickUpdateTime > 50f)
			{
				//lastTickUpdateTime = currentTime;
				this.update(lastTickUpdateTime);
				lastTickUpdateTime = 0;
			}
			
			if(lastThinkUpdateTime > 500f)
			{
				chase = false;
				//lastThinkUpdateTime = currentTime;
				bt.update(lastThinkUpdateTime);
				lastThinkUpdateTime = 0;
			}
			

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
			}
			currentAnimation = name;
			
		}

	}
}

