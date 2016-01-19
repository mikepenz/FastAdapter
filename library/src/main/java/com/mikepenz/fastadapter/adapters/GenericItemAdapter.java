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
    List<Model> mItems = new ArrayList<>();

    Class<Model> modelClass;
    Class<Item> itemClass;

    public GenericItemAdapter(Class<Item> itemClass, Class<Model> modelClass) {
        this.itemClass = itemClass;
        this.modelClass = modelClass;
    }

    public void setModel(List<Model> models) {
        super.set(toItems(models));
        mItems = models;
    }

    public void addModel(Model... models) {
        super.add(toItems(models));
        Collections.addAll(mItems, models);
    }

    public void addModel(List<Model> models) {
        super.add(toItems(models));
        mItems.addAll(models);
    }

    public void addModel(int position, Model... models) {
        super.add(position, toItems(models));
        mItems.addAll(position - getFastAdapter().getItemCount(getOrder()), Arrays.asList(models));
    }

    public void addModel(int position, List<Model> models) {
        super.add(position, toItems(models));
        mItems.addAll(position - getFastAdapter().getItemCount(getOrder()), models);
    }

    public void setModel(int position, Model model) {
        super.set(position, getAbstractItem(model));
        mItems.set(position - getFastAdapter().getItemCount(getOrder()), model);
    }

    public void addModel(Model model) {
        super.add(getAbstractItem(model));
        mItems.add(model);
    }

    public void addModel(int position, Model model) {
        super.add(position, getAbstractItem(model));
        mItems.add(position - getFastAdapter().getItemCount(getOrder()), model);
    }

    public void clearModel() {
        super.clear();
        mItems.clear();
    }

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
