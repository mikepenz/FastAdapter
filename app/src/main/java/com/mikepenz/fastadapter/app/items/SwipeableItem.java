package com.mikepenz.fastadapter.app.items;

import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter_extensions.drag.IDraggable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;
import com.mikepenz.fastadapter.app.R;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialdrawer.holder.StringHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mattias on 2016-02-15.
 */
public class SwipeableItem extends AbstractItem<SwipeableItem, SwipeableItem.ViewHolder> implements ISwipeable<SwipeableItem, IItem>, IDraggable<SwipeableItem, IItem> {

    public StringHolder name;
    public StringHolder description;

    public StringHolder undoTextSwipeFromRight;
    public StringHolder undoTextSwipeFromLeft;
    public StringHolder undoTextSwipeFromTop;
    public StringHolder undoTextSwipeFromBottom;

    public int swipedDirection;
    private Runnable swipedAction;
    public boolean swipeable = true;
    public boolean draggable = true;

    public SwipeableItem withName(String Name) {
        this.name = new StringHolder(Name);
        return this;
    }

    public SwipeableItem withName(@StringRes int NameRes) {
        this.name = new StringHolder(NameRes);
        return this;
    }

    public SwipeableItem withDescription(String description) {
        this.description = new StringHolder(description);
        return this;
    }

    public SwipeableItem withDescription(@StringRes int descriptionRes) {
        this.description = new StringHolder(descriptionRes);
        return this;
    }

    @Override
    public boolean isSwipeable() {
        return swipeable;
    }

    @Override
    public SwipeableItem withIsSwipeable(boolean swipeable) {
        this.swipeable = swipeable;
        return this;
    }

    @Override
    public boolean isDraggable() {
        return draggable;
    }

    @Override
    public SwipeableItem withIsDraggable(boolean draggable) {
        this.draggable = draggable;
        return this;
    }

    public void setSwipedDirection(int swipedDirection) {
        this.swipedDirection = swipedDirection;
    }

    public void setSwipedAction(Runnable action) {
        this.swipedAction = action;
    }

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    @Override
    public int getType() {
        return R.id.fastadapter_swipable_item_id;
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    @Override
    public int getLayoutRes() {
        return R.layout.swipeable_item;
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param viewHolder the viewHolder of this item
     */
    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        //set the text for the name
        StringHolder.applyTo(name, viewHolder.name);
        //set the text for the description or hide
        StringHolder.applyToOrHide(description, viewHolder.description);

        viewHolder.swipeResultContent.setVisibility(swipedDirection != 0 ? View.VISIBLE : View.GONE);
        viewHolder.itemContent.setVisibility(swipedDirection != 0 ? View.GONE : View.VISIBLE);

        CharSequence swipedAction = null;
        CharSequence swipedText = null;
        if (swipedDirection != 0) {
            swipedAction = viewHolder.itemView.getContext().getString(R.string.action_undo);
            swipedText = swipedDirection == ItemTouchHelper.LEFT ? "Removed" : "Archived";
            viewHolder.swipeResultContent.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), swipedDirection == ItemTouchHelper.LEFT ? R.color.md_red_900 : R.color.md_blue_900));
        }
        viewHolder.swipedAction.setText(swipedAction == null ? "" : swipedAction);
        viewHolder.swipedText.setText(swipedText == null ? "" : swipedText);
        viewHolder.swipedActionRunnable = this.swipedAction;
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.name.setText(null);
        holder.description.setText(null);
        holder.swipedAction.setText(null);
        holder.swipedText.setText(null);
        holder.swipedActionRunnable = null;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    /**
     * our ViewHolder
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.material_drawer_name)
        TextView name;
        @BindView(R.id.material_drawer_description)
        TextView description;
        @BindView(R.id.swipe_result_content)
        View swipeResultContent;
        @BindView(R.id.item_content)
        View itemContent;
        @BindView(R.id.swiped_text)
        TextView swipedText;
        @BindView(R.id.swiped_action)
        TextView swipedAction;

        public Runnable swipedActionRunnable;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            swipedAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (swipedActionRunnable != null) {
                        swipedActionRunnable.run();
                    }
                }
            });
        }
    }
}
