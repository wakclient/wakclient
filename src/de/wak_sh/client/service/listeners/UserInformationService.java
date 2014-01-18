package de.wak_sh.client.service.listeners;

import java.io.IOException;

import de.wak_sh.client.model.UserInformation;

public interface UserInformationService {
	public UserInformation getUserInformation() throws IOException;
}
