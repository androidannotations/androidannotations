package com.googlecode.androidannotations.test15.ormlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Car {

	@DatabaseField(generatedId = true)
	private long id;
	
	@DatabaseField
	private String brand;
	
	@DatabaseField
	private String model;
	
	public Car() {

	}

	public Car(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
}
