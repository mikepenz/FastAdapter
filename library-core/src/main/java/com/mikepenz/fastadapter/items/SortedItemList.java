package com.mikepenz.fastadapter.items;

import android.support.v7.util.ListUpdateCallback;
import android.support.v7.util.SortedList;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.IItemList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SortedItemList<Item extends IItem> extends IItemList<Item> {
  public interface Callback<Item> {
    int compare(Item lhs, Item rhs);

    boolean isChanged(Item oldItem, Item newItem);

    boolean areEqual(Item o1, Item o2);
  }

  private final Callback<Item> callback;
  private final SortedList<Item> list;

  public SortedItemList(Callback callback, Class<Item> clazz) {
    this(callback, clazz, -1);
  }

  public SortedItemList(Callback callback, Class<Item> clazz, int initialSize) {
    this.callback = callback;
    list = initialSize < 0 ? new SortedList<>(clazz, sortedListCallback) : new SortedList<>(clazz, sortedListCallback, initialSize);
  }

  @Override
  public int size() {
    return list.size();
  }

  @Override
  public Item get(int position) {
    return list.get(position);
  }

  @Override
  public List<Item> getAll() {
    int size = list.size();
    ArrayList<Item> copy = new ArrayList<>(size);
    for(int i = 0; i < size; i++) {
      copy.add(list.get(i));
    }
    return copy;
  }

  @Override
  public void add(Item item) {
    list.add(item);
  }

  @Override
  public void add(int position, Item item) {
    throw new IllegalArgumentException("You can't add at a specific position for a sorted list");
  }

  @Override
  public void addAll(Collection<? extends Item> items) {
    list.addAll(new ArrayList<>(items));
  }

  @Override
  public void addAll(int position, Collection<? extends Item> items) {
    throw new IllegalArgumentException("You can't add at a specific position for a sorted list");
  }

  @Override
  public void set(int position, Item item) {
    throw new IllegalArgumentException("You can't set at a specific position for a sorted list");
  }

  @Override
  public void set(List<? extends Item> items) {
    list.beginBatchedUpdates();
    list.clear();
    list.addAll(new ArrayList<>(items));
    list.endBatchedUpdates();
  }

  @Override
  public void update(Item item) {
    int index = list.indexOf(item);
    if(index != SortedList.INVALID_POSITION) {
      list.updateItemAt(index, item);
    }
  }

  @Override
  public void update(Item oldItem, Item newItem) {
    int index = list.indexOf(oldItem);
    if(index != SortedList.INVALID_POSITION) {
      list.updateItemAt(index, newItem);
    }
  }

  @Override
  public void move(int fromPosition, int toPosition) {
    throw new IllegalArgumentException("You can't manually move items in a sorted list");
  }

  @Override
  public Boolean remove(Item item) {
    return list.remove(item);
  }

  @Override
  public Item remove(int position) {
    return list.removeItemAt(position);
  }

  @Override
  public void removeRange(int fromPosition, int count) {
    int startingPos = Math.min(fromPosition + count, list.size() - 1);
    list.beginBatchedUpdates();
    for(int i = startingPos; i >= fromPosition; i--) {
      list.removeItemAt(i);
    }
    list.endBatchedUpdates();
  }

  @Override
  public void clear() {
    list.clear();
  }

  @Override
  public int getAdapterPosition(Item item) {
    return list.indexOf(item);
  }

  @Override
  public int getAdapterPosition(long identifier) {
    for(int i = 0, size = list.size(); i < size; i++) {
      if(list.get(i).getIdentifier() == identifier) {
        return i;
      }
    }
    return -1;
  }

  private final SortedList.Callback<Item> sortedListCallback = new SortedList.Callback<Item>() {
    @Override
    public int compare(Item o1, Item o2) {
      return callback.compare(o1, o2);
    }

    @Override
    public void onChanged(int position, int count) {
      if(listCallback != null) listCallback.onChanged(position, count, null);
    }

    @Override
    public boolean areContentsTheSame(Item oldItem, Item newItem) {
      return !callback.isChanged(oldItem, newItem);
    }

    @Override
    public boolean areItemsTheSame(Item item1, Item item2) {
      return callback.areEqual(item1, item2);
    }

    @Override
    public void onInserted(int position, int count) {
      if(listCallback != null) listCallback.onInserted(position, count);
    }

    @Override
    public void onRemoved(int position, int count) {
      if(listCallback != null) listCallback.onRemoved(position, count);
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
      if(listCallback != null) listCallback.onMoved(fromPosition, toPosition);
    }
  };
}
