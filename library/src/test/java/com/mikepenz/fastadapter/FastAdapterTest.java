package com.mikepenz.fastadapter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Shubham Chaudhary on 17/03/16
 */
@RunWith(RobolectricGradleTestRunner.class)
public class FastAdapterTest {
    @Test public void constructor_HasStableId() throws Exception {
        FastAdapter<IItem> adapter = new FastAdapter<>();
        assertThat(adapter.hasStableIds()).isTrue();
    }
}
