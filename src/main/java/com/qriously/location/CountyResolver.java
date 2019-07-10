package com.qriously.location;

import java.io.*;
import java.util.Map;

/**
 * CountyResolver provides base functionality for resolving locations
 */
public abstract class CountyResolver implements Closeable {

    private static final String[] SHAPE_FILE_EXTENSIONS = new String[]{ ".dbf", ".prj", ".shp", ".shx" };

    private long randomExtension;

    public interface OnCompletedListener {
        void completed();
    }

    /**
     * Initialize resources, warm caches or whatever is necessary to make it fast.
     */
    public abstract void init();

    /**
     * Resolve locations from given LocationProvider, when completed call the onCompletedListener.
     * When calling .read on the locationProvider will return a double[] of size 2, where the first element
     * is the latitude and the second element if the longitude, or null when there are no more locations to resolve.
     */
    public abstract void resolve(LocationProvider readFrom, OnCompletedListener onCompletedListener);

    /**
     * Returns a Map<String, Integer> indicating how many locations were resolved to a county.
     * key: county id (LVL_2_ID from shapefile)
     * value: count of locations resolved to this county.
     */
    public abstract Map<String, Integer> getResult();

    /**
     * Copy shapefiles from resources to a temporary location
     */
    String copyShapeFilesToTempDirectory(String shapeFileWithoutExtension) {
        String pathToShapeFile = null;
        String tempDir = System.getProperty("java.io.tmpdir");
        randomExtension = System.currentTimeMillis();

        for (String extension : SHAPE_FILE_EXTENSIONS) {
            try {
                File file = new File(tempDir + "/" + shapeFileWithoutExtension + "-" + randomExtension + extension);
                byte[] buffer = new byte[1024];
                try (InputStream in = getClass().getResourceAsStream("/" + shapeFileWithoutExtension + extension);
                     OutputStream out = new FileOutputStream(file)) {
                    int read;
                    while ((read = in.read(buffer)) > 0) {
                        out.write(buffer, 0, read);
                    }
                }
                if (extension.equals(".shp")) {
                    pathToShapeFile = file.getAbsolutePath();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

        return pathToShapeFile;
    }

    /**
     * Cleanup temporary files
     */
    public void close() {
        String tempDir = System.getProperty("java.io.tmpdir");

        for (String extension : SHAPE_FILE_EXTENSIONS) {
            File file = new File(tempDir + "/usa_counties" + "-" + randomExtension + extension);
            if (file.exists()) {
                file.delete();
            }
        }
    }

}
