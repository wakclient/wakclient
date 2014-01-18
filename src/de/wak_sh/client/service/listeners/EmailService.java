package de.wak_sh.client.service.listeners;

import java.io.IOException;
import java.util.List;

import de.wak_sh.client.model.Email;
import de.wak_sh.client.model.Recipient;

public interface EmailService {
	public void sendEmail(Email email, List<Recipient> recipients);

	public List<Recipient> getRecipients(String username) throws IOException;

	public List<Recipient> getRecipientOptions() throws IOException;

	public Email getEmailContent(Email email) throws IOException;

	public List<Email> getEmails() throws IOException;
}
