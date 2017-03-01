/*
 * Copyright 2016 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.ISubItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.utils.IdDistributor;

import java.util.Iterator;
import java.util.List;

/**
 * The RealmBaseRecyclerAdapter class is an abstract utility class for binding RecyclerView UI elements to Realm data.
 * <p>
 * This adapter will automatically handle any updates to its data and call notifyDataSetChanged() as appropriate.
 * Currently there is no support for RecyclerView's data callback methods like notifyItemInserted(int), notifyItemRemoved(int),
 * notifyItemChanged(int) etc.
 * It means that, there is no possibility to use default data animations.
 * <p>
 * The RealmAdapter will stop receiving updates if the Realm instance providing the {@link OrderedRealmCollection} is
 * closed.
 *
 * @param <Item> type of {@link RealmModel} {@link IItem} stored in the adapter.
 */
public class RealmItemAdapter<Item extends RealmModel & IItem> extends ItemAdapter<Item> {

    private final boolean hasAutoUpdates;
    private final OrderedRealmCollectionChangeListener listener;
    @Nullable
    private OrderedRealmCollection<Item> adapterData;

    private OrderedRealmCollectionChangeListener createListener() {
        return new OrderedRealmCollectionChangeListener() {
            @Override
            public void onChange(Object collection, OrderedCollectionChangeSet changeSet) {
                if (collection instanceof List) {
                    List<Item> items = (List<Item>) collection;
                    if (isUseIdDistributor()) {
                        IdDistributor.checkIds(items);
                    }
                    mapPossibleTypes(items);
                }
                // null Changes means the async query returns the first time.
                if (changeSet == null) {
                    notifyDataSetChanged();
                    return;
                }
                // For deletions, the adapter has to be notified in reverse order.
                OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
                for (int i = deletions.length - 1; i >= 0; i--) {
                    OrderedCollectionChangeSet.Range range = deletions[i];
                    notifyItemRangeRemoved(range.startIndex, range.length);
                }

                OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
                for (OrderedCollectionChangeSet.Range range : insertions) {
                    notifyItemRangeInserted(range.startIndex, range.length);
                }

                OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                for (OrderedCollectionChangeSet.Range range : modifications) {
                    notifyItemRangeChanged(range.startIndex, range.length);
                }
            }
        };
    }

    public RealmItemAdapter(@Nullable OrderedRealmCollection<Item> data, boolean autoUpdate) {
        if (data != null && !data.isManaged())
            throw new IllegalStateException("Only use this adapter with managed RealmCollection, " +
                    "for un-managed lists you can just use the BaseRecyclerViewAdapter");
        this.adapterData = data;
        this.hasAutoUpdates = autoUpdate;
        this.listener = hasAutoUpdates ? createListener() : null;
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (hasAutoUpdates && isDataValid()) {
            //noinspection ConstantConditions
            addListener(adapterData);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (hasAutoUpdates && isDataValid()) {
            //noinspection ConstantConditions
            removeListener(adapterData);
        }
    }

    @Override
    public int getAdapterItemCount() {
        //noinspection ConstantConditions
        return isDataValid() ? adapterData.size() : 0;
    }

    /**
     * @return the items within this adapter
     */
    @Override
    public List<Item> getAdapterItems() {
        //noinspection ConstantConditions
        return isDataValid() ? adapterData.subList(0, adapterData.size()) : null;
    }

    @Override
    public int getAdapterPosition(Item item) {
        if (!isDataValid()) {
            return -1;
        }

        //noinspection ConstantConditions
        Iterator<Item> iterator = adapterData.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            if (iterator.next().getIdentifier() == item.getIdentifier()) {
                return count;
            }
            count++;
        }
        return -1;
    }

