package com.mikepenz.fastadapter.helpers;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mikepenz on 25.01.16.
 */
public class ClickListenerHelper<Item extends IItem> {
    //
    private FastAdapter<Item> mFastAdapter;

    //
    private List<EventHook> eventHooks = new LinkedList<>();

    /**
     * ctor
     *
     * @param fastAdapter the fastAdapter which manages these items
     */
    public ClickListenerHelper(FastAdapter<Item> fastAdapter) {
        this.mFastAdapter = fastAdapter;
    }

    /**
     *
     * @param fastAdapter the fastAdapter which manages these items
     * @param eventHooks the event hooks we want to use for this item
     */
    public ClickListenerHelper(FastAdapter<Item> fastAdapter, List<EventHook> eventHooks) {
        this.mFastAdapter = fastAdapter;
        this.eventHooks = eventHooks;
    }

    /**
     *
     * @return the added event hooks
     */
    public List<EventHook> getEventHooks() {
        return eventHooks;
    }

    /**
     * adds a new list of eventHook
     * note this will not add new event hooks for existing viewHolders after it was created
     * @param eventHooks a new list of eventHook
     * @return this
     */
    public ClickListenerHelper<Item> setEventHooks(List<EventHook> eventHooks) {
        this.eventHooks = eventHooks;
        return this;
    }

    /**
     * adds a new eventHook
     * note this will not add new event hooks for existing viewHolders after it was created
     * @param eventHook a new eventHook
     * @return this
     */
    public ClickListenerHelper<Item> addEventHook(EventHook eventHook) {
        this.eventHooks.add(eventHook);
        return this;
    }


    /**
     * binds the hooks to the viewHolder
     * @param viewHolder the viewHolder of the item
     */
    public void bind(@NonNull final RecyclerView.ViewHolder viewHolder) {
        for(final EventHook event : eventHooks) {
            View view = event.onBind(viewHolder);
            if(view == null) {
                continue;
            }
            if(event instanceof ClickEventHook){
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {//we get the adapterPosition from the viewHolder
                        int pos = mFastAdapter.getHolderAdapterPosition(viewHolder);
                        //make sure the click was done on a valid item
                        if (pos != RecyclerView.NO_POSITION) {
                            //we update our item with the changed property
                            ((ClickEventHook<Item>) event).onClick(v, pos, mFastAdapter.getItem(pos));
                        }
                    }
                });
            } else if(event instanceof LongClickEventHook) {
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = mFastAdapter.getHolderAdapterPosition(viewHolder);
                        //make sure the click was done on a valid item
                        if (pos != RecyclerView.NO_POSITION) {
                            //we update our item with the changed property
                            return ((LongClickEventHook<Item>) event).onLongClick(v, pos, mFastAdapter.getItem(pos));
                        }
                        return false;
                    }
                });
            } else if(event instanceof TouchEventHook) {
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent e) {
                        //we get the adapterPosition from the viewHolder
                        int pos = mFastAdapter.getHolderAdapterPosition(viewHolder);
                        //make sure the click was done on a valid item
                        if (pos != RecyclerView.NO_POSITION) {
                            //we update our item with the changed property
                            return ((TouchEventHook) event).onTouch(v, e, pos, mFastAdapter.getItem(pos));
                        }
                        return false;
                    }
                });
            } else if(event instanceof CustomEventHook) {
                ((CustomEventHook) event).onEvent(mFastAdapter, viewHolder, view);
            }
        }
    }

    public interface EventHook {
        public View onBind(RecyclerView.ViewHolder viewHolder);
    }

    public static abstract class ClickEventHook<Item extends IItem> implements EventHook {
        public abstract void onClick(View v, int position, Item item);
    }

    public static abstract class LongClickEventHook<Item extends IItem> implements EventHook {
        public abstract boolean onLongClick(View v, int position, Item item);
    }

    public static abstract class TouchEventHook<Item extends IItem> implements EventHook {
        public abstract boolean onTouch(View v, MotionEvent event, int position, Item item);
    }

    public static abstract class CustomEventHook<Item extends IItem> implements EventHook {
        public abstract void onEvent(FastAdapter<Item> fastAdapter, RecyclerView.ViewHolder viewHolder, View view);
    }


    /**
     * @param viewHolder      the viewHolder which got created
     * @param view            the view which listens for the click
     * @param onClickListener the listener which gets called
     */
    @Deprecated
    public void listen(final RecyclerView.ViewHolder viewHolder, View view, final OnClickListener<Item> onClickListener) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //we get the adapterPosition from the viewHolder
                int pos = mFastAdapter.getHolderAdapterPosition(viewHolder);
                //make sure the click was done on a valid item
                if (pos != RecyclerView.NO_POSITION) {
                    //we update our item with the changed property
                    onClickListener.onClick(v, pos, mFastAdapter.getItem(pos));
                }
            }
        });
    }

    /**
     * @param viewHolder      the viewHolder which got created
     * @param viewId          the viewId which listens for the click
     * @param onClickListener the listener which gets called
     */
    @Deprecated
    public void listen(final RecyclerView.ViewHolder viewHolder, @IdRes int viewId, final OnClickListener<Item> onClickListener) {
        viewHolder.itemView.findViewById(viewId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //we get the adapterPosition from the viewHolder
                int pos = mFastAdapter.getHolderAdapterPosition(viewHolder);
                //make sure the click was done on a valid item
                if (pos != RecyclerView.NO_POSITION) {
                    //we update our item with the changed property
                    onClickListener.onClick(v, pos, mFastAdapter.getItem(pos));
                }
            }
        });
    }

    @Deprecated
    public interface OnClickListener<Item extends IItem> {
        /**
         * @param v        the view which got clicked
         * @param position the items position which got clicked
         * @param item     the item which is responsible for this position
         */
        @Deprecated
        void onClick(View v, int position, Item item);
    }
}
