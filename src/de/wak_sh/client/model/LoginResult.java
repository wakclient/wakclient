package de.wak_sh.client.model;

public class LoginResult {
	private boolean loggedIn;
	private String errorMessage;

	public LoginResult() {
		loggedIn = true;
	}

	public LoginResult(String errorMessage) {
		loggedIn = false;
		this.errorMessage = errorMessage;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
