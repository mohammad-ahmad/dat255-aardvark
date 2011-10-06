package edu.chalmers.aardvark.ctrl;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.util.Log;
import android.widget.Toast;

import edu.chalmers.aardvark.AardvarkApp;
import edu.chalmers.aardvark.model.LocalUser;
import edu.chalmers.aardvark.model.User;
import edu.chalmers.aardvark.util.ComBus;
import edu.chalmers.aardvark.util.ServerConnection;
import edu.chalmers.aardvark.util.StateChanges;

public class ServerHandlerCtrl {
    private XMPPConnection connection = ServerConnection.getConnection();
    private static ServerHandlerCtrl instance;

    private ServerHandlerCtrl() {
	Log.i("INFO", this.toString() + " STARTED");
    }

    public static ServerHandlerCtrl getInstance() {
	if (instance == null) {
	    instance = new ServerHandlerCtrl();
	}
	return instance;
    }

    public void subscribeToUserPresence(String aardvarkID) {
	Roster roster = connection.getRoster();
	try {
	    roster.createEntry(aardvarkID, aardvarkID, null);
	} catch (XMPPException e) {
	    Toast.makeText(AardvarkApp.getContext(), e.getMessage().toString(),
		    Toast.LENGTH_LONG);
	}
    }

    public boolean isOnline(User user) {
	Roster roster = connection.getRoster();
	Presence presence = roster.getPresence(user.getAardvarkID());

	if (presence.isAvailable()) {
	    return true;
	} else {
	    return false;
	}
    }

    public boolean isAliasAvailable(String alias) {
	String aliasMatch = getAardvarkID(alias);
	if (aliasMatch == null) {
	    return true;
	} else {
	    return false;
	}
    }

    public void logInWithAlias(String alias) {
	LocalUser.setAlias(alias);
	
	try {
	    ServerConnection.login(LocalUser.getLocalUser().getAardvarkID(),
	    	LocalUser.getPassword());
	    
	    //Log.i("INFO", "Getting attributes first time...");
	    Registration r = new Registration();
	    connection.sendPacket(r);
	    //Log.i("INFO", "SENT PACKAGE");
	    //Log.i("INFO", r.getAttributes().toString());
	    
		Map<String, String> aliasAttribute = new HashMap<String, String>();
		aliasAttribute.put("name", LocalUser.getLocalUser().getAlias());
		
		Registration aliasPacket = new Registration();
		aliasPacket.setAttributes(aliasAttribute);

		//Log.i("INFO", "Sending attributes...");
	    connection.sendPacket(aliasPacket);
	    
	    //Log.i("INFO", "Getting attributes second time...");
	    Registration r2 = new Registration();
	    Log.i("INFO", r2.getAttributes().toString());
	    
	    ComBus.notifyListeners(StateChanges.LOGGED_IN.toString(), null);
	} catch (XMPPException e) {
	    ComBus.notifyListeners(StateChanges.LOGIN_FAILED.toString(), null);
	}
    }

    public void logOut() {
	Map<String, String> aliasAttribute = new HashMap<String, String>();
	aliasAttribute.put("name", "");
	
	Registration aliasPacket = new Registration();
	aliasPacket.setAttributes(aliasAttribute);

	connection.sendPacket(aliasPacket);

	ServerConnection.kill();
	ComBus.notifyListeners(StateChanges.LOGGED_OUT.toString(), null);
    }
    
    public String getAardvarkID(String alias) {
	UserSearchManager search = new UserSearchManager(connection);
	ReportedData aliasData = null;
	try {
	    Form searchForm = search
		    .getSearchForm(AardvarkApp
			    .getContext()
			    .getString(
				    edu.chalmers.aardvark.R.string.server_search_address));
	    Form answerForm = searchForm.createAnswerForm();
	    answerForm.setAnswer("name", alias);
	    aliasData = search
		    .getSearchResults(
			    answerForm,
			    AardvarkApp
				    .getContext()
				    .getString(
					    edu.chalmers.aardvark.R.string.server_search_address));
	} catch (XMPPException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	if (aliasData == null || !aliasData.getRows().hasNext()) {
	    return null;
	} 
	
	String aardvarkID = aliasData.getRows().next().getValues("username").toString();
	return aardvarkID;
    }
    
    public String getAlias(String aardvarkID) {
	UserSearchManager search = new UserSearchManager(connection);
	ReportedData idData = null;
	try {
	    Form searchForm = search
		    .getSearchForm(AardvarkApp
			    .getContext()
			    .getString(
				    edu.chalmers.aardvark.R.string.server_search_address));
	    Form answerForm = searchForm.createAnswerForm();
	    answerForm.setAnswer("username", aardvarkID);
	    idData = search
		    .getSearchResults(
			    answerForm,
			    AardvarkApp
				    .getContext()
				    .getString(
					    edu.chalmers.aardvark.R.string.server_search_address));
	} catch (XMPPException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	if (idData == null || !idData.getRows().hasNext()) {
	    return null;
	} 
	
	String alias = idData.getRows().next().getValues("alias").toString();
	return alias;	
    }
}
