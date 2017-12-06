package com.mikepenz.fastadapter;

import java.util.List;

/**
 * The Item list interface
 */

public interface IItemList<Item> {

    int getAdapterPosition(long identifier);

    void remove(int position, int preItemCount);

    int removeRange(int position, int itemCount, int preItemCount);

    void move(int fromPosition, int toPosition, int preItemCount);

    int size();

    void clear();

    boolean isEmpty();

    void addAll(List<Item> items);

    void set(int position, Item item);

    void setNewList(List<Item> items);

    void addAll(int position, List<Item> items);

    List<Item> getItems();

    Item get(int position);
}
