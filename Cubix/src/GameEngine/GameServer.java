package GameEngine;

import java.io.IOException;
import java.util.UUID;
import java.net.InetAddress;

import sage.networking.server.GameConnectionServer;
import sage.networking.server.IClientInfo;

public class GameServer extends GameConnectionServer<UUID>{
	public GameServer(int port) throws IOException
	{
		super(port,ProtocolType.TCP);
	}
	
	public void acceptClient(IClientInfo client, Object obj)
	{
		String message = (String)obj;
		String[] messageTokens = message.split(",");
		
		
		System.out.println("Join Request Received from " + messageTokens[1]);
		if(messageTokens.length > 0)
		{
			if(messageTokens[0].compareTo("join") == 0)
			{
				UUID clientID = UUID.fromString(messageTokens[1]);
				addClient(client, clientID);
				sendJoinedMessage(clientID, true);
				System.out.println("Client: " + client);
				System.out.println("Client ID: "+ clientID);
			}
		}
	}
	
	public void processPacket(Object obj, InetAddress senderIP, int sndPort)
	{
		String message = (String) obj;
		String[] msgTokens = message.split(",");
		
		if(msgTokens.length > 0)
		{
			if(msgTokens[0].compareTo("bye")== 0)
			{
				UUID clientID = UUID.fromString(msgTokens[1]);
				sendByeMessages(clientID);
				removeClient(clientID);
			}
			
			if(msgTokens[0].compareTo("create")== 0)
			{
				UUID clientID = UUID.fromString(msgTokens[1]);
				String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
				sendCreateMessages(clientID, pos);
				sendWantsDetailsMessages(clientID);
			}

			if(msgTokens[0].compareTo("dsfr")== 0)
			{
				UUID remoteID = UUID.fromString(msgTokens[1]);
				UUID clientID = UUID.fromString(msgTokens[2]);
				String[] pos = {msgTokens[3], msgTokens[4], msgTokens[5]};
				sndDetailsMsg(clientID, remoteID, pos);
			}
			
			if(msgTokens[0].compareTo("move")== 0)
			{
				
			}
				
		}
		
	}
	
	public void sendJoinedMessage(UUID clientID, boolean success)
	{
		try
		{
			String message = new String("join,");
			if(success) message += "success";
			else message += "failure";
			System.out.println("Server Sending Joined Message: " + message + " to: " + clientID.toString());
			sendPacket(message, clientID);
		}
		catch(IOException e) {e.printStackTrace();}
	}
	
	public void sendCreateMessages(UUID clientID, String[] position)
	{
		try
		{
			String message = new String("create," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			forwardPacketToAll(message, clientID);
			System.out.println("Server Sending Create Messages");
		}
		catch(IOException e) {e.printStackTrace();}
	}
	
	public void sndDetailsMsg(UUID clientID, UUID remoteID, String[] position)
	{
		try
		{
			String message = new String("dsfr," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			sendPacket(message, remoteID);
			System.out.println("Server Sending Details Messages");
		}
		catch(IOException e) {e.printStackTrace();}
	}
	
	public void sendWantsDetailsMessages(UUID clientID)
	{
		try
		{
			String message = new String("wsds," + clientID.toString());
			forwardPacketToAll(message, clientID);
		}
		catch(IOException e) {e.printStackTrace();}
	}
	
	public void sendMoveMessages(UUID clientID, String[] position)
	{

	}
	
	public void sendByeMessages(UUID clientID)
	{
		try
		{
			String message = new String("bye," + clientID);
			forwardPacketToAll(message, clientID);
			System.out.println("Server Sending Bye Messages");
		}
		catch(IOException e) {e.printStackTrace();}
		
	}

}