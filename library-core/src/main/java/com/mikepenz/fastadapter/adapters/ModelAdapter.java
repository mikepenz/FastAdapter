package com.mikepenz.fastadapter.adapters;

import android.support.v7.util.ListUpdateCallback;
import com.mikepenz.fastadapter.AbstractAdapter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapterExtension;
import com.mikepenz.fastadapter.IIdDistributor;
import com.mikepenz.fastadapter.IInterceptor;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.IModelItem;
import com.mikepenz.fastadapter.items.SimpleItemList;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by mikepenz on 27.12.15.
 * A general ItemAdapter implementation based on the AbstractAdapter to speed up development for general items
 */
public class ModelAdapter<Model, Item extends IItem> extends AbstractAdapter<Item> implements IItemAdapter<Model, Item> {
    //the items handled and managed by this item
    private final IItemList<Item> mItems;

    public ModelAdapter(IInterceptor<Model, Item> interceptor) {
        this(null, interceptor);
    }

    public ModelAdapter(IItemList<Item> items, IInterceptor<Model, Item> interceptor) {
        mItems = items == null ? new SimpleItemList<Item>() : items;
        this.mInterceptor = interceptor;
    }

    @Override
    public AbstractAdapter<Item> withFastAdapter(FastAdapter<Item> fastAdapter) {
        AbstractAdapter<Item> adapter = super.withFastAdapter(fastAdapter);
        mItems.setListUpdateCallback(listCallback);
        return adapter;
    }

    /**
     * static method to retrieve a new `ItemAdapter`
     *
     * @return a new ItemAdapter
     */
    public static <Model, Item extends IItem> ModelAdapter<Model, Item> models(IInterceptor<Model, Item> interceptor) {
        return new ModelAdapter<>(interceptor);
    }

    private IInterceptor<Model, Item> mInterceptor;

    public IInterceptor<Model, Item> getInterceptor() {
        return mInterceptor;
    }

    public ModelAdapter<Model, Item> withInterceptor(IInterceptor<Model, Item> mInterceptor) {
        this.mInterceptor = mInterceptor;
        return this;
    }

    private IInterceptor<Item, Model> mReverseInterceptor;

    public IInterceptor<Item, Model> getReverseInterceptor() {
        return mReverseInterceptor;
    }

    public ModelAdapter<Model, Item> withReverseInterceptor(IInterceptor<Item, Model> reverseInterceptor) {
        this.mReverseInterceptor = reverseInterceptor;
        return this;
    }

    /**
     * Generates a `Item` based on it's `Model` using the interceptor
     *
     * @param model the `Model` which will be used to create the `Item`
     * @return the generated `Item`
     */
    @Nullable
    public Item intercept(Model model) {
        return mInterceptor.intercept(model);
    }

    /**
     * Generates a List of Item based on it's List of Model using the interceptor
     *
     * @param models the List of Model which will be used to create the List of Item
     * @return the generated List of Item
     */
    public List<Item> intercept(List<Model> models) {
        List<Item> items = new ArrayList<>(models.size());
        Item item;
        for (Model model : models) {
            item = intercept(model);
            if (item == null) continue;
            items.add(item);
        }
        return items;
    }

    private IIdDistributor<Item> mIdDistributor;

    /**
     * defines the idDistributor that is used to provide an ID to all added items
     *
     * @param idDistributor the idDistributor to use
     * @return this
     */
    public ModelAdapter<Model, Item> withIdDistributor(IIdDistributor<Item> idDistributor) {
        this.mIdDistributor = idDistributor;
        return this;
    }

    public IIdDistributor<Item> getIdDistributor() {
        if (mIdDistributor == null) {
            return (IIdDistributor<Item>) IIdDistributor.DEFAULT;
        }
        return mIdDistributor;
    }

    //defines if the DefaultIdDistributor is used to set ID's to all added items
    private boolean mUseIdDistributor = true;

    /**
     * defines if the DefaultIdDistributor is used to provide an ID to all added items which do not yet define an id
     *
     * @param useIdDistributor false if the DefaultIdDistributor shouldn't be used
     * @return this
     */
    public ModelAdapter<Model, Item> withUseIdDistributor(boolean useIdDistributor) {
        this.mUseIdDistributor = useIdDistributor;
        return this;
    }

    /**
     * @return if we use the idDistributor with this adapter
     */
    public boolean isUseIdDistributor() {
        return mUseIdDistributor;
    }

    //filters the items
    private ItemFilter<Model, Item> mItemFilter = new ItemFilter<>(this);

    /**
     * allows you to define your own Filter implementation instead of the default `ItemFilter`
     *
     * @param itemFilter the filter to use
     * @return this
     */
    public ModelAdapter<Model, Item> withItemFilter(ItemFilter<Model, Item> itemFilter) {
        this.mItemFilter = itemFilter;
        return this;
    }

