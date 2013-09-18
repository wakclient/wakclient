package de.wak_sh.client.backend;

public class DataService {
	private static DataService instance;

	public static DataService getInstance() {
		if (instance == null) {
			instance = new DataService();
		}
		return instance;
	}

	private DataService() {
	}

	public boolean login(String email, String password) {
		return true;
	}

	public boolean isLoggedIn() {
		return true;
	}
}
