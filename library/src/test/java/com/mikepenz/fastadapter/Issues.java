package com.mikepenz.fastadapter;

import com.mikepenz.fastadapter.adapters.FooterAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by fabianterhorst on 22.12.16.
 */

@RunWith(RobolectricTestRunner.class)
public class Issues {

    @Test
    public void test298() {
        HeaderAdapter<TestItem> headerAdapter = new HeaderAdapter<>();
        FastItemAdapter<TestItem> fastItemAdapter = new FastItemAdapter<>();
        FooterAdapter<TestItem> footerAdapter = new FooterAdapter<>();
        headerAdapter.wrap(footerAdapter.wrap(fastItemAdapter.items()));
        fastItemAdapter.items().add(new TestItem());
        fastItemAdapter.items().add(new TestItem());
        fastItemAdapter.items().add(new TestItem());
        int countBefore = fastItemAdapter.items().getAdapterItemCount();
        fastItemAdapter.items().getAdapterItems().add(new TestItem());
        int position = fastItemAdapter.getPreItemCountByOrder(fastItemAdapter.items().getOrder()) + countBefore;
        assertThat(position).isEqualTo(3);
    }
}
