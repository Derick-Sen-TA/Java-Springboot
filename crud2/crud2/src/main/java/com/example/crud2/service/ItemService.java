package com.example.crud2.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.crud2.entity.Item;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ItemService {

	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
//	public void createJsonItem(Item item) {
//		try(SqlSession sqlSession = sqlSessionFactory.openSession()){
//			Connection connection = sqlSession.getConnection(); 
//			
//			String sql = "INSERT INTO jsonItems VALUES(?::json);";
//			ObjectMapper Obj = new ObjectMapper();
//			
//			PreparedStatement ps = connection.prepareStatement(sql);
//			ps.setString(1, Obj.writeValueAsString(item));
//			ps.executeUpdate();
//			sqlSession.commit();
//			
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	public void createJsonItem(MultipartFile file) {
	    try (SqlSession sqlSession = sqlSessionFactory.openSession();
	         InputStream inputStream = file.getInputStream();
	         InputStreamReader reader = new InputStreamReader(inputStream)) {
	        
	        Connection connection = sqlSession.getConnection();
	        
	        ObjectMapper objectMapper = new ObjectMapper();
	        Item item = objectMapper.readValue(reader, Item.class); 

	        String sql = "INSERT INTO jsonItems VALUES(?::json);";
	        PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, objectMapper.writeValueAsString(item));
            ps.executeUpdate();
            sqlSession.commit();
	   
	    } 
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	}
 

    public void createExcelItems(MultipartFile file) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession();
             InputStream inputStream = file.getInputStream()) {
        	
            Connection connection = sqlSession.getConnection();
            Workbook workbook = new XSSFWorkbook(inputStream);
            
            Sheet sheet = workbook.getSheetAt(0);

            String sql = "INSERT INTO jsonItems VALUES(?::json);";
            ObjectMapper objectMapper = new ObjectMapper();
            PreparedStatement ps = connection.prepareStatement(sql);
            AtomicBoolean headerRow = new AtomicBoolean(true);
            
            sheet.forEach(row -> { 
            	if (headerRow.get()) { 
            		headerRow.set(false); 
            		return; 
            	} 
            	Item item = new Item(); 
            	item.setId((int) row.getCell(0).getNumericCellValue()); 
            	item.setName(row.getCell(1).getStringCellValue()); 
            	item.setPrice((int) row.getCell(2).getNumericCellValue()); 
            	try { 
            		ps.setString(1, objectMapper.writeValueAsString(item)); 
            		ps.addBatch(); 
            	} 
            	catch (Exception e) { 
            		e.printStackTrace(); 
            	} 
            });
            ps.executeBatch();
            sqlSession.commit();
            workbook.close();
        } 
    	catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void createCsvItems(MultipartFile file) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession();
             BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            Connection connection = sqlSession.getConnection();
            String sql = "INSERT INTO jsonItems VALUES(?::json);";
            ObjectMapper objectMapper = new ObjectMapper();
            PreparedStatement ps = connection.prepareStatement(sql);

            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] values = line.split(",");
                
                Item item = new Item();
                item.setId(Integer.parseInt(values[0]));
                item.setName(values[1]); 
                item.setPrice(Integer.parseInt(values[2]));
                
                try {
                    ps.setString(1, objectMapper.writeValueAsString(item));
                    ps.addBatch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ps.executeBatch();
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	
	public void updateItem(Item item) { 
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) { 
			
			Connection connection = sqlSession.getConnection(); 
			
			List<Object> parameters = new ArrayList<>();
			String sql = "UPDATE item SET "; 
					
			if(item.getName() != null) {
				sql += "name = ?, ";
				
				parameters.add(item.getName());
			}
			
			if(item.getPrice() != null) {
				sql += "price = ?, ";
				parameters.add(item.getPrice());
			}
			
			if(sql.endsWith(", ")) {
				sql = sql.substring(0, sql.length()-2);
			}
			
			sql += "WHERE id = ?;";
			parameters.add(item.getId());
			
			
			PreparedStatement ps = connection.prepareStatement(sql); 

			for(int i = 0; i<parameters.size(); i++) {
				ps.setObject(i+1, parameters.get(i));
			}
			
			ps.executeUpdate(); 
			sqlSession.commit(); 
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Item getItemById(Integer id) { 
		Item item = null; 
		try { 
			SqlSession sqlSession = sqlSessionFactory.openSession();
			Connection connection = sqlSession.getConnection(); 
			String sql = "SELECT * FROM item WHERE id = ?"; 
			PreparedStatement ps = connection.prepareStatement(sql); 
			ps.setInt(1, id); 
			ResultSet rs = ps.executeQuery(); 
			if (rs.next()) { 
				item = new Item();
				item.setId(rs.getInt("id")); 
				item.setName(rs.getString("name")); 
				item.setPrice(rs.getInt("price")); 
			} 
			sqlSession.close();
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
		return item;
	}
	
	
}
