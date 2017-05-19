package GameEngine;

import Game.PlayerAvatar;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class MoveRXAction extends AbstractInputAction
{
	private PlayerAvatar avatar;
	
	public MoveRXAction(PlayerAvatar p)
	{
		avatar = p;
	}
	 
	 public void performAction(float time, net.java.games.input.Event e)
	 {		 
		 Matrix3D view = new Matrix3D();
		 
		 if (e.getValue() < -0.35)
		 { 
			 avatar.move(new Vector3D(0,0,-1), new Vector3D(1,0,0));
		 }
		 else if (e.getValue() > 0.35)
		 {
			 avatar.move(new Vector3D(0,0,1), new Vector3D(-1,0,0));
		 }

	 }
}
