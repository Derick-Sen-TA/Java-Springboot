package com.example.banking;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.banking.controller.AccountController;

@SpringBootApplication
public class BankingApplication  {

//    @Autowired
//    private AccountController accountController;

    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }

//    @Override
//    public void run(String... args) throws Exception {
//        ExecutorService executorService = Executors.newFixedThreadPool(4); 
//        
//        //IntStream.of(6).parallel().forEach();
//        
//        JSONArray transactions = new JSONArray();
//        transactions.put(new JSONObject().put("senderId", 1).put("receiverId", 2).put("amount", 100.0));
//        transactions.put(new JSONObject().put("senderId", 2).put("receiverId", 3).put("amount", 200.0));
//        transactions.put(new JSONObject().put("senderId", 3).put("receiverId", 4).put("amount", 300.0));
//        transactions.put(new JSONObject().put("senderId", 4).put("receiverId", 5).put("amount", 400.0));
//        transactions.put(new JSONObject().put("senderId", 5).put("receiverId", 6).put("amount", 500.0));
//        transactions.put(new JSONObject().put("senderId", 6).put("receiverId", 7).put("amount", 600.0));
//
//        transactions.toList().parallelStream().forEach(transaction -> {
//            JSONObject jsonObject = new JSONObject((java.util.Map<?, ?>) transaction);
//            int senderId = jsonObject.getInt("senderId");
//            int receiverId = jsonObject.getInt("receiverId");
//            double amount = jsonObject.getDouble("amount");
//
//            executorService.submit(() -> accountController.transfer(senderId, receiverId, amount));
//        });
//
//        executorService.shutdown();
//    }
}

