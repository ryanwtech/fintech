package com.fintech.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Scanner;

@SpringBootApplication
public class WebhookSimulator {

    public static void main(String[] args) {
        SpringApplication.run(WebhookSimulator.class, args);
    }

    @Bean
    public CommandLineRunner webhookSimulator() {
        return args -> {
            Scanner scanner = new Scanner(System.in);
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();

            System.out.println("=== Mock Bank Webhook Simulator ===");
            System.out.println("This tool simulates webhook calls to test transaction processing");
            System.out.println();

            while (true) {
                System.out.println("Options:");
                System.out.println("1. Simulate new transactions webhook");
                System.out.println("2. Simulate income transaction");
                System.out.println("3. Simulate expense transaction");
                System.out.println("4. Simulate multiple transactions");
                System.out.println("5. Exit");
                System.out.print("Choose an option (1-5): ");

                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        simulateNewTransactionsWebhook(restTemplate, objectMapper, scanner);
                        break;
                    case "2":
                        simulateIncomeTransaction(restTemplate, objectMapper, scanner);
                        break;
                    case "3":
                        simulateExpenseTransaction(restTemplate, objectMapper, scanner);
                        break;
                    case "4":
                        simulateMultipleTransactions(restTemplate, objectMapper, scanner);
                        break;
                    case "5":
                        System.out.println("Goodbye!");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
                System.out.println();
            }
        };
    }

    private void simulateNewTransactionsWebhook(RestTemplate restTemplate, ObjectMapper objectMapper, Scanner scanner) {
        System.out.print("Enter account ID (external): ");
        String accountId = scanner.nextLine().trim();

        System.out.print("Enter number of transactions: ");
        int count = Integer.parseInt(scanner.nextLine().trim());

        var payload = createWebhookPayload(accountId, count);
        sendWebhook(restTemplate, objectMapper, payload);
    }

