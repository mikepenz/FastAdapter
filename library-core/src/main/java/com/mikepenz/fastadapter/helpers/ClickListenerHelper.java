package com.mikepenz.fastadapter.helpers;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.listeners.CustomEventHook;
import com.mikepenz.fastadapter.listeners.EventHook;
import com.mikepenz.fastadapter.listeners.LongClickEventHook;
import com.mikepenz.fastadapter.listeners.TouchEventHook;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mikepenz on 25.01.16.
 */
public class ClickListenerHelper<Item extends IItem> {
    //
    private FastAdapter<Item> mFastAdapter;

    //
    private List<EventHook<Item>> eventHooks = new LinkedList<>();

    /**
     * ctor
     *
     * @param fastAdapter the fastAdapter which manages these items
     */
    public ClickListenerHelper(FastAdapter<Item> fastAdapter) {
        this.mFastAdapter = fastAdapter;
    }

    /**
     * @param fastAdapter the fastAdapter which manages these items
     * @param eventHooks  the event hooks we want to use for this item
     */
    public ClickListenerHelper(FastAdapter<Item> fastAdapter, List<EventHook<Item>> eventHooks) {
        this.mFastAdapter = fastAdapter;
        this.eventHooks = eventHooks;
    }

    /**
     * @return the added event hooks
     */
    public List<EventHook<Item>> getEventHooks() {
        return eventHooks;
    }

    /**
     * adds a new list of eventHook
     * note this will not add new event hooks for existing viewHolders after it was created
     *
     * @param eventHooks a new list of eventHook
     * @return this
     */
    public ClickListenerHelper<Item> setEventHooks(List<EventHook<Item>> eventHooks) {
        this.eventHooks = eventHooks;
        return this;
    }

    /**
     * adds a new eventHook
     * note this will not add new event hooks for existing viewHolders after it was created
     *
     * @param eventHook a new eventHook
     * @return this
     */
    public ClickListenerHelper<Item> addEventHook(EventHook<Item> eventHook) {
        this.eventHooks.add(eventHook);
        return this;
    }


    /**
     * binds the hooks to the viewHolder
     *
     * @param viewHolder the viewHolder of the item
     */
    public void bind(@NonNull final RecyclerView.ViewHolder viewHolder) {
        for (final EventHook<Item> event : eventHooks) {
            View view = event.onBind(viewHolder);
            if (view != null) {
                attachToView(event, viewHolder, view);
            }

            List<? extends View> views = event.onBindMany(viewHolder);
            if (views != null) {
                for (View v : views) {
                    attachToView(event, viewHolder, v);
                }
            }
        }
    }

    /**
     * attaches the specific event to a view
     *
     * @param event      the event to attach
     * @param viewHolder the viewHolder containing this view
     * @param view       the view to attach to
     */
    public void attachToView(final EventHook<Item> event, final RecyclerView.ViewHolder viewHolder, final View view) {
        if (event instanceof ClickEventHook) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //we get the adapterPosition from the viewHolder
                    int pos = mFastAdapter.getHolderAdapterPosition(viewHolder);
                    //make sure the click was done on a valid item
                    if (pos != RecyclerView.NO_POSITION) {
                        //we update our item with the changed property
                        ((ClickEventHook<Item>) event).onClick(v, pos, mFastAdapter, mFastAdapter.getItem(pos));
                    }
                }
            });
        } else if (event instanceof LongClickEventHook) {
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //we get the adapterPosition from the viewHolder
                    int pos = mFastAdapter.getHolderAdapterPosition(viewHolder);
                    //make sure the click was done on a valid item
                    if (pos != RecyclerView.NO_POSITION) {
                        //we update our item with the changed property
                        return ((LongClickEventHook<Item>) event).onLongClick(v, pos, mFastAdapter, mFastAdapter.getItem(pos));
                    }
                    return false;
                }
            });
        } else if (event instanceof TouchEventHook) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent e) {
                    //we get the adapterPosition from the viewHolder
                    int pos = mFastAdapter.getHolderAdapterPosition(viewHolder);
                    //make sure the click was done on a valid item
                    if (pos != RecyclerView.NO_POSITION) {
                        //we update our item with the changed property
                        return ((TouchEventHook<Item>) event).onTouch(v, e, pos, mFastAdapter, mFastAdapter.getItem(pos));
                    }
                    return false;
                }
            });
        } else if (event instanceof CustomEventHook) {
            //we update our item with the changed property
            ((CustomEventHook<Item>) event).onEvent(view, viewHolder, mFastAdapter);
        }
    }

    /**
     * a small helper to get a list of views from a dynamic amout of views.
     *
     * @param views the views to get as list
     * @return the list with the views
     */
    public static List<View> toList(View... views) {
        return Arrays.asList(views);
    }
}
