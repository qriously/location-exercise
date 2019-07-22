package com.qriously.location;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class ResolverTest {

    @Test
    public void testBasicCountyResolver() throws Exception {
        try (CoordinateSupplier coordinateSupplier = new CoordinateSupplier("/locations-small.csv");
            CountyResolver resolver = new BasicCountyResolver()) {

            long start = System.currentTimeMillis();
            Map<String, Integer> result = resolver.resolve(coordinateSupplier);
            long end = System.currentTimeMillis();

            int resolved = result.values().stream()
                    .mapToInt(i -> i)
                    .sum();

            int attempted = coordinateSupplier.getCoordinatesRead();

            double resolvedFraction = (double) resolved / attempted;

            System.out.println(String.format("Resolver managed to resolve %.2f of locations (%d / %d)",
                    resolvedFraction, resolved, attempted));
            Assert.assertTrue("Resolver success rate was less that required threshold (90%)",
                    resolvedFraction >= 0.9);

            double resolveRate = (double) resolved / ((end - start) / 1000.0);
            System.out.println(String.format("Resolver managed to resolve locations at %.2f / second", resolveRate));
            Assert.assertTrue("Resolver rate was less than required threshold (5000 per second)", (resolveRate >= 5000));
        }
    }
}
