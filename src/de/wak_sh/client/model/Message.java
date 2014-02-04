package de.wak_sh.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String from;
	private String subject;
	private String message;
	private String date;
	private boolean read;
	private boolean attachment;
	private List<Attachment> attachments;

	public Message(long id, String from, String subject, String message,
			String date, boolean read, boolean attachment) {
		super();
		this.id = id;
		this.from = from;
		this.subject = subject;
		this.message = message;
		this.date = date;
		this.read = read;
		this.attachment = attachment;
		this.attachments = new ArrayList<Attachment>();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public boolean hasAttachment() {
		return attachment;
	}

	public void setHasAttachment(boolean attachment) {
		this.attachment = attachment;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", from=" + from + ", subject=" + subject
				+ ", message=" + message + ", date=" + date + ", read=" + read
				+ ", attachment=" + attachment + ", attachments=" + attachments
				+ "]";
	}

}
