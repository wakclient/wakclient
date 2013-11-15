package de.wak_sh.client.backend.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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

	public void deleteFile(FileItem item) throws IOException {
		String path = URLDecoder.decode(item.getPath(), "UTF-8");

		int indexOfDir = path.indexOf("dir=") + 4;
		String dir = path.substring(indexOfDir, path.indexOf("&", indexOfDir));

		List<NameValuePair> data = new ArrayList<NameValuePair>();
		data.add(new BasicNameValuePair("dir", dir));
		data.add(new BasicNameValuePair("mountpoint", "718"));
		data.add(new BasicNameValuePair("task", "delete"));
		data.add(new BasicNameValuePair("confirmed", "yes"));
		data.add(new BasicNameValuePair("filename", item.getName()));

		service.postPage("/c_dateiablage.html?no_cache=1", data);
	}

	public void renameFile(FileItem item, String newName) throws IOException {
		String path = URLDecoder.decode(item.getPath(), "UTF-8");

		int indexOfDir = path.indexOf("dir=") + 4;
		String dir = path.substring(indexOfDir, path.indexOf("&", indexOfDir));

		List<NameValuePair> data = new ArrayList<NameValuePair>();
		data.add(new BasicNameValuePair("dir", dir));
		data.add(new BasicNameValuePair("mountpoint", "718"));
		data.add(new BasicNameValuePair("task", "rename"));
		data.add(new BasicNameValuePair("oldname", item.getName()));
		data.add(new BasicNameValuePair("newname", newName));

		service.postPage("/c_dateiablage.html?no_cache=1", data);
	}
}
