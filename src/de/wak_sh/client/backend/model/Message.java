package de.wak_sh.client.backend.model;

public class Message {

	private String id;
	private String date;
	private String sender;
	private String subject;
	private String content;

	public Message(String id, String date, String sender, String subject) {
		this.id = id;
		this.date = date;
		this.sender = sender;
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public String getDate() {
		return date;
	}

	public String getId() {
		return id;
	}

	public String getSender() {
		return sender;
	}

	public String getSubject() {
		return subject;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
