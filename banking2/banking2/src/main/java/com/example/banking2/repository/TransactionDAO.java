package com.example.banking2.repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("deprecation")
public class TransactionDAO {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
    public int initialUpdate(int senderId, int receiverId, Double amount, String status) {
    	
    	String remarks = "-";
    	
    	if(status.equals("Failure")) {
    		remarks = "Error Connecting to Database";
    	}
    	
        String sql = "INSERT INTO transactions (senderId, receiverId, amount, status, remarks) " +
                     "VALUES (?, ?, ?, ?, ?) RETURNING transactionId;";
        
        Integer generatedId = jdbcTemplate.queryForObject(sql, new Object[]{senderId, receiverId, amount, status, remarks}, Integer.class);
        
        return generatedId;
    }
    

	
	public void finalUpdate(int transactionId, int senderId, int receiverId, Double amount, String status, String remarks) {
	    String sql = "UPDATE transactions SET senderId = ?, receiverId = ?, amount = ?, status = ?, remarks = ? " +
	                 "WHERE transactionId = ?;";

	    jdbcTemplate.update(sql, senderId, receiverId, amount, status, remarks, transactionId);
	}
	
	public double getAmount(int senderId) {
		
		String sql = "SELECT account_balance FROM accounts WHERE id = ?";
		
		return jdbcTemplate.queryForObject(sql, new Object[]{senderId}, Double.class);
		
	}
	
	public void transfer(int senderId, int receiverId, Double amount) {
		
		String lockSenderSql = "SELECT account_balance FROM accounts WHERE id = ? FOR UPDATE";
	    String lockReceiverSql = "SELECT account_balance FROM accounts WHERE id = ? FOR UPDATE";
	    
	    jdbcTemplate.queryForObject(lockSenderSql, new Object[]{senderId}, Double.class);
	    jdbcTemplate.queryForObject(lockReceiverSql, new Object[]{receiverId}, Double.class);
		
		String sql = "UPDATE accounts SET account_balance = account_balance - ? WHERE id = ?";
		
		jdbcTemplate.update(sql, amount, senderId);
        jdbcTemplate.update(sql, -amount, receiverId);
		
	}
	
	public List<Map<String, Object>> getFailed(){
		String sql = "SELECT * FROM transactions WHERE status = 'Failure' "
				+ "AND remarks = 'Error Connecting to Database' ORDER BY transactionId ASC LIMIT 10";
		return jdbcTemplate.queryForList(sql);
	}
	
	public List<Map<String, Object>> getOldest5PendingTransactions(){
		String sql = "SELECT * FROM transactions WHERE status = 'Pending' ORDER BY transactionId ASC LIMIT 5";
		return jdbcTemplate.queryForList(sql);
	}
	
	public Map<String, Object> getCustomerById(int id) {
	    String sql = "SELECT * FROM accounts WHERE id = ?";
	    return jdbcTemplate.queryForMap(sql, id);
	}
	
	public List<Map<String, Object>> getLast10Transactions() {
	    String sql = "SELECT * FROM transactions ORDER BY transactionId DESC LIMIT 10";
	    return jdbcTemplate.queryForList(sql);
	}
	
	

}
