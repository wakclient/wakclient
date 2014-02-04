package de.wak_sh.client.service.listeners;

import java.io.IOException;
import java.util.List;

import de.wak_sh.client.model.Message;
import de.wak_sh.client.model.Recipient;

public interface MessageService {
	public void sendMessage(Message message, List<Recipient> recipients);

	public List<Recipient> getRecipients(String username) throws IOException;

	public List<Recipient> getRecipientOptions() throws IOException;

	public Message getMessageContent(Message message) throws IOException;

	public List<Message> getMessages() throws IOException;
}
