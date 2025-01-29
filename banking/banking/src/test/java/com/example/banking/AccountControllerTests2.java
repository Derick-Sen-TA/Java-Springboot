package com.example.banking;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
	    "spring.datasource.url=jdbc:h2:mem:testdb",
	    "spring.datasource.driverClassName=org.h2.Driver",
	    "spring.datasource.username=sa",
	    "spring.datasource.password=password",
	    "spring.datasource.platform=h2"
	})
public class AccountControllerTests2 {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testTransferWithH2Database() throws Exception {
    	
        jdbcTemplate.execute("CREATE TABLE accounts (id INT PRIMARY KEY, account_balance DOUBLE PRECISION)");
        jdbcTemplate.execute("INSERT INTO accounts (id, account_balance) VALUES (1, 500.0), (2, 400.0)");

        String transactionJson = "{ \"senderId\": 1, \"receiverId\": 2, \"amount\": 100.0 }";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/transfer")
                .contentType("application/json")
                .content(transactionJson))
                .andExpect(status().isOk()); 

        Double senderBalance = jdbcTemplate.queryForObject(
            "SELECT account_balance FROM accounts WHERE id = 1", Double.class);
        Double receiverBalance = jdbcTemplate.queryForObject(
            "SELECT account_balance FROM accounts WHERE id = 2", Double.class);
       

        System.out.println("After Transfer - Sender: " + senderBalance + " Receiver: " + receiverBalance);

        assertEquals(400.0, senderBalance);
        assertEquals(500.0, receiverBalance);
    }
}
