package de.wak_sh.client.backend.model;

import java.util.ArrayList;
import java.util.List;

public class Message {

	private String id;
	private String date;
	private String sender;
	private String subject;
	private String content;
	private List<String> attachmentFilenames;
	private List<Integer> attachmentIds;

	public Message(String id, String date, String sender, String subject) {
		this.id = id;
		this.date = date;
		this.sender = sender;
		this.subject = subject;
		attachmentFilenames = new ArrayList<String>();
		attachmentIds = new ArrayList<Integer>();
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
