package GameEngine;

import Game.NPCGhostController;
import sage.ai.behaviortrees.BTAction;
import sage.ai.behaviortrees.BTStatus;

public class MoveToWaypoint extends BTAction
{
	private NPCGhostController controller;
	public MoveToWaypoint(NPCGhostController c)
	{
		controller = c;
	}

	protected BTStatus update(float time) {
		controller.setAnimation("Move");
		controller.setMoving(true);
		return BTStatus.BH_SUCCESS;
	}
}
