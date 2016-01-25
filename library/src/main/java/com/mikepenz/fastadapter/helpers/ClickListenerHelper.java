package com.mikepenz.fastadapter.helpers;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;

/**
 * Created by mikepenz on 25.01.16.
 */
public class ClickListenerHelper<Item extends IItem> {
    //
    private FastAdapter<Item> mFastAdapter;

    /**
     * ctor
     *
     * @param fastAdapter the fastAdapter which manages these items
     */
    public ClickListenerHelper(FastAdapter<Item> fastAdapter) {
        this.mFastAdapter = fastAdapter;
    }

    /**
     * @param viewHolder      the viewHolder which got created
     * @param view            the view which listens for the click
     * @param onClickListener the listener which gets called
     */
    public void listen(final RecyclerView.ViewHolder viewHolder, View view, final OnClickListener onClickListener) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //we get the adapterPosition from the viewHolder
                int pos = viewHolder.getAdapterPosition();
                //make sure the click was done on a valid item
                if (pos != RecyclerView.NO_POSITION) {
                    //we update our item with the changed property
                    Item item = mFastAdapter.getItem(pos);
                    onClickListener.onClick(v, pos, item);
                }
            }
        });
    }

    /**
     * @param viewHolder      the viewHolder which got created
     * @param viewId          the viewId which listens for the click
     * @param onClickListener the listener which gets called
     */
    public void listen(final RecyclerView.ViewHolder viewHolder, @IdRes int viewId, final OnClickListener onClickListener) {
        viewHolder.itemView.findViewById(viewId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //we get the adapterPosition from the viewHolder
                int pos = viewHolder.getAdapterPosition();
                //make sure the click was done on a valid item
                if (pos != RecyclerView.NO_POSITION) {
                    //we update our item with the changed property
                    Item item = mFastAdapter.getItem(pos);
                    onClickListener.onClick(v, pos, item);
                }
            }
        });
    }

    public interface OnClickListener<Item extends IItem> {
        /**
         * @param v        the view which got clicked
         * @param position the items position which got clicked
         * @param item     the item which is responsible for this position
         */
        void onClick(View v, int position, Item item);
    }
}
