package de.wak_sh.client.backend.model;

import java.util.ArrayList;
import java.util.List;

public class Message {

	private int id;
	private String date;
	private String sender;
	private String subject;
	private String content;
	private boolean read;
	private boolean attachment;
	private List<String> attachmentFilenames;
	private List<Integer> attachmentIds;

	public Message(int id, String date, String sender, String subject,
			boolean read, boolean attachment) {
		this.id = id;
		this.date = date;
		this.sender = sender;
		this.subject = subject;
		this.read = read;
		this.attachment = attachment;
		attachmentFilenames = new ArrayList<String>();
		attachmentIds = new ArrayList<Integer>();
	}

	public String getContent() {
		return content;
	}

	public String getDate() {
		return date;
	}

	public int getId() {
		return id;
	}

	public String getSender() {
		return sender;
	}

	public String getSubject() {
		return subject;
	}

	public boolean hasAttachment() {
		return attachment;
	}

	public boolean isRead() {
		return read;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public void addAttachment(int id, String filename) {
		attachmentFilenames.add(filename);
		attachmentIds.add(id);
	}

	public List<String> getAttachmentFilenames() {
		return attachmentFilenames;
	}

	public List<Integer> getAttachmentIds() {
		return attachmentIds;
	}

}
