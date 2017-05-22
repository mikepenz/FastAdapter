package com.mikepenz.fastadapter.listeners;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.IItem;

import java.util.List;

public interface EventHook<Item extends IItem> {

    /*
	 * Return the view for this hook that the listener should be bound to
	 *
	 * @return null, if the provided ViewHolder should not be bound to the event hook; return the view responsible for the event otherwise
	*/
    @Nullable
    View onBind(@NonNull RecyclerView.ViewHolder viewHolder);

    /*
	 * Return the views for this hook that the listener should be bound to
	 *
	 * @return null, if the provided ViewHolder should not be bound to the event hook; return the views responsible for the event otherwise
	*/
    @Nullable
    List<? extends View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder);
}