    /**
     * @return the filter used to filter items
     */
    public ItemFilter<Model, Item> getItemFilter() {
        return mItemFilter;
    }

    /**
     * filters the items with the constraint using the provided Predicate
     *
     * @param constraint the string used to filter the list
     */
    public void filter(@Nullable CharSequence constraint) {
        mItemFilter.filter(constraint);
    }

    /**
     * the ModelAdapter does not keep a list of input model's to get retrieve them a `reverseInterceptor` is required
     * usually it is used to get the `Model` from a `IModelItem`
     *
     * @return a List of initial Model's
     */
    public List<Model> getModels() {
        ArrayList<Model> list = new ArrayList<>(mItems.size());
        for (Item item : mItems.getAll()) {
            if (mReverseInterceptor != null) {
                list.add(mReverseInterceptor.intercept(item));
            } else if (item instanceof IModelItem) {
                list.add((Model) ((IModelItem) item).getModel());
            } else {
                throw new RuntimeException("to get the list of models, the item either needs to implement `IModelItem` or you have to provide a `reverseInterceptor`");
            }
        }
        return list;
    }

    /**
     * @return the count of items within this adapter
     */
    @Override
    public int getAdapterItemCount() {
        return mItems.size();
    }

    /**
     * @return the items within this adapter
     */
    @Override
    public List<Item> getAdapterItems() {
        return mItems.getAll();
    }

    /**
     * Searches for the given item and calculates its relative position
     *
     * @param item the item which is searched for
     * @return the relative position
     */
    @Override
    public int getAdapterPosition(Item item) {
        return mItems.getAdapterPosition(item);
    }

    /**
     * Searches for the given identifier and calculates its relative position
     *
     * @param identifier the identifier of an item which is searched for
     * @return the relative position
     */
    @Override
    public int getAdapterPosition(long identifier) {
        return mItems.getAdapterPosition(identifier);
    }

    /**
     * returns the global position if the relative position within this adapter was given
     *
     * @param position the relative position
     * @return the global position
     */
    public int getGlobalPosition(int position) {
        return position + getFastAdapter().getPreItemCountByOrder(getOrder());
    }

    /**
     * @param position the relative position
     * @return the item inside this adapter
     */
    @Override
    public Item getAdapterItem(int position) {
        return mItems.get(position);
    }

    /**
     * set a new list of items and apply it to the existing list (clear - add) for this adapter
     * NOTE may consider using setNewList if the items list is a reference to the list which is used inside the adapter
     *
     * @param items the items to set
     */
    public ModelAdapter<Model, Item> set(List<Model> items) {
        return set(items, true);
    }

    /**
     * set a new list of model items and apply it to the existing list (clear - add) for this adapter
     * NOTE may consider using setNewList if the items list is a reference to the list which is used inside the adapter
     *
     * @param list            the items to set
     * @param resetFilter     `true` if the filter should get reset
     * @return this
     */
    public ModelAdapter<Model, Item> set(List<Model> list, boolean resetFilter) {
        List<Item> items = intercept(list);
        return setInternal(items, resetFilter);
    }

    /**
     * set a new list of model and apply it to the existing list (clear - add) for this adapter
     * NOTE may consider using setNewList if the items list is a reference to the list which is used inside the adapter
     *
     * @param items           the items to set
     * @param resetFilter     `true` if the filter should get reset
     * @return this
     */
    public ModelAdapter<Model, Item> setInternal(List<Item> items, boolean resetFilter) {
        if (mUseIdDistributor) {
            getIdDistributor().checkIds(items);
        }

        //reset the filter
        if (resetFilter && getItemFilter().getConstraint() != null) {
            getItemFilter().performFiltering(null);
        }

        for (IAdapterExtension<Item> ext : getFastAdapter().getExtensions()) {
            ext.set(items, resetFilter);
        }

        mItems.set(items);

        //map the types
        mapPossibleTypes(items);

        return this;
    }

    public IItemAdapter<Model, Item> update(Item item) {
        mItems.update(item);
        return this;
    }

    public IItemAdapter<Model, Item> update(Item oldItem, Item newItem) {
        mItems.update(oldItem, newItem);
        return this;
    }

    /**
     * sets a complete new list of items onto this adapter, using the new list. Calls notifyDataSetChanged
     *
     * @param items the new items to set
     */
    public ModelAdapter<Model, Item> setNewList(List<Model> items) {
        return setNewList(items, false);
    }

