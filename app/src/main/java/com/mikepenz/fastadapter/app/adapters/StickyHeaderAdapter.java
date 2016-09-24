package com.mikepenz.fastadapter.app.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepenz.fastadapter.AbstractAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.app.R;
import com.mikepenz.fastadapter.app.items.SimpleItem;
import com.mikepenz.fastadapter.app.items.expandable.SimpleSubExpandableItem;
import com.mikepenz.fastadapter.app.items.expandable.SimpleSubItem;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.security.SecureRandom;
import java.util.List;

/**
 * Created by mikepenz on 30.12.15.
 * This is a FastAdapter adapter implementation for the awesome Sticky-Headers lib by timehop
 * https://github.com/timehop/sticky-headers-recyclerview
 */
public class StickyHeaderAdapter extends AbstractAdapter implements StickyRecyclerHeadersAdapter {
    @Override
    public long getHeaderId(int position) {
        IItem item = getItem(position);

        //in our sample we want a separate header per first letter of our items
        //this if is not necessary for your code, we only use it as this sticky header is reused for different item implementations
        if (item instanceof SimpleItem && ((SimpleItem) item).header != null) {
            return ((SimpleItem) item).header.charAt(0);
        } else if (item instanceof SimpleSubItem && ((SimpleSubItem) item).header != null) {
            return ((SimpleSubItem) item).header.charAt(0);
        } else if (item instanceof SimpleSubExpandableItem && ((SimpleSubExpandableItem) item).header != null) {
            return ((SimpleSubExpandableItem) item).header.charAt(0);
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        //we create the view for the header
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;

        IItem item = getItem(position);
        if (item instanceof SimpleSubItem && ((SimpleSubItem) item).header != null) {
            //based on the position we set the headers text
            textView.setText(String.valueOf(((SimpleSubItem) item).header.charAt(0)));
        } else if (item instanceof SimpleSubExpandableItem && ((SimpleSubExpandableItem) item).header != null) {
            //based on the position we set the headers text
            textView.setText(String.valueOf(((SimpleSubExpandableItem) item).header.charAt(0)));
        }
        holder.itemView.setBackgroundColor(getRandomColor());
    }

    //just to prettify things a bit
    private int getRandomColor() {
        SecureRandom rgen = new SecureRandom();
        return Color.HSVToColor(150, new float[]{
                rgen.nextInt(359), 1, 1
        });
    }

    /**
     * REQUIRED FOR THE FastAdapter. Set order to < 0 to tell the FastAdapter he can ignore this one.
     **/

    /**
     * @return
     */
    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public int getAdapterItemCount() {
        return 0;
    }

    @Override
    public List<IItem> getAdapterItems() {
        return null;
    }

    @Override
    public IItem getAdapterItem(int position) {
        return null;
    }

    @Override
    public int getAdapterPosition(IItem item) {
        return -1;
    }

    @Override
    public int getGlobalPosition(int position) {
        return -1;
    }

}
