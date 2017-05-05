package GameEngine;

import Game.NPCGhostController;
import Game.PlayerAvatar;
import sage.ai.behaviortrees.BTAction;
import sage.ai.behaviortrees.BTStatus;
import sage.scene.Group;

public class ChasePlayer extends BTAction
{
	private NPCGhostController controller;
	public ChasePlayer(NPCGhostController c)
	{
		controller = c;
	}

	protected BTStatus update(float time) {
		controller.setChase(true);
		return BTStatus.BH_SUCCESS;
	}
}
