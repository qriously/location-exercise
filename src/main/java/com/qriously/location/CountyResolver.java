package com.qriously.location;

import java.io.Closeable;
import java.util.Map;


public interface CountyResolver extends Closeable {

    /**
     * Reads coordinates from the given supplier, resolves them, and returns a map from resolved county id to the number
     * of coordinates that resolved to that county.
     */
    Map<String, Integer> resolve(CoordinateSupplier coordinateSupplier) throws ResolverException;
}
