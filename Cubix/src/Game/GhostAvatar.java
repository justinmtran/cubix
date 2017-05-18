package Game;

import java.util.UUID;
import java.util.LinkedList;
import java.util.Queue;

import graphicslib3D.Vector3D;

public class GhostAvatar extends PlayerAvatar{
	
	private UUID id;
	private Queue<Vector3D> rotQueue, transQueue;
	private boolean reset;
	
	
	public GhostAvatar(String textureName, Vector3D position, UUID id, CubixGame g, Tile t) {
		super(textureName, g, null, t);
		this.translate((float)position.getX(), (float)position.getY(), (float)position.getZ());
		this.id = id;
		rotQueue = new LinkedList<Vector3D>();
		transQueue = new LinkedList<Vector3D>();
		this.reset();
		
	}
	
	public UUID getID()
	{
		return id;
	}
	
	public void move(Vector3D rotAxis, Vector3D trans)
	{
		rotQueue.add(rotAxis);
		transQueue.add(trans);
	}
	
	public void update(float time)
	{
		if(!getIsMoving() && reset)
		{
			super.reset();
			reset = false;
		}
		if(!getIsMoving() && !transQueue.isEmpty())
		{
			super.move(rotQueue.remove(), transQueue.remove());
		}
		super.update(time);
	}
	
	public void reset()
	{
		reset = true;
	}

}
