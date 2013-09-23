package de.wak_sh.client.backend;

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
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.net.http.AndroidHttpClient;
import de.wak_sh.client.Utils;
import de.wak_sh.client.backend.model.UserInformation;

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

	private boolean loggedIn;
	private String sessionId;

	private UserInformation userInformation;

	private DataService() {
		loggedIn = false;

		// TODO: add Context parameter for SSL cache
		client = AndroidHttpClient.newInstance("WakClient");
		cookieStore = new BasicCookieStore();
		httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}

	public boolean login(String email, String password) throws IOException {
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
		fetchUserInfo(response);
		loggedIn = !(response.contains("Anmeldefehler") || response
				.contains("gesperrt"));
		return loggedIn;
	}

	private void fetchUserInfo(String response) {
		// TODO: fetch user info
		String benutzername = "Max Mustermann";
		String studiengang = "Winformatik";
		String studiengruppe = "BA123WINF7";
		String matrikelnummer = "12345";
		userInformation = new UserInformation(benutzername, studiengang,
				studiengruppe, matrikelnummer);
	}

	public void logout() throws IOException {
		get("/431.html");
	}

	public UserInformation getUserInformation() {
		return userInformation;
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
}
