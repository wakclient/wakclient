package de.wak_sh.client.backend.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
			FileItem item = new FileItem(point[1], point[0].replaceAll("amp;",
					""), "", false);
			items.add(item);
		}

		return items;
	}

	private List<FileItem> fetchItems(String path) throws IOException {
		List<FileItem> items = new ArrayList<FileItem>();
		String regexFolders = "<td><a href=\"(c_dateiablage.html\\?\\&amp;no_cache=1\\&amp;dir=.*?\\&amp;mountpoint=\\d*).*?\">(.*?)</a><br.*?<span class=\"info\">(.*?)</span>";
		String regexFiles = "<td><a href=\"(c_dateiablage.html\\?\\&amp;no_cache=1\\&amp;filename=.*?task=download\\&amp;mountpoint=\\d*).*?\">(.*?)</a><br.*?<span class=\"info\">(.*?)</span>";

		String site = service.fetchPage("/" + path);

		List<String[]> folders = Utils.matchAll(regexFolders, site);
		List<String[]> files = Utils.matchAll(regexFiles, site);

		for (String[] folder : folders) {
			FileItem item = new FileItem(folder[1], folder[0].replaceAll(
					"amp;", ""), folder[2], false);

			items.add(item);
		}

		for (String[] file : files) {
			FileItem item = new FileItem(file[1],
					file[0].replaceAll("amp;", ""), file[2], true);

			String deletePattern = file[0].replace("task=download",
					"task=delete");
			String renamePattern = file[0].replace("task=download",
					"task=rename");

			if (site.contains(deletePattern) || site.contains(renamePattern)) {
				item.setOwner(true);
			}

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

	public void downloadFile(String url, File file) throws IOException {
		String content = service.downloadFile(url);
		OutputStream os = new FileOutputStream(file);
		for (int i = 0; i < content.length(); i++) {
			os.write(content.codePointAt(i));
		}
		os.close();
	}
}
