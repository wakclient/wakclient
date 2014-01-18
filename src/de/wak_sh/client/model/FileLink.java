package de.wak_sh.client.model;

import java.io.Serializable;

public class FileLink implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String dir;
	private String date;
	private Mountpoint mountpoint;
	private boolean file;

	public FileLink(String name, String dir, String date,
			Mountpoint mountpoint, boolean file) {
		super();
		this.name = name;
		this.dir = dir;
		this.date = date;
		this.mountpoint = mountpoint;
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Mountpoint getMountpoint() {
		return mountpoint;
	}

	public void setMountpoint(Mountpoint mountpoint) {
		this.mountpoint = mountpoint;
	}

	public boolean isFile() {
		return file;
	}

	public void setFile(boolean file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return "FileLink [name=" + name + ", dir=" + dir + ", date=" + date
				+ ", mountpoint=" + mountpoint + ", file=" + file + "]";
	}

}
