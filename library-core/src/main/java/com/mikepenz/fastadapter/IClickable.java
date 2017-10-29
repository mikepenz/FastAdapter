package com.mikepenz.fastadapter;

import com.mikepenz.fastadapter.listeners.OnClickListener;

/**
 * Created by mikepenz on 25.02.16.
 */
public interface IClickable<Item extends IItem> {
    Item withOnItemPreClickListener(OnClickListener<Item> onItemPreClickListener);

    OnClickListener<Item> getOnPreItemClickListener();

    Item withOnItemClickListener(OnClickListener<Item> onItemClickListener);

    OnClickListener<Item> getOnItemClickListener();
}
