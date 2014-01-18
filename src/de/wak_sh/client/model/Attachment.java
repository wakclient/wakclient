package de.wak_sh.client.model;

import java.io.Serializable;

public class Attachment implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String name;

	public Attachment(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Attachment [id=" + id + ", name=" + name + "]";
	}
}
