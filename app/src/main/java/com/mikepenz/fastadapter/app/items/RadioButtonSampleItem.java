package com.mikepenz.fastadapter.app.items;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.app.R;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.materialdrawer.holder.StringHolder;

import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mikepenz on 28.12.15.
 */
public class RadioButtonSampleItem extends AbstractItem<RadioButtonSampleItem, RadioButtonSampleItem.ViewHolder> {

    public String header;
    public StringHolder name;
    public StringHolder description;

    public RadioButtonSampleItem withHeader(String header) {
        this.header = header;
        return this;
    }

    public RadioButtonSampleItem withName(String Name) {
        this.name = new StringHolder(Name);
        return this;
    }

    public RadioButtonSampleItem withName(@StringRes int NameRes) {
        this.name = new StringHolder(NameRes);
        return this;
    }

    public RadioButtonSampleItem withDescription(String description) {
        this.description = new StringHolder(description);
        return this;
    }

    public RadioButtonSampleItem withDescription(@StringRes int descriptionRes) {
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
        return R.id.fastadapter_radiobutton_sample_item_id;
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    @Override
    public int getLayoutRes() {
        return R.layout.radiobutton_sample_item;
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param viewHolder the viewHolder of this item
     */
    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        viewHolder.radioButton.setChecked(isSelected());

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
        @BindView(R.id.radiobutton)
        public RadioButton radioButton;
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

    public static class RadioButtonClickEvent extends ClickEventHook<RadioButtonSampleItem> {
        @Override
        public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof RadioButtonSampleItem.ViewHolder) {
                return ((ViewHolder) viewHolder).radioButton;
            }
            return null;
        }

        @Override
        public void onClick(View v, int position, FastAdapter<RadioButtonSampleItem> fastAdapter, RadioButtonSampleItem item) {
            if (!item.isSelected()) {
                Set<Integer> selections = fastAdapter.getSelections();
                if (!selections.isEmpty()) {
                    int selectedPosition = selections.iterator().next();
                    fastAdapter.deselect();
                    fastAdapter.notifyItemChanged(selectedPosition);
                }
                fastAdapter.select(position);
            }
        }
    }
}