    /**
     * sets a complete new list of items onto this adapter, using the new list.
     *
     * @param list         the new items to set
     * @param retainFilter set to true if you want to keep the filter applied
     * @return this
     */
    public ModelAdapter<Model, Item> setNewList(List<Model> list, boolean retainFilter) {
        List<Item> items = intercept(list);

        if (mUseIdDistributor) {
            getIdDistributor().checkIds(items);
        }

        //reset the filter
        CharSequence filter = null;
        if (getItemFilter().getConstraint() != null) {
            filter = getItemFilter().getConstraint();
            getItemFilter().performFiltering(null);
        }

        mItems.set(items);

        mapPossibleTypes(items);

        if (filter != null && retainFilter) {
            getItemFilter().publishResults(filter, getItemFilter().performFiltering(filter));
        }

        return this;
    }

    /**
     * forces to remap all possible types for the RecyclerView
     */
    public void remapMappedTypes() {
        getFastAdapter().clearTypeInstance();
        mapPossibleTypes(mItems.getAll());
    }

    /**
     * add an array of items to the end of the existing items
     *
     * @param items the items to add
     */
    @SafeVarargs
    public final ModelAdapter<Model, Item> add(Model... items) {
        return add(asList(items));
    }

    /**
     * add a list of items to the end of the existing items
     *
     * @param list the items to add
     */
    public ModelAdapter<Model, Item> add(List<Model> list) {
        List<Item> items = intercept(list);
        return addInternal(items);
    }

    public ModelAdapter<Model, Item> addInternal(List<Item> items) {
        if (mUseIdDistributor) {
            getIdDistributor().checkIds(items);
        }

        mItems.addAll(items);
        mapPossibleTypes(items);
        return this;
    }

    /**
     * add an array of items at the given position within the existing items
     *
     * @param position the global position
     * @param items    the items to add
     */
    @SafeVarargs
    public final ModelAdapter<Model, Item> add(int position, Model... items) {
        return add(position, asList(items));
    }

    /**
     * add a list of items at the given position within the existing items
     *
     * @param position the global position
     * @param list     the items to add
     */
    public ModelAdapter<Model, Item> add(int position, List<Model> list) {
        List<Item> items = intercept(list);
        return addInternal(position, items);
    }

    public ModelAdapter<Model, Item> addInternal(int position, List<Item> items) {
        if (mUseIdDistributor) {
            getIdDistributor().checkIds(items);
        }
        if (items != null && items.size() > 0) {
            mItems.addAll(position - getFastAdapter().getPreItemCountByOrder(getOrder()), items);
            mapPossibleTypes(items);
        }
        return this;
    }

    /**
     * sets an item at the given position, overwriting the previous item
     *
     * @param position the global position
     * @param element  the item to set
     */
    public ModelAdapter<Model, Item> set(int position, Model element) {
        Item item = intercept(element);
        if (item == null) return this;
        return setInternal(position, item);
    }

    public ModelAdapter<Model, Item> setInternal(int position, Item item) {
        if (mUseIdDistributor) {
            getIdDistributor().checkId(item);
        }
        mItems.set(position - getFastAdapter().getPreItemCount(position), item);
        mFastAdapter.registerTypeInstance(item);
        return this;
    }

    /**
     * moves an item within the list from a position to a position
     *
     * @param fromPosition the position global from which we want to move
     * @param toPosition   the global position to which to move
     * @return this
     */
    public ModelAdapter<Model, Item> move(int fromPosition, int toPosition) {
        int preItemCount = getFastAdapter().getPreItemCount(fromPosition);
        mItems.move(fromPosition - preItemCount, toPosition - preItemCount);
        return this;
    }

    /**
     * removes an item at the given position within the existing icons
     *
     * @param position the global position
     */
    public ModelAdapter<Model, Item> remove(int position) {
        mItems.remove(position - getFastAdapter().getPreItemCount(position));
        return this;
    }

    /**
     * removes a range of items starting with the given position within the existing icons
     *
     * @param position  the global position
     * @param itemCount the count of items which were removed
     */
    public ModelAdapter<Model, Item> removeRange(int position, int itemCount) {
        int preItemCount = getFastAdapter().getPreItemCount(position);

        mItems.removeRange(position - preItemCount, itemCount);
        return this;
    }

    /**
     * removes all items of this adapter
     */
    public ModelAdapter<Model, Item> clear() {
        mItems.clear();
        return this;
    }

    private final ListUpdateCallback listCallback = new ListUpdateCallback() {
        @Override
        public void onInserted(int position, int count) {
            mFastAdapter.notifyAdapterItemRangeInserted(getGlobalPosition(position), count);
        }

        @Override
        public void onRemoved(int position, int count) {
            mFastAdapter.notifyAdapterItemRangeRemoved(getGlobalPosition(position), count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            mFastAdapter.notifyAdapterItemMoved(getGlobalPosition(fromPosition), getGlobalPosition(toPosition));
        }

        @Override
        public void onChanged(int position, int count, Object payload) {
            mFastAdapter.notifyAdapterItemRangeChanged(getGlobalPosition(position), count, payload);
        }
    };
}
