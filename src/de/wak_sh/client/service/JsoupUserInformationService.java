package de.wak_sh.client.service;

import java.io.IOException;
import java.util.List;
import java.util.regex.MatchResult;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;

import de.wak_sh.client.backend.Utils;
import de.wak_sh.client.model.UserInformation;
import de.wak_sh.client.service.listeners.UserInformationService;

public class JsoupUserInformationService implements UserInformationService {

	private static JsoupUserInformationService userInformationService;

	private JsoupDataService dataService;

	private JsoupUserInformationService() {
		dataService = JsoupDataService.getInstance();
	}

	public static JsoupUserInformationService getInstance() {
		if (userInformationService == null) {
			userInformationService = new JsoupUserInformationService();
		}
		return userInformationService;
	}

	@Override
	public UserInformation getUserInformation() throws IOException {
		String regex = "<td><h4>Noten.*?<td>(.*?) </td>.*?<td>Studierendennummer: (.*?) </td>";
		List<MatchResult> matchResults = Utils.matchAll(regex, dataService
				.getModulesPage().html());

		regex = "<a.*?>(BA.*?)</a>";

		if (matchResults.size() == 0) {
			Connection conn = dataService.connect(JsoupDataService.BASE_URL
					+ "/432.html", Method.GET, true);
			Response response = dataService.fetchPage(conn);

			regex = "Hallo&nbsp;(.+?)</b>";
			String name = Utils.match(regex, response.body());

			return new UserInformation(name, null, null);
		}

		String name = matchResults.get(0).group(1);
		String studentNumber = matchResults.get(0).group(2);
		String studentGroup = Utils
				.matchAll(regex, dataService.getFileDepotPage().html()).get(0)
				.group(1);

		return new UserInformation(name, studentNumber, studentGroup);
	}

}
