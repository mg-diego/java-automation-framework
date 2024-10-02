package Helpers;

import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public final class RetryPolicies {

    private static final int DEFAULT_DELAY_IN_SECONDS = 1;
    private static final int DEFAULT_NUMBER_OF_RETRIES = 5;

    private RetryPolicies() {
        throw new IllegalStateException("Utility class");
    }

    public static void executeActionWithRetries(Runnable action) {
        executeActionWithRetries(action, DEFAULT_NUMBER_OF_RETRIES, DEFAULT_DELAY_IN_SECONDS);
    }

    public static void executeActionWithRetries(Runnable action, int numberOfRetries) {
        executeActionWithRetries(action, numberOfRetries, DEFAULT_DELAY_IN_SECONDS);
    }

    public static void executeActionWithRetries(Runnable action, int numberOfRetries, int delayInSeconds) {
        RetryPolicy<Object> retryPolicy = RetryPolicy.builder()
                .handle(Throwable.class)
                .withDelay(Duration.ofSeconds(delayInSeconds))
                .withMaxRetries(numberOfRetries)
                .build();

        Failsafe.with(retryPolicy).run(action::run);
    }

    public static void executeActionWithRetries(Runnable action, Runnable actionIfCatch) {
        executeActionWithRetries(action, actionIfCatch, DEFAULT_NUMBER_OF_RETRIES, DEFAULT_DELAY_IN_SECONDS);
    }

    public static void executeActionWithRetries(Runnable action, Runnable actionIfCatch, int numberOfRetries) {
        executeActionWithRetries(action, actionIfCatch, numberOfRetries, DEFAULT_DELAY_IN_SECONDS);
    }

    public static void executeActionWithRetries(Runnable action, Runnable actionIfCatch, int numberOfRetries, int delayInSeconds) {
        RetryPolicy<Object> retryPolicy = RetryPolicy.builder()
                .handle(Throwable.class)
                .onRetry(e -> actionIfCatch.run())
                .withDelay(Duration.ofSeconds(delayInSeconds))
                .withMaxRetries(numberOfRetries)
                .build();

        Failsafe.with(retryPolicy).run(action::run);
    }

    public static void executeActionWithTimeout(Runnable action, int timeoutInMinutes) throws Throwable {
        executeActionWithTimeout(action, TimeUnit.MINUTES.toMillis(timeoutInMinutes));
    }

    public static void executeActionWithTimeout(Runnable action, long timeoutInMilliseconds) throws Throwable {
        var succeeded = false;
        var lastException = new Throwable();
        var startingTime = Instant.now().toEpochMilli();
        var currentTime = Instant.now().toEpochMilli();

        while (!succeeded && currentTime <= startingTime + timeoutInMilliseconds) {
            currentTime = Instant.now().toEpochMilli();
            try {
                action.run();
                succeeded = true;
                lastException = null;
            }
            catch (Throwable e) {
                lastException = e;
            }
        }

        if (lastException != null) {
            throw lastException;
        }
    }
}
