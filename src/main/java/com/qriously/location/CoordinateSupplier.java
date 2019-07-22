package com.qriously.location;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Supplier;

public class CoordinateSupplier implements Supplier<Coordinate>, Closeable {

    private final static String LOCATIONS_CSV = "/locations.csv";

    private BufferedReader reader;
    private int coordinatesRead;

    CoordinateSupplier() {
        reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(LOCATIONS_CSV)));
        coordinatesRead = 0;
    }

    @Override
    public Coordinate get() {
        return getNextCoordinate();
    }

    private synchronized Coordinate getNextCoordinate() {
        String line;
        try {
            if ((line = reader.readLine()) != null) {
                coordinatesRead++;
                String[] parts = line.split(",");
                return new Coordinate(Double.valueOf(parts[0]), Double.valueOf(parts[1]));
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return null;
    }

    int getCoordinatesRead() {
        return coordinatesRead;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
