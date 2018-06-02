package com.mikepenz.fastadapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnBindViewHolderListener;
import com.mikepenz.fastadapter.select.SelectExtension;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;


/**
 * @author Shubham Chaudhary on 17/03/16
 */
@RunWith(RobolectricTestRunner.class)
public class FastAdapterTest {
    private FastAdapter<TestItem> adapter;
    private ItemAdapter<TestItem> itemAdapter;
    private SelectExtension<TestItem> selectExtension;

    @Before
    public void setUp() throws Exception {
        itemAdapter = new ItemAdapter<>();
        adapter = FastAdapter.with(itemAdapter);
        selectExtension = new SelectExtension<>();
        adapter.addExtension(selectExtension);
        //adapter.withPositionBasedStateManagement(true);
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
        assertThat(adapter.withSelectable(false).isSelectable()).isFalse();
        assertThat(adapter.withSelectable(true).isSelectable()).isTrue();
    }

    @Test
    public void select() throws Exception {
        itemAdapter.set(TestDataGenerator.genTestItemList(100));

        SelectExtension<TestItem> selectExtension = adapter.getExtension(SelectExtension.class);

        assertThat(selectExtension).isEqualTo(this.selectExtension);

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
        TestItem testItem = TestDataGenerator.genTestItem(1);
        itemAdapter.add(testItem);

        assertThat(adapter.getPosition(testItem)).isEqualTo(0);
    }

    @Test
    public void getItem() throws Exception {
        List<TestItem> items = TestDataGenerator.genTestItemList(100);
        TestItem item = items.get(40);
        itemAdapter.set(items);

        assertThat(adapter.getItem(40)).isEqualTo(item);
    }

    @Test
    public void getRelativeInfo() throws Exception {
        List<TestItem> items = TestDataGenerator.genTestItemList(100);
        TestItem item = items.get(40);
        itemAdapter.set(items);

        assertThat(adapter.getRelativeInfo(40).item).isEqualTo(item);
        assertThat(adapter.getRelativeInfo(40).adapter).isEqualTo(itemAdapter);
        assertThat(adapter.getRelativeInfo(40).position).isEqualTo(40);

        assertThat(adapter.getRelativeInfo(0).position).isEqualTo(0);
        assertThat(adapter.getRelativeInfo(100).item).isEqualTo(null);
    }

    @Test
    public void getAdapter() throws Exception {
        List<TestItem> items = TestDataGenerator.genTestItemList(100);
        itemAdapter.set(items);

        assertThat(adapter.getAdapter(40)).isEqualTo(itemAdapter);
    }

    @Test
    public void getItemViewType() throws Exception {
        List<TestItem> items = TestDataGenerator.genTestItemList(100);
        itemAdapter.set(items);

        assertThat(adapter.getItemViewType(40)).isEqualTo(-1);
    }

    @Test
    public void getItemId() throws Exception {
        List<TestItem> items = TestDataGenerator.genTestItemList(100);
        itemAdapter.set(items);

        assertThat(adapter.getItemId(40)).isEqualTo(40);
    }

    @Test
    public void getItemCount() throws Exception {
        List<TestItem> items = TestDataGenerator.genTestItemList(100);
        itemAdapter.set(items);

        assertThat(adapter.getItemCount()).isEqualTo(100);
    }

    @Test
    public void getPreItemCountByOrder() throws Exception {
        List<TestItem> items = TestDataGenerator.genTestItemList(100);
        itemAdapter.set(items);

        assertThat(adapter.getPreItemCountByOrder(itemAdapter.getOrder())).isEqualTo(0);
    }

    @Test
    public void getPreItemCount() throws Exception {
        List<TestItem> items = TestDataGenerator.genTestItemList(100);
        itemAdapter.set(items);

        assertThat(adapter.getPreItemCount(40)).isEqualTo(0);
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
        OnBindViewHolderListener listener = mock(OnBindViewHolderListener.class);
        RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(mock(View.class)) {
        };
        adapter.withOnBindViewHolderListener(listener);

        adapter.onBindViewHolder(holder, 10, new ArrayList<>());

        verify(listener, only()).onBindViewHolder(holder, 10, new ArrayList<>());
    }

    @Test
    public void testAddPreviouslyFilledAdapterPropagatesPossibleTypesToParentFastAdapter() {
        final TestItem testItem = new TestItem("example name");
        ItemAdapter<TestItem> itemAdapter = new ItemAdapter<>();
        itemAdapter.add(testItem);
        FastAdapter<TestItem> adapter = new FastAdapter<>();
        adapter.addAdapter(0, itemAdapter);
        final ViewGroup dummyParent = new FrameLayout(RuntimeEnvironment.application);
        adapter.onCreateViewHolder(dummyParent, testItem.getType());
    }
}