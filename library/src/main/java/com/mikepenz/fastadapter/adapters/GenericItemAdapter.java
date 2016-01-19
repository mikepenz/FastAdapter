package com.mikepenz.fastadapter.adapters;

import com.mikepenz.fastadapter.items.GenericAbstractItem;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by mikepenz on 27.12.15.
 * A general ItemAdapter implementation based on the AbstractAdapter to speed up development for general items
 * This adapter has the order of 500 which is the centered order
 */
public class GenericItemAdapter<Model, Item extends GenericAbstractItem> extends ItemAdapter<Item> {
    private List<Model> mItems = new ArrayList<>();
    private Class<Model> modelClass;
    private Class<Item> itemClass;

    /**
     * @param itemClass  the class of your item (Item extends GenericAbstractItem)
     * @param modelClass the class which is your model class
     */
    public GenericItemAdapter(Class<Item> itemClass, Class<Model> modelClass) {
        this.itemClass = itemClass;
        this.modelClass = modelClass;
    }


    /**
     * set a new list of models for this adapter
     *
     * @param models
     */
    public void setModel(List<Model> models) {
        super.set(toItems(models));
        mItems = models;
    }

    /**
     * add an array of models
     *
     * @param models
     */
    public void addModel(Model... models) {
        super.add(toItems(models));
        Collections.addAll(mItems, models);
    }

    /**
     * add a list of models
     *
     * @param models
     */
    public void addModel(List<Model> models) {
        super.add(toItems(models));
        mItems.addAll(models);
    }

    /**
     * add an array of models at a given (global) position
     *
     * @param position
     * @param models
     */
    public void addModel(int position, Model... models) {
        super.add(position, toItems(models));
        mItems.addAll(position - getFastAdapter().getItemCount(getOrder()), Arrays.asList(models));
    }

    /**
     * add a list of models at a given (global) position
     *
     * @param position
     * @param models
     */
    public void addModel(int position, List<Model> models) {
        super.add(position, toItems(models));
        mItems.addAll(position - getFastAdapter().getItemCount(getOrder()), models);
    }

    /**
     * set a model at a given position
     *
     * @param position
     * @param model
     */
    public void setModel(int position, Model model) {
        super.set(position, getAbstractItem(model));
        mItems.set(position - getFastAdapter().getItemCount(getOrder()), model);
    }

    /**
     * add a model at the end of the list
     *
     * @param model
     */
    public void addModel(Model model) {
        super.add(getAbstractItem(model));
        mItems.add(model);
    }

    /**
     * add a model at the given (global) position
     *
     * @param position
     * @param model
     */
    public void addModel(int position, Model model) {
        super.add(position, getAbstractItem(model));
        mItems.add(position - getFastAdapter().getItemCount(getOrder()), model);
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
     * @param position
     * @param itemCount
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
     * @param position
     */
    public void removeModel(int position) {
        super.remove(position);
        mItems.remove(position - getFastAdapter().getItemCount(getOrder()));
    }

    /**
     * helper to get a list of item from a list o model
     *
     * @param models
     * @return
     */
    private List<Item> toItems(List<Model> models) {
        ArrayList<Item> items = new ArrayList<>();
        if (models != null) {
            for (Model model : models) {
                items.add(getAbstractItem(model));
            }
        }
        return items;
    }

    /**
     * helper to get a list of item from a list of model
     *
     * @param models
     * @return
     */
    private List<Item> toItems(Model... models) {
        ArrayList<Item> items = new ArrayList<>();
        if (models != null) {
            for (Model model : models) {
                items.add(getAbstractItem(model));
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
    private Item getAbstractItem(Model model) {
        try {
            return itemClass.getDeclaredConstructor(modelClass).newInstance(model);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
