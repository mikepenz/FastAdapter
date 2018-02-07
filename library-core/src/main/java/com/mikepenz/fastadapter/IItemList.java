package com.mikepenz.fastadapter;

import java.util.List;

import javax.annotation.Nullable;

/**
 * The Item list interface
 */

public interface IItemList<Item> {

    int getAdapterPosition(long identifier);

    void remove(int position, int preItemCount);

    void removeRange(int position, int itemCount, int preItemCount);

    void move(int fromPosition, int toPosition, int preItemCount);

    int size();

    void clear(int preItemCount);

    boolean isEmpty();

    void addAll(List<Item> items, int preItemCount);

    void set(int position, Item item, int preItemCount);

    void set(List<Item> items, int preItemCount, @Nullable IAdapterNotifier adapterNotifier);

    void setNewList(List<Item> items, boolean notify);

    void addAll(int position, List<Item> items, int preItemCount);

    List<Item> getItems();

    Item get(int position);
}
