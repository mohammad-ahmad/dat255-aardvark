package edu.chalmers.aardvark.services;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Packet;

import edu.chalmers.aardvark.model.LocalUser;
import edu.chalmers.aardvark.util.MessagePacket;
import edu.chalmers.aardvark.util.ServerConnection;
import android.app.IntentService;
import android.content.Intent;

public class MessageSender extends IntentService {

    public MessageSender() {
	super("MessageSender");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
	String message = intent.getStringExtra("msg");
	String recipient = intent.getStringExtra("to");

	Packet messagePacket = new MessagePacket(LocalUser.getLocalUser()
		.getAardvarkID(), recipient, message);

	XMPPConnection connection = ServerConnection.getConnection();
	connection.sendPacket(messagePacket);
    }

}
