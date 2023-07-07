package com.interview.payment.console;

import com.interview.payment.service.CurrencyPaymentTracker;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
public class ConsoleDisplay {
    private CurrencyPaymentTracker paymentTracker;
    public ConsoleDisplay(CurrencyPaymentTracker paymentTracker) {
        this.paymentTracker = paymentTracker;
    }
    public void startDisplay(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println();
                System.out.println("Current payment records:");
                for(Map.Entry<String, Double> entry : paymentTracker.getNetAmounts().entrySet()){
                    if(entry.getValue()!=0){
                        System.out.println(entry.getKey()+" "+entry.getValue());
                    }
                }
            }
        }, 0, 1000 * 60);
    }

}
