package edu.chalmers.aardvark.model;

import java.util.ArrayList;
import java.util.List;

public class ContactsContainer {
    private List<Contact> contactList;

    public ContactsContainer() {
	contactList = new ArrayList<Contact>();
    }

    public void addContact(Contact contact) {
	contactList.add(contact);
    }

    public void removeContact(Contact contact) {
	contactList.remove(contact);
    }

    public Contact findContact(String nickname) {
	for (Contact c : contactList) {
	    if (c.getNickname().equals(nickname)) {
		return c;
	    }
	}
	return null;
    }

    public Contact findContactByID(String aardvarkID) {
	for (Contact c : contactList) {
	    if (c.getAardvarkID().equals(aardvarkID)) {
		return c;
	    }
	}
	return null;
    }

}
