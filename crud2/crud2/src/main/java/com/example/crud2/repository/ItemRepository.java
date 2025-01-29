package com.example.crud2.repository;

import java.util.List;

import com.example.crud2.entity.Item;

public interface ItemRepository {
	List<Item> findAll();
	
	Item findById(Integer id);
	
	void insert(Item user);
	void update(Item user);
	void delete(Integer id);
}
