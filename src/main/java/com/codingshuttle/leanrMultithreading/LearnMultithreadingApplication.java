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


//        Here Main thread is block until not get the result from future
//        learnFuture();

//        Here out main thread is free after calling method learCompletableFuture() there is no blocking.
        learnCompletableFuture();

//        learnCF2();

//        for future -> it run after method complete bcz main thread is block
//        for CompletableFuture -> it run instantly bcz main thread is not block
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
                .thenApplyAsync(lengthOfName -> {  // this will work on separate thread. but  currently previous thread was free then it can use same thread.
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
        CompletableFuture<String> nameFuture = CompletableFuture.supplyAsync(() -> getName()); // this take 5s
        CompletableFuture<String> addressFuture = CompletableFuture.supplyAsync(() -> getAddress()); // this take 2s
//        overall it will take 5s bcz both future are running parallel with different thread so max time it will wait

        CompletableFuture.allOf(nameFuture, addressFuture) // combine both future name and address
                        .thenAccept((v) -> {
                            log.info("Got the name: {} and address here: {}", nameFuture.join(), addressFuture.join()); // we use join bcz get() throw exception and we need to handle
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
        return "md";
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
