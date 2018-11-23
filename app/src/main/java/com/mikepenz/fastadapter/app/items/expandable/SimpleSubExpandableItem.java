package com.mikepenz.fastadapter.app.items.expandable;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IClickable;
import com.mikepenz.fastadapter.ISubItem;
import com.mikepenz.fastadapter.app.R;
import com.mikepenz.fastadapter.ui.utils.FastAdapterUIUtils;
import com.mikepenz.fastadapter.expandable.items.AbstractExpandableItem;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.materialdrawer.holder.StringHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mikepenz on 28.12.15.
 */
public class SimpleSubExpandableItem extends AbstractExpandableItem<SimpleSubExpandableItem.ViewHolder> implements IClickable<SimpleSubExpandableItem>, ISubItem<SimpleSubExpandableItem.ViewHolder> {

    public String header;
    public StringHolder name;
    public StringHolder description;

    private OnClickListener<SimpleSubExpandableItem> mOnClickListener;

    public SimpleSubExpandableItem withHeader(String header) {
        this.header = header;
        return this;
    }

    public SimpleSubExpandableItem withName(String Name) {
        this.name = new StringHolder(Name);
        return this;
    }

    public SimpleSubExpandableItem withName(@StringRes int NameRes) {
        this.name = new StringHolder(NameRes);
        return this;
    }

    public SimpleSubExpandableItem withDescription(String description) {
        this.description = new StringHolder(description);
        return this;
    }

    public SimpleSubExpandableItem withDescription(@StringRes int descriptionRes) {
        this.description = new StringHolder(descriptionRes);
        return this;
    }

    @Override
    public void setOnItemClickListener(OnClickListener<SimpleSubExpandableItem> onClickListener) {
        mOnClickListener = onClickListener;
    }

    //we define a clickListener in here so we can directly animate
    final private OnClickListener<SimpleSubExpandableItem> onClickListener = new OnClickListener<SimpleSubExpandableItem>() {
        @Override
        public boolean onClick(View v, IAdapter adapter, @NonNull SimpleSubExpandableItem item, int position) {
            if (item.getSubItems() != null) {
                if (!item.isExpanded()) {
                    ViewCompat.animate(v.findViewById(R.id.material_drawer_icon)).rotation(180).start();
                } else {
                    ViewCompat.animate(v.findViewById(R.id.material_drawer_icon)).rotation(0).start();
                }
                return mOnClickListener == null || mOnClickListener.onClick(v, adapter, item, position);
            }
            return mOnClickListener != null && mOnClickListener.onClick(v, adapter, item, position);
        }
    };

    /**
     * we overwrite the item specific click listener so we can automatically animate within the item
     *
     * @return
     */
    @Override
    public OnClickListener<SimpleSubExpandableItem> getOnItemClickListener() {
        return onClickListener;
    }

    @Override
    public OnClickListener<SimpleSubExpandableItem> getOnPreItemClickListener() {
        return null;
    }

    @Override
    public void setOnPreItemClickListener(OnClickListener<SimpleSubExpandableItem> onClickListener) {

    }

    @Override
    public boolean isSelectable() {
        //this might not be true for your application
        return getSubItems() == null;
    }

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    @Override
    public int getType() {
        return R.id.fastadapter_expandable_item_id;
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    @Override
    public int getLayoutRes() {
        return R.layout.expandable_item;
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param viewHolder the viewHolder of this item
     */
    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        //get the context
        Context ctx = viewHolder.itemView.getContext();

        //set the background for the item
        viewHolder.view.clearAnimation();
        ViewCompat.setBackground(viewHolder.view, FastAdapterUIUtils.getSelectableBackground(ctx, Color.RED, true));
        //set the text for the name
        StringHolder.applyTo(name, viewHolder.name);
        //set the text for the description or hide
        StringHolder.applyToOrHide(description, viewHolder.description);

        if (getSubItems() == null || getSubItems().size() == 0) {
            viewHolder.icon.setVisibility(View.GONE);
        } else {
            viewHolder.icon.setVisibility(View.VISIBLE);
        }

        if (isExpanded()) {
            viewHolder.icon.setRotation(0);
        } else {
            viewHolder.icon.setRotation(180);
        }
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.name.setText(null);
        holder.description.setText(null);
        //make sure all animations are stopped
        holder.icon.clearAnimation();
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    /**
     * our ViewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        @BindView(R.id.material_drawer_name)
        public
        TextView name;
        @BindView(R.id.material_drawer_description)
        public
        TextView description;
        @BindView(R.id.material_drawer_icon)
        ImageView icon;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }
}
