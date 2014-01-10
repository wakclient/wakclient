package de.wak_sh.client.backend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.util.SparseArray;
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
	private SparseArray<String> recipients;

	private MessageService() {
		dataService = DataService.getInstance();
		messages = new ArrayList<Message>();
	}

	public void fetchMessages() throws IOException {
		String regex = "(messages_(un)?read).*?<td>(<img.*?messages_attachment)?.*?single&msg_uid=(\\d+).*?\">(.*?)<.*?<td>(.*?)</td>.*?<td>(.*?)&";
		List<String[]> nachrichten = Utils.matchAll(regex,
				dataService.getMessagesPage());
		for (String[] nachricht : nachrichten) {
			boolean read = (nachricht[1] == null);
			boolean attachment = (nachricht[2] != null);
			int id = Integer.parseInt(nachricht[3]);
			String subject = nachricht[4];
			String date = nachricht[5];
			String sender = nachricht[6];
			messages.add(new Message(id, date, sender, subject, read,
					attachment));
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

	public void fetchMessageRecipients() throws IOException {
		String regex = "option value=\"(\\d+)\">(.*?)</";
		List<String[]> matches = Utils.matchAll(regex,
				dataService.getRecipientPage());
		recipients = new SparseArray<String>();
		for (String[] match : matches) {
			recipients.put(Integer.parseInt(match[0]), match[1]);
		}
	}

	public List<Message> getMessages() {
		return messages;
	}

	public Message getMessage(int index) {
		return messages.get(index);
	}

	public SparseArray<String> getRecipients() {
		return recipients;
	}

	public static String buildAttachmentUrl(int msgId, int attachmentId) {
		return String.format(Locale.getDefault(), ATTACHMENT_URL, msgId,
				attachmentId);
	}

}