package com.mikepenz.fastadapter;

/**
 * Created by mikepenz on 28.10.17.
 */

public interface IAdapterNotifier {

    IAdapterNotifier DEFAULT = new IAdapterNotifier() {
        @Override
        public boolean notify(FastAdapter fastAdapter, int newItemsCount, int previousItemsCount, int itemsBeforeThisAdapter) {
            //now properly notify the adapter about the changes
            if (newItemsCount > previousItemsCount) {
                if (previousItemsCount > 0) {
                    fastAdapter.notifyAdapterItemRangeChanged(itemsBeforeThisAdapter, previousItemsCount);
                }
                fastAdapter.notifyAdapterItemRangeInserted(itemsBeforeThisAdapter + previousItemsCount, newItemsCount - previousItemsCount);
            } else if (newItemsCount > 0) {
                fastAdapter.notifyAdapterItemRangeChanged(itemsBeforeThisAdapter, newItemsCount);
                if (newItemsCount < previousItemsCount) {
                    fastAdapter.notifyAdapterItemRangeRemoved(itemsBeforeThisAdapter + newItemsCount, previousItemsCount - newItemsCount);
                }
            } else if (newItemsCount == 0) {
                fastAdapter.notifyAdapterItemRangeRemoved(itemsBeforeThisAdapter, previousItemsCount);
            } else {
                //this condition should practically never happen
                fastAdapter.notifyAdapterDataSetChanged();
            }
            return false;
        }
    };

    IAdapterNotifier LEGACY_DEFAULT = new IAdapterNotifier() {
        @Override
        public boolean notify(FastAdapter fastAdapter, int newItemsCount, int previousItemsCount, int itemsBeforeThisAdapter) {
            //now properly notify the adapter about the changes
            if (newItemsCount > previousItemsCount) {
                if (previousItemsCount > 0) {
                    fastAdapter.notifyAdapterItemRangeChanged(itemsBeforeThisAdapter, previousItemsCount);
                }
                fastAdapter.notifyAdapterItemRangeInserted(itemsBeforeThisAdapter + previousItemsCount, newItemsCount - previousItemsCount);
            } else if (newItemsCount > 0 && newItemsCount < previousItemsCount) {
                fastAdapter.notifyAdapterItemRangeChanged(itemsBeforeThisAdapter, newItemsCount);
                fastAdapter.notifyAdapterItemRangeRemoved(itemsBeforeThisAdapter + newItemsCount, previousItemsCount - newItemsCount);
            } else if (newItemsCount == 0) {
                fastAdapter.notifyAdapterItemRangeRemoved(itemsBeforeThisAdapter, previousItemsCount);
            } else {
                //this condition should practically never happen
                fastAdapter.notifyAdapterDataSetChanged();
            }
            return false;
        }
    };

    boolean notify(FastAdapter fastAdapter, int newItemsCount, int previousItemsCount, int itemsBeforeThisAdapter);
}
