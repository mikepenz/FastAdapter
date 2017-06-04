package com.mikepenz.fastadapter.commons.adapters;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IGenericItem;
import com.mikepenz.fastadapter.adapters.GenericItemAdapter;
import com.mikepenz.fastadapter.utils.Function;

import java.util.List;

/**
 * Created by fabianterhorst on 31.03.16.
 */
public class GenericFastItemAdapter<Model, Item extends IGenericItem<Model, Item, ?>> extends FastAdapter<Item> {
    private GenericItemAdapter<Model, Item> genericItemAdapter;

    /**
     * @param itemFactory a factory that takes a model as an argument and returns an item as a result
     */
    public GenericFastItemAdapter(Function<Model, Item> itemFactory) {
        genericItemAdapter = new GenericItemAdapter<>(itemFactory);
        addAdapter(0, genericItemAdapter);
        cacheSizes();
    }

    /**
     * returns the internal created GenericItemAdapter
     *
     * @return the GenericItemAdapter used inside this GenericFastItemAdapter
     */
    public GenericItemAdapter<Model, Item> getGenericItemAdapter() {
        return genericItemAdapter;
    }

    /**
     * returns the list of the model generated from the list of items
     *
     * @return the list with all model objects
     */
    public List<Model> getModels() {
        return getGenericItemAdapter().getModels();
    }

    /**
     * set a new list of models for this adapter
     *
     * @param models the set models
     */
    public GenericFastItemAdapter<Model, Item> setModel(List<Model> models) {
        getGenericItemAdapter().setModel(models);
        return this;
    }

    /**
     * sets a complete new list of items onto this adapter, using the new list. Calls notifyDataSetChanged
     *
     * @param models the set models
     */
    public GenericFastItemAdapter<Model, Item> setNewModel(List<Model> models) {
        getGenericItemAdapter().setNewModel(models);
        return this;
    }

    /**
     * add an array of models
     *
     * @param models the added models
     */
    @SafeVarargs
    public final GenericFastItemAdapter<Model, Item> addModel(Model... models) {
        getGenericItemAdapter().addModel(models);
        return this;
    }

    /**
     * add a list of models
     *
     * @param models the added models
     */
    public GenericFastItemAdapter<Model, Item> addModel(List<Model> models) {
        getGenericItemAdapter().addModel(models);
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
        getGenericItemAdapter().addModel(position, models);
        return this;
    }

    /**
     * add a list of models at a given (global) position
     *
     * @param position the global position
     * @param models   the added models
     */
    public GenericFastItemAdapter<Model, Item> addModel(int position, List<Model> models) {
        getGenericItemAdapter().addModel(position, models);
        return this;
    }

    /**
     * set a model at a given position
     *
     * @param position the global position
     * @param model    the set model
     */
    public GenericFastItemAdapter<Model, Item> setModel(int position, Model model) {
        getGenericItemAdapter().setModel(position, model);
        return this;
    }

    /**
     * clear all models
     */
    public GenericFastItemAdapter<Model, Item> clearModel() {
        getGenericItemAdapter().clearModel();
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
        getGenericItemAdapter().moveModel(fromPosition, toPosition);
        return this;
    }

    /**
     * remove a range oof model items starting with the (global) position and the size
     *
     * @param position  the global position
     * @param itemCount the count of items which were removed
     */
    public GenericFastItemAdapter<Model, Item> removeModelRange(int position, int itemCount) {
        getGenericItemAdapter().removeModelRange(position, itemCount);
        return this;
    }

    /**
     * remove a model at the given (global) position
     *
     * @param position the global position
     */
    public GenericFastItemAdapter<Model, Item> removeModel(int position) {
        getGenericItemAdapter().removeModel(position);
        return this;
    }
}
