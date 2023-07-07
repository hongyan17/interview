package com.interview.payment.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CurrencyPaymentTracker {
    private ConcurrentHashMap<String, Double> netAmounts;
    private ConcurrentHashMap<String, Boolean> isUpdated ;

    public CurrencyPaymentTracker() {

        netAmounts = new ConcurrentHashMap<>();
        isUpdated = new ConcurrentHashMap<>();
    }

    public void addPayment(String currencyCode, Double amount) {
        if(amount!=0) isUpdated.put(currencyCode, true);
        netAmounts.put(currencyCode, netAmounts.getOrDefault(currencyCode, 0.0) + amount);
    }
    public void loadPaymentFromFile(String filePath){
        File file = new File(filePath);
        try {
            BufferedReader br= new BufferedReader(new FileReader(file));
            String record;
            while ((record = br.readLine()) != null){
                String[] parts = record.trim().split("\\s+");
                if(parts[0].length()!=3){
                    System.out.println("Invalid currency code");
                }
                if (parts.length == 2) {
                    String currencyCode = parts[0].toUpperCase();
                    try {
                        double amount = Double.parseDouble(parts[1]);
                        addPayment(parts[0], amount);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid amount. Please enter a valid number.");
                    }
                } else {
                    System.out.println("Invalid input format. Please enter currency code and amount separated by a space.");
                }
            }
        } catch (Exception e) {
            System.out.println("File "+ filePath+" does not exist or there is an error when read the file");
        }
    }
    public Map<String, Double> getNetAmounts() {
        return netAmounts;
    }
    public Double getNetAmountByCurrency(String currencyCode) {

        return netAmounts.get(currencyCode);
    }
    public boolean isUpdated(String currencyCode,boolean isReset){
        if(isUpdated.get(currencyCode)!=null && isUpdated.get(currencyCode)){
            if(isReset) isUpdated.put(currencyCode, false);
            return true;
        } else {
            return false;
        }
    }
}
