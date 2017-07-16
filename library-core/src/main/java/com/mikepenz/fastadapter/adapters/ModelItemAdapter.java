package com.mikepenz.fastadapter.adapters;

import android.support.annotation.Nullable;

import com.mikepenz.fastadapter.IModelItem;
import com.mikepenz.fastadapter.utils.Function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by mikepenz on 27.12.15.
 * A general ItemAdapter implementation based on the AbstractAdapter to speed up development for general items
 * This adapter has the order of 500 which is the centered order
 */
public class ModelItemAdapter<Model, Item extends IModelItem<? extends Model, ?, ?>> extends ItemAdapter<Item> {
    private final Function<Model, Item> mItemFactory;

    /**
     * @param itemFactory a factory that takes a model as an argument and returns an item as a result
     */
    public ModelItemAdapter(Function<Model, Item> itemFactory) {
        this.mItemFactory = itemFactory;
    }

    /**
     * returns the list of the model generated from the list of items
     *
     * @return the list with all model objects
     */
    public List<Model> getModels() {
        int size = getAdapterItems().size();
        List<Model> models = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            models.add(getAdapterItems().get(i).getModel());
        }
        return models;
    }

    /**
     * set a new list of models for this adapter
     *
     * @param models the set models
     */
    public ModelItemAdapter<Model, Item> setModel(List<Model> models) {
        super.set(toItems(models));
        return this;
    }

    /**
     * sets a complete new list of items onto this adapter, using the new list. Calls notifyDataSetChanged
     *
     * @param models the set models
     */
    public ModelItemAdapter<Model, Item> setNewModel(List<Model> models) {
        super.setNewList(toItems(models));
        return this;
    }

    /**
     * add an array of models
     *
     * @param models the added models
     */
    @SafeVarargs
    public final ModelItemAdapter<Model, Item> addModel(Model... models) {
        addModel(asList(models));
        return this;
    }

    /**
     * add a list of models
     *
     * @param models the added models
     */
    public ModelItemAdapter<Model, Item> addModel(List<Model> models) {
        super.add(toItems(models));
        return this;
    }

    /**
     * add an array of models at a given (global) position
     *
     * @param position the global position
     * @param models   the added models
     */
    @SafeVarargs
    public final ModelItemAdapter<Model, Item> addModel(int position, Model... models) {
        addModel(position, asList(models));
        return this;
    }

    /**
     * add a list of models at a given (global) position
     *
     * @param position the global position
     * @param models   the added models
     */
    public ModelItemAdapter<Model, Item> addModel(int position, List<Model> models) {
        super.add(position, toItems(models));
        return this;
    }

    /**
     * set a model at a given position
     *
     * @param position the global position
     * @param model    the set model
     */
    public ModelItemAdapter<Model, Item> setModel(int position, Model model) {
        Item item = toItem(model);
        if (item != null) {
            super.set(position, item);
        }
        return this;
    }

    /**
     * clear all models
     */
    public ModelItemAdapter<Model, Item> clearModel() {
        super.clear();
        return this;
    }

    /**
     * moves an model within the list from a position to a position
     *
     * @param fromPosition the position global from which we want to move
     * @param toPosition   the global position to which to move
     * @return this
     */
    public ModelItemAdapter<Model, Item> moveModel(int fromPosition, int toPosition) {
        super.move(fromPosition, toPosition);
        return this;
    }


    /**
     * remove a range oof model items starting with the (global) position and the size
     *
     * @param position  the global position
     * @param itemCount the count of items which were removed
     */
    public ModelItemAdapter<Model, Item> removeModelRange(int position, int itemCount) {
        super.removeRange(position, itemCount);
        return this;
    }

    /**
     * remove a model at the given (global) position
     *
     * @param position the global position
     */
    public ModelItemAdapter<Model, Item> removeModel(int position) {
        super.remove(position);
        return this;
    }

    /**
     * helper to get a list of item from a list o model
     *
     * @param models the models
     * @return the list of items referencing the models
     */
    protected List<Item> toItems(List<Model> models) {
        if (models == null) {
            return Collections.emptyList();
        }

        int size = models.size();
        List<Item> items = new ArrayList<>(size);
        Item item;
        for (int i = 0; i < size; i++) {
            item = toItem(models.get(i));
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }

    /**
     * gets an instance of our TypedItem from a Model
     *
     * @param model the model class we want to wrap into a typedItem
     * @return our typedItem
     */
    @Nullable
    protected Item toItem(Model model) {
        return mItemFactory.apply(model);
    }
}
