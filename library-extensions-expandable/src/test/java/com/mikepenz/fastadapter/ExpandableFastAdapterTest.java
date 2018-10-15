package com.mikepenz.fastadapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.expandable.ExpandableExtension;
import com.mikepenz.fastadapter.listeners.OnBindViewHolderListener;
import com.mikepenz.fastadapter.select.SelectExtension;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;


/**
 * @author Shubham Chaudhary on 17/03/16
 */
@RunWith(RobolectricTestRunner.class)
public class ExpandableFastAdapterTest {
    private FastAdapter<ExpandableTestItem> adapter;
    private ItemAdapter<ExpandableTestItem> itemAdapter;
    private ExpandableExtension<ExpandableTestItem> expandableExtension;
    private SelectExtension<ExpandableTestItem> selectExtension;

    @Before
    public void setUp() throws Exception {
        itemAdapter = new ItemAdapter<>();
        adapter = FastAdapter.with(itemAdapter);
        expandableExtension = new ExpandableExtension<>(adapter);
        selectExtension = new SelectExtension<>(adapter);
        adapter.addExtension(expandableExtension);
        adapter.addExtension(selectExtension);
        //adapter.withPositionBasedStateManagement(true);
    }

    @Test
    public void constructor_HasStableId() throws Exception {
        assertThat(adapter.hasStableIds()).isTrue();
    }

    @Test
    public void select() throws Exception {
        selectExtension.setSelectable(true);
        itemAdapter.set(TestDataGenerator.genTestItemList(100));

        assertThat(selectExtension.getSelectedItems().size()).isEqualTo(0);
        assertThat(selectExtension.getSelections().size()).isEqualTo(0);

        selectExtension.select(10);

        assertThat(selectExtension.getSelectedItems().size()).isEqualTo(1);
        assertThat(selectExtension.getSelectedItems().iterator().next().getIdentifier()).isEqualTo(10);
        assertThat(selectExtension.getSelections().size()).isEqualTo(1);
        assertThat(selectExtension.getSelections().iterator().next()).isEqualTo(10);
    }

    @Test
    public void getPosition() throws Exception {
        ExpandableTestItem expandableTestItem = TestDataGenerator.genTestItem(1);
        itemAdapter.add(expandableTestItem);

        assertThat(adapter.getPosition(expandableTestItem)).isEqualTo(0);
    }

    @Test
    public void getItem() throws Exception {
        List<ExpandableTestItem> items = TestDataGenerator.genTestItemList(100);
        ExpandableTestItem item = items.get(40);
        itemAdapter.set(items);

        assertThat(adapter.getItem(40)).isEqualTo(item);
    }

    @Test
    public void getRelativeInfo() throws Exception {
        List<ExpandableTestItem> items = TestDataGenerator.genTestItemList(100);
        ExpandableTestItem item = items.get(40);
        itemAdapter.set(items);

        assertThat(adapter.getRelativeInfo(40).getItem()).isEqualTo(item);
        assertThat(adapter.getRelativeInfo(40).getAdapter()).isEqualTo(itemAdapter);
        assertThat(adapter.getRelativeInfo(40).getPosition()).isEqualTo(40);
    }

    @Test
    public void getAdapter() throws Exception {
        List<ExpandableTestItem> items = TestDataGenerator.genTestItemList(100);
        itemAdapter.set(items);

        assertThat(adapter.getAdapter(40)).isEqualTo(itemAdapter);
    }

    @Test
    public void getItemViewType() throws Exception {
        List<ExpandableTestItem> items = TestDataGenerator.genTestItemList(100);
        itemAdapter.set(items);

        assertThat(adapter.getItemViewType(40)).isEqualTo(-1);
    }

    @Test
    public void getItemId() throws Exception {
        List<ExpandableTestItem> items = TestDataGenerator.genTestItemList(100);
        itemAdapter.set(items);

        assertThat(adapter.getItemId(40)).isEqualTo(40);
    }

    @Test
    public void getItemCount() throws Exception {
        List<ExpandableTestItem> items = TestDataGenerator.genTestItemList(100);
        itemAdapter.set(items);

        assertThat(adapter.getItemCount()).isEqualTo(100);
    }

    @Test
    public void getPreItemCountByOrder() throws Exception {
        List<ExpandableTestItem> items = TestDataGenerator.genTestItemList(100);
        itemAdapter.set(items);

        assertThat(adapter.getPreItemCountByOrder(itemAdapter.getOrder())).isEqualTo(0);
    }

    @Test
    public void getPreItemCount() throws Exception {
        List<ExpandableTestItem> items = TestDataGenerator.genTestItemList(100);
        itemAdapter.set(items);

        assertThat(adapter.getPreItemCount(40)).isEqualTo(0);
    }

    @Test
    public void getExpandedItemsCount() throws Exception {
        List<ExpandableTestItem> items = TestDataGenerator.genTestItemWithSubItemsList(10, 1);
        itemAdapter.set(items);

        expandableExtension.expand(5);

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(10);
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
        ExpandableTestItem movedItem = itemAdapter.getAdapterItem(0);
        ExpandableTestItem changedItem = itemAdapter.getAdapterItem(1);
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
        OnBindViewHolderListener listener = mock(OnBindViewHolderListener.class);
        RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(mock(View.class)) {
        };
        adapter.setOnBindViewHolderListener(listener);

        adapter.onBindViewHolder(holder, 10, Collections.emptyList());

        verify(listener, only()).onBindViewHolder(holder, 10, Collections.emptyList());
    }
}
