package com.qriously.location;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class TestResolver {

    @Test
    public void runTest() {
        try {
            final LocationProvider locationProvider = new LocationProvider();
            try (final CountyResolver resolver = new BasicCountyResolver()) {
                resolver.init();

                MyOnCompletedListener listener = new MyOnCompletedListener(locationProvider, resolver);

                resolver.resolve(locationProvider, listener);

                Thread.sleep(LocationProvider.MAX_RUNNING_TIME + 5000L);

                listener.assertResult();

                Assert.assertTrue("for some reason you never called complete on the listener", listener.isDidComplete());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyOnCompletedListener implements CountyResolver.OnCompletedListener {
        private boolean didComplete = false;

        private LocationProvider locationProvider;
        private CountyResolver countyResolver;

        MyOnCompletedListener(LocationProvider locationProvider, CountyResolver countyResolver) {
            this.locationProvider = locationProvider;
            this.countyResolver = countyResolver;
        }

        @Override
        public void completed() {
            didComplete = true;
            locationProvider.stop();
        }

        void assertResult() {
            // BasicCountyResolver should resolve at least 2k locations / second.
            // We would like implementations to be at least better than that.
            int perSecond = locationProvider.getLinesRead() / (int) (locationProvider.getRuntime() / 1000L);
            System.out.println("Resolved total: " + locationProvider.getLinesRead());
            System.out.println("Resolved / second: " + perSecond);
            Assert.assertTrue("The BasicCountyResolver should resolve at least 2000 location per second. This attempt was only : " + perSecond, perSecond > 2_000);

            Map<String, Integer> currentStatus = countyResolver.getResult();
            int resolved = 0;
            for (String key : currentStatus.keySet()) {
                resolved += currentStatus.get(key);
            }

            float percentResolved = (float) resolved / locationProvider.getLinesRead();
            System.out.println("Percent resolved: " + percentResolved);
            Assert.assertTrue("You resolved less than 90% of locations (" + percentResolved + "%), you made too big a trade-off for speed", percentResolved > 0.9f);

        }

        boolean isDidComplete() {
            return didComplete;
        }
    }
}
