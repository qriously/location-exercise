package com.qriously.location;

import java.io.Closeable;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class CountyResolver implements Runnable, Closeable {

    private static int MAX_RUNTIME = 30;

    private CoordinateSupplier coordinateSupplier;

    private long start, end;
    private int attempted, resolved;
    private boolean didError = false;
    private Map<String, AtomicInteger> results = new ConcurrentHashMap<>();

    /**
     * CountyResolvers must be initialised with a coordinateSupplier
     */
    CountyResolver(CoordinateSupplier coordinateSupplier) {
        this.coordinateSupplier = coordinateSupplier;
    }

    /**
     * Resolve the given coordinate to a US county
     * <p>
     * Returns the LVL_2_ID code corresponding to the US county or null if unresolved.
     */
    public abstract String resolve(Coordinate coordinate);

    /**
     * Run the resolver
     * - While there are
     */
    @Override
    public void run() {
        start = System.currentTimeMillis();

        Coordinate coordinate;
        try {
            while (getRuntimeDuration() < MAX_RUNTIME && (coordinate = coordinateSupplier.get()) != null) {
                attempted++;
                String county = resolve(coordinate);
                if (county != null) {
                    resolved++;
                    results.computeIfAbsent(county, key -> new AtomicInteger(0)).incrementAndGet();
                }
                end = System.currentTimeMillis();
            }
        } catch (Exception ex) {
            didError = true;
        }
        end = System.currentTimeMillis();

    }

    /**
     * Returns the results map containing LVL_2_ID, resolved count.
     */
    Map<String, AtomicInteger> getResults() {
        return results;
    }

    /**
     * Returns the count of resolved locations
     */
    public int getResolvedCount() {
        return resolved;
    }

    /**
     * Returns the count of resolved locations
     */
    public int getAttemptedCount() {
        return attempted;
    }

    /**
     * Returns the duration in seconds for the last run of the resolver.
     */
    public long getRuntimeDuration() {
        return (end - start) / Duration.ofSeconds(1).toMillis();
    }

    /**
     * Returns the fraction of resolve attempts that were successful.
     */
    public float getResolvedFraction() {
        return (float) resolved / attempted;
    }

    /**
     * Returns true if the resolver process failed otherwise false.
     */
    public boolean resolverError() {
        return didError;
    }

}
