package de.wak_sh.client.model;

public class DrawerItem {

	private int imageRessource;
	private String name;

	public DrawerItem(int imageRessource, String name) {
		super();
		this.imageRessource = imageRessource;
		this.name = name;
	}

	public int getImageRessource() {
		return imageRessource;
	}

	public void setImageRessource(int imageRessource) {
		this.imageRessource = imageRessource;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
