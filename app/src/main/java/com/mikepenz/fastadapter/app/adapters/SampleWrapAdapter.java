package com.mikepenz.fastadapter.app.adapters;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.AbstractWrapAdapter;

import java.util.List;

/**
 * Created by mikepenz on 03.03.16.
 */
public class SampleWrapAdapter<Item extends IItem> extends AbstractWrapAdapter<Item> {

    public SampleWrapAdapter(List<Item> list) {
        super(list);
    }

    @Override
    public boolean shouldInsertItemAtPosition(int position) {
        return position == 10;
    }

    @Override
    public int itemInsertedBeforeCount(int position) {
        if (position > 10) {
            return 1;
        }
        return 0;
    }
}
