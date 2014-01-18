package de.wak_sh.client.model;

import java.util.Arrays;

public class Module {
	private int semester;
	private String name;
	private String type;
	private int credits;
	private float[] grades;

	public Module(int semester, String name, String type, int credits,
			float[] grades) {
		super();
		this.semester = semester;
		this.name = name;
		this.type = type;
		this.credits = credits;
		this.grades = grades;
	}

	public int getSemester() {
		return semester;
	}

	public void setSemester(int semester) {
		this.semester = semester;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getCredits() {
		return credits;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}

	public float[] getGrades() {
		return grades;
	}

	public void setGrades(float[] grades) {
		this.grades = grades;
	}

	public float getRelevantGrade() {
		if (grades[2] != 0f) {
			return grades[2];
		} else if (grades[1] != 0f) {
			return grades[1];
		} else {
			return grades[0];
		}
	}

	@Override
	public String toString() {
		return "Module [semester=" + semester + ", name=" + name + ", type="
				+ type + ", credtis=" + credits + ", grades="
				+ Arrays.toString(grades) + "]";
	}
}
