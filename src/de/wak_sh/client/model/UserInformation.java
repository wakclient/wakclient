package de.wak_sh.client.model;

public class UserInformation {
	private String name;
	private String studentNumber;
	private String studentGroup;

	public UserInformation(String name, String studentNumber,
			String studentGroup) {
		super();
		this.name = name;
		this.studentNumber = studentNumber;
		this.studentGroup = studentGroup;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStudentNumber() {
		return studentNumber;
	}

	public void setStudentNumber(String studentNumber) {
		this.studentNumber = studentNumber;
	}

	public String getStudentGroup() {
		return studentGroup;
	}

	public void setStudentGroup(String studentGroup) {
		this.studentGroup = studentGroup;
	}

	@Override
	public String toString() {
		return "UserInformation [name=" + name + ", studentNumber="
				+ studentNumber + ", studentGroup=" + studentGroup + "]";
	}
}
