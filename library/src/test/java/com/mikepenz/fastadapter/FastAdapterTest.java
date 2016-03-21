package com.mikepenz.fastadapter;

import android.support.v7.widget.RecyclerView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


/**
 * @author Shubham Chaudhary on 17/03/16
 */
@RunWith(RobolectricGradleTestRunner.class)
public class FastAdapterTest {
    private FastAdapter<IItem> adapter;

    @Before public void setUp() throws Exception {
        adapter = new FastAdapter<>();
    }

    @Test public void constructor_HasStableId() throws Exception {
        assertThat(adapter.hasStableIds()).isTrue();
    }

    @Test public void constructor_Default_IsSelectable() throws Exception {
        assertThat(adapter.isSelectable()).isTrue();    //default
    }

    @Test public void withSelectable() throws Exception {
        assertThat(adapter.withSelectable(true).isSelectable()).isTrue();
        assertThat(adapter.withSelectable(false).isSelectable()).isFalse();
    }

    @Test public void withBindViewHolderListener_OnBindViewHolder_Callback() throws Exception {
        FastAdapter.OnBindViewHolderListener listener = mock(FastAdapter.OnBindViewHolderListener.class);
        RecyclerView.ViewHolder holder = mock(RecyclerView.ViewHolder.class);
        adapter.withOnBindViewHolderListener(listener);

        adapter.onBindViewHolder(holder, 10);

        verify(listener, only()).onBindViewHolder(holder, 10);
    }
}
