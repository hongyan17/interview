package com.interview.payment;

import com.interview.payment.console.ConsoleDisplay;
import com.interview.payment.console.ConsoleInputReader;
import com.interview.payment.service.CurrencyPaymentTracker;
import io.muserver.Method;
import io.muserver.MuServer;
import io.muserver.MuServerBuilder;
import io.muserver.SsePublisher;

public class PaymentApplication {
    public static void main(String[] args) {
        CurrencyPaymentTracker paymentTracker = new CurrencyPaymentTracker();
        ConsoleInputReader inputReader = new ConsoleInputReader(paymentTracker);
        ConsoleDisplay consoleDisplay = new ConsoleDisplay(paymentTracker);
        if(args.length>0){
              paymentTracker.loadPaymentFromFile(args[0]);
        }
        // Start the console input reader in a separate thread
        Thread inputReaderThread = new Thread(inputReader);
        inputReaderThread.start();

        // Start the periodic display
        consoleDisplay.startDisplay();

        // Set up the web server
        MuServer server = MuServerBuilder.httpServer()
                .withHttpPort(8181)
                .addHandler(Method.GET, "/payment/{currencyCode}",
                        (request, response, pathParams) -> {
                            String currencyCode = pathParams.get("currencyCode");
                            Double amount = paymentTracker.getNetAmountByCurrency(currencyCode);
                            if(amount!=null){
                                response.write("amount is " + amount);
                            }else{
                                response.write("No record found for currency");
                            }

                        })
                .addHandler(Method.GET, "/sse/{currencyCode}",(request, response, pathParams) -> {
                    String currencyCode = pathParams.get("currencyCode");
                    SsePublisher publisher = SsePublisher.start(request, response);
                    new Thread(() -> update(publisher, paymentTracker, currencyCode)).start();  })
                .start();
    }
    public static void update(SsePublisher publisher, CurrencyPaymentTracker paymentTracker, String currencyCode) {

        for (int i = 0; i < 100; i++) {
            try {
                boolean isUpdated = paymentTracker.isUpdated(currencyCode, true);
                while(!isUpdated){
                    Thread.sleep(1000);
                    isUpdated = paymentTracker.isUpdated(currencyCode, true);
                }
                Double amount = paymentTracker.getNetAmountByCurrency(currencyCode);
                publisher.send("Amount after update " + amount);

            } catch (Exception e) {
                break;
            }
        }
        publisher.close();
    }

}
