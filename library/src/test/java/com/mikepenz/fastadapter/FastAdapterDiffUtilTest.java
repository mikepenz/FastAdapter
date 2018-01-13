package com.mikepenz.fastadapter;

import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;

import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * @author Shubham Chaudhary on 17/03/16
 */
@RunWith(RobolectricGradleTestRunner.class)
public class FastAdapterDiffUtilTest {
    private FastAdapter<TestItem> adapter;
    private ItemAdapter<TestItem> itemAdapter;

    @Before
    public void setUp() throws Exception {
        itemAdapter = new ItemAdapter<>();
        adapter = FastAdapter.with(itemAdapter);
    }

    @Test
    public void testDiffUtilEqualSize() {
        List<TestItem> originalList = TestDataGenerator.genTestItemList(100);
        List<TestItem> updatedList = TestDataGenerator.genTestItemList(100);
        Collections.shuffle(updatedList, new Random(1342348L));
        test(originalList, updatedList);
    }

    @Test
    public void testDiffUtilAdd() {
        List<TestItem> originalList = TestDataGenerator.genTestItemList(100);
        List<TestItem> updatedList = TestDataGenerator.genTestItemList(150);
        test(originalList, updatedList);
    }

    @Test
    public void testDiffUtilAddShuffle() {
        List<TestItem> originalList = TestDataGenerator.genTestItemList(100);
        List<TestItem> updatedList = TestDataGenerator.genTestItemList(150);
        Collections.shuffle(updatedList, new Random(1342348L));
        test(originalList, updatedList);
    }

    @Test
    public void testDiffUtilRemove() {
        List<TestItem> originalList = TestDataGenerator.genTestItemList(100);
        List<TestItem> updatedList = TestDataGenerator.genTestItemList(50);
        test(originalList, updatedList);
    }

    @Test
    public void testDiffUtilRemoveShuffle() {
        List<TestItem> originalList = TestDataGenerator.genTestItemList(100);
        List<TestItem> updatedList = TestDataGenerator.genTestItemList(50);
        Collections.shuffle(updatedList, new Random(1342348L));

        test(originalList, updatedList);
    }

    public void test(List<TestItem> original, List<TestItem> adjusted) {
        itemAdapter.set(original);

        FastAdapterDiffUtil.set(itemAdapter, adjusted);

        Assert.assertEquals(adjusted.size(), itemAdapter.getAdapterItemCount());

        for (int i = 0; i < adjusted.size(); i++) {
            Assert.assertEquals(adjusted.get(i), itemAdapter.getAdapterItem(i));
        }
    }
}
