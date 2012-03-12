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
import java.util.HashMap;
import java.util.Map;

import ymsg.network.AccountLockedException;
import ymsg.network.LoginRefusedException;
import ymsg.network.Session;

import org.aitools.programd.Core;
import org.aitools.programd.bot.Bot;
import org.aitools.programd.interfaces.shell.ShellCommandable;
import org.aitools.programd.util.XMLKit;
import org.apache.log4j.Logger;

/**
 * Provides a listener for Yahoo Instant Messenger.
 * 
 * @author <a href="mailto:noel@aitools.org">Noel Bush</a>
 */
public class YahooListener implements Listener, ShellCommandable
{
    /** The parameters that can be set for this listener. */
    protected Map<String, String> parameters;
    
    /** The YM session. */
    protected Session session;

    /** The logger to use. */
    protected Logger logger = Logger.getLogger("programd.listener.yahoo");
    
    /** The user id. */
    protected String userid;
    
    /** The password. */
    protected String password;

    /**
     * Creates a new YahooListener chat listener for a given bot.
     * 
     * @param core the Core object in use
     * @param bot the bot for whom to listen
     * @param parametersToUse the parameters for the listener and their default
     *            values
     */
    public YahooListener(Core core, Bot bot, HashMap<String, String> parametersToUse)
    {
        this.parameters = parametersToUse;
        
        this.userid = this.parameters.get("userid");
        this.password = this.parameters.get("password");
        
        this.session = new Session();
        YahooSessionListener sessionListener = new YahooSessionListener(this.session, core, bot.getID());
        this.session.addSessionListener(sessionListener);
    }

    /**
     * @see org.aitools.programd.listener.Listener#checkParameters()
     */
    public void checkParameters() throws InvalidListenerParameterException
    {
        if (this.userid == null)
        {
            throw new InvalidListenerParameterException("Must specify a userid!");
        }
        if (this.userid.length() == 0)
        {
            throw new InvalidListenerParameterException("Userid cannot be zero-length!");
        }
        if (this.password == null)
        {
            throw new InvalidListenerParameterException("Must specify a password!");
        }
        if (this.password.length() == 0)
        {
            throw new InvalidListenerParameterException("Password cannot be zero-length!");
        }
    }

    /**
     * @see org.aitools.programd.util.ManagedProcess#shutdown()
     */
    public void shutdown()
    {
        try
        {
            this.session.logout();
        }
        catch (IOException e)
        {
            this.logger.error("Yahoo listener: Error logging out!", e);
            return;
        }
        this.logger.info("Yahoo listener logged out.");
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        this.logger.info("Yahoo listener logging in.");
        try
        {
            this.session.login(this.userid, this.password);
        }
        catch (IOException e)
        {
            this.logger.error("IO exception trying to log in to Yahoo.", e);
        } 
        catch (AccountLockedException e)
        {
            this.logger.error("This Yahoo account is locked.");
        }
        catch (LoginRefusedException e)
        {
            this.logger.error("The Yahoo network refuses login.");
        }
    }

    /**
     * @see org.aitools.programd.interfaces.shell.ShellCommandable#getShellCommands()
     */
    public String getShellCommands()
    {
        return "/msg userid message - Sends the given message to the given userid.";
    }

    /**
     * @see org.aitools.programd.interfaces.shell.ShellCommandable#getShellDescription()
     */
    public String getShellDescription()
    {
        return "Program D Yahoo listener.";
    }

    /**
     * @see org.aitools.programd.interfaces.shell.ShellCommandable#getShellID()
     */
    public String getShellID()
    {
        return "yahoo";
    }

    /**
     * @see org.aitools.programd.interfaces.shell.ShellCommandable#processShellCommand(java.lang.String)
     */
    public void processShellCommand(String commandLine)
    {
        if (commandLine.startsWith("msg"))
        {
            int firstSpace = commandLine.indexOf(" ");
            if (firstSpace == -1)
            {
                this.logger.info("Invalid command.");
                return;
            }
            int secondSpace = commandLine.indexOf(" ", firstSpace + 1);
            if (secondSpace == -1)
            {
                this.logger.info("Invalid command.");
                return;
            }
            String recipient = commandLine.substring(firstSpace, secondSpace);
            String message = commandLine.substring(secondSpace + 1);
            try
            {
                this.session.sendMessage(recipient, message);
            }
            catch (IOException e)
            {
                this.logger.error("IO Error sending Yahoo message!", e);
            }
        }
    }
    
    /**
     * Filters out non-text from a message from a Yahoo client.
     * So far, this includes removing any markup (but leaving whatever's inside it),
     * and removing some control codes that it seems the Yahoo client (at least more recent versions of it)
     * uses to format text.
     * 
     * @param text the text to filter
     * @return the filtered text
     */
    public static String filterText(String text)
    {
        return XMLKit.removeMarkup(text).replaceAll("\\x1b\\[x?\\d+m", "");
    }
}
