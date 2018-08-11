package com.mikepenz.fastadapter.app.items;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.app.R;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LetterItem extends AbstractItem<LetterItem, LetterItem.ViewHolder> {

    public String letter;

    public LetterItem(String letter) {
        this.letter = letter;
    }

    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.text.setText(letter);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.text.setText(null);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.fastadapter_letter_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.letter_item;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text)
        public TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
