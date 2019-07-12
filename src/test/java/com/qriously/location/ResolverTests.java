package com.qriously.location;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ResolverTests {

    static long THREAD_SLEEP = 100;

    private void waitForExecutorsToComplete(ThreadPoolExecutor executorService) {
        try {
            while (executorService.getQueue().size() > 0 || executorService.getActiveCount() > 0) {
                Thread.sleep(THREAD_SLEEP);
            }
            Thread.sleep(THREAD_SLEEP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ThreadPoolExecutor initialiseThreadPool(boolean preStart) {
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
        if (preStart) {
            executorService.prestartAllCoreThreads();
        }

        return executorService;
    }

    @Test
    public void testBasicCountyResolver() throws IOException {
        ThreadPoolExecutor executorService = initialiseThreadPool(false);

        try (CoordinateSupplier coordinateSupplier = new CoordinateSupplier();
             CountyResolver resolver = new BasicCountyResolver(coordinateSupplier)) {

            executorService.submit(resolver);

            waitForExecutorsToComplete(executorService);

            Assert.assertFalse("There were error when running the resolver", resolver.resolverError());

            System.out.println(String.format("Resolver managed to resolve %.2f of locations (%d / %d)", resolver.getResolvedFraction(), resolver.getResolvedCount(), resolver.getAttemptedCount()));
            Assert.assertTrue("Resolver success rate was less that required threshold (90%)", resolver.getResolvedFraction() > 0.9);

            float resolveRate = resolver.getResolvedCount() / resolver.getRuntimeDuration();
            System.out.println(String.format("Resolver managed to resolve locations at %.2f / second", resolveRate));
            Assert.assertTrue("Resolver rate was less than required threshold (1000 per second)", (resolveRate > 2000));
        }

    }

}
