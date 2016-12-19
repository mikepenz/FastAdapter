package com.mikepenz.fastadapter;

import android.support.test.InstrumentationRegistry;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.junit.runner.RunWith;

import java.io.File;

import dk.ilios.spanner.AfterExperiment;
import dk.ilios.spanner.BeforeExperiment;
import dk.ilios.spanner.Benchmark;
import dk.ilios.spanner.BenchmarkConfiguration;
import dk.ilios.spanner.Param;
import dk.ilios.spanner.SpannerConfig;
import dk.ilios.spanner.config.RuntimeInstrumentConfig;
import dk.ilios.spanner.junit.SpannerRunner;

/**
 * Created by fabianterhorst on 19.12.16.
 */

@RunWith(SpannerRunner.class)
public class FastAdapterBenchmark {

    private File filesDir = InstrumentationRegistry.getTargetContext().getFilesDir();
    private File resultsDir = new File(filesDir, "results");

    @BenchmarkConfiguration
    public SpannerConfig configuration = new SpannerConfig.Builder()
            .saveResults(resultsDir, FastAdapterBenchmark.class.getCanonicalName() + ".json") // Save results to disk
            .medianFailureLimit(Float.MAX_VALUE) // Fail if difference vs. baseline is to big. Should normally be 10-15%  (0.15)
            .addInstrument(RuntimeInstrumentConfig.defaultConfig()) // Configure how benchmark is run/measured
            .build();

    // Public test parameters (value chosen and injected by Experiment)
    @Param(value = {"java.util.Date", "java.lang.Object"})
    public String value;

    private FastItemAdapter<TestItem> fastItemAdapter;

    @BeforeExperiment
    public void before() {
        fastItemAdapter = new FastItemAdapter<>();
    }

    @AfterExperiment
    public void after() {

    }

    @Benchmark
    public void addItems() {
        fastItemAdapter.items().add(TestDataGenerator.genTestItem(100));
    }
}
