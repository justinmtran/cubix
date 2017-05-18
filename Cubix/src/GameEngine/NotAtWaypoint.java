package GameEngine;


import Game.NPCGhostController;
import Game.PlayerAvatar;
import graphicslib3D.Vector3D;
import sage.ai.behaviortrees.BTCondition;
import sage.scene.Group;

public class NotAtWaypoint extends BTCondition
{
	private NPCGhostController ghost;
	public NotAtWaypoint(NPCGhostController g, boolean toNegate)
	{
		super(toNegate);	
		ghost = g;
	}

	protected boolean check() {
		Vector3D location = ghost.getWorldTranslation().getCol(3); 
		Vector3D location2 = location.minus(ghost.getWaypoint());
		if(location2.magnitude() > 1)
		{
			return true;
		}
		else
		{
			return false;			
		}

	}
}