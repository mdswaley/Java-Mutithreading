package com.codingshuttle.leanrMultithreading;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RestController
@Slf4j
public class DummyController {

    @GetMapping("/hello")
    public ResponseEntity<String> getName() throws InterruptedException {

// if you get 1000 req per sec then this method is choked and not able to handle all of that request.
        log.info("Thread is blocked");

        Thread.sleep(5000);

        return ResponseEntity.ok("MD");
//        By default, tomcat server use 200 thread which will run concurrently
    }

    @GetMapping("/hello-cf")
    public CompletableFuture<ResponseEntity<String>> getNameCF() throws InterruptedException {

//      Here bcz of CompletableFuture request is continue bcz once thread is free it will be able to handle other request.
        log.info("Thread is called");

        return CompletableFuture.supplyAsync(() -> {
            log.info("inside the future call");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return ResponseEntity.ok("MD");
        }, Executors.newFixedThreadPool(4));
    }
}
