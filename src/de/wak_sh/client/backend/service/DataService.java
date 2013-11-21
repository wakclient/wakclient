package de.wak_sh.client.backend.service;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.net.http.AndroidHttpClient;
import de.wak_sh.client.Utils;

/*
 * Parts of this file are based on the work of Patrick Gotthard:
 * http://www.patrick-gotthard.de/4659/wakclient
 */
@SuppressWarnings("deprecation")
public class DataService {
	private static final String BASE_URL = "https://www.wak-sh.de";

	private static DataService instance;

	public static DataService getInstance() {
		if (instance == null) {
			instance = new DataService();
		}
		return instance;
	}

	private HttpClient client;
	private CookieStore cookieStore;
	private HttpContext httpContext;

	private String email;
	private String password;

	private boolean loggedIn;
	private String sessionId;

	private String overviewPage;
	private String newsPage;
	private String fileDepotPage;
	private String gradesPage;
	private String recipientPage;

	private DataService() {
		loggedIn = false;

		// TODO: add Context parameter for SSL cache
		client = AndroidHttpClient.newInstance("WakClient");
		cookieStore = new BasicCookieStore();
		httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}

	public boolean login(String email, String password) throws IOException {
		this.email = email;
		this.password = password;
		return doLogin();
	}

	private boolean doLogin() throws IOException {
		String challenge;
		{
			String response = get("/30.html");
			String regex = "<input type=\"hidden\" name=\"challenge\" value=\"(.*?)\">";
			challenge = Utils.match(regex, response);
		}

		String passphrase = md5(String.format("%s:%s:%s", email, md5(password),
				challenge));

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("user", email));
		parameters.add(new BasicNameValuePair("pass", passphrase));
		parameters.add(new BasicNameValuePair("submit", "Anmelden"));
		parameters.add(new BasicNameValuePair("logintype", "login"));
		parameters.add(new BasicNameValuePair("pid", "3"));
		parameters.add(new BasicNameValuePair("redirect_url", ""));
		parameters.add(new BasicNameValuePair("challenge", challenge));
		String response = post("/community-login.html", parameters);
		loggedIn = !(response.contains("Anmeldefehler") || response
				.contains("gesperrt"));
		return loggedIn;
	}

	public void logout() throws IOException {
		get("/431.html");
	}

	private String execute(HttpUriRequest request) throws IOException {
		HttpResponse response;
		response = client.execute(request, httpContext);

		for (Cookie c : cookieStore.getCookies()) {
			if (c.getName().equals("fe_typo_user")
					&& !c.getValue().equals(sessionId)) {
				sessionId = c.getValue();
				// session id changed = login or logout
				loggedIn = false;
			}
		}
		return EntityUtils.toString(response.getEntity());
	}

	private String get(String path) throws IOException {
		return execute(new HttpGet(BASE_URL + path));
	}

	private String post(String path, List<NameValuePair> parameters)
			throws IOException {
		HttpPost request = new HttpPost(BASE_URL + path);
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters);
		request.setEntity(entity);
		return execute(request);
	}

	private String post(String path, FileEntity entity) throws IOException {
		HttpPost request = new HttpPost(BASE_URL + path);
		request.setEntity(entity);
		return execute(request);
	}

	private String md5(String text) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		digest.update(text.getBytes());
		StringBuilder sb = new StringBuilder();
		for (byte b : digest.digest()) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public String fetchPage(String url) throws IOException {
		String page = get(url);
		while (page.contains("Benutzeranmeldung")) {
			doLogin();
			page = get(url);
		}

		return page;
	}

	public String postPage(String url, List<NameValuePair> data)
			throws IOException {
		String page = post(url, data);
		while (page.contains("Benutzeranmeldung")) {
			doLogin();
			page = post(url, data);
		}
		return page;
	}

	public String getOverviewPage() throws IOException {
		if (overviewPage == null) {
			overviewPage = fetchPage("/c_uebersicht.html");
		}
		return overviewPage;
	}

	public String getMessagesPage() throws IOException {
		if (newsPage == null) {
			newsPage = fetchPage("/c_email.html");
		}
		return newsPage;
	}

	public String getRecipientPage() throws IOException {
		if (recipientPage == null) {
			recipientPage = fetchPage("/89.html");
		}
		return recipientPage;
	}

	public String getFileDepotPage() throws IOException {
		if (fileDepotPage == null) {
			fileDepotPage = fetchPage("/c_dateiablage.html");
		}
		return fileDepotPage;
	}

	public String getGradesPage() throws IOException {
		if (gradesPage == null) {
			gradesPage = fetchGradesPage();
		}
		return gradesPage;
	}

	public void uploadFile(String url, File file) throws IOException {
		HttpPost post = new HttpPost(BASE_URL + url);
		MultipartEntity entity = new MultipartEntity();

		FileBody fileBody = new FileBody(file);
		entity.addPart("upload_0", fileBody);
		entity.addPart("uploadfile", new StringBody("Hochladen"));

		post.setEntity(entity);

		execute(post);
	}

	private String fetchGradesPage() throws IOException {
		String notenAnmeldung = fetchPage("/notenabfrage_bc.html");
		String id = Utils.match(
				"<input type=\"hidden\" name=\"id\" value=\"(.*?)\">",
				notenAnmeldung);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("Passwort", password));
		return post("/index.php", params);
	}

}
