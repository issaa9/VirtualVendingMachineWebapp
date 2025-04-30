package com.example.finalyearproject.service;

import com.example.finalyearproject.model.Product;
import com.example.finalyearproject.model.Transaction;
import com.example.finalyearproject.model.TransactionProduct;
import com.example.finalyearproject.repository.ProductRepo;
import com.example.finalyearproject.repository.TransactionProductRepo;
import com.example.finalyearproject.repository.TransactionRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepo transactionRepo;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private TransactionProductRepo transactionProductRepo;


    @InjectMocks
    private TransactionService transactionService;

    @Test //test for creating a transaction entry
    void testCreateTransaction_validInput_createsTransaction() {
        Map<String, Integer> cart = new HashMap<>(); //create a cart
        cart.put("A1", 2);

        Product p = new Product(); //create the product in the cart
        p.setId("A1");
        p.setPrice(1.5);
        p.setStock(5);
        when(productRepo.findAllById(Set.of("A1"))).thenReturn(List.of(p));
        when(productRepo.findById("A1")).thenReturn(Optional.of(p));
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction tx = invocation.getArgument(0);
            tx.setId(1L); //fake Transaction ID to prevent it being null
            return tx;
        });

        Transaction result = transactionService.createTransaction(cart, 5.0, "user");

        assertNotNull(result);
        assertEquals(3.0, result.getTotalCost());
        assertEquals(5.0, result.getPaymentReceived());
        assertEquals("user", result.getUser());
        assertEquals(1, result.getTransactionProducts().size());
    }

    @Test //test for the rounding helper method (required for all money values)
    void testRoundTwoDP_roundsCorrectly() {
        //expect all numbers to be correctly rounded AND 2 decimal places
        assertEquals(2.67, transactionService.roundTwoDP(2.666));
        assertEquals(5.99, transactionService.roundTwoDP(5.994));
        assertEquals(10.00, transactionService.roundTwoDP(10.0));
    }
}
