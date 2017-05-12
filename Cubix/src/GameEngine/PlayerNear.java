package GameEngine;


import Game.PlayerAvatar;
import graphicslib3D.Vector3D;
import sage.ai.behaviortrees.BTCondition;
import sage.scene.Group;

public class PlayerNear extends BTCondition
{
	private Group ghost;
	private PlayerAvatar player;
	public PlayerNear(Group g, PlayerAvatar p, boolean toNegate)
	{
		super(toNegate);	
		ghost = g;
		player = p;
	}

	protected boolean check() {

		Vector3D location = ghost.getWorldTranslation().getCol(3); 
		Vector3D location2 = location.minus(player.getWorldTranslation().getCol(3));
		if(location2.magnitude() < 15)
		{
			return true;
		}
		else
		{
			return false;			
		}

	}
}