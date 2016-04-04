package com.mikepenz.fastadapter.adapters;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.GenericAbstractItem;
import com.mikepenz.fastadapter.utils.Function;

import java.util.List;

/**
 * Created by fabianterhorst on 31.03.16.
 */
public class GenericFastItemAdapter<Model, Item extends GenericAbstractItem<Model, Item, ?>> extends FastAdapter<Item> {

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
    public GenericItemAdapter<Model, Item> getGenericItemAdapter() {
        return mItemAdapter;
    }

    /**
     * returns the list of the model generated from the list of items
     *
     * @return the list with all model objects
     */
    public List<Model> getModels() {
        return mItemAdapter.getModels();
    }

    /**
     * set a new list of models for this adapter
     *
     * @param models the set models
     */
    public GenericFastItemAdapter<Model, Item> setModel(List<Model> models) {
        mItemAdapter.setModel(models);
        return this;
    }

    /**
     * sets a complete new list of items onto this adapter, using the new list. Calls notifyDataSetChanged
     *
     * @param models the set models
     */
    public GenericFastItemAdapter<Model, Item> setNewModel(List<Model> models) {
        mItemAdapter.setNewModel(models);
        return this;
    }

    /**
     * add an array of models
     *
     * @param models the added models
     */
    @SafeVarargs
    public final GenericFastItemAdapter<Model, Item> addModel(Model... models) {
        mItemAdapter.addModel(models);
        return this;
    }

    /**
     * add a list of models
     *
     * @param models the added models
     */
    public GenericFastItemAdapter<Model, Item> addModel(List<Model> models) {
        mItemAdapter.addModel(models);
        return this;
    }

    /**
     * add an array of models at a given (global) position
     *
     * @param position the global position
     * @param models   the added models
     */
    @SafeVarargs
    public final GenericFastItemAdapter<Model, Item> addModel(int position, Model... models) {
        mItemAdapter.addModel(position, models);
        return this;
    }

    /**
     * add a list of models at a given (global) position
     *
     * @param position the global position
     * @param models   the added models
     */
    public GenericFastItemAdapter<Model, Item> addModel(int position, List<Model> models) {
        mItemAdapter.addModel(position, models);
        return this;
    }

    /**
     * set a model at a given position
     *
     * @param position the global position
     * @param model    the set model
     */
    public GenericFastItemAdapter<Model, Item> setModel(int position, Model model) {
        mItemAdapter.setModel(position, model);
        return this;
    }

    /**
     * clear all models
     */
    public GenericFastItemAdapter<Model, Item> clearModel() {
        mItemAdapter.clearModel();
        return this;
    }

    /**
     * moves an model within the list from a position to a position
     *
     * @param fromPosition the position global from which we want to move
     * @param toPosition   the global position to which to move
     * @return this
     */
    public GenericFastItemAdapter<Model, Item> moveModel(int fromPosition, int toPosition) {
        mItemAdapter.moveModel(fromPosition, toPosition);
        return this;
    }

    /**
     * remove a range oof model items starting with the (global) position and the size
     *
     * @param position  the global position
     * @param itemCount the count of items which were removed
     */
    public GenericFastItemAdapter<Model, Item> removeModelRange(int position, int itemCount) {
        mItemAdapter.removeModelRange(position, itemCount);
        return this;
    }

    /**
     * remove a model at the given (global) position
     *
     * @param position the global position
     */
    public GenericFastItemAdapter<Model, Item> removeModel(int position) {
        mItemAdapter.removeModel(position);
        return this;
    }
}
