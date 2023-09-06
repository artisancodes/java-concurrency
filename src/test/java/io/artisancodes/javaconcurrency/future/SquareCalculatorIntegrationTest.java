package io.artisancodes.javaconcurrency.future;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertTrue;

public class SquareCalculatorIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(SquareCalculatorIntegrationTest.class);

    private long start;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        start = System.currentTimeMillis();
        logger.info(String.format("test started: %s", testInfo.getDisplayName()));
    }
    @AfterEach
    void tearDown(TestInfo testInfo) {
        logger.info(String.format("Test %s took %s ms \n", testInfo.getDisplayName(),
                System.currentTimeMillis() - start));
    }

    private SquareCalculator squareCalculator;

    @Test
    public void givenExecutorIsSingleThreaded_whenExecutionAreTriggered_thenRunInSequence()
            throws InterruptedException, ExecutionException {

        // We are using just one thread
        squareCalculator = new SquareCalculator(Executors.newSingleThreadExecutor());

        // The second task only starts once the first task is complete, making the whole process take around 2 seconds.
        // to finish.
        Future<Integer> result1 = squareCalculator.calculate(5);
        Future<Integer> result2 = squareCalculator.calculate(1000);

        while (!result1.isDone() || !result2.isDone()) {
            logger.info(String.format("Task 1 is %s and Task 2 is %s.",
                    result1.isDone() ? "done" : "not done",
                    result2.isDone() ? "done" : "not done"));

            Thread.sleep(300);
        }

        assertEquals(25, result1.get().intValue());
        assertEquals(1000000, result2.get().intValue());
    }


    @Test
    public void whenGetWithTimeoutLowerThanExecutionTime_thenThrowException() {
        squareCalculator = new SquareCalculator(Executors.newSingleThreadExecutor());

        Future<Integer> result = squareCalculator.calculate(5);

        // Throw a TimeoutException if the task doesn't return before the specified timeout period.
        assertThrows(TimeoutException.class, () -> result.get(500, TimeUnit.MILLISECONDS));
    }

    @Test
    public void givenExecutorIsMultiThreaded_whenTwoExecutionsAreTriggered_thenRunInParallel()
            throws InterruptedException, ExecutionException {

        // Using more than one thread makes our program multithreaded.
        // Now, we have an executor which is able to use 2 simultaneous threads.
        // We can see 2 tasks start and finish running simultaneously, and the whole process takes
        // around 1 second to complete.
        squareCalculator = new SquareCalculator(Executors.newFixedThreadPool(2));

        Future<Integer> result1 = squareCalculator.calculate(5);
        Future<Integer> result2 = squareCalculator.calculate(1000);

        while (!result1.isDone() || !result2.isDone()) {
            logger.info(String.format("Task 1 is %s and Task 2 is %s.",
                    result1.isDone() ? "done" : "not done",
                    result2.isDone() ? "done" : "not done"));

            Thread.sleep(300);
        }

        assertEquals(25, result1.get().intValue());
        assertEquals(1000000, result2.get().intValue());
    }

    @Test
    public void whenCancelFutureAndCallGet_thenThrowException() {
        squareCalculator = new SquareCalculator(Executors.newSingleThreadExecutor());

        Future<Integer> result = squareCalculator.calculate(4);

        // The instance of Future will never complete its operation.
        boolean canceled = result.cancel(true);

        assertTrue("Future was canceled", canceled);

        // .isCancelled() method tell us if a Future was already cancelled.
        assertTrue("Future was canceled", result.isCancelled());

        assertThrows(CancellationException.class, result::get);
    }
}
