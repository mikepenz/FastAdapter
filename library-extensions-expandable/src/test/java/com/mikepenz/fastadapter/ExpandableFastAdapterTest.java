package com.mikepenz.fastadapter;

import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.expandable.ExpandableExtension;
import com.mikepenz.fastadapter.select.SelectExtension;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
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
    }

    @Test
    public void getExpandedItemsCount() throws Exception {
        List<ExpandableTestItem> items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 1);
        itemAdapter.set(items);

        expandableExtension.expand(5);

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(10);

        expandableExtension.collapse(5);

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(0);
    }

    @Test
    public void expand() throws Exception {
        List<ExpandableTestItem> items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 1);
        itemAdapter.set(items);

        assertThat(itemAdapter.getAdapterItem(1).getIdentifier()).isEqualTo(12);

        expandableExtension.expand(0);

        assertThat(itemAdapter.getAdapterItem(1).getIdentifier()).isEqualTo(2);
    }

    @Test
    public void expandDepth2() throws Exception {
        List<ExpandableTestItem> items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 2);
        itemAdapter.set(items);

        expandableExtension.expand(0);

        expandableExtension.expand(10);

        assertThat(itemAdapter.getAdapterItem(13).getIdentifier()).isEqualTo(104);
    }

    @Test
    public void collapse() throws Exception {
        List<ExpandableTestItem> items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 1);
        itemAdapter.set(items);

        expandableExtension.expand(0);

        assertThat(itemAdapter.getAdapterItem(1).getIdentifier()).isEqualTo(2);

        expandableExtension.collapse(0);

        assertThat(itemAdapter.getAdapterItem(1).getIdentifier()).isEqualTo(12);
    }

    @Test
    public void collapseDepth2() throws Exception {
        List<ExpandableTestItem> items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 2);
        itemAdapter.set(items);

        expandableExtension.expand(0);

        expandableExtension.expand(10);

        expandableExtension.collapse(0);

        assertThat(itemAdapter.getAdapterItem(0).getIdentifier()).isEqualTo(1);

        assertThat(expandableExtension.getExpandedItemsCount(0, 2210)).isEqualTo(0);
    }

    @Test
    public void toggle() throws Exception {
        List<ExpandableTestItem> items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 1);
        itemAdapter.set(items);

        expandableExtension.toggleExpandable(0);

        assertThat(itemAdapter.getAdapterItem(1).getIdentifier()).isEqualTo(2);

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(10);

        expandableExtension.toggleExpandable(0);

        assertThat(itemAdapter.getAdapterItem(1).getIdentifier()).isEqualTo(12);

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(0);
    }

    @Test
    public void toggleNoSubItemsExpandable() throws Exception {
        List<ExpandableTestItem> items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 0);
        itemAdapter.set(items);

        expandableExtension.toggleExpandable(0);

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(0);

        expandableExtension.toggleExpandable(0);

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(0);
    }

    @Test
    public void toggleNoneExpandable() throws Exception {
        ItemAdapter<NoneExpandableTestItem> itemAdapter = new ItemAdapter<>();
        FastAdapter<NoneExpandableTestItem> fastAdapter = FastAdapter.with(itemAdapter);
        ExpandableExtension<NoneExpandableTestItem> expandableExtension = new ExpandableExtension<>(fastAdapter);
        fastAdapter.addExtension(expandableExtension);
        List<NoneExpandableTestItem> noneExpandableTestItems = new ArrayList<>();
        NoneExpandableTestItem item = new NoneExpandableTestItem();
        item.setIdentifier(1);
        NoneExpandableTestItem item2 = new NoneExpandableTestItem();
        item2.setIdentifier(2);
        noneExpandableTestItems.add(item);
        noneExpandableTestItems.add(item2);
        itemAdapter.set(noneExpandableTestItems);

        expandableExtension.toggleExpandable(0);

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(0);

        expandableExtension.toggleExpandable(0);

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(0);
    }

    @Test
    public void expandAll() throws Exception {
        List<ExpandableTestItem> items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 1);
        itemAdapter.set(items);

        expandableExtension.expand();

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(10 * 10);
    }

    @Test
    public void collapseAll() throws Exception {
        List<ExpandableTestItem> items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 1);
        itemAdapter.set(items);

        expandableExtension.expand();

        expandableExtension.collapse();

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(0);
    }
}
