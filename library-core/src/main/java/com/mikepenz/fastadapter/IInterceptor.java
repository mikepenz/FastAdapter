package com.mikepenz.fastadapter;

/**
 * Created by mikepenz on 30.12.15.
 */
public interface IInterceptor<Element, Item extends IItem> {
    Item intercept(Element element);
}
