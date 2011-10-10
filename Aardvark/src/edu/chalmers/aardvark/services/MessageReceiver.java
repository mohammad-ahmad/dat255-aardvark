package edu.chalmers.aardvark.services;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.ToContainsFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import edu.chalmers.aardvark.ctrl.ChatCtrl;
import edu.chalmers.aardvark.ctrl.ServerHandlerCtrl;
import edu.chalmers.aardvark.model.LocalUser;
import edu.chalmers.aardvark.util.ServerConnection;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MessageReceiver extends Service implements PacketListener {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		PacketFilter filter = new ToContainsFilter(LocalUser.getLocalUser()
			.getAardvarkID());
	
		ServerConnection.getConnection().addPacketListener(this, filter);
		Log.i("INFO", this.toString() + " STARTED");
		
		return START_STICKY;
    }

    @Override
    public void processPacket(Packet packet) {
    	Log.i("INFO", "Received packet, processing...");
		if (packet instanceof Message) {
			Log.i("INFO", "Message received from "+ packet.getFrom());
			Message m = (Message) packet;
			Log.i("INFO", "Message::"+m.getBody());
			String fromUser = packet.getFrom();
			String fromAardvarkID = fromUser.substring(0, fromUser.lastIndexOf("@"));
			packet.setFrom(fromAardvarkID);
		    ChatCtrl.getInstance().receiveMessage(packet);
		} else {
			Log.i("INFO", "Package received from "+ packet.getFrom());
			Log.i("INFO", "PACKET: "+packet.toXML());
		}
    }

    @Override
    public IBinder onBind(Intent arg0) {
	// Not used
	return null;
    }
    
    @Override
    public void onDestroy() {
	ServerHandlerCtrl.getInstance().logOut();
    }

}
