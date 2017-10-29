package com.mikepenz.fastadapter.app.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.app.R;
import com.mikepenz.fastadapter.items.ModelAbstractItem;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mikepenz on 28.12.15.
 */
public class ModelIconItem extends ModelAbstractItem<com.mikepenz.fastadapter.app.model.IconModel, ModelIconItem, ModelIconItem.ViewHolder> {

    public ModelIconItem(com.mikepenz.fastadapter.app.model.IconModel icon) {
        super(icon);
    }

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    @Override
    public int getType() {
        return R.id.fastadapter_model_icon_item_id;
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    @Override
    public int getLayoutRes() {
        return R.layout.icon_item;
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param viewHolder the viewHolder of this item
     */
    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        //define our data for the view
        viewHolder.image.setIcon(new IconicsDrawable(viewHolder.image.getContext(), getModel().icon));
        viewHolder.name.setText(getModel().icon.getName());
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.image.setImageDrawable(null);
        holder.name.setText(null);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    /**
     * our ViewHolder
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected View view;
        @BindView(R.id.name)
        public TextView name;
        @BindView(R.id.icon)
        public IconicsImageView image;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }
}
