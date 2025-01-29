package com.example.banking2.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.banking2.repository.TransactionDAO;

@Service
public class TransactionServices {
	
	@Autowired
    private TransactionDAO dao;
	
	public void transfer(Map<String, Object> transaction) {
		
		int senderId = (int) transaction.get("senderId");
        int receiverId = (int) transaction.get("receiverId");
        double amount = ((Number) transaction.get("amount")).doubleValue();
        String status = (String) transaction.get("status");
        
        System.out.println("Service entered, senderId: " + senderId + " receiverId: " + receiverId);
        
        int transactionId = dao.initialUpdate(senderId, receiverId, amount, status);
        
        if(senderId == receiverId) {
        	dao.finalUpdate(transactionId, senderId, receiverId, amount, "Failure", "Cannot transfer to the same account.");
        	return;
        }
        
        double senderAmount = dao.getAmount(senderId);
        
        if(senderAmount < amount) {
        	dao.finalUpdate(transactionId, senderId, receiverId, amount, "Failure", "Sender does not have enough funds");
        	return;
        }
        
        dao.transfer(senderId, receiverId, amount);
        
        dao.finalUpdate(transactionId, senderId, receiverId, amount, "Completed", "Success");
        
        System.out.println("Service exited, senderId: " + senderId + " receiverId: " + receiverId);

        
	}

	public void insertTransaction(Map<String, Object> transaction) {
		
		int senderId = (int) transaction.get("senderId");
        int receiverId = (int) transaction.get("receiverId");
        double amount = ((Number) transaction.get("amount")).doubleValue();
        String status = (String) transaction.get("status");
                

        dao.initialUpdate(senderId, receiverId, amount, status);

        
	}
	
	public List<Map<String, Object>> getPending(){
		return dao.getOldest5PendingTransactions();
	}

	public void processTransaction(Map<String, Object> transaction) {
				
		int senderId = (int) transaction.get("senderid");
        int receiverId = (int) transaction.get("receiverid");
        double amount = ((Number) transaction.get("amount")).doubleValue();
		int transactionId = (int) transaction.get("transactionid");
		
        double senderAmount = dao.getAmount(senderId);
        
        if(senderId == receiverId) {
        	dao.finalUpdate(transactionId, senderId, receiverId, amount, "Failure", "Cannot transfer to the same account.");
        	return;
        }
                
        if(senderAmount < amount) {
        	dao.finalUpdate(transactionId, senderId, receiverId, amount, "Failure", "Sender does not have enough funds");
        	return;
        }
        
        dao.transfer(senderId, receiverId, amount);
        
        dao.finalUpdate(transactionId, senderId, receiverId, amount, "Completed", "Success");
                
	}
	
	public List<Map<String, Object>> getFailed(){
		return dao.getFailed();
	}
	
	public Map<String, Object> getCustomerById(int id) {
	    return dao.getCustomerById(id);
	}

	public List<Map<String, Object>> getLast10Transactions() {
	    return dao.getLast10Transactions();
	}
	
}
