package com.example.crud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.crud.entity.Item;

import jakarta.transaction.Transactional;


@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

@Query(nativeQuery = true, value = "SELECT * FROM item")
List<Item> findAll1();

@Transactional
@Modifying
@Query(nativeQuery = true, value = "DELETE FROM item WHERE id = ?1")
void delete_test_function(Integer id);
	
}
