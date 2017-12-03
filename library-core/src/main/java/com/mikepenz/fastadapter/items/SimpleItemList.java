package com.mikepenz.fastadapter.items;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.IItemList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class SimpleItemList<Item extends IItem> extends IItemList<Item> {
  private final List<Item> list;

  public SimpleItemList() {
    this(-1);
  }
  
  public SimpleItemList(int initialSize) {
    list = initialSize < 0 ? new ArrayList<Item>() : new ArrayList<Item>(initialSize);
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
    return new ArrayList<>(list);
  }

  @Override
  public void add(Item item) {
    list.add(item);
    if(listCallback != null) listCallback.onInserted(list.size() - 1, 1);
  }

  @Override
  public void add(int position, Item item) {
    list.add(position, item);
    if(listCallback != null) listCallback.onInserted(position, 1);
  }

  @Override
  public void addAll(Collection<? extends Item> items) {
    int insertionPos = list.size();
    list.addAll(items);
    if(listCallback != null) listCallback.onInserted(insertionPos, items.size());
  }

  @Override
  public void addAll(int position, Collection<? extends Item> items) {
    list.addAll(position, items);
    if(listCallback != null) listCallback.onInserted(position, items.size());
  }

  @Override
  public void set(int position, Item item) {
    list.set(position, item);
    if(listCallback != null) listCallback.onChanged(position, 1, null);
  }

  @Override
  public void set(List<? extends Item> items) {
    int newSize = items.size();
    int currentSize = list.size();

    list.clear();
    list.addAll(items);

    if(listCallback != null) {
      if(newSize > currentSize) {
        if(currentSize > 0) {
          listCallback.onChanged(0, currentSize, null);
        }
        listCallback.onInserted(currentSize, newSize - currentSize);
      }
      else if(newSize == 0) {
        listCallback.onRemoved(0, currentSize);
      }
      else if(newSize < currentSize) {
        listCallback.onChanged(0, newSize, null);
        listCallback.onRemoved(newSize, currentSize - newSize);
      }
      else {
        listCallback.onChanged(0, newSize, null);
      }
    }
  }

  @Override
  public void update(Item item) {
    int pos = getAdapterPosition(item);
    if(pos >= 0) {
      list.set(pos, item);
      if(listCallback != null) listCallback.onChanged(pos, 1, null);
    }
  }

  @Override
  public void update(Item oldItem, Item newItem) {
    int pos = getAdapterPosition(oldItem);
    if(pos >= 0) {
      list.set(pos, newItem);
      if(listCallback != null) listCallback.onChanged(pos, 1, null);
    }
  }

  @Override
  public void move(int fromPosition, int toPosition) {
    Item item = list.remove(fromPosition);
    list.add(toPosition, item);
    if(listCallback != null) listCallback.onMoved(fromPosition, toPosition);
  }

  @Override
  public Boolean remove(Item item) {
    int pos = getAdapterPosition(item);
    if(pos >= 0) {
      list.remove(pos);
      if(listCallback != null) listCallback.onRemoved(pos, 1);
      return true;
    }
    else {
      return false;
    }
  }

  @Override
  public Item remove(int position) {
    Item item = list.remove(position);
    if(item != null) {
      if(listCallback != null) listCallback.onRemoved(position, 1);
    }
    return item;
  }

  @Override
  public void removeRange(int fromPosition, int count) {
    int startingPos = Math.min(fromPosition + count, list.size() - 1);
    for(int i = startingPos; i >= fromPosition; i--) {
      list.remove(i);
    }
  }

  @Override
  public void clear() {
    int size = list.size();
    list.clear();
    if(listCallback != null) listCallback.onRemoved(0, size);
  }

  @Override
  public int getAdapterPosition(Item item) {
    return getAdapterPosition(item.getIdentifier());
  }

  @Override
  public int getAdapterPosition(long identifier) {
    for(int i = 0, size = list.size(); i < size; i++) {
      Item item = list.get(i);
      if(item.getIdentifier() == identifier) {
        return i;
      }
    }

    return -1;
  }
}
