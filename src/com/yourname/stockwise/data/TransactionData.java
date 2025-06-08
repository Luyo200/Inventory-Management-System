package com.yourname.stockwise.data;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.yourname.stockwise.model.Product;
import com.yourname.stockwise.model.Transaction;
import com.yourname.stockwise.model.TransactionType;

public class TransactionData {
	private static final List<Transaction> transactions = new ArrayList<>();

    static {
        
     // Sample transactions
        List<Product> products = InventoryData.getAllProducts();
        if (!products.isEmpty()) {
            transactions.add(new Transaction(
                "T001",
                products.get(0),
                TransactionType.SALE,
                2,
                LocalDateTime.now().minusDays(1).withHour(10).withMinute(30)
            ));
            transactions.add(new Transaction(
                "T002",
                products.get(1),
                TransactionType.RESTOCK,
                5, 
                LocalDateTime.now().withHour(14).withMinute(45)
            ));
            transactions.add(new Transaction(
                "T003",
                products.get(1),
                TransactionType.RESTOCK,
                7,
                LocalDateTime.now().plusDays(2).withHour(9).withMinute(15)
            ));
        }
    }

    public static List<Transaction> getAllTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public static void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }
   
    public static void clearTransactions() {
        transactions.clear();
    }

	public static void setTransactons(List<Transaction> loadedTransaction) {
		// TODO Auto-generated method stub
		transactions.clear();
    	transactions.addAll(loadedTransaction);
	}
}