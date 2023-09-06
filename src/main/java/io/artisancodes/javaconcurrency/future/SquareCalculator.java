package io.artisancodes.javaconcurrency.future;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class SquareCalculator {

    private final ExecutorService executorService;

    public SquareCalculator(ExecutorService executorService) {
        this.executorService = executorService;
    }

    Future<Integer> calculate(Integer input) {
        return executorService.submit(() -> {
            Thread.sleep(1000);
           return input * input;
        });
    }
}
