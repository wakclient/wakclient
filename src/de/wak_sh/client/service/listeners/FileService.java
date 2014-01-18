package de.wak_sh.client.service.listeners;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import de.wak_sh.client.model.FileLink;
import de.wak_sh.client.model.Mountpoint;

public interface FileService {

	public List<FileLink> getFileLinks(FileLink fileLink) throws IOException;

	public List<Mountpoint> getMountpoints() throws IOException;

	public boolean rename(FileLink fileLink, String name) throws IOException;

	public boolean delete(FileLink fileLink) throws IOException;

	public InputStream getFileStream(FileLink fileLink) throws IOException;

	public InputStream getFileStream(String url) throws IOException;

	public OutputStream getFileStream(FileLink fileLink, File file)
			throws IOException;

}
