package com.mikepenz.fastadapter;

import android.support.annotation.Nullable;

/**
 * Created by mikepenz on 30.12.15.
 */
public interface IInterceptor<Element, Item extends IItem> {

    IInterceptor<IItem, IItem> DEFAULT = new IInterceptor<IItem, IItem>() {
        @Override
        public IItem intercept(IItem item) {
            return item;
        }
    };

    @Nullable
    Item intercept(Element element);
}
