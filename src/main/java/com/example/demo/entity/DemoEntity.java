package com.example.demo.entity;

public class DemoEntity {

	private int age;
	private String name;
	private String city;
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String name) {
		this.city = city;
	}
	public DemoEntity(int age, String name,String city) {
		super();
		this.age = age;
		this.name = name;
		this.city=city;
	}
	
}
