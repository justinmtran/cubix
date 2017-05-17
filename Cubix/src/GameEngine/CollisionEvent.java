package GameEngine;
import Game.PlayerAvatar;
import sage.event.*;

public class CollisionEvent extends AbstractGameEvent
{
	private PlayerAvatar object;
	 
	public CollisionEvent(PlayerAvatar obj) 
	{
		object = obj;	
	}
	
	public PlayerAvatar getObject()
	{
		return object;
	}
}