package com.mikepenz.fastadapter;

import android.support.test.InstrumentationRegistry;

import com.mikepenz.fastadapter.adapters.ItemAdapter;

import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import dk.ilios.spanner.AfterExperiment;
import dk.ilios.spanner.BeforeExperiment;
import dk.ilios.spanner.Benchmark;
import dk.ilios.spanner.BenchmarkConfiguration;
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
            .maxBenchmarkThreads(1)
            .build();

    private ItemAdapter<TestItem> itemAdapter;
    private FastAdapter<TestItem> fastAdapter;
    private List<TestItem> items;
    private List<TestItem> noIdItems;

    @BeforeExperiment
    public void before() {
        itemAdapter = new ItemAdapter<>();
        fastAdapter = new FastAdapter<>();
        itemAdapter.wrap(fastAdapter);
        items = TestDataGenerator.genTestItemList(1000);
        noIdItems = TestDataGenerator.genNoIdTestItemList(1000);
    }

    @AfterExperiment
    public void after() {

    }

    @Benchmark
    public void generateNoIdTestItemList() {
        TestDataGenerator.genNoIdTestItemList(1000);
    }

    @Benchmark
    public void generateItems() {
        TestDataGenerator.genTestItemList(1000);
    }

    @Benchmark
    public void setNewList() {
        itemAdapter.setNewList(items);
    }

    @Benchmark
    public void add() {
        itemAdapter.add(items);
    }

    @Benchmark
    public void set() {
        itemAdapter.set(items);
    }
}
