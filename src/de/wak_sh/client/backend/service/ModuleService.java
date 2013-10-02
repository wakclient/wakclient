package de.wak_sh.client.backend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.wak_sh.client.Utils;
import de.wak_sh.client.backend.model.Module;

/*
 * Parts of this file are based on the work of Patrick Gotthard:
 * http://www.patrick-gotthard.de/4659/wakclient
 */
public class ModuleService {
	private DataService dataService;
	private List<Module> modules;

	public ModuleService(DataService dataService) {
		this.dataService = dataService;
		modules = new ArrayList<Module>();
	}

	public void fetchModules() throws IOException {
		String page = dataService.getGradesPage();
		String regex = "<tr .*?>.*?<td .*?>(.?)</td>.*?<td>(.*?)</td>.*?<td .*?>.*?</td>.*?<td .*?>(.*?)</td>.*?<td .*?>(.*?)</td>.*?<td .*?>(.*?)</td>.*?<td .*?>(.*?)</td>.*?</tr>";
		List<String[]> matches = Utils.matchAll(regex, page);

		for (String[] module : matches) {
			int semester = Integer.parseInt(module[0]);
			String name = module[1];
			int credits = Integer.parseInt(module[2]);
			float[] grades = new float[3];
			grades[0] = Float.parseFloat(module[3].replace(",", "."));
			if (module[4].equals("-")) {
				grades[1] = 0f;
				grades[2] = 0f;
			} else {
				grades[1] = Float.parseFloat(module[4].replace(",", "."));
				if (module[5].equals("-")) {
					grades[2] = 0f;
				} else {
					grades[2] = Float.parseFloat(module[5].replace(",", "."));
				}
			}
			modules.add(new Module(semester, name, credits, grades));
		}
	}

	public int countSemesters() {
		int semester = 0;
		int count = 0;
		for (Module module : modules) {
			if (module.getSemester() != semester) {
				count++;
				semester = module.getSemester();
			}
		}
		return count;
	}

	public ArrayList<Module> getGrades(int semester) {
		ArrayList<Module> grades = new ArrayList<Module>();
		for (Module module : modules) {
			if (module.getSemester() == semester) {
				grades.add(module);
			}
		}
		return grades;
	}

	public int getCredits(int semester) {
		int credits = 0;
		for (Module module : modules) {
			if (semester == 0 || semester == module.getSemester()) {
				credits += module.getCredits();
			}
		}
		return credits;
	}

	public float getAverageGrade(int semester) {
		float average = 0f;
		for (Module module : modules) {
			if (semester == 0 || semester == module.getSemester()) {
				average += module.getCredits() * module.getRelevantGrade();
			}
		}
		average /= getCredits(semester);
		return average;
	}
}
