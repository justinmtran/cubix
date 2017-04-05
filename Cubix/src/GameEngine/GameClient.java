package GameEngine;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import Game.CubixGame;
import Game.GhostAvatar;
import graphicslib3D.Vector3D;
import sage.networking.client.GameConnectionClient;

public class GameClient extends GameConnectionClient{
	private CubixGame game;
	private UUID id;
	private ArrayList<GhostAvatar> ghostAvatars;

	
	public GameClient(InetAddress remAddr, int port, ProtocolType pType, CubixGame game) throws IOException
	{
		super(remAddr, port, pType);
		this.game = game;
		this.id = UUID.randomUUID();
		this.ghostAvatars = new ArrayList<GhostAvatar>();

	}
	
	protected void processPacket (Object msg)
	{
		System.out.println("MESSAGE: " + (String)msg);
		String message = (String)msg;
		String[] msgTokens = message.split(",");
		
		if(msgTokens[0].compareTo("join")== 0)
		{
			System.out.println("Join " + msgTokens[1]);
			if(msgTokens[1].compareTo("success")== 0)
			{
				game.setIsConnected(true);
				sendCreateMessage(game.getPosition());
			}
			if(msgTokens[1].compareTo("failure")== 0)
			{
				game.setIsConnected(false);
			}
		
		}
			
		if(msgTokens[0].compareTo("bye")== 0)
		{
			UUID ghostID = UUID.fromString(msgTokens[1]);
			removeGhostAvatar(ghostID);
			
			System.out.println("Bye Received by server");
		}
		
		if(msgTokens[0].compareTo("dsfr")== 0)
		{
			UUID ghostID = UUID.fromString(msgTokens[1]);
			Vector3D ghostPosition = new Vector3D(Double.parseDouble(msgTokens[2]), Double.parseDouble(msgTokens[3]), Double.parseDouble(msgTokens[4]));
			createGhostAvatar(ghostID, ghostPosition);
			
			System.out.println("DSFR Received by server");
		}
		
		if(msgTokens[0].compareTo("create")== 0)
		{
			UUID ghostID = UUID.fromString(msgTokens[1]);
			Vector3D ghostPosition = new Vector3D(Double.parseDouble(msgTokens[2]), Double.parseDouble(msgTokens[3]), Double.parseDouble(msgTokens[4]));
			createGhostAvatar(ghostID, ghostPosition);
			
			System.out.println("Create Received by server");
			
		}
		
		if(msgTokens[0].compareTo("wsds")== 0)
		{
			UUID ghostID = UUID.fromString(msgTokens[1]);
			Vector3D pos = game.getPosition();
			sendDetailsForMessage(ghostID, pos);
			
			System.out.println("WSDS Received by server");

		}
		
		if(msgTokens[0].compareTo("move")== 0)
		{
			UUID ghostID = UUID.fromString(msgTokens[1]);
			GhostAvatar ghost = getGhost(ghostID);
			ghost.translate(Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));
			
		}
	}
	
	public void sendCreateMessage(Vector3D pos)
	{
		try
		{
			String message = new String("create," + id.toString());
			message += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ();
			sendPacket(message);
		}
		catch(IOException e) {e.printStackTrace();}
		System.out.println("Sending Create Message: X = " + pos.getX() + ", Y = " + pos.getY() + ", Z = " + pos.getZ());
	}
	
	public void sendJoinMessage()
	{
		try
		{
			sendPacket(new String("join," + id.toString()));
		}
		catch(IOException e) {e.printStackTrace();}
		System.out.println("Sending Join message: " + id.toString());
	}

	public void sendByeMessage()
	{
		try
		{
			System.out.println("Sending Bye message to server");
			String message = new String("bye," + id);
			sendPacket(message);
		}
		catch(IOException e) {e.printStackTrace();}
		
	}
	
	public void sendDetailsForMessage(UUID remid, Vector3D pos)
	{
		try
		{
			String message = new String("dsfr," + remid.toString() + "," + id.toString());
			message += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ();
			sendPacket(message);
		}
		catch(IOException e) {e.printStackTrace();}
		
		System.out.println("Sending DetailsForMessage");
	}
	
	public void sendMoveMessage(Vector3D pos)
	{
		try
		{
			String message = new String("move," + id.toString());
			message += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ();
			sendPacket(message);
		}
		catch(IOException e) {e.printStackTrace();}

	}
	
	public void removeGhostAvatar(UUID id)
	{
		System.out.println("Remove Ghost Avatar");
		
		GhostAvatar ghost = getGhost(id);

		if(ghost != null)
		{
			game.removeGhost(ghost);
			ghostAvatars.remove(ghost);
		}
		else
		{
			System.out.println("Ghost does not exist");
		}
		
	}
	
	
	public void createGhostAvatar(UUID id, Vector3D position)
	{
		GhostAvatar newGhost = new GhostAvatar(position, id);
		game.addGhost(newGhost);
		ghostAvatars.add(newGhost);
		
		System.out.println("Adding new Ghost");
	}
	
	public ArrayList<GhostAvatar> getGhostAvatars()
	{
		return ghostAvatars;
	}
	
	private GhostAvatar getGhost(UUID id)
	{
		Iterator<GhostAvatar> iterator = ghostAvatars.iterator();
		GhostAvatar ghost;
		
		while(iterator.hasNext())
		{
			ghost = iterator.next();
			if (ghost.getID().toString().equals(id.toString()))
			{
				return ghost;
			}
		}
		return null;
		
	}
}