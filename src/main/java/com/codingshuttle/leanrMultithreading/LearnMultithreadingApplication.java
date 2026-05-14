package com.codingshuttle.leanrMultithreading;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.Future;

@SpringBootApplication
@Slf4j
public class LearnMultithreadingApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(LearnMultithreadingApplication.class, args);

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
}
