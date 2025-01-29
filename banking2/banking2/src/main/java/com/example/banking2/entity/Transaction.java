package com.example.banking2.entity;

public class Transaction {

	int senderId;
	int receiverId;
	Double amount;
	String status;
	String remarks;
	public Transaction() {
		
	}
	public Transaction(int senderId, int receiverId, Double amount, String status, String remarks) {
		super();
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.amount = amount;
		this.status = status;
		this.remarks = remarks;
	}
	public int getSenderId() {
		return senderId;
	}
	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}
	public int getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(int receiverId) {
		this.receiverId = receiverId;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	
	
}