    /**
     * Returns the item associated with the specified position.
     * Can return {@code null} if provided Realm instance by {@link OrderedRealmCollection} is closed.
     *
     * @param index index of the item.
     * @return the item at the specified position, {@code null} if adapter data is not valid.
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public Item getItem(int index) {
        //noinspection ConstantConditions
        return isDataValid() ? adapterData.get(index) : null;
    }

    /**
     * Returns data associated with this adapter.
     *
     * @return adapter data.
     */
    @Nullable
    public OrderedRealmCollection<Item> getData() {
        return adapterData;
    }

    /**
     * Updates the data associated to the Adapter. Useful when the query has been changed.
     * If the query does not change you might consider using the automaticUpdate feature.
     *
     * @param data the new {@link OrderedRealmCollection} to display.
     */
    @SuppressWarnings("WeakerAccess")
    public void updateData(@Nullable OrderedRealmCollection<Item> data) {
        if (hasAutoUpdates) {
            if (isDataValid()) {
                //noinspection ConstantConditions
                removeListener(adapterData);
            }
            if (data != null) {
                addListener(data);
            }
        }

        this.adapterData = data;
        notifyDataSetChanged();
    }

    private void addListener(@NonNull OrderedRealmCollection<Item> data) {
        if (data instanceof RealmResults) {
            RealmResults<Item> results = (RealmResults<Item>) data;
            //noinspection unchecked
            results.addChangeListener(listener);
        } else if (data instanceof RealmList) {
            RealmList<Item> list = (RealmList<Item>) data;
            //noinspection unchecked
            list.addChangeListener(listener);
        } else {
            throw new IllegalArgumentException("RealmCollection not supported: " + data.getClass());
        }
    }

    private void removeListener(@NonNull OrderedRealmCollection<Item> data) {
        if (data instanceof RealmResults) {
            RealmResults<Item> results = (RealmResults<Item>) data;
            //noinspection unchecked
            results.removeChangeListener(listener);
        } else if (data instanceof RealmList) {
            RealmList<Item> list = (RealmList<Item>) data;
            //noinspection unchecked
            list.removeChangeListener(listener);
        } else {
            throw new IllegalArgumentException("RealmCollection not supported: " + data.getClass());
        }
    }

    private boolean isDataValid() {
        return adapterData != null && adapterData.isValid();
    }

    @Override
    public <T extends IItem & IExpandable<T, S>, S extends IItem & ISubItem<Item, T>> T setSubItems(T collapsible, List<S> subItems) {
        throw new UnsupportedOperationException("this is not supported by the RealmRecyclerViewAdapter");
    }

    @Override
    public ItemAdapter<Item> set(List<Item> items) {
        throw new UnsupportedOperationException("this is not supported by the RealmRecyclerViewAdapter");
    }

    @Override
    public ItemAdapter<Item> setNewList(List<Item> items) {
        throw new UnsupportedOperationException("this is not supported by the RealmRecyclerViewAdapter");
    }

    @Override
    public ItemAdapter<Item> add(List<Item> items) {
        throw new UnsupportedOperationException("this is not supported by the RealmRecyclerViewAdapter");
    }

    @Override
    public ItemAdapter<Item> add(int position, List<Item> items) {
        throw new UnsupportedOperationException("this is not supported by the RealmRecyclerViewAdapter");
    }

    @Override
    public ItemAdapter<Item> set(int position, Item item) {
        throw new UnsupportedOperationException("this is not supported by the RealmRecyclerViewAdapter");
    }

    @Override
    public ItemAdapter<Item> move(int fromPosition, int toPosition) {
        throw new UnsupportedOperationException("this is not supported by the RealmRecyclerViewAdapter");
    }

    @Override
    public ItemAdapter<Item> remove(int position) {
        throw new UnsupportedOperationException("this is not supported by the RealmRecyclerViewAdapter");
    }

    @Override
    public ItemAdapter<Item> removeRange(int position, int itemCount) {
        throw new UnsupportedOperationException("this is not supported by the RealmRecyclerViewAdapter");
    }

    @Override
    public ItemAdapter<Item> clear() {
        if (adapterData != null) {
            adapterData.clear();
        }
        return this;
    }
}