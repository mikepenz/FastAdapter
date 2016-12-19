package com.mikepenz.fastadapter;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.junit.runner.RunWith;

import dk.ilios.spanner.AfterExperiment;
import dk.ilios.spanner.BeforeExperiment;
import dk.ilios.spanner.Benchmark;
import dk.ilios.spanner.junit.SpannerRunner;

/**
 * Created by fabianterhorst on 19.12.16.
 */

@RunWith(SpannerRunner.class)
public class FastAdapterBenchmark {

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
        fastItemAdapter.items().setNewList(TestDataGenerator.genTestItemList(100));
    }
}
