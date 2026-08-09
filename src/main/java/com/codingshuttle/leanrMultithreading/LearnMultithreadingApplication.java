package com.codingshuttle.leanrMultithreading;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.*;

@SpringBootApplication
@Slf4j
public class LearnMultithreadingApplication {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		SpringApplication.run(LearnMultithreadingApplication.class, args);


//        learnFuture();

//        learnCompletableFuture();

        learnCF2();

        log.info("After the method call");
    }

    static void learnThread() {
        log.info("Before thread, Name of thread: {}, State: {}", Thread.currentThread().getName(), Thread.currentThread().getState());

        Thread workerThread = new Thread(() -> {
            log.info("Inside the thread Name of thread: {}, State: {}", Thread.currentThread().getName(), Thread.currentThread().getState());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        );
        workerThread.start();

//        workerThread.join(); // block the calling thread

        log.info("After worker thread, State of worker: {}", workerThread.getState());

        log.info("After thread");
    }

    static void learnFuture() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        try {
            Future<String> myNameFuture = executorService.submit(() -> getName());
            myNameFuture.get(); // block the calling thread
            log.info("After nameFuture: {}", Thread.currentThread().getState());
        }finally {
            executorService.shutdown();
        }

    }


    static void learnCompletableFuture() {

        CompletableFuture<String> myNameCF = CompletableFuture
                .supplyAsync(() -> getName())
                .thenApply(name -> name.toUpperCase())
                .thenApply(upperCaseName -> upperCaseName.length())
                .thenApplyAsync(lengthOfName -> {
                    log.info("Inside method with length");
                    if(true) throw new RuntimeException("Faking an error.");
                    return "length was "+lengthOfName;
                })
                .exceptionally((err) -> {
                    return "Default value in case of failure";
                });

        myNameCF.thenAccept(name -> {
            log.info("Got the name length: {}", name);
        });
    }

    static void learnCF2() {
        CompletableFuture<String> nameFuture = CompletableFuture.supplyAsync(() -> getName());
        CompletableFuture<String> addressFuture = CompletableFuture.supplyAsync(() -> getAddress());

        CompletableFuture.allOf(nameFuture, addressFuture)
                        .thenAccept((v) -> {
                            log.info("Got the name: {} and address here: {}", nameFuture.join(), addressFuture.join());
                        });

//        log.info("Got the name: {} and address here: {}", nameFuture.join(), addressFuture.join());
    }

    static String getName() {
        try {
            log.info("Inside nameFuture: {}", Thread.currentThread().getState());
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "MD";
    }

    static String getAddress() {
        try {
            log.info("Inside Addressfuture: {}", Thread.currentThread().getState());
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "New Delhi";
    }


}
