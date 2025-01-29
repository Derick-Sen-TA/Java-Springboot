package com.example.banking2.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.banking2.service.TransactionServices;

@RestController
@RequestMapping("/api")
public class TransactionController {

	private final ReentrantLock lock = new ReentrantLock();
	
	@Autowired
    private TransactionServices services;
	
	@PostMapping("/transfer")
	public void transfer(@RequestBody Map<String, Object> transaction) {
		
		System.out.println("Transfer method entered: " + transaction);
		
		lock.lock();
		
		try {
			services.transfer(transaction);
		}
		finally {
			lock.unlock();
		}
		
		System.out.println("Transfer method exited: " + transaction);
	}
	
	@PostMapping("/insert")
	public void transfer1(@RequestBody Map<String, Object> transaction) {
				
		lock.lock();
		
		try {
			services.insertTransaction(transaction);
		}
		finally {
			lock.unlock();
		}
		
	}
	
	@GetMapping("/getPending")
	public List<Map<String, Object>> transfer2(){
		return services.getPending();
	}
	
	@PostMapping("/process")
	public void transfer3(@RequestBody Map<String, Object> transaction) {
				
		lock.lock();
		
		try {
			services.processTransaction(transaction);
		}
		finally {
			lock.unlock();
		}
		
	}
	
	@GetMapping("/getFailed")
	public List<Map<String, Object>> getFailed(){
		return services.getFailed();
	}
	
	
	
	@GetMapping("/{id}")
	public Map<String, Object> getCustomerById(@PathVariable Integer id) {
	    return services.getCustomerById(id);
	}
	
	@GetMapping("/recent")
	public List<Map<String, Object>> getLast10Transactions() {
	    return services.getLast10Transactions();
	}
	
}
