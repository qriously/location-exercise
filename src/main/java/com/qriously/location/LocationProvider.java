package com.qriously.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LocationProvider {

    public static final long MAX_RUNNING_TIME = 15000L;

    private String fileName = "/locations.csv";

    private int linesRead = 0;

    private BufferedReader reader;
    private long firstRequestTS = 0L;
    private long stoppedTS;

    public LocationProvider() {
        reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(fileName)));
    }

    /**
     * Get the next location
     * Returns a lat,lon pair if available otherwise.
     */
    public synchronized double[] getNextLocation() {
        if (linesRead == 0) {
            firstRequestTS = System.currentTimeMillis();
        }
        String line;
        try {
            if ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                linesRead++;
                if (System.currentTimeMillis() - firstRequestTS < MAX_RUNNING_TIME) {
                    return new double[]{ Double.valueOf(parts[0]), Double.valueOf(parts[1]) };
                }
            }
        } catch (Exception e) {
            //log swallowed exception...
        }

        return null;
    }


    public synchronized void stop() {
        try {
            reader.close();
            stoppedTS = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized int getLinesRead() {
        return this.linesRead;
    }

    public synchronized long getRuntime() {
        return stoppedTS - firstRequestTS;
    }
}
