package GameEngine;

import Game.PlayerAvatar;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class MoveRYAction extends AbstractInputAction
{
	private PlayerAvatar avatar;
	 

	 public MoveRYAction(PlayerAvatar p)
	 { 
		 avatar = p;
	 }
	 
	 public void performAction(float time, net.java.games.input.Event e)
	 {
		 if (e.getValue() < -0.35)
		 { 
			 avatar.move(new Vector3D(1,0,0), new Vector3D(0,0,1));
		 }
		 else if (e.getValue() > 0.35)
		 {
			 avatar.move(new Vector3D(-1,0,0), new Vector3D(0,0,-1));
		 }
	 }
}
