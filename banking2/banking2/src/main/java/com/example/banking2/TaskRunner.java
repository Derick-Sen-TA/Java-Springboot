package com.example.banking2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@EnableScheduling
@Component
public class TaskRunner {
	
	RestTemplate restTemplate = new RestTemplate();
    ExecutorService executorService = Executors.newFixedThreadPool(4);

	
	@Scheduled(fixedDelay = 5000)
	public void TransactionInsert() {
		
		JSONArray transactions = new JSONArray();
		

		int x = 1 + (int)(Math.random() * 10);
		int y = 1 + (int)(Math.random() * 10);
		
		for(int i=0; i<10; i++) {
			int randomId1 = 1 + (int)(Math.random() * 10);
			int randomId2 = 1 + (int)(Math.random() * 10);
			
			int randomAmount = 10 + (int)(Math.random() * 91);
			
			if(i == x || i == y) {
				transactions.put(new JSONObject().put("senderId", randomId1)
		        		.put("receiverId", randomId2).put("amount", randomAmount).put("status", "Failure"));
				
			}
			else {
				transactions.put(new JSONObject().put("senderId", randomId1)
	        		.put("receiverId", randomId2).put("amount", randomAmount).put("status", "Pending"));
			}
	        
		}
		
		
        List<String> transactionsList = new ArrayList<>();
        for (int i = 0; i < transactions.length(); i++) {
            transactionsList.add(transactions.getJSONObject(i).toString());
        }
		
        transactionsList.parallelStream().forEach(transaction -> {
        	
            executorService.submit(() -> {

                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Content-Type", "application/json");
                    HttpEntity<String> request = new HttpEntity<>(transaction, headers);
                    
                    String apiUrl = "http://localhost:8080/api/insert"; 

                    restTemplate.postForEntity(apiUrl, request, String.class);
                    
                } 
                catch (Exception e) {
                	
                	e.printStackTrace();
                }
            });
        });
		
	}
	
	@SuppressWarnings("unchecked")
	@Scheduled(fixedDelay = 30000)
	public void ProcessPending() {
		
		String url = "http://localhost:8080/api/getPending";
		
		List<Map<String, Object>> pendingTransactionsList = restTemplate.getForObject(url, List.class);
		
		if(pendingTransactionsList != null) {
		
			pendingTransactionsList.parallelStream().forEach(transaction -> {
				
	            executorService.submit(() -> {
	
	                try {
	                	
	                	ObjectMapper objectMapper = new ObjectMapper();
	                    String transactionJson = objectMapper.writeValueAsString(transaction);
	                	
	                    HttpHeaders headers = new HttpHeaders();
	                    headers.set("Content-Type", "application/json");
	                    HttpEntity<String> request = new HttpEntity<>(transactionJson, headers);
	                    
	                    String apiUrl = "http://localhost:8080/api/process"; 
	
	                    restTemplate.postForEntity(apiUrl, request, String.class);
	                    
	                } 
	                catch (Exception e) {
	                	
	                	e.printStackTrace();
	                }
	            });
	
			});
		}
	}
	
	@SuppressWarnings("unchecked")
	@Scheduled(fixedDelay = 60000)
	public void ProcessFailure() {
		
		
		String url = "http://localhost:8080/api/getFailed";
		
		List<Map<String, Object>> failedTransactionsList = restTemplate.getForObject(url, List.class);
		
		if(failedTransactionsList != null) {
			
			failedTransactionsList.parallelStream().forEach(transaction -> {
				
	            executorService.submit(() -> {
	
	                try {
	                	
	                	ObjectMapper objectMapper = new ObjectMapper();
	                    String transactionJson = objectMapper.writeValueAsString(transaction);
	                	
	                    HttpHeaders headers = new HttpHeaders();
	                    headers.set("Content-Type", "application/json");
	                    HttpEntity<String> request = new HttpEntity<>(transactionJson, headers);
	                    
	                    String apiUrl = "http://localhost:8080/api/process"; 
	
	                    restTemplate.postForEntity(apiUrl, request, String.class);
	                    
	                } 
	                catch (Exception e) {
	                	
	                	e.printStackTrace();
	                }
	            });
	
			});
		}
	}
	
}
