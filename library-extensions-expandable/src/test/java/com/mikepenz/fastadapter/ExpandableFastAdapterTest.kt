package com.mikepenz.fastadapter

import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.expandable.ExpandableExtension
import com.mikepenz.fastadapter.expandable.getExpandableExtension
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.fastadapter.select.getSelectExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class ExpandableFastAdapterTest {
    private lateinit var adapter: FastAdapter<ExpandableTestItem>
    private lateinit var itemAdapter: ItemAdapter<ExpandableTestItem>
    private lateinit var expandableExtension: ExpandableExtension<ExpandableTestItem>
    private lateinit var selectExtension: SelectExtension<ExpandableTestItem>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        itemAdapter = ItemAdapter()
        adapter = FastAdapter.with(itemAdapter)
        expandableExtension = adapter.getExpandableExtension()
        selectExtension = adapter.getSelectExtension()
    }

    @Test
    @Throws(Exception::class)
    fun getExpandedItemsCount() {
        val items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 1)
        itemAdapter.set(items)

        expandableExtension.expand(5)

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(10)

        expandableExtension.collapse(5)

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(0)
    }

    @Test
    @Throws(Exception::class)
    fun expand() {
        val items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 1)
        itemAdapter.set(items)

        assertThat(itemAdapter.getAdapterItem(1).identifier).isEqualTo(12)

        expandableExtension.expand(0)

        assertThat(itemAdapter.getAdapterItem(1).identifier).isEqualTo(2)
    }

    @Test
    @Throws(Exception::class)
    fun expandDepth2() {
        val items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 2)
        itemAdapter.set(items)

        expandableExtension.expand(0)

        expandableExtension.expand(10)

        assertThat(itemAdapter.getAdapterItem(13).identifier).isEqualTo(104)
    }

    @Test
    @Throws(Exception::class)
    fun collapse() {
        val items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 1)
        itemAdapter.set(items)

        expandableExtension.expand(0)

        assertThat(itemAdapter.getAdapterItem(1).identifier).isEqualTo(2)

        expandableExtension.collapse(0)

        assertThat(itemAdapter.getAdapterItem(1).identifier).isEqualTo(12)
    }

    @Test
    @Throws(Exception::class)
    fun collapseDepth2() {
        val items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 2)
        itemAdapter.set(items)

        expandableExtension.expand(0)

        expandableExtension.expand(10)

        expandableExtension.collapse(0)

        assertThat(itemAdapter.getAdapterItem(0).identifier).isEqualTo(1)

        assertThat(expandableExtension.getExpandedItemsCount(0, 2210)).isEqualTo(0)
    }

    @Test
    @Throws(Exception::class)
    fun toggle() {
        val items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 1)
        itemAdapter.set(items)

        expandableExtension.toggleExpandable(0)

        assertThat(itemAdapter.getAdapterItem(1).identifier).isEqualTo(2)

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(10)

        expandableExtension.toggleExpandable(0)

        assertThat(itemAdapter.getAdapterItem(1).identifier).isEqualTo(12)

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(0)
    }

    @Test
    @Throws(Exception::class)
    fun toggleNoSubItemsExpandable() {
        val items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 0)
        itemAdapter.set(items)

        expandableExtension.toggleExpandable(0)

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(0)

        expandableExtension.toggleExpandable(0)

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(0)
    }

    @Test
    @Throws(Exception::class)
    fun toggleNoneExpandable() {
        val itemAdapter = ItemAdapter<NoneExpandableTestItem>()
        val fastAdapter = FastAdapter.with(itemAdapter)
        val expandableExtension = fastAdapter.getExpandableExtension()
        val noneExpandableTestItems = ArrayList<NoneExpandableTestItem>()
        val item = NoneExpandableTestItem()
        item.identifier = 1
        val item2 = NoneExpandableTestItem()
        item2.identifier = 2
        noneExpandableTestItems.add(item)
        noneExpandableTestItems.add(item2)
        itemAdapter.set(noneExpandableTestItems)

        expandableExtension.toggleExpandable(0)

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(0)

        expandableExtension.toggleExpandable(0)

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(0)
    }

    @Test
    @Throws(Exception::class)
    fun expandAll() {
        val items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 1)
        itemAdapter.set(items)

        expandableExtension.expand()

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(10 * 10)
    }

    @Test
    @Throws(Exception::class)
    fun collapseAll() {
        val items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 1)
        itemAdapter.set(items)

        expandableExtension.expand()

        expandableExtension.collapse()

        assertThat(expandableExtension.getExpandedItemsCount(0, 100)).isEqualTo(0)
    }

    @Test
    @Throws(Exception::class)
    fun saveEmptyInstanceState() {
        val items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 1)
        itemAdapter.set(items)

        val bundle = Bundle()
        expandableExtension.saveInstanceState(bundle, "")
        assertThat(bundle.getLongArray("bundle_expanded")).isEqualTo(arrayOf<Long>())
    }

    @Test
    @Throws(Exception::class)
    fun saveInstanceState() {
        val items = ExpandableTestDataGenerator.genTestItemWithSubItemsList(10, 1)
        itemAdapter.set(items)

        expandableExtension.expand(0)

        var bundle = Bundle()
        expandableExtension.saveInstanceState(bundle, "")
        assertThat(bundle.getLongArray("bundle_expanded")).isEqualTo(arrayOf(1L))

        expandableExtension.expand(11)

        bundle = Bundle()
        expandableExtension.saveInstanceState(bundle, "")
        assertThat(bundle.getLongArray("bundle_expanded")).isEqualTo(arrayOf(1L, 12L))
    }
}
