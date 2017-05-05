package Game;

import GameEngine.ChasePlayer;
import GameEngine.PlayerNear;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.ai.behaviortrees.BTCompositeType;
import sage.ai.behaviortrees.BTCondition;
import sage.ai.behaviortrees.BTSequence;
import sage.ai.behaviortrees.BehaviorTree;
import sage.scene.Group;

public class NPCGhostController {
	private PlayerAvatar player;
	private Group ghost;
	
	BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR);
	private boolean chase;
	private float lastThinkUpdateTime;
	private float lastTickUpdateTime;
	
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
			ghost.translate((float)direction.getX()*time/1000, 0, (float)direction.getZ()*time/1000);
			Matrix3D test = new Matrix3D();
			test.rotateY(Math.toDegrees(Math.atan2(direction.getX(), direction.getZ())));
			ghost.setLocalRotation(test);
		}
		
	}
	
	public void npcLoop()
	{
		//while(true)
		{
			long currentTime = System.nanoTime();
			float elapsedThinkMilliSecs = (currentTime - lastThinkUpdateTime)/(1000000.0f);
			float elapsedTickMilliSecs = (currentTime - lastTickUpdateTime)/(1000000.0f);
			
			if(elapsedTickMilliSecs >= 50f)
			{
				lastTickUpdateTime = currentTime;
				this.update(elapsedTickMilliSecs);
			}
			
			if(elapsedThinkMilliSecs >= 500.0f)
			{
				chase = false;
				lastThinkUpdateTime = currentTime;
				bt.update(elapsedThinkMilliSecs);
			}
			

		}
	}
}

