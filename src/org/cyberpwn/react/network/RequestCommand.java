package org.cyberpwn.react.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import org.cyberpwn.react.L;
import org.cyberpwn.react.util.JSONObject;

public class RequestCommand extends Thread
{
	private RequestCommandCallback callback;
	private NetworkedServer ns;
	private Socket s;
	private String action;
	
	public RequestCommand(NetworkedServer ns, RequestCommandCallback callback, String action)
	{
		this.callback = callback;
		this.ns = ns;
		this.action = action;
	}
	
	@Override
	public void run()
	{
		try
		{
			L.l("Requesting Command: " + action);
			s = new Socket(ns.getAddress(), ns.getPort());
			s.setSoTimeout(500);
			DataInputStream i = new DataInputStream(s.getInputStream());
			DataOutputStream o = new DataOutputStream(s.getOutputStream());
			PacketRequest pr = new PacketRequest(ns.getUsername(), ns.getPassword(), "COMMAND " + action);
			L.n("OUT: " + pr.toString());
			o.writeUTF(pr.toString());
			o.flush();
			String response = i.readUTF();
			PacketResponse ps = new PacketResponse(new JSONObject(response));
			L.n("IN: " + ps.toString());
			Thread.sleep(50);
			
			if(ps.getString("type").equals("OK"))
			{
				callback.run(true);
			}
			
			else
			{
				callback.run(false);
			}
		}
		
		catch(Exception e)
		{
			
		}
	}
}
