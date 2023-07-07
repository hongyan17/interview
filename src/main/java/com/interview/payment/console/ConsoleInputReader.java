package com.interview.payment.console;

import com.interview.payment.service.CurrencyPaymentTracker;

import java.util.Scanner;

public class ConsoleInputReader implements Runnable {
    private CurrencyPaymentTracker paymentTracker;
    private boolean running;

    public ConsoleInputReader(CurrencyPaymentTracker paymentTracker) {
        this.paymentTracker = paymentTracker;
        this.running = true;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (running) {
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("quit")) {
                running = false;
                break;
            }

            String[] parts = input.split(" ");
            if (parts.length == 2) {
                String currencyCode = parts[0];
                double amount = Double.parseDouble(parts[1]);
                paymentTracker.addPayment(currencyCode, amount);
            }
        }

        scanner.close();
    }
}
