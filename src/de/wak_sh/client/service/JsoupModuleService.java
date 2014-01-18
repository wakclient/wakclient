package de.wak_sh.client.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.select.Elements;

import de.wak_sh.client.model.Module;
import de.wak_sh.client.service.listeners.ModuleService;

public class JsoupModuleService implements ModuleService {

	private static JsoupModuleService moduleService;

	private JsoupDataService dataService;

	private JsoupModuleService() {
		dataService = JsoupDataService.getInstance();
	}

	public static JsoupModuleService getInstance() {
		if (moduleService == null) {
			moduleService = new JsoupModuleService();
		}
		return moduleService;
	}

	@Override
	public List<Module> getModules() throws IOException {
		List<Module> modules = new ArrayList<Module>();

		Elements moduleElements = dataService.getModulesPage()
				.getElementsByClass("klein");
		for (int i = 1; i < moduleElements.size() - 3; i++) {
			Elements moduleData = moduleElements.get(i).getAllElements();

			int semester = Integer.parseInt(moduleData.get(1).text());
			String name = moduleData.get(2).text();
			String type = moduleData.get(3).text();
			int credits = Integer.parseInt(moduleData.get(4).text());
			float grade1 = moduleData.get(5).text().contains("-") ? 0 : Float
					.parseFloat(moduleData.get(5).text().replaceAll(",", "."));
			float grade2 = moduleData.get(6).text().contains("-") ? 0 : Float
					.parseFloat(moduleData.get(6).text().replaceAll(",", "."));
			float grade3 = moduleData.get(7).text().contains("-") ? 0 : Float
					.parseFloat(moduleData.get(7).text().replaceAll(",", "."));

			float[] grades = new float[] { grade1, grade2, grade3 };

			modules.add(new Module(semester, name, type, credits, grades));
		}

		return modules;
	}

}
