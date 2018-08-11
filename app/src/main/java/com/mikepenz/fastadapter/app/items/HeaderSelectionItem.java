package com.mikepenz.fastadapter.app.items;

import android.content.Context;
import android.graphics.Color;
import androidx.core.view.ViewCompat;

import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.ISubItem;
import com.mikepenz.fastadapter.app.items.expandable.SimpleSubExpandableItem;
import com.mikepenz.fastadapter.commons.utils.FastAdapterUIUtils;
import com.mikepenz.materialdrawer.holder.StringHolder;

import java.util.List;

/**
 * Created by flisar on 21.09.2016.
 */

public class HeaderSelectionItem<T extends IItem & IExpandable, S extends IItem & ISubItem> extends SimpleSubExpandableItem<T, S> {

    private ISubSelectionProvider mSubSelectionProvider;

    public HeaderSelectionItem<T, S> withSubSelectionProvider(ISubSelectionProvider subSelectionProvider) {
        this.mSubSelectionProvider = subSelectionProvider;
        return this;
    }

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

        int selectedSubItems = 0;
        if (mSubSelectionProvider != null)
            selectedSubItems = mSubSelectionProvider.getSelectedSubItems();
        StringHolder descr = new StringHolder(description.getText());
        if (selectedSubItems > 0)
            descr.setText("Selected children: " + selectedSubItems + "/" + getSubItems().size());
        StringHolder.applyToOrHide(descr, viewHolder.description);

        viewHolder.description.setTextColor(selectedSubItems == 0 ? Color.BLACK : Color.RED);
    }

    public interface ISubSelectionProvider {
        int getSelectedSubItems();
    }
}
