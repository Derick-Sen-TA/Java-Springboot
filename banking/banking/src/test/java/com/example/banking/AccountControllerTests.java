package com.example.banking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.banking.controller.AccountController;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("deprecation")
public class AccountControllerTests {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testTransfer() throws IOException {
        Map<String, Object> transaction = Map.of(
            "senderId", 1,
            "receiverId", 2,
            "amount", 100.0
        );

        when(jdbcTemplate.queryForObject(
            eq("SELECT account_balance FROM accounts WHERE id = ?"),
            any(Object[].class),
            eq(Double.class)))
            .thenReturn(500.0, 400.0); 

        accountController.transfer(transaction);

        verify(jdbcTemplate).update(
            "UPDATE accounts SET account_balance = account_balance - ? WHERE id = ?",
            100.0, 1
        );
        verify(jdbcTemplate).update(
            "UPDATE accounts SET account_balance = account_balance - ? WHERE id = ?",
            -100.0, 2
        );
    }
}

