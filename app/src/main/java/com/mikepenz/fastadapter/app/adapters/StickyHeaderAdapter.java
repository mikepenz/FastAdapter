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
import com.mikepenz.fastadapter.app.items.ExpandableItem;
import com.mikepenz.fastadapter.app.items.SampleItem;
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
        if (item instanceof SampleItem && ((SampleItem) item).header != null) {
            return ((SampleItem) item).header.charAt(0);
        } else if (item instanceof ExpandableItem && ((ExpandableItem) item).header != null) {
            return ((ExpandableItem) item).header.charAt(0);
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
        if (item instanceof SampleItem && ((SampleItem) item).header != null) {
            //based on the position we set the headers text
            textView.setText(String.valueOf(((SampleItem) item).header.charAt(0)));
        } else if (item instanceof ExpandableItem && ((ExpandableItem) item).header != null) {
            //based on the position we set the headers text
            textView.setText(String.valueOf(((ExpandableItem) item).header.charAt(0)));
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
