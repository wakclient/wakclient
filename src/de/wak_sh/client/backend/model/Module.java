package de.wak_sh.client.backend.model;

import java.io.Serializable;

public class Module implements Serializable {
	private static final long serialVersionUID = -5400042093685033589L;

	private int semester;
	private String name;
	private int credits;
	private float[] grades;

	public Module(int semester, String name, int credits, float[] grades) {
		this.semester = semester;
		this.name = name;
		this.credits = credits;
		this.grades = grades;
	}

	public int getSemester() {
		return semester;
	}

	public String getName() {
		return name;
	}

	public int getCredits() {
		return credits;
	}

	public float[] getGrades() {
		return grades;
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

}
