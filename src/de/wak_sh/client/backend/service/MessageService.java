package de.wak_sh.client.backend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.wak_sh.client.Utils;
import de.wak_sh.client.backend.DataService;
import de.wak_sh.client.backend.model.Message;

/*
 * Parts of this file are based on the work of Patrick Gotthard:
 * http://www.patrick-gotthard.de/4659/wakclient
 */
public class MessageService {
	private static final String READ_URL = "/c_email.html?&action=getviewmessagessingle&msg_uid=";

	private static MessageService instance;

	public static MessageService getInstance() {
		if (instance == null) {
			instance = new MessageService();
		}
		return instance;
	}

	private DataService dataService;
	private List<Message> messages;

	private MessageService() {
		dataService = DataService.getInstance();
		messages = new ArrayList<Message>();
	}

	public void fetchMessages() throws IOException {
		String regex = "single&msg_uid=(\\d+).*?\">(.*?)<.*?<td>(.*?)</td>.*?<td>(.*?)&";
		List<String[]> nachrichten = Utils.matchAll(regex,
				dataService.getMessagesPage());
		for (String[] nachricht : nachrichten) {
			String id = nachricht[0];
			String subject = nachricht[1];
			String date = nachricht[2];
			String sender = nachricht[3];
			messages.add(new Message(id, date, sender, subject));
		}
	}

	public void fetchMessagesContent(int index) throws IOException {
		Message message = messages.get(index);
		if (message.getContent() == null) {
			String readUrl = READ_URL + message.getId();
			message.setContent(Utils.match("Nachricht:.+?<td>(.*?)</td>",
					dataService.get(readUrl)));
		}
	}

	public List<Message> getMessages() {
		return messages;
	}

	public Message getMessage(int index) {
		return messages.get(index);
	}

}