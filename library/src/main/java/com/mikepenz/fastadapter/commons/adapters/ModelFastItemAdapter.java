package com.mikepenz.fastadapter.commons.adapters;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IModelItem;
import com.mikepenz.fastadapter.adapters.ModelItemAdapter;
import com.mikepenz.fastadapter.utils.Function;

import java.util.List;

/**
 * Created by fabianterhorst on 31.03.16.
 */
public class ModelFastItemAdapter<Model, Item extends IModelItem<Model, Item, ?>> extends FastAdapter<Item> {
    private ModelItemAdapter<Model, Item> modelItemAdapter;

    /**
     * @param itemFactory a factory that takes a model as an argument and returns an item as a result
     */
    public ModelFastItemAdapter(Function<Model, Item> itemFactory) {
        modelItemAdapter = new ModelItemAdapter<>(itemFactory);
        addAdapter(0, modelItemAdapter);
        cacheSizes();
    }

    /**
     * returns the internal created ModelItemAdapter
     *
     * @return the ModelItemAdapter used inside this ModelFastItemAdapter
     */
    public ModelItemAdapter<Model, Item> getModelItemAdapter() {
        return modelItemAdapter;
    }

    /**
     * returns the list of the model generated from the list of items
     *
     * @return the list with all model objects
     */
    public List<Model> getModels() {
        return getModelItemAdapter().getModels();
    }

    /**
     * set a new list of models for this adapter
     *
     * @param models the set models
     */
    public ModelFastItemAdapter<Model, Item> setModel(List<Model> models) {
        getModelItemAdapter().setModel(models);
        return this;
    }

    /**
     * sets a complete new list of items onto this adapter, using the new list. Calls notifyDataSetChanged
     *
     * @param models the set models
     */
    public ModelFastItemAdapter<Model, Item> setNewModel(List<Model> models) {
        getModelItemAdapter().setNewModel(models);
        return this;
    }

    /**
     * add an array of models
     *
     * @param models the added models
     */
    @SafeVarargs
    public final ModelFastItemAdapter<Model, Item> addModel(Model... models) {
        getModelItemAdapter().addModel(models);
        return this;
    }

    /**
     * add a list of models
     *
     * @param models the added models
     */
    public ModelFastItemAdapter<Model, Item> addModel(List<Model> models) {
        getModelItemAdapter().addModel(models);
        return this;
    }

    /**
     * add an array of models at a given (global) position
     *
     * @param position the global position
     * @param models   the added models
     */
    @SafeVarargs
    public final ModelFastItemAdapter<Model, Item> addModel(int position, Model... models) {
        getModelItemAdapter().addModel(position, models);
        return this;
    }

    /**
     * add a list of models at a given (global) position
     *
     * @param position the global position
     * @param models   the added models
     */
    public ModelFastItemAdapter<Model, Item> addModel(int position, List<Model> models) {
        getModelItemAdapter().addModel(position, models);
        return this;
    }

    /**
     * set a model at a given position
     *
     * @param position the global position
     * @param model    the set model
     */
    public ModelFastItemAdapter<Model, Item> setModel(int position, Model model) {
        getModelItemAdapter().setModel(position, model);
        return this;
    }

    /**
     * clear all models
     */
    public ModelFastItemAdapter<Model, Item> clearModel() {
        getModelItemAdapter().clearModel();
        return this;
    }

    /**
     * moves an model within the list from a position to a position
     *
     * @param fromPosition the position global from which we want to move
     * @param toPosition   the global position to which to move
     * @return this
     */
    public ModelFastItemAdapter<Model, Item> moveModel(int fromPosition, int toPosition) {
        getModelItemAdapter().moveModel(fromPosition, toPosition);
        return this;
    }

    /**
     * remove a range oof model items starting with the (global) position and the size
     *
     * @param position  the global position
     * @param itemCount the count of items which were removed
     */
    public ModelFastItemAdapter<Model, Item> removeModelRange(int position, int itemCount) {
        getModelItemAdapter().removeModelRange(position, itemCount);
        return this;
    }

    /**
     * remove a model at the given (global) position
     *
     * @param position the global position
     */
    public ModelFastItemAdapter<Model, Item> removeModel(int position) {
        getModelItemAdapter().removeModel(position);
        return this;
    }
}
