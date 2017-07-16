package com.mikepenz.fastadapter.expandable.items;

import android.support.v7.widget.RecyclerView;

import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.ISubItem;
import com.mikepenz.fastadapter.items.ModelAbstractItem;

import java.util.List;

public abstract class ModelAbstractExpandableItem<Model, Parent extends ModelAbstractItem<Model, Parent, VH> & IExpandable & ISubItem, VH extends RecyclerView.ViewHolder, SubItem extends IItem & ISubItem> extends ModelAbstractItem<Model, Parent, VH> implements IExpandable<ModelAbstractExpandableItem, SubItem>, ISubItem<ModelAbstractExpandableItem, Parent> {

    private List<SubItem> mSubItems;
    private Parent mParent;
    private boolean mExpanded = false;

    public ModelAbstractExpandableItem(Model model) {
        super(model);
    }

    @Override
    public boolean isExpanded() {
        return mExpanded;
    }

    @Override
    public ModelAbstractExpandableItem<Model, Parent, VH, SubItem> withIsExpanded(boolean expanded) {
        mExpanded = expanded;
        return this;
    }

    @Override
    public List<SubItem> getSubItems() {
        return mSubItems;
    }

    @Override
    public boolean isAutoExpanding() {
        return true;
    }

    @Override
    public ModelAbstractExpandableItem<Model, Parent, VH, SubItem> withSubItems(List<SubItem> subItems) {
        this.mSubItems = subItems;
        for (SubItem subItem : subItems) {
            subItem.withParent(this);
        }
        return this;
    }

    @Override
    public Parent getParent() {
        return mParent;
    }

    @Override
    public ModelAbstractExpandableItem<Model, Parent, VH, SubItem> withParent(Parent parent) {
        this.mParent = parent;
        return this;
    }

    @Override
    public boolean isSelectable() {
        //this might not be true for your application
        return getSubItems() == null;
    }
}
