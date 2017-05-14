package GameEngine;

import Game.NPCGhostController;
import sage.ai.behaviortrees.BTAction;
import sage.ai.behaviortrees.BTStatus;

public class ChasePlayer extends BTAction
{
	private NPCGhostController controller;
	public ChasePlayer(NPCGhostController c)
	{
		controller = c;
	}

	protected BTStatus update(float time) {
		controller.setAnimation("Move");
		controller.setChase(true);
		return BTStatus.BH_SUCCESS;
	}
}
