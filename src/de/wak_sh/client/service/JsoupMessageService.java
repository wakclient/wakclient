package de.wak_sh.client.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.wak_sh.client.backend.Utils;
import de.wak_sh.client.model.Attachment;
import de.wak_sh.client.model.Message;
import de.wak_sh.client.model.Recipient;
import de.wak_sh.client.service.listeners.MessageService;

public final class JsoupMessageService implements MessageService {

	private static JsoupMessageService messageService;

	private JsoupDataService dataService;

	private JsoupMessageService() {
		dataService = JsoupDataService.getInstance();
	}

	public static JsoupMessageService getInstance() {
		if (messageService == null) {
			messageService = new JsoupMessageService();
		}
		return messageService;
	}

	@Override
	public void sendMessage(Message message, List<Recipient> recipients) {
		// TODO: Implement
		Map<String, String> data = new HashMap<String, String>();
		data.put("subject", message.getSubject());
		data.put("body", message.getMessage());
	}

	@Override
	public List<Recipient> getRecipients(String username) throws IOException {
		List<Recipient> recipients = new ArrayList<Recipient>();

		Map<String, String> data = new HashMap<String, String>();
		data.put("tx_cwtcommunity_pi1[usersearch]", username);

		Connection conn = dataService.connect(JsoupDataService.BASE_URL
				+ "/89.html", Connection.Method.POST, true);
		conn.data(data);

		Document doc = dataService.fetchPage(conn).parse();

		for (Element element : doc.getElementsByTag("span")) {
			String[] recipientData = element.attr("title").split("/");
			long id = Long.parseLong(recipientData[0]);
			String name = element.text();
			String location = "Kein Ort gefunden";
			if (recipientData.length > 1) {
				location = recipientData[1];
			}

			recipients.add(new Recipient(id, name, location));
		}

		return recipients;
	}

	@Override
	public List<Recipient> getRecipientOptions() throws IOException {
		List<Recipient> options = new ArrayList<Recipient>();

		Connection conn = dataService.connect(JsoupDataService.BASE_URL
				+ "/89.html", Connection.Method.GET, true);

		Document doc = dataService.fetchPage(conn).parse();

		for (Element element : doc.getElementsByTag("option")) {
			long id = Long.parseLong(element.attr("value"));
			String name = element.text();

			options.add(new Recipient(id, name, null));
		}

		return options;
	}

	@Override
	public Message getMessageContent(Message message) throws IOException {
		Connection conn = dataService.connect(JsoupDataService.BASE_URL
				+ "/c_email.html?&action=getviewmessagessingle&msg_uid="
				+ message.getId(), Connection.Method.GET, true);

		Response response = dataService.fetchPage(conn);

		String regexMessage = "Nachricht:.+?<td>(.*?)</td>";
		String regexAttachments = "<a href=\"index\\.php.*?&a=(\\d+).*?\".*?&nbsp;(.*?)</a";
		String content = Utils.matchAll(regexMessage, response.body()).get(0)
				.group(1);

		message.setMessage(content);

		List<MatchResult> matchResults = Utils.matchAll(regexAttachments,
				response.body());
		for (MatchResult matchResult : matchResults) {
			long id = Long.parseLong(matchResult.group(1));
			String name = matchResult.group(2);

			message.getAttachments().add(new Attachment(id, name));
		}

		return message;
	}

	@Override
	public List<Message> getMessages() throws IOException {
		List<Message> messages = new ArrayList<Message>();

		Connection conn = dataService.connect(JsoupDataService.BASE_URL
				+ "/c_email.html", Connection.Method.GET, true);

		Document doc = dataService.fetchPage(conn).parse();

		String regex = "(messages_(un)?read).*?<td>(<img.*?messages_attachment)?.*?"
				+ "single&amp;msg_uid=(\\d+).*?\">(.*?)<.*?<td>(.*?)</td>.*?<td>(.*?)&";
		List<MatchResult> matchResults = Utils.matchAll(regex, doc.html());
		for (MatchResult matchResult : matchResults) {
			boolean read = matchResult.group(2) == null;
			boolean attachment = matchResult.group(3) != null;
			long id = Long.parseLong(matchResult.group(4));
			String subject = matchResult.group(5);
			String date = matchResult.group(6);
			String from = matchResult.group(7);

			messages.add(new Message(id, from, subject, null, date, read,
					attachment));
		}

		return messages;
	}
}
