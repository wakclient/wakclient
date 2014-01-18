package de.wak_sh.client.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.wak_sh.client.backend.Utils;
import de.wak_sh.client.model.FileLink;
import de.wak_sh.client.model.Mountpoint;
import de.wak_sh.client.service.listeners.FileService;

public class JsoupFileService implements FileService {

	private static JsoupFileService fileService;

	private JsoupDataService dataService;

	private JsoupFileService() {
		dataService = JsoupDataService.getInstance();
	}

	public static JsoupFileService getInstance() {
		if (fileService == null) {
			fileService = new JsoupFileService();
		}
		return fileService;
	}

	@Override
	public boolean rename(FileLink fileLink, String name) throws IOException {
		return false;
	}

	@Override
	public boolean delete(FileLink fileLink) throws IOException {
		return false;
	}

	@Override
	public OutputStream getFileStream(FileLink fileLink, File file)
			throws IOException {
		String url = JsoupDataService.BASE_URL + "/c_dateiablage.html?"
				+ "&dir=" + fileLink.getDir() + "&mountpoint="
				+ fileLink.getMountpoint().getId();

		return dataService.prepareFileUpload(url);
	}

	@Override
	public InputStream getFileStream(FileLink fileLink) throws IOException {
		String url = JsoupDataService.BASE_URL + "/c_dateiablage.html?"
				+ "&dir=" + fileLink.getDir() + "&filename="
				+ fileLink.getName() + "&task=download" + "&mountpoint="
				+ fileLink.getMountpoint().getId();

		return dataService.prepareFileDownload(url);
	}

	@Override
	public InputStream getFileStream(String url) throws IOException {
		return dataService.prepareFileDownload(url);
	}

	@Override
	public List<FileLink> getFileLinks(FileLink fileLink) throws IOException {
		List<FileLink> fileLinks = new ArrayList<FileLink>();

		String url = JsoupDataService.BASE_URL + "/c_dateiablage.html?"
				+ "&dir=" + fileLink.getDir() + "&mountpoint="
				+ fileLink.getMountpoint().getId();

		Connection conn = dataService.connect(url, Connection.Method.GET, true);

		Document doc = dataService.fetchPage(conn).parse();

		Elements fileLinkElements = doc
				.select("[colspan=4] + td .filelink, .info:matches(\\d{1,2}.\\d{2}.\\d{4}|-leer-)");
		for (int i = 0; i < fileLinkElements.size(); i += 2) {
			Element element = fileLinkElements.get(i);
			String dir = Utils.match("dir=(.*?)&", element.attr("href"));
			String name = element.text();
			String date = fileLinkElements.get(i + 1).text();
			Mountpoint mountpoint = fileLink.getMountpoint();
			boolean file = element.attr("href").contains("filename=");

			fileLinks.add(new FileLink(name, dir, date, mountpoint, file));
		}

		return fileLinks;
	}

	@Override
	public List<Mountpoint> getMountpoints() throws IOException {
		List<Mountpoint> mountpoints = new ArrayList<Mountpoint>();

		Document doc = dataService.getFileDepotPage();

		Elements mountpointElements = doc.select(".mounttable a");
		for (Element e : mountpointElements) {
			int id = Integer.parseInt(Utils
					.matchAll("mountpoint=(\\d*)", e.attr("href")).get(0)
					.group(1));
			String name = e.text();

			mountpoints.add(new Mountpoint(id, name));
		}

		return mountpoints;
	}

}
