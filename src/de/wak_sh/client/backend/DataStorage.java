package de.wak_sh.client.backend;

import java.util.ArrayList;
import java.util.List;

import de.wak_sh.client.model.Message;
import de.wak_sh.client.model.Module;
import de.wak_sh.client.model.UserInformation;

public class DataStorage {
	private UserInformation mUserInformation;
	private List<Module> mModules;
	private List<Message> mMessages;

	private static DataStorage mStorage;

	private DataStorage() {
		mModules = new ArrayList<Module>();
		mMessages = new ArrayList<Message>();
	}

	public static DataStorage getInstance() {
		if (mStorage == null) {
			mStorage = new DataStorage();
		}
		return mStorage;
	}

	public UserInformation getUserInformation() {
		return mUserInformation;
	}

	public void setUserInformation(UserInformation userInformation) {
		this.mUserInformation = userInformation;
	}

	public List<Module> getModules() {
		return mModules;
	}

	public void setModules(List<Module> modules) {
		this.mModules = modules;
	}

	public List<Message> getMessages() {
		return mMessages;
	}

	public void setMessages(List<Message> messages) {
		this.mMessages = messages;
	}

	public void clear() {
		mStorage = null;
	}

}
