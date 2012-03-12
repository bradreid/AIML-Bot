package org.aitools.programd.listener.xmpp;

import org.aitools.programd.Core;
import org.aitools.programd.util.XMLKit;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class XmppListener {

	Core core;
	String botID;
	Logger logger = Logger.getLogger("programd.listener.irc");
	MultiUserChat muc;
	String me;

	/**
	 * Creates a new YahooSessionListner.
	 * 
	 * @param sessionToUse the Session to use
	 * @param coreToUse the Core to use
	 * @param botIDToUse the botid to use
	 */
	public XmppListener(MultiUserChat sessionToUse, Core coreToUse, String botIDToUse, String username)
	{
		this.muc = sessionToUse;
		this.core = coreToUse;
		this.botID = botIDToUse;
		this.me = username;

		muc.addMessageListener(new PacketListener(){

			@Override
			public void processPacket(Packet arg0) {
				if (arg0 instanceof Message) {
					Message message = (Message) arg0;
					ProcessMessage(message.getFrom(), message.getBody());				
				}

			}

		});
	}

	private void ProcessMessage(String from, String message) {
		if (me == username(from)  || "jr".equals(username(from)) || !toJr(message)){
			return;
		}
		message = stripMessage(message);
		String[] response = XMLKit.filterViaHTMLTags(this.core.getResponse(message, username(from), this.botID));
		StringBuffer r = new StringBuffer();
		for (int i = 0; i < response.length; i++) {
			r.append(response[i]);
		}
		
		if (r.toString().length() > 0)
		{
			try {
				muc.sendMessage(r.toString());							
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}  
	
	private String username(String from) {
		String[] tokens = from.split("/");
		return tokens[tokens.length - 1];
	}
	
	private boolean toJr(String message){
		message = message.trim().toLowerCase();
		if (message.substring(0, 2).equals("jr")){
			message = message.substring(3);
			return true;
		}
		if (message.substring(0, 3).equals("@jr")){
			message = message.substring(4);
			return true;
		}
		return false;
	}
	
	private String stripMessage(String message) {
		if (message.substring(0, 2).equals("jr")){
			message = message.substring(3);
			return message;
		}
		if (message.substring(0, 3).equals("@jr")){
			message = message.substring(4);
			return message;
		}		
		return message;
	}
}
