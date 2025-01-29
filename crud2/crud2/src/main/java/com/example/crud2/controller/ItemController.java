package com.example.crud2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.crud2.entity.Item;
import com.example.crud2.repository.ItemRepository;
import com.example.crud2.service.ItemService;

@RestController
@RequestMapping("/api/items")
public class ItemController { 
	
	@Autowired
	private ItemRepository itemRepository;
	
	@Autowired
	private ItemService itemService;
	
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestBody MultipartFile file) {

        try {
        	
        	String content = file.getContentType();
        	
        	if(content.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
        		itemService.createExcelItems(file);
        	}
        	else if(content.equals("text/csv")) {
        		itemService.createCsvItems(file);
        	}
        	else if(content.equals("application/json")){
        		itemService.createJsonItem(file);
        	}
        	else {
        		return ResponseEntity.badRequest().body("Invalid file type or missing file");
        	}
        	
            return ResponseEntity.ok("File processed successfully");
        } 
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file");
        }
    }
    
	
//	@PostMapping("/json")
//	public Item insertJsonItem(@RequestBody(required = false) Item item) {
//		itemService.createJsonItem(item);
//		return item;
//	}
	
	
	@PostMapping
	public Item createItem(@RequestBody Item item) {
		itemRepository.insert(item);
		return item;
	}
	
	@PostMapping("/bulk")
	public List<Item> createItemsBulk(@RequestBody List<Item> items) { 
		for (Item item : items) { 
			itemRepository.insert(item); 
		} 
		return items;
	}

	
	@GetMapping
	public List<Item> getAllItems(){
		return itemRepository.findAll();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Integer id){
		Item item = itemRepository.findById(id); 
		//Item item = itemService.getItemById(id);
		if (item != null) { 
			return ResponseEntity.ok(item); 
		} 
		return ResponseEntity.notFound().build();	
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Item> updateItem(@PathVariable Integer id, @RequestBody Item itemDetails){
		Item existingItem = itemRepository.findById(id);
		
		if(existingItem != null) {
			
//			if(itemDetails.getName() != null)
//				existingItem.setName(itemDetails.getName());
//			
//			if(itemDetails.getPrice() != null)
//				existingItem.setPrice(itemDetails.getPrice());
			
			//itemRepository.update(existingItem);
			itemDetails.setId(id);
			itemService.updateItem(itemDetails);
			
			return ResponseEntity.ok(existingItem);
		}
		return ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteItem(@PathVariable Integer id){
		
		Item item = itemRepository.findById(id);
		
		if(item != null) {
			itemRepository.delete(id);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}
	
	
}

