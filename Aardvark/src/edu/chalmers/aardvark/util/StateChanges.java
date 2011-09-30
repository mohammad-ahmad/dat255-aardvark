package edu.chalmers.aardvark.util;

public enum StateChanges {
	CONTACT_ADDED, CONTACT_REMOVED, CONTACT_RENAMED,
	
	ENCRYPTION_ENABLED, ENCRYPTION_DISABLED, ENCRYPTION_REQUEST_RECEIVED, ENCRYPTION_REQUEST_SENT,
	
	MESSAGE_RECEIVED, MESSAGE_SENT, ENCRYPTED_MESSAGE_RECEIVED, ENCRYPTED_MESSAGE_SENT,
	
	SERVER_CONNECTION_LOST, SERVER_CONNECTION_ESTABLISHED, LOGGED_IN, LOGGED_OUT
}