/*
 * Copyright 2016 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mikepenz.fastadapter_extensions.firebase;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.ISubItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a generic way of backing an RecyclerView with a Firebase location.
 * It handles all of the child events at the given Firebase location. It marshals received data into the given
 * class type.
 * <p>
 * To use this class in your app, subclass it passing in all required parameters and implement the
 * populateViewHolder method.
 * <p>
 * <blockquote><pre>
 * {@code
 *     private static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
 *         TextView messageText;
 *         TextView nameText;
 * <p>
 *         public ChatMessageViewHolder(View itemView) {
 *             super(itemView);
 *             nameText = (TextView)itemView.findViewById(android.R.id.text1);
 *             messageText = (TextView) itemView.findViewById(android.R.id.text2);
 *         }
 *     }
 * <p>
 *     FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder> adapter;
 *     DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
 * <p>
 *     RecyclerView recycler = (RecyclerView) findViewById(R.id.messages_recycler);
 *     recycler.setHasFixedSize(true);
 *     recycler.setLayoutManager(new LinearLayoutManager(this));
 * <p>
 *     adapter = new FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder>(ChatMessage.class, android.R.layout.two_line_list_item, ChatMessageViewHolder.class, ref) {
 *         public void populateViewHolder(ChatMessageViewHolder chatMessageViewHolder, ChatMessage chatMessage, int position) {
 *             chatMessageViewHolder.nameText.setText(chatMessage.getName());
 *             chatMessageViewHolder.messageText.setText(chatMessage.getMessage());
 *         }
 *     };
 *     recycler.setAdapter(mAdapter);
 * }
 * </pre></blockquote>
 */
public class FirebaseItemAdapter<Item extends IItem> extends ItemAdapter<Item> {

    private Class<Item> mModelClass;
    private FirebaseArray mSnapshots;


    public FirebaseItemAdapter(Class<Item> modelClass, Query ref) {
        mModelClass = modelClass;
        mSnapshots = new FirebaseArray(ref);

        mSnapshots.setOnChangedListener(new FirebaseArray.OnChangedListener() {
            @Override
            public void onChanged(EventType type, int index, int oldIndex) {
                switch (type) {
                    case Added:
                        notifyItemInserted(index);
                        break;
                    case Changed:
                        notifyItemChanged(index);
                        break;
                    case Removed:
                        notifyItemRemoved(index);
                        break;
                    case Moved:
                        notifyItemMoved(oldIndex, index);
                        break;
                    default:
                        throw new IllegalStateException("Incomplete case statement");
                }
            }
        });
    }


    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (mSnapshots != null) {
            mSnapshots.attach();
        }
    }

    @Override
    public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (mSnapshots != null) {
            mSnapshots.cleanup();
        }
    }

    /**
     * @return the count of items within this adapter
     */
    @Override
    public int getAdapterItemCount() {
        return mSnapshots.getCount();
    }

    /**
     * @return the items within this adapter
     */
    @Override
    public List<Item> getAdapterItems() {
        List<Item> items = new ArrayList<>(getAdapterItemCount());
        for (DataSnapshot snapshot : mSnapshots.getItems()) {
            items.add(parseSnapshot(snapshot));
        }
        return items;
    }

    /**
     * Searches for the given item and calculates it's relative position
     *
     * @param item the item which is searched for
     * @return the relative position
     */
    @Override
    public int getAdapterPosition(Item item) {
        int count = 0;
        for (DataSnapshot snapshot : mSnapshots.getItems()) {
            if (parseSnapshot(snapshot).getIdentifier() == item.getIdentifier()) {
                return count;
            }
            count++;
        }
        return -1;
    }

    /**
     * This method parses the DataSnapshot into the requested type. You can override it in subclasses
     * to do custom parsing.
     *
     * @param snapshot the DataSnapshot to extract the model from
     * @return the model extracted from the DataSnapshot
     */
    protected Item parseSnapshot(DataSnapshot snapshot) {
        return snapshot.getValue(mModelClass);
    }


    /**
     * @param position the relative position
     * @return the item inside this adapter
     */
    @Nullable
    public Item getAdapterItem(int position) {
        return parseSnapshot(mSnapshots.getItem(position));
    }

    /**
     * @param position the relative position
     * @return the reference of an item at the given position
     */
    public DatabaseReference getRef(int position) {
        return mSnapshots.getItem(position).getRef();
    }

    /**
     * removes all listeners
     */
    public void cleanup() {
        mSnapshots.cleanup();
    }

    @Override
    public long getItemId(int position) {
        // http://stackoverflow.com/questions/5100071/whats-the-purpose-of-item-ids-in-android-listview-adapter
        return mSnapshots.getItem(position).getKey().hashCode();
    }

    @Override
    public <T extends IItem & IExpandable<T, S>, S extends IItem & ISubItem<Item, T>> T setSubItems(T collapsible, List<S> subItems) {
        throw new UnsupportedOperationException("this is not supported by the FirebaseItemAdapter");
    }

    @Override
    public ItemAdapter<Item> set(List<Item> items) {
        throw new UnsupportedOperationException("this is not supported by the FirebaseItemAdapter");
    }

    @Override
    public ItemAdapter<Item> setNewList(List<Item> items) {
        throw new UnsupportedOperationException("this is not supported by the FirebaseItemAdapter");
    }

    @Override
    public ItemAdapter<Item> add(List<Item> items) {
        throw new UnsupportedOperationException("this is not supported by the FirebaseItemAdapter");
    }

    @Override
    public ItemAdapter<Item> add(int position, List<Item> items) {
        throw new UnsupportedOperationException("this is not supported by the FirebaseItemAdapter");
    }

    @Override
    public ItemAdapter<Item> set(int position, Item item) {
        throw new UnsupportedOperationException("this is not supported by the FirebaseItemAdapter");
    }

    @Override
    public ItemAdapter<Item> move(int fromPosition, int toPosition) {
        throw new UnsupportedOperationException("this is not supported by the FirebaseItemAdapter");
    }

    @Override
    public ItemAdapter<Item> remove(int position) {
        throw new UnsupportedOperationException("this is not supported by the FirebaseItemAdapter");
    }

    @Override
    public ItemAdapter<Item> removeRange(int position, int itemCount) {
        throw new UnsupportedOperationException("this is not supported by the FirebaseItemAdapter");
    }

    @Override
    public ItemAdapter<Item> clear() {
        throw new UnsupportedOperationException("this is not supported by the FirebaseItemAdapter");
    }
}
