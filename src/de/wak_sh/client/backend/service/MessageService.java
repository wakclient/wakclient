package de.wak_sh.client.backend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.wak_sh.client.Utils;
import de.wak_sh.client.backend.model.Message;

/*
 * Parts of this file are based on the work of Patrick Gotthard:
 * http://www.patrick-gotthard.de/4659/wakclient
 */
public class MessageService {
	private static final String READ_URL = "/c_email.html?&action=getviewmessagessingle&msg_uid=";
	private static final String ATTACHMENT_URL = "/index.php?eID=tx_cwtcommunity_pi1_download&m=%d&a=%d";

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
			int id = Integer.parseInt(nachricht[0]);
			String subject = nachricht[1];
			String date = nachricht[2];
			String sender = nachricht[3];
			messages.add(new Message(id, date, sender, subject));
		}
	}

	public void fetchMessagesContent(int index) throws IOException {
		Message message = messages.get(index);
		if (message.getContent() == null) {
			String pattern = "Nachricht:.+?<td>(.*?)</td>";
			String subject = dataService.fetchPage(READ_URL + message.getId());
			String content = Utils.match(pattern, subject).replaceAll("<br />",
					"");
			message.setContent(content);

			pattern = "<a href=\"index\\.php.*?&a=(\\d+).*?\".*?&nbsp;(.*?)</a";
			List<String[]> matches = Utils.matchAll(pattern, subject);
			for (String[] match : matches) {
				message.addAttachment(Integer.parseInt(match[0]), match[1]);
			}
		}
	}

	public List<Message> getMessages() {
		return messages;
	}

	public Message getMessage(int index) {
		return messages.get(index);
	}

	public static String buildAttachmentUrl(int msgId, int attachmentId) {
		return String.format(Locale.getDefault(), ATTACHMENT_URL, msgId,
				attachmentId);
	}

}