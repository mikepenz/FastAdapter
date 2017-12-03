package com.mikepenz.fastadapter.adapters;

import android.support.v7.util.ListUpdateCallback;
import com.mikepenz.fastadapter.IItem;

import java.util.Collection;
import java.util.List;

public abstract class IItemList<Item extends IItem> {
  protected ListUpdateCallback listCallback;

  void setListUpdateCallback(ListUpdateCallback listCallback) {
    this.listCallback = listCallback;

    int size = size();
    if(size > 0) {
      listCallback.onInserted(0, size);
    }
  }

  public abstract int size();

  public abstract Item get(int position);
  public abstract List<Item> getAll();

  public abstract void add(Item item);
  public abstract void add(int position, Item item);
  public abstract void addAll(Collection<? extends Item> items);
  public abstract void addAll(int position, Collection<? extends Item> items);

  public abstract void set(int position, Item item);
  public abstract void set(List<? extends Item> items);

  public abstract void update(Item item);
  public abstract void update(Item oldItem, Item newItem);

  public abstract void move(int fromPosition, int toPosition);

  public abstract Boolean remove(Item item);
  public abstract Item remove(int position);
  public abstract void removeRange(int fromPosition, int count);
  public abstract void clear();

  public abstract int getAdapterPosition(Item item);
  public abstract int getAdapterPosition(long identifier);
}
