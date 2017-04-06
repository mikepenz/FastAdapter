package com.mikepenz.fastadapter.app.items;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.app.R;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.materialdrawer.holder.StringHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mikepenz on 28.12.15.
 */
public class CheckBoxSampleItem extends AbstractItem<CheckBoxSampleItem, CheckBoxSampleItem.ViewHolder> {

    public String header;
    public StringHolder name;
    public StringHolder description;

    public CheckBoxSampleItem withHeader(String header) {
        this.header = header;
        return this;
    }

    public CheckBoxSampleItem withName(String Name) {
        this.name = new StringHolder(Name);
        return this;
    }

    public CheckBoxSampleItem withName(@StringRes int NameRes) {
        this.name = new StringHolder(NameRes);
        return this;
    }

    public CheckBoxSampleItem withDescription(String description) {
        this.description = new StringHolder(description);
        return this;
    }

    public CheckBoxSampleItem withDescription(@StringRes int descriptionRes) {
        this.description = new StringHolder(descriptionRes);
        return this;
    }

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    @Override
    public int getType() {
        return R.id.fastadapter_checkbox_sample_item_id;
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    @Override
    public int getLayoutRes() {
        return R.layout.checkbox_sample_item;
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param viewHolder the viewHolder of this item
     */
    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        viewHolder.checkBox.setChecked(isSelected());

        //set the text for the name
        StringHolder.applyTo(name, viewHolder.name);
        //set the text for the description or hide
        StringHolder.applyToOrHide(description, viewHolder.description);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.name.setText(null);
        holder.description.setText(null);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    /**
     * our ViewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected View view;
        @BindView(R.id.checkbox)
        public CheckBox checkBox;
        @BindView(R.id.material_drawer_name)
        TextView name;
        @BindView(R.id.material_drawer_description)
        TextView description;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }

    public static class CheckBoxClickEvent extends ClickEventHook<CheckBoxSampleItem> {
        @Override
        public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof CheckBoxSampleItem.ViewHolder) {
                return ((CheckBoxSampleItem.ViewHolder) viewHolder).checkBox;
            }
            return null;
        }

        @Override
        public void onClick(View v, int position, FastAdapter<CheckBoxSampleItem> fastAdapter, CheckBoxSampleItem item) {
            fastAdapter.toggleSelection(position);
        }
    }
}
