package com.mikepenz.fastadapter.adapters;

import com.mikepenz.fastadapter.IInterceptor;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by mikepenz on 27.12.15.
 * A general ItemAdapter implementation based on the AbstractAdapter to speed up development for general items
 */
public class ItemAdapter<Item extends IItem<? extends RecyclerView.ViewHolder>> extends ModelAdapter<Item, Item> {

    public ItemAdapter() {
        super((IInterceptor<Item, Item>) IInterceptor.Companion.getDEFAULT());
    }

    public ItemAdapter(IItemList<Item> itemList) {
        super(itemList, (IInterceptor<Item, Item>) IInterceptor.Companion.getDEFAULT());
    }

    /**
     * static method to retrieve a new `ItemAdapter`
     *
     * @return a new ItemAdapter
     */
    public static <Item extends IItem<? extends RecyclerView.ViewHolder>> ItemAdapter<Item> items() {
        return new ItemAdapter<>();
    }
}