    private void simulateIncomeTransaction(RestTemplate restTemplate, ObjectMapper objectMapper, Scanner scanner) {
        System.out.print("Enter account ID (external): ");
        String accountId = scanner.nextLine().trim();

        System.out.print("Enter amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());

        System.out.print("Enter description: ");
        String description = scanner.nextLine().trim();

        var payload = createIncomeTransactionPayload(accountId, amount, description);
        sendWebhook(restTemplate, objectMapper, payload);
    }

    private void simulateExpenseTransaction(RestTemplate restTemplate, ObjectMapper objectMapper, Scanner scanner) {
        System.out.print("Enter account ID (external): ");
        String accountId = scanner.nextLine().trim();

        System.out.print("Enter amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim()).negate();

        System.out.print("Enter description: ");
        String description = scanner.nextLine().trim();

        System.out.print("Enter merchant: ");
        String merchant = scanner.nextLine().trim();

        var payload = createExpenseTransactionPayload(accountId, amount, description, merchant);
        sendWebhook(restTemplate, objectMapper, payload);
    }

    private void simulateMultipleTransactions(RestTemplate restTemplate, ObjectMapper objectMapper, Scanner scanner) {
        System.out.print("Enter account ID (external): ");
        String accountId = scanner.nextLine().trim();

        var payload = createMultipleTransactionsPayload(accountId);
        sendWebhook(restTemplate, objectMapper, payload);
    }

    private void sendWebhook(RestTemplate restTemplate, ObjectMapper objectMapper, Object payload) {
        try {
            String url = "http://localhost:8080/api/webhooks/test/simulate";
            String response = restTemplate.postForObject(url, payload, String.class);
            System.out.println("Webhook sent successfully!");
            System.out.println("Response: " + response);
        } catch (Exception e) {
            System.err.println("Error sending webhook: " + e.getMessage());
        }
    }

    private Object createWebhookPayload(String accountId, int count) {
        WebhookPayload payload = new WebhookPayload();
        payload.eventType = "transactions.new";
        payload.accountId = accountId;
        payload.transactions = new TransactionData[count];

        for (int i = 0; i < count; i++) {
            TransactionData txData = new TransactionData();
            txData.transactionId = "txn_" + System.currentTimeMillis() + "_" + i;
            txData.amount = new BigDecimal(Math.random() * 200 - 100).setScale(2, BigDecimal.ROUND_HALF_UP);
            txData.description = "Test transaction " + (i + 1);
            txData.merchant = "Test Merchant " + (i + 1);
            txData.postedAt = LocalDateTime.now().minusHours(i);
            txData.currency = "USD";
            txData.category = i % 2 == 0 ? "Food" : "Transport";
            txData.status = "cleared";
            payload.transactions[i] = txData;
        }

        return payload;
    }

    private Object createIncomeTransactionPayload(String accountId, BigDecimal amount, String description) {
        WebhookPayload payload = new WebhookPayload();
        payload.eventType = "transactions.new";
        payload.accountId = accountId;
        
        TransactionData txData = new TransactionData();
        txData.transactionId = "txn_income_" + System.currentTimeMillis();
        txData.amount = amount;
        txData.description = description;
        txData.merchant = "Employer";
        txData.postedAt = LocalDateTime.now();
        txData.currency = "USD";
        txData.category = "Income";
        txData.status = "cleared";
        
        payload.transactions = new TransactionData[]{txData};
        return payload;
    }

    private Object createExpenseTransactionPayload(String accountId, BigDecimal amount, String description, String merchant) {
        WebhookPayload payload = new WebhookPayload();
        payload.eventType = "transactions.new";
        payload.accountId = accountId;
        
        TransactionData txData = new TransactionData();
        txData.transactionId = "txn_expense_" + System.currentTimeMillis();
        txData.amount = amount;
        txData.description = description;
        txData.merchant = merchant;
        txData.postedAt = LocalDateTime.now();
        txData.currency = "USD";
        txData.category = "Food";
        txData.status = "cleared";
        
        payload.transactions = new TransactionData[]{txData};
        return payload;
    }

    private Object createMultipleTransactionsPayload(String accountId) {
        WebhookPayload payload = new WebhookPayload();
        payload.eventType = "transactions.new";
        payload.accountId = accountId;
        
        TransactionData[] transactions = new TransactionData[3];
        
        TransactionData salary = new TransactionData();
        salary.transactionId = "txn_salary_" + System.currentTimeMillis();
        salary.amount = new BigDecimal("3000.00");
        salary.description = "Monthly salary";
        salary.merchant = "Employer Corp";
        salary.postedAt = LocalDateTime.now().minusDays(1);
        salary.currency = "USD";
        salary.category = "Income";
        salary.status = "cleared";
        transactions[0] = salary;
        
        TransactionData groceries = new TransactionData();
        groceries.transactionId = "txn_groceries_" + System.currentTimeMillis();
        groceries.amount = new BigDecimal("-85.50");
        groceries.description = "Grocery shopping";
        groceries.merchant = "SuperMart";
        groceries.postedAt = LocalDateTime.now().minusHours(6);
        groceries.currency = "USD";
        groceries.category = "Food";
        groceries.status = "cleared";
        transactions[1] = groceries;
        
        TransactionData gas = new TransactionData();
        gas.transactionId = "txn_gas_" + System.currentTimeMillis();
        gas.amount = new BigDecimal("-45.00");
        gas.description = "Gas station";
        gas.merchant = "Shell";
        gas.postedAt = LocalDateTime.now().minusHours(2);
        gas.currency = "USD";
        gas.category = "Transport";
        gas.status = "cleared";
        transactions[2] = gas;
        
        payload.transactions = transactions;
        return payload;
    }

    // Helper classes for webhook payload
    static class WebhookPayload {
        public String eventType;
        public String accountId;
        public TransactionData[] transactions;
    }

    static class TransactionData {
        public String transactionId;
        public BigDecimal amount;
        public String description;
        public String merchant;
        public LocalDateTime postedAt;
        public String currency;
        public String category;
        public String status;
    }
}
