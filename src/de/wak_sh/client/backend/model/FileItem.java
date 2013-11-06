package de.wak_sh.client.backend.model;

public class FileItem {
	private String name;
	private String path;
	private String date;
	private boolean file;
	private boolean owner;

	public FileItem(String name, String path, String date, boolean file) {
		super();
		this.name = name;
		this.path = path;
		this.date = date;
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean isFile() {
		return file;
	}

	public void setFile(boolean file) {
		this.file = file;
	}

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}

}
