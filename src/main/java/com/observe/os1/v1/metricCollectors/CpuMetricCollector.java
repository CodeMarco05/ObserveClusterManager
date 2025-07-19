package com.observe.os1.v1.metricCollectors;


import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;

public class CpuMetricCollector {
    @Scheduled(every = "1s")
    public void executeCpuMetricJob() {
        /*try {
            // Add your CPU metric collection logic here
            // Or inject and call your CpuMetricJob logic
        } catch (Exception e) {
            Log.error("Failed to execute CPU metric job: " + e.getMessage(), e);
        }*/
    }
}
