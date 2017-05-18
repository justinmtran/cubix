package GameEngine;

import Game.NPCGhostController;
import sage.ai.behaviortrees.BTAction;
import sage.ai.behaviortrees.BTStatus;

public class GhostDefault extends BTAction
{
	private NPCGhostController controller;
	public GhostDefault(NPCGhostController c)
	{
		controller = c;
	}

	protected BTStatus update(float time) {
		controller.setAnimation("Default");
		controller.nextWaypoint();
		return BTStatus.BH_SUCCESS;
	}
}
