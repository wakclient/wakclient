package de.wak_sh.client.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.wak_sh.client.backend.Utils;
import de.wak_sh.client.model.LoginResult;

public final class JsoupDataService {

	private static JsoupDataService dataService;

	public static final String BASE_URL = "https://www.wak-sh.de";
	public static final String COOKIE_NAME = "fe_typo_user";

	private String cookie;
	private String username;
	private String password;

	private boolean loggedIn;

	private JsoupDataService() {
	}

	public static JsoupDataService getInstance() {
		if (dataService == null) {
			dataService = new JsoupDataService();
		}
		return dataService;
	}

	public LoginResult login(String username, String password)
			throws IOException {
		this.username = username;
		this.password = password;

		Connection conn = connect(BASE_URL + "/30.html", Connection.Method.GET,
				false);

		Document doc = conn.execute().parse();

		String challenge = doc.getElementsByAttributeValue("name", "challenge")
				.val();
		String passphrase = Utils.getMd5(String.format("%s:%s:%s", username,
				Utils.getMd5(password), challenge));

		Map<String, String> map = new HashMap<String, String>();
		map.put("user", username);
		map.put("pass", passphrase);
		map.put("submit", "Anmelden");
		map.put("logintype", "login");
		map.put("pid", "3");
		map.put("redirect_url", "");
		map.put("challenge", challenge);

		conn = connect(BASE_URL + "/community-login.html",
				Connection.Method.GET, false);

		doc = conn.data(map).execute().parse();

		if (doc.toString().contains("Anmeldefehler")) {
			loggedIn = false;
			return new LoginResult("Benutzername oder Passwort falsch");
		} else if (doc.toString().contains("gesperrt")) {
			loggedIn = false;
			return new LoginResult("Account wurde gesperrt");
		}

		cookie = conn.response().cookies().get(COOKIE_NAME);
		loggedIn = true;

		return new LoginResult();
	}

	public void logout() throws IOException {
		Jsoup.connect(BASE_URL + "/431.hmtl").timeout(0).get();
	}

	public Connection connect(String url, Connection.Method method,
			boolean cookie) {
		Connection conn = Jsoup.connect(url);
		conn.method(method);
		conn.timeout(0);

		if (cookie) {
			conn.cookie(COOKIE_NAME, this.cookie);
		}

		return conn;
	}

	public Response fetchPage(Connection conn) throws IOException {
		Response response = conn.execute();
		while ((response.body().contains("Benutzeranmeldung"))) {
			login(username, password);
			conn.cookie(COOKIE_NAME, cookie);
			response = conn.execute();
		}
		return response;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public OutputStream prepareFileUpload(String url) throws IOException {
		fetchPage(connect(BASE_URL + "/432.html", Connection.Method.GET, true));

		HttpURLConnection conn = (HttpURLConnection) new URL(url)
				.openConnection();
		conn.addRequestProperty("Connection", "Keep-Alive");
		conn.addRequestProperty("Content-Type", "multipart/form-data");
		conn.addRequestProperty("Cookie", COOKIE_NAME + "=" + cookie);
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setChunkedStreamingMode(1024);
		conn.connect();

		return conn.getOutputStream();
	}

	public InputStream prepareFileDownload(String url) throws IOException {
		fetchPage(connect(BASE_URL + "/432.html", Connection.Method.GET, true));

		HttpURLConnection conn = (HttpURLConnection) new URL(url)
				.openConnection();
		conn.addRequestProperty("Connection", "Keep-Alive");
		conn.addRequestProperty("Cookie", COOKIE_NAME + "=" + cookie);
		conn.setRequestMethod("GET");
		conn.setChunkedStreamingMode(1024);
		conn.setDoOutput(true);

		return conn.getInputStream();
	}

	public Document getFileDepotPage() throws IOException {
		Connection conn = connect(BASE_URL + "/c_dateiablage.html",
				Connection.Method.GET, true);
		return fetchPage(conn).parse();
	}

	public Element getModulesPage() throws IOException {
		Connection conn = connect(BASE_URL + "/notenabfrage_bc.html",
				Connection.Method.GET, true);

		Response response = fetchPage(conn);

		String html = response.body();
		String regex = "<input type=\"hidden\" name=\"id\" value=\"(.*?)\">";
		String id = Utils.match(regex, html);

		Map<String, String> data = new HashMap<String, String>();
		data.put("id", id);
		data.put("Passwort", password);

		conn.url(BASE_URL + "/index.php");
		conn.method(Connection.Method.POST);
		conn.data(data);

		return conn.post();
	}

}
