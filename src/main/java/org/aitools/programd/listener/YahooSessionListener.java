/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.aitools.programd.listener;

import java.io.IOException;

import org.aitools.programd.Core;
import org.aitools.programd.util.DeveloperError;
import org.aitools.programd.util.XMLKit;
//import org.aitools.programd.util.XMLKit;
import org.apache.log4j.Logger;

import ymsg.network.Session;
import ymsg.network.event.SessionChatEvent;
import ymsg.network.event.SessionConferenceEvent;
import ymsg.network.event.SessionErrorEvent;
import ymsg.network.event.SessionEvent;
import ymsg.network.event.SessionExceptionEvent;
import ymsg.network.event.SessionFileTransferEvent;
import ymsg.network.event.SessionFriendEvent;
import ymsg.network.event.SessionListener;
import ymsg.network.event.SessionNewMailEvent;
import ymsg.network.event.SessionNotifyEvent;

/**
 * Implements the methods of {@link SessionListener}
 * that we care about for our YahooListener.
 * 
 * @author <a href="mailto:noel@aitools.org">Noel Bush</a>
 * @version 1.0
 */
public class YahooSessionListener implements SessionListener
{
    /** The Core object in use. */
    protected Core core;

    /** The id of the bot for which this listener works. */
    protected String botID;

    /** The logger to use. */
    protected Logger logger = Logger.getLogger("programd.listener.irc");
    
    /** The YM session. */
    protected Session session;

    /**
     * Creates a new YahooSessionListner.
     * 
     * @param sessionToUse the Session to use
     * @param coreToUse the Core to use
     * @param botIDToUse the botid to use
     */
    public YahooSessionListener(Session sessionToUse, Core coreToUse, String botIDToUse)
    {
        this.session = sessionToUse;
        this.core = coreToUse;
        this.botID = botIDToUse;
    }
    
    /**
     * @see ymsg.network.event.SessionListener#fileTransferReceived(ymsg.network.event.SessionFileTransferEvent)
     */
    @SuppressWarnings("unused")
    public void fileTransferReceived(SessionFileTransferEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#connectionClosed(ymsg.network.event.SessionEvent)
     */
    @SuppressWarnings("unused")
    public void connectionClosed(SessionEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#listReceived(ymsg.network.event.SessionEvent)
     */
    @SuppressWarnings("unused")
    public void listReceived(SessionEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#messageReceived(ymsg.network.event.SessionEvent)
     */
    public void messageReceived(SessionEvent ev)
    {
        String senderID = ev.getFrom();
        // Be sure the Core is ready.
        while (this.core.getStatus() != Core.Status.READY)
        {
            this.logger.warn("Yahoo listener: Waiting for Core to become ready.");
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                // Nothing to do.
            }
        }
        String[] response = XMLKit.filterViaHTMLTags(this.core.getResponse(YahooListener.filterText(ev.getMessage()), senderID, this.botID));
        if (response.length > 0)
        {
            for (int line = 0; line < response.length; line++)
            {
                try
                {
                    this.session.sendMessage(senderID, XMLKit.filterWhitespace(response[line]));
                }
                catch (IOException e)
                {
                    throw new DeveloperError("IO error sending a message!", e);
                }
            }
        }
    }

    /**
     * @see ymsg.network.event.SessionListener#buzzReceived(ymsg.network.event.SessionEvent)
     */
    @SuppressWarnings("unused")
    public void buzzReceived(SessionEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#offlineMessageReceived(ymsg.network.event.SessionEvent)
     */
    @SuppressWarnings("unused")
    public void offlineMessageReceived(SessionEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#errorPacketReceived(ymsg.network.event.SessionErrorEvent)
     */
    @SuppressWarnings("unused")
    public void errorPacketReceived(SessionErrorEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#inputExceptionThrown(ymsg.network.event.SessionExceptionEvent)
     */
    @SuppressWarnings("unused")
    public void inputExceptionThrown(SessionExceptionEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#newMailReceived(ymsg.network.event.SessionNewMailEvent)
     */
    @SuppressWarnings("unused")
    public void newMailReceived(SessionNewMailEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#notifyReceived(ymsg.network.event.SessionNotifyEvent)
     */
    @SuppressWarnings("unused")
    public void notifyReceived(SessionNotifyEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#contactRequestReceived(ymsg.network.event.SessionEvent)
     */
    @SuppressWarnings("unused")
    public void contactRequestReceived(SessionEvent arg0)
    {
        // Always grant the request.
    }

    /**
     * @see ymsg.network.event.SessionListener#contactRejectionReceived(ymsg.network.event.SessionEvent)
     */
    @SuppressWarnings("unused")
    public void contactRejectionReceived(SessionEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#conferenceInviteReceived(ymsg.network.event.SessionConferenceEvent)
     */
    @SuppressWarnings("unused")
    public void conferenceInviteReceived(SessionConferenceEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#conferenceInviteDeclinedReceived(ymsg.network.event.SessionConferenceEvent)
     */
    @SuppressWarnings("unused")
    public void conferenceInviteDeclinedReceived(SessionConferenceEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#conferenceLogonReceived(ymsg.network.event.SessionConferenceEvent)
     */
    @SuppressWarnings("unused")
    public void conferenceLogonReceived(SessionConferenceEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#conferenceLogoffReceived(ymsg.network.event.SessionConferenceEvent)
     */
    @SuppressWarnings("unused")
    public void conferenceLogoffReceived(SessionConferenceEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#conferenceMessageReceived(ymsg.network.event.SessionConferenceEvent)
     */
    @SuppressWarnings("unused")
    public void conferenceMessageReceived(SessionConferenceEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#friendsUpdateReceived(ymsg.network.event.SessionFriendEvent)
     */
    @SuppressWarnings("unused")
    public void friendsUpdateReceived(SessionFriendEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#friendAddedReceived(ymsg.network.event.SessionFriendEvent)
     */
    @SuppressWarnings("unused")
    public void friendAddedReceived(SessionFriendEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#friendRemovedReceived(ymsg.network.event.SessionFriendEvent)
     */
    @SuppressWarnings("unused")
    public void friendRemovedReceived(SessionFriendEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#chatLogonReceived(ymsg.network.event.SessionChatEvent)
     */
    @SuppressWarnings("unused")
    public void chatLogonReceived(SessionChatEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#chatLogoffReceived(ymsg.network.event.SessionChatEvent)
     */
    @SuppressWarnings("unused")
    public void chatLogoffReceived(SessionChatEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#chatMessageReceived(ymsg.network.event.SessionChatEvent)
     */
    @SuppressWarnings("unused")
    public void chatMessageReceived(SessionChatEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#chatUserUpdateReceived(ymsg.network.event.SessionChatEvent)
     */
    @SuppressWarnings("unused")
    public void chatUserUpdateReceived(SessionChatEvent arg0)
    {
        // Ignore this.
    }

    /**
     * @see ymsg.network.event.SessionListener#chatConnectionClosed(ymsg.network.event.SessionEvent)
     */
    @SuppressWarnings("unused")
    public void chatConnectionClosed(SessionEvent arg0)
    {
        // Ignore this.
    }
}
