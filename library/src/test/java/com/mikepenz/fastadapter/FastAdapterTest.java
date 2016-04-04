package com.mikepenz.fastadapter;

import android.support.v7.widget.RecyclerView;

import com.mikepenz.fastadapter.adapters.ItemAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;


/**
 * @author Shubham Chaudhary on 17/03/16
 */
@RunWith(RobolectricGradleTestRunner.class)
public class FastAdapterTest {
    private FastAdapter<TestItem> adapter;
    private ItemAdapter<TestItem> itemAdapter;

    @Before
    public void setUp() throws Exception {
        adapter = new FastAdapter<>();
        itemAdapter = new ItemAdapter<>();
        itemAdapter.wrap(adapter);
    }

    @Test
    public void constructor_HasStableId() throws Exception {
        assertThat(adapter.hasStableIds()).isTrue();
    }

    @Test
    public void constructor_Default_IsSelectable() throws Exception {
        assertThat(adapter.isSelectable()).isFalse();    //default
    }

    @Test
    public void withSelectable() throws Exception {
        assertThat(adapter.withSelectable(true).isSelectable()).isTrue();
        assertThat(adapter.withSelectable(false).isSelectable()).isFalse();
    }

    @Test
    public void wrap() throws Exception {
        assertThat(itemAdapter.getFastAdapter()).isEqualTo(adapter);
    }

    @Test
    public void set() throws Exception {
        itemAdapter.set(TestDataGenerator.genTestItemList(100));
        assertThat(itemAdapter.getAdapterItemCount()).isEqualTo(100);
    }

    @Test
    public void move() throws Exception {
        itemAdapter.set(TestDataGenerator.genTestItemList(11));
        assertThat(itemAdapter.getAdapterItemCount()).isEqualTo(11);
        TestItem movedItem = itemAdapter.getAdapterItem(0);
        TestItem changedItem = itemAdapter.getAdapterItem(1);
        itemAdapter.move(0, 10);
        assertThat(itemAdapter.getAdapterItemCount()).isEqualTo(11);
        assertThat(movedItem).isEqualTo(itemAdapter.getAdapterItem(10));
        assertThat(changedItem).isEqualTo(itemAdapter.getAdapterItem(0));
    }

    @Test
    public void clear() throws Exception {
        itemAdapter.set(TestDataGenerator.genTestItemList(100));
        assertThat(itemAdapter.getAdapterItemCount()).isEqualTo(100);
        itemAdapter.clear();
        assertThat(itemAdapter.getAdapterItemCount()).isEqualTo(0);
    }

    @Test
    public void withBindViewHolderListener_OnBindViewHolder_Callback() throws Exception {
        FastAdapter.OnBindViewHolderListener listener = mock(FastAdapter.OnBindViewHolderListener.class);
        RecyclerView.ViewHolder holder = mock(RecyclerView.ViewHolder.class);
        adapter.withOnBindViewHolderListener(listener);

        adapter.onBindViewHolder(holder, 10);

        verify(listener, only()).onBindViewHolder(holder, 10);
    }
}
