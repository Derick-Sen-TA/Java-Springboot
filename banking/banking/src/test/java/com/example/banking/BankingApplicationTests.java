package com.example.banking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class BankingApplicationTests {
	
	@Autowired
    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

	private final RestTemplate restTemplate = new RestTemplate();

    @Test
    void testTransferThreadSafetyWithLogs() throws InterruptedException, JSONException {
    	
          	
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        
        JSONArray transactions = new JSONArray();
        transactions.put(new JSONObject().put("senderId", 1).put("receiverId", 2).put("amount", 1.0));
        transactions.put(new JSONObject().put("senderId", 2).put("receiverId", 3).put("amount", 2.0));
        transactions.put(new JSONObject().put("senderId", 3).put("receiverId", 4).put("amount", 3.0));
        transactions.put(new JSONObject().put("senderId", 4).put("receiverId", 5).put("amount", 4.0));
        transactions.put(new JSONObject().put("senderId", 5).put("receiverId", 6).put("amount", 5.0));
        transactions.put(new JSONObject().put("senderId", 6).put("receiverId", 7).put("amount", 6.0));
        
        transactions.put(new JSONObject().put("senderId", 5).put("receiverId", 6).put("amount", 5.0));
        transactions.put(new JSONObject().put("senderId", 5).put("receiverId", 6).put("amount", 5.0));
        transactions.put(new JSONObject().put("senderId", 5).put("receiverId", 6).put("amount", 5.0));
        transactions.put(new JSONObject().put("senderId", 5).put("receiverId", 6).put("amount", 5.0));
        transactions.put(new JSONObject().put("senderId", 5).put("receiverId", 6).put("amount", 5.0));
        transactions.put(new JSONObject().put("senderId", 5).put("receiverId", 6).put("amount", 5.0));
                
        
        List<String> transactionsList = new ArrayList<>();
        for (int i = 0; i < transactions.length(); i++) {
            transactionsList.add(transactions.getJSONObject(i).toString());
        }
        
        Map<Integer, Double> initialBalances = new HashMap<>();
        for (int i = 0; i < transactions.length(); i++) {
        	JSONObject transaction = transactions.getJSONObject(i);
            int senderId = transaction.getInt("senderId");
            int receiverId = transaction.getInt("receiverId");

            initialBalances.putIfAbsent(senderId, jdbcTemplate.queryForObject(
                    "SELECT account_balance FROM accounts WHERE id = ?", Double.class, senderId));
            initialBalances.putIfAbsent(receiverId, jdbcTemplate.queryForObject(
                    "SELECT account_balance FROM accounts WHERE id = ?", Double.class, receiverId));
        }
        
        //System.out.println(initialBalances);
        
        Map<Integer, Double> expectedBalances = new HashMap<>(initialBalances);
        for (int i = 0; i < transactions.length(); i++) {
            JSONObject transaction = transactions.getJSONObject(i);
            int senderId = transaction.getInt("senderId");
            int receiverId = transaction.getInt("receiverId");
            double amount = transaction.getDouble("amount");

            expectedBalances.put(senderId, expectedBalances.get(senderId) - amount);
            expectedBalances.put(receiverId, expectedBalances.get(receiverId) + amount);
        }
        
        //System.out.println(expectedBalances);
        
        transactionsList.parallelStream().forEach(transaction -> {
        	
            executorService.submit(() -> {

                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Content-Type", "application/json");
                    HttpEntity<String> request = new HttpEntity<>(transaction, headers);
                    
                    String apiUrl = "http://localhost:8080/api/transfer"; 

                    restTemplate.postForEntity(apiUrl, request, String.class);
                    
                } 
                catch (Exception e) {
                	
                	e.printStackTrace();
                }
            });
        });

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        
        Map<Integer, Double> finalBalances = new HashMap<>();
        for (Integer accountId : expectedBalances.keySet()) {
            finalBalances.put(accountId, jdbcTemplate.queryForObject(
                    "SELECT account_balance FROM accounts WHERE id = ?", Double.class, accountId));
        }
        
        //System.out.println(finalBalances);

        expectedBalances.entrySet().stream()
        .forEach(entry -> {
            int accountId = entry.getKey();
            double expectedBalance = entry.getValue();
            double actualBalance = finalBalances.get(accountId);
            assertEquals(expectedBalance, actualBalance);
        });
        
        

        
   
        
    }
}

