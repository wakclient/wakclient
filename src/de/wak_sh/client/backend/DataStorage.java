package de.wak_sh.client.backend;

import java.util.ArrayList;
import java.util.List;

import de.wak_sh.client.model.Email;
import de.wak_sh.client.model.Module;
import de.wak_sh.client.model.UserInformation;

public class DataStorage {
	private UserInformation mUserInformation;
	private List<Module> mModules;
	private List<Email> mEmails;

	private static DataStorage mStorage;

	private DataStorage() {
		mModules = new ArrayList<Module>();
		mEmails = new ArrayList<Email>();
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

	public List<Email> getEmails() {
		return mEmails;
	}

	public void setEmails(List<Email> emails) {
		this.mEmails = emails;
	}

	public void clear() {
		mStorage = null;
	}

}
