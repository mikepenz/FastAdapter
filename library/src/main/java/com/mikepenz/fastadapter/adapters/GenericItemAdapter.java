package com.mikepenz.fastadapter.adapters;

import com.mikepenz.fastadapter.items.GenericAbstractItem;
import com.mikepenz.fastadapter.utils.Function;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by mikepenz on 27.12.15.
 * A general ItemAdapter implementation based on the AbstractAdapter to speed up development for general items
 * This adapter has the order of 500 which is the centered order
 */
public class GenericItemAdapter<Model, Item extends GenericAbstractItem<Model, Item, ?>> extends ItemAdapter<Item> {
    private final Function<Model, Item> mItemFactory;
    private List<Model> mItems = new ArrayList<>();

    /**
     * @param itemClass  the class of your item (Item extends GenericAbstractItem)
     * @param modelClass the class which is your model class
     */
    public GenericItemAdapter(Class<? extends Item> itemClass, Class<? extends Model> modelClass) {
        this(new ReflectionBasedItemFactory<>(modelClass, itemClass));
    }

    /**
     * @param itemFactory a factory that takes a model as an argument and returns an item as a result
     */
    public GenericItemAdapter(Function<Model, Item> itemFactory) {
        this.mItemFactory = itemFactory;
    }

    /**
     * set a new list of models for this adapter
     *
     * @param models the set models
     */
    public void setModel(List<Model> models) {
        super.set(toItems(models));
        mItems = models;
    }

    /**
     * sets a complete new list of items onto this adapter, using the new list. Calls notifyDataSetChanged
     *
     * @param models the set models
     */
    public void setNewModel(List<Model> models) {
        super.setNewList(toItems(models));
        mItems = models;
    }

    /**
     * add an array of models
     *
     * @param models the added models
     */
    @SafeVarargs
    public final void addModel(Model... models) {
        addModel(asList(models));
    }

    /**
     * add a list of models
     *
     * @param models the added models
     */
    public void addModel(List<Model> models) {
        super.add(toItems(models));
        mItems.addAll(models);
    }

    /**
     * add an array of models at a given (global) position
     *
     * @param position the global position
     * @param models   the added models
     */
    @SafeVarargs
    public final void addModel(int position, Model... models) {
        addModel(position, asList(models));
    }

    /**
     * add a list of models at a given (global) position
     *
     * @param position the global position
     * @param models   the added models
     */
    public void addModel(int position, List<Model> models) {
        super.add(position, toItems(models));
        mItems.addAll(position - getFastAdapter().getItemCount(getOrder()), models);
    }

    /**
     * set a model at a given position
     *
     * @param position the global position
     * @param model    the set model
     */
    public void setModel(int position, Model model) {
        super.set(position, toItem(model));
        mItems.set(position - getFastAdapter().getItemCount(getOrder()), model);
    }

    /**
     * clear all models
     */
    public void clearModel() {
        super.clear();
        mItems.clear();
    }

    /**
     * remove a range oof model items starting with the (global) position and the size
     *
     * @param position  the global position
     * @param itemCount the count of items which were removed
     */
    public void removeModelRange(int position, int itemCount) {
        super.removeRange(position, itemCount);

        //global position to relative
        int length = mItems.size();
        //make sure we do not delete to many items
        int saveItemCount = Math.min(itemCount, length - position + getFastAdapter().getItemCount(getOrder()));

        for (int i = 0; i < saveItemCount; i++) {
            mItems.remove(position - getFastAdapter().getItemCount(getOrder()));
        }
    }

    /**
     * remove a model at the given (global) position
     *
     * @param position the global position
     */
    public void removeModel(int position) {
        super.remove(position);
        mItems.remove(position - getFastAdapter().getItemCount(getOrder()));
    }

    /**
     * helper to get a list of item from a list o model
     *
     * @param models the models
     * @return the list of items referencing the models
     */
    protected List<Item> toItems(List<Model> models) {
        List<Item> items = new ArrayList<>();
        if (models != null) {
            for (Model model : models) {
                items.add(toItem(model));
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
    protected Item toItem(Model model) {
        return mItemFactory.apply(model);
    }

    protected static class ReflectionBasedItemFactory<Model, Item> implements Function<Model, Item> {
        private final Class<? extends Model> modelClass;
        private final Class<? extends Item> itemClass;

        public ReflectionBasedItemFactory(Class<? extends Model> modelClass, Class<? extends Item> itemClass) {
            this.modelClass = modelClass;
            this.itemClass = itemClass;
        }

        @Override
        public Item apply(Model model) {
            try {
                Constructor<? extends Item> constructor = itemClass.getDeclaredConstructor(modelClass);
                constructor.setAccessible(true);
                return constructor.newInstance(model);
            } catch (Exception e) {
                throw new RuntimeException("Please provide a constructor that takes a model as an argument");
            }
        }
    }
}
