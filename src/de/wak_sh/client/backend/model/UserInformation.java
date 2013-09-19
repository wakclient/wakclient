package de.wak_sh.client.backend.model;

public class UserInformation {

	private String benutzername;
	private String studiengang;
	private String studiengruppe;
	private String matrikelnummer;

	public UserInformation(String benutzername, String studiengang,
			String studiengruppe, String matrikelnummer) {
		this.benutzername = benutzername;
		this.studiengang = studiengang;
		this.studiengruppe = studiengruppe;
		this.matrikelnummer = matrikelnummer;
	}

	public String getBenutzername() {
		return benutzername;
	}

	public String getStudiengang() {
		return studiengang;
	}

	public String getStudiengruppe() {
		return studiengruppe;
	}

	public String getMatrikelnummer() {
		return matrikelnummer;
	}

}
