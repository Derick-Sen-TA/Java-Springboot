package com.example.banking.controller;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@SuppressWarnings("deprecation")
public class AccountController {
	
	
	private final ReentrantLock lock = new ReentrantLock();

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @PostMapping("/transfer")
    public void transfer(@RequestBody Map<String, Object> transaction) throws IOException {
    	
    	
    	//System.out.println("Transfer method executed senderId: " + (int) transaction.get("senderId"));
    	

		lock.lock();
	
    	//synchronized(this) {
    	
    	int senderId = (int) transaction.get("senderId");
        int receiverId = (int) transaction.get("receiverId");
        double amount = ((Number) transaction.get("amount")).doubleValue();
        
    	System.out.println("Tranfer method entered " + "senderId: " + senderId + " receiverId: " + receiverId);
    	
    	try {
    	    	
	    	
	        String getUserSql = "SELECT account_balance FROM accounts WHERE id = ?";
	        String updateBalanceSql = "UPDATE accounts SET account_balance = account_balance - ? WHERE id = ?";
        
			Double senderBalance = jdbcTemplate.queryForObject(getUserSql, new Object[]{senderId}, Double.class);
            if (senderBalance == null) {
                throw new Exception("Sender does not exist.");
            }

            Double receiverBalance = jdbcTemplate.queryForObject(getUserSql, new Object[]{receiverId}, Double.class);
            if (receiverBalance == null) {
                throw new Exception("Receiver does not exist.");
            }
            

	    	System.out.println("SenderBalance: " + senderBalance);
	    	System.out.println("ReceiverBalance: " + receiverBalance);

            if (senderBalance < amount) {
                throw new Exception("Sender does not have sufficient funds.");
            }

            jdbcTemplate.update(updateBalanceSql, amount, senderId);
            jdbcTemplate.update(updateBalanceSql, -amount, receiverId);
            
			senderBalance = jdbcTemplate.queryForObject(getUserSql, new Object[]{senderId}, Double.class);
			receiverBalance = jdbcTemplate.queryForObject(getUserSql, new Object[]{receiverId}, Double.class);

		
	    	System.out.println("SenderBalance: " + senderBalance);
	    	System.out.println("ReceiverBalance: " + receiverBalance);



        } 
        catch (Exception e) {
            e.printStackTrace();
        } 
        finally {
        	System.out.println("Tranfer method exited " + "senderId: " + senderId + " receiverId: " + receiverId);
        }
        
    	//}
	
		lock.unlock();
    
    	
    	//System.out.println("Transfer method executed senderId: " + (int) transaction.get("senderId"));

    }

    
}
