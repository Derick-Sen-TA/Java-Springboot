package com.example.bankingexecute;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class BankingexecuteApplication implements CommandLineRunner{
	
	
	public static void main(String[] args) {
		SpringApplication.run(BankingexecuteApplication.class, args);
	}
	
	@Override
    public void run(String... args) {
        RestTemplate restTemplate = new RestTemplate();
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        JSONArray transactions = new JSONArray();
        //transactions.put(new JSONObject().put("senderId", 1).put("receiverId", 2).put("amount", 100.0));
        transactions.put(new JSONObject().put("senderId", 2).put("receiverId", 3).put("amount", 2.0));
        transactions.put(new JSONObject().put("senderId", 3).put("receiverId", 4).put("amount", 3.0));
        transactions.put(new JSONObject().put("senderId", 4).put("receiverId", 5).put("amount", 4.0));
        transactions.put(new JSONObject().put("senderId", 5).put("receiverId", 6).put("amount", 5.0));
        transactions.put(new JSONObject().put("senderId", 6).put("receiverId", 7).put("amount", 6.0));
        
        transactions.put(new JSONObject().put("senderId", 5).put("receiverId", 6).put("amount", 5.0));
        transactions.put(new JSONObject().put("senderId", 5).put("receiverId", 6).put("amount", 5.0));
        

        transactions.toList().parallelStream().forEach(transaction -> {
        	
        	//System.out.println("Hello Transaction Info: " + transaction.toString());
        	
            executorService.submit(() -> {
         
                try {
                	
                    JSONObject jsonObject = new JSONObject((java.util.Map<?, ?>) transaction);

                    String apiUrl = "http://localhost:8080/api/transfer"; 

                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Content-Type", "application/json");
                    HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);

                    restTemplate.postForEntity(apiUrl, request, String.class);
                    
                    

                } 
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {

                }

            });
            
            //System.out.println("Bye Transaction Info: " + transaction.toString());
        });

        executorService.shutdown();
    }

}
