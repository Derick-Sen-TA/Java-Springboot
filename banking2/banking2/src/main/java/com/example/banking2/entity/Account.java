package com.example.banking2.entity;

public class Account {

	int id;
	String name;
	Double account_balance;
	
	public Account() {
		
	}
	public Account(int id, String name, Double account_balance) {
		super();
		this.id = id;
		this.name = name;
		this.account_balance = account_balance;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getAccount_balance() {
		return account_balance;
	}
	public void setAccount_balance(double account_balance) {
		this.account_balance = account_balance;
	}
	
	
}
