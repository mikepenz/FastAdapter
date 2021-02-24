package com.mikepenz.fastadapter.diff;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * @author Shubham Chaudhary on 17/03/16
 */
@RunWith(RobolectricTestRunner.class)
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

    @Test
    public void testDiffUtilIdDistributor() {
        TestItem testItem1 = new TestItem("Test1");
        TestItem testItem2 = new TestItem("Test2");
        List<TestItem> originalList = new ArrayList<>();
        originalList.add(testItem1);
        originalList.add(testItem2);

        List<TestItem> updatedList = new ArrayList<>();
        updatedList.add(testItem2);
        updatedList.add(testItem1);

        test(originalList, updatedList);

        Assert.assertEquals(updatedList.get(0).getIdentifier(), originalList.get(1).getIdentifier());
        Assert.assertEquals(updatedList.get(1).getIdentifier(), originalList.get(0).getIdentifier());
    }

    public void test(List<TestItem> original, List<TestItem> adjusted) {
        itemAdapter.set(original);

        FastAdapterDiffUtil.INSTANCE.set(itemAdapter, adjusted);

        Assert.assertEquals(adjusted.size(), itemAdapter.getAdapterItemCount());

        for (int i = 0; i < adjusted.size(); i++) {
            Assert.assertEquals(adjusted.get(i), itemAdapter.getAdapterItem(i));
        }
    }
}
