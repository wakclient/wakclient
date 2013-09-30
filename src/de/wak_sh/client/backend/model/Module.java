package de.wak_sh.client.backend.model;

public class Module {
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

}
