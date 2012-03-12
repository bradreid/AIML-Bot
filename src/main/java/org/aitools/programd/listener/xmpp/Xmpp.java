package org.aitools.programd.listener.xmpp;

import java.util.HashMap;
import java.util.Map;

import org.aitools.programd.Core;
import org.aitools.programd.bot.Bot;
import org.aitools.programd.listener.InvalidListenerParameterException;
import org.aitools.programd.listener.Listener;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class Xmpp implements Listener {

	protected Map<String, String> parameters;
	protected Logger logger = Logger.getLogger("programd.listener.xmpp");
	protected String userid;
	protected String password;
	protected String room;
	protected String server;
	protected String domain;
	protected XMPPConnection connection;
	protected Core core;
	protected Bot bot;
	protected XmppListener listener;

	public Xmpp(Core core, Bot bot, HashMap<String, String> parametersToUse)
			throws XMPPException {
		this.parameters = parametersToUse;

		this.userid = this.parameters.get("userid");
		this.password = this.parameters.get("password");
		this.room = this.parameters.get("room");
		this.server = this.parameters.get("server");
		this.domain = this.parameters.get("domain");
		this.core = core;
		this.bot = bot;

		ConnectionConfiguration config = null;
		if (this.domain != null) {
			config = new ConnectionConfiguration(this.server, 5222, this.domain);
		} else {
			config = new ConnectionConfiguration(this.server, 5222);
		}
		config.setCompressionEnabled(true);
		config.setSASLAuthenticationEnabled(true);
		// SASLAuthentication.supportSASLMechanism("PLAIN", 0);

		this.connection = new XMPPConnection(config);
		connection.connect();
	}

	@Override
	public void checkParameters() throws InvalidListenerParameterException {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// Log into the server
		try {
			this.connection.login(this.userid, this.password, "resource");

	          // Create a MultiUserChat using an XMPPConnection for a room
	          MultiUserChat muc2 = new MultiUserChat(this.connection, this.room);

	          DiscussionHistory history = new DiscussionHistory();
	          history.setMaxStanzas(0);
	          muc2.join("jr", "password", history, SmackConfiguration.getPacketReplyTimeout());
	          listener = new XmppListener(muc2, this.core, this.bot.getID(), this.userid);


		} catch (XMPPException e) {
			this.logger.error("Could not login to server", e);
		}
	}

}
