package com.qriously.location;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * BasicCountyResolver
 * - Resolves location coordinates to US counties
 */
public class BasicCountyResolver extends CountyResolver {

    private Map<String, Integer> currentMap = new HashMap<>();

    private String pathToShapeFile;

    @Override
    public void init() {
        pathToShapeFile = copyShapeFilesToTempDirectory("usa_counties");
    }

    @Override
    public void resolve(final LocationProvider readFrom, final OnCompletedListener listener) {

        Runnable task = new Runnable() {
            @Override
            public void run() {
                SimpleFeatureIterator featureIterator = null;

                try {
                    FileDataStore store = FileDataStoreFinder.getDataStore(new File(pathToShapeFile));
                    SimpleFeatureSource featureSource = store.getFeatureSource();
                    String geometryPropertyName = featureSource.getSchema().getGeometryDescriptor().getLocalName();

                    double[] loc;

                    while ((loc = readFrom.getNextLocation()) != null) {

                        Filter filter = CQL.toFilter("CONTAINS(" + geometryPropertyName + ", POINT(" + loc[1] + " " + loc[0] + "))");

                        SimpleFeatureCollection features = featureSource.getFeatures(filter);

                        featureIterator = features.features();
                        while (featureIterator.hasNext()) {
                            SimpleFeature sf = featureIterator.next();
                            String countyId = sf.getAttribute("LVL_2_ID").toString();
                            if (!currentMap.containsKey(countyId)) {
                                currentMap.put(countyId, 0);
                            }

                            currentMap.put(countyId, currentMap.get(countyId) + 1);
                        }
                        featureIterator.close();
                    }
                } catch (IOException | CQLException e) {
                    e.printStackTrace();
                } finally {
                    if (featureIterator != null) {
                        featureIterator.close();
                    }
                }
                listener.completed();
            }
        };

        Executors.newSingleThreadExecutor().execute(task);
    }

    @Override
    public Map<String, Integer> getResult() {
        return currentMap;
    }

}
