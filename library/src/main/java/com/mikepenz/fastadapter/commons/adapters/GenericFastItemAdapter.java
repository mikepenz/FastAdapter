package com.mikepenz.fastadapter.commons.adapters;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IGenericItem;
import com.mikepenz.fastadapter.adapters.GenericItemAdapter;
import com.mikepenz.fastadapter.utils.Function;

/**
 * Created by fabianterhorst on 31.03.16.
 */
public class GenericFastItemAdapter<Model, Item extends IGenericItem<Model, Item, ?>> extends FastAdapter<Item> {

    private final GenericItemAdapter<Model, Item> mItemAdapter;

    /**
     * @param itemClass  the class of your item (Item extends GenericAbstractItem)
     * @param modelClass the class which is your model class
     */
    public GenericFastItemAdapter(Class<? extends Item> itemClass, Class<? extends Model> modelClass) {
        mItemAdapter = new GenericItemAdapter<>(itemClass, modelClass);
        mItemAdapter.wrap(this);
    }

    /**
     * @param itemFactory a factory that takes a model as an argument and returns an item as a result
     */
    public GenericFastItemAdapter(Function<Model, Item> itemFactory) {
        mItemAdapter = new GenericItemAdapter<>(itemFactory);
        mItemAdapter.wrap(this);
    }

    /**
     * returns the internal created GenericItemAdapter
     *
     * @return the GenericItemAdapter used inside this GenericFastItemAdapter
     */
    public GenericItemAdapter<Model, Item> items() {
        return mItemAdapter;
    }
}
