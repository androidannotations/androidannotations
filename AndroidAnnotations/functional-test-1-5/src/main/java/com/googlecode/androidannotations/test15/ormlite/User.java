package com.googlecode.androidannotations.test15.ormlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(daoClass = UserDaoImpl.class)
public class User {

	@DatabaseField(generatedId = true)
	private long id;
	
	@DatabaseField
	private String firstName;
	
	@DatabaseField
	private String lastName;
	
	public User() {

	}

	public User(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
