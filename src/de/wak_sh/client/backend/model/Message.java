package de.wak_sh.client.backend.model;

public class Message {

	private String id;
	private String date;
	private String sender;
	private String subject;
	private String content;
	private String attachmentFilename;
	private int attachmentId;

	public Message(String id, String date, String sender, String subject) {
		this.id = id;
		this.date = date;
		this.sender = sender;
		this.subject = subject;
	}

	public String getAttachmentFilename() {
		return attachmentFilename;
	}

	public int getAttachmentId() {
		return attachmentId;
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

	public void setAttachmentFilename(String attachmentFilename) {
		this.attachmentFilename = attachmentFilename;
	}

	public void setAttachmentId(int attachmentId) {
		this.attachmentId = attachmentId;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
