package de.wak_sh.client.backend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.wak_sh.client.Utils;
import de.wak_sh.client.backend.model.FileItem;

public class FileService {

	private static FileService instance;

	private DataService service;
	private List<FileItem> mountpoints;

	public static FileService getInstance() {
		if (instance == null) {
			instance = new FileService();
		}
		return instance;
	}

	public FileService() {
		service = DataService.getInstance();
	}

	private List<FileItem> fetchMountpoints() throws IOException {
		List<FileItem> items = new ArrayList<FileItem>();
		String regex = "<a href=\"(c_dateiablage.html\\?\\&amp;no_cache=1\\&amp;mountpoint=\\d*)\".*?>(.*?)</a>";

		List<String[]> points = Utils.matchAll(regex,
				service.getFileDepotPage());

		for (String[] point : points) {
			FileItem item = new FileItem();
			item.path = point[0].replaceAll("amp;", "");
			item.name = point[1];
			items.add(item);
		}

		return items;
	}

	private List<FileItem> fetchItems(String path) throws IOException {
		List<FileItem> items = new ArrayList<FileItem>();
		String regex = "<td><a href=\"(c_dateiablage.html\\?\\&amp;no_cache=1\\&amp;.*?mountpoint=\\d*)\".*?>(.*?)</a>";

		List<String[]> points = Utils.matchAll(regex,
				service.fetchPage("/" + path));

		for (String[] point : points) {
			FileItem item = new FileItem();
			item.path = point[0].replaceAll("amp;", "");
			item.name = point[1];
			// TODO: Fix date parsing -> add <br><span
			// class=\"info\">(.*?)</span> to regex
			// item.date = point[2];
			items.add(item);
		}

		return items;
	}

	public List<FileItem> getMountpoints() throws IOException {
		if (mountpoints == null) {
			mountpoints = fetchMountpoints();
		}
		return mountpoints;
	}

	public List<FileItem> getFileItems(String path) throws IOException {
		List<FileItem> items = new ArrayList<FileItem>();
		items.addAll(fetchItems(path));
		return items;
	}

}
