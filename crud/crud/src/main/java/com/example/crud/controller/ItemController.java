package com.example.crud.controller;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.crud.entity.Item;
import com.example.crud.repository.ItemRepository;

@RestController
@RequestMapping("/api/items")
public class ItemController { 
	
	@Autowired
	private ItemRepository itemRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	//private final ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	@PostMapping
	public Item createItem(@RequestBody Item item) {
		String sql = "INSERT INTO item (name, price) VALUES (?, ?)"; 
		jdbcTemplate.update(sql, item.getName(), item.getPrice());
		return item;
	}
	
	@PostMapping("/bulk") 
	public ResponseEntity<String> createItemsBulk(@RequestBody List<Item> items) { 
		
		Thread thread = new Thread(() -> items.stream().forEach(item -> itemRepository.save(item)));
		
		thread.start();
		
		//items.parallelStream().forEach(item -> itemRepository.save(item));
		
		return ResponseEntity.ok("Your items are being added");
	}
	
	@GetMapping
	public List<Item> getAllItems(){
		return itemRepository.findAll1();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Integer id){
		return itemRepository.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Item> updateItem(@PathVariable Integer id, @RequestBody Item itemDetails){
		Optional<Item> optionalItem = itemRepository.findById(id);
		
		if(optionalItem.isPresent()) {
			Item existingItem = optionalItem.get();
			existingItem.setName(itemDetails.getName());
			existingItem.setPrice(itemDetails.getPrice());
			
			Item updatedItem = itemRepository.save(existingItem);
			
			return ResponseEntity.ok(updatedItem);
		} else {
			return ResponseEntity.notFound().build();
		}
		
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteItem(@PathVariable Integer id){
		String sql = "DELETE FROM item WHERE id = ?";
		int count_deleted = jdbcTemplate.update(sql, id);
		if(count_deleted > 0) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
		
	}
	
	
	
	
}
