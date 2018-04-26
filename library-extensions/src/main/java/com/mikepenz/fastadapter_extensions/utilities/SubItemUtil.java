package com.mikepenz.fastadapter_extensions.utilities;

import android.support.annotation.NonNull;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.ISubItem;
import com.mikepenz.fastadapter.expandable.ExpandableExtension;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.fastadapter.utils.AdapterPredicate;
import com.mikepenz.fastadapter.utils.Triple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * Created by flisar on 15.09.2016.
 */
public class SubItemUtil {

    /**
     * returns a set of selected items, regardless of their visibility
     *
     * @param adapter the adapter instance
     * @return a set of all selected items and subitems
     * @deprecated See {@link FastAdapter#getSelectedItems()} ()} ()}
     */
    @Deprecated
    public static Set<IItem> getSelectedItems(FastAdapter adapter) {
        Set<IItem> selections = new HashSet<>();
        int length = adapter.getItemCount();
        List<IItem> items = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            items.add(adapter.getItem(i));
        }
        updateSelectedItemsWithCollapsed(selections, items);
        return selections;
    }

    private static void updateSelectedItemsWithCollapsed(Set<IItem> selected, List<IItem> items) {
        int length = items.size();
        for (int i = 0; i < length; i++) {
            if (items.get(i).isSelected()) {
                selected.add(items.get(i));
            }
            if (items.get(i) instanceof IExpandable && ((IExpandable) items.get(i)).getSubItems() != null) {
                updateSelectedItemsWithCollapsed(selected, ((IExpandable) items.get(i)).getSubItems());
            }
        }
    }

    /**
     * counts the items in the adapter, respecting subitems regardless of there current visibility
     *
     * @param adapter   the adapter instance
     * @param predicate predicate against which each item will be checked before counting it
     * @return number of items in the adapter that apply to the predicate
     */
    public static int countItems(final IItemAdapter adapter, IPredicate predicate) {
        return countItems(adapter.getAdapterItems(), true, false, predicate);
    }

    /**
     * counts the items in the adapter, respecting subitems regardless of there current visibility
     *
     * @param adapter      the adapter instance
     * @param countHeaders if true, headers will be counted as well
     * @return number of items in the adapter
     */
    public static int countItems(final IItemAdapter adapter, boolean countHeaders) {
        return countItems(adapter.getAdapterItems(), countHeaders, false, null);
    }

    private static int countItems(List<IItem> items, boolean countHeaders, boolean subItemsOnly, IPredicate predicate) {
        return getAllItems(items, countHeaders, subItemsOnly, predicate).size();
    }

    /**
     * retrieves a flat list of the items in the adapter, respecting subitems regardless of there current visibility
     *
     * @param adapter   the adapter instance
     * @param predicate predicate against which each item will be checked before adding it to the result
     * @return list of items in the adapter that apply to the predicate
     */
    public static List<IItem> getAllItems(final IItemAdapter adapter, IPredicate predicate) {
        return getAllItems(adapter.getAdapterItems(), true, false, predicate);
    }

    /**
     * retrieves a flat list of the items in the adapter, respecting subitems regardless of there current visibility
     *
     * @param adapter      the adapter instance
     * @param countHeaders if true, headers will be counted as well
     * @return list of items in the adapter
     */
    public static List<IItem> getAllItems(final IItemAdapter adapter, boolean countHeaders) {
        return getAllItems(adapter.getAdapterItems(), countHeaders, false, null);
    }

    /**
     * retrieves a flat list of the items in the provided list, respecting subitems regardless of there current visibility
     *
     * @param items        the list of items to process
     * @param countHeaders if true, headers will be counted as well
     * @return list of items in the adapter
     */
    public static List<IItem> getAllItems(List<IItem> items, boolean countHeaders, IPredicate predicate) {
        return getAllItems(items, countHeaders, false, predicate);
    }

    /**
     * internal function!
     * <p>
     * Why countHeaders and subItems => because the subItemsOnly is an internal flag for the recursive call to optimise it!
     */
    private static List<IItem> getAllItems(List<IItem> items, boolean countHeaders, boolean subItemsOnly, IPredicate predicate) {
        List<IItem> res = new ArrayList<>();
        if (items == null || items.size() == 0) {
            return res;
        }

        int temp;
        int itemCount = items.size();
        IItem item;
        List<IItem> subItems;
        for (int i = 0; i < itemCount; i++) {
            item = items.get(i);
            if (item instanceof IExpandable && ((IExpandable) item).getSubItems() != null) {
                subItems = ((IExpandable) item).getSubItems();
                if (predicate == null) {
                    if (countHeaders) {
                        res.add(item);
                    }
                    if (subItems != null && subItems.size() > 0) {
                        res.addAll(subItems);
                    }
                    res.addAll(getAllItems(subItems, countHeaders, true, predicate));
                } else {
                    if (countHeaders && predicate.apply(item)) {
                        res.add(item);
                    }
                    temp = subItems != null ? subItems.size() : 0;
                    for (int j = 0; j < temp; j++) {
                        if (predicate.apply(subItems.get(j))) {
                            res.add(subItems.get(j));
                        }
                    }
                }
            }
            // in some cases, we must manually check, if the item is a sub item, process is optimised as much as possible via the subItemsOnly parameter already
            // sub items will be counted in above if statement!
            else if (!subItemsOnly && getParent(item) == null) {
                if (predicate == null) {
                    res.add(item);
                } else if (predicate.apply(item)) {
                    res.add(item);
                }
            }

        }
        return res;
    }

    /**
     * counts the selected items in the adapter underneath an expandable item, recursively
     *
     * @param adapter the adapter instance
     * @param header  the header who's selected children should be counted
     * @return number of selected items underneath the header
     */
    public static <T extends IItem & IExpandable> int countSelectedSubItems(final FastAdapter adapter, T header) {
        SelectExtension extension = (SelectExtension) adapter.getExtension(SelectExtension.class);
        if (extension != null) {
            Set<IItem> selections = extension.getSelectedItems();
            return countSelectedSubItems(selections, header);
        }
        return 0;
    }

    public static <T extends IItem & IExpandable> int countSelectedSubItems(Set<IItem> selections, T header) {
        int count = 0;
        List<IItem> subItems = header.getSubItems();
        int items = header.getSubItems() != null ? header.getSubItems().size() : 0;
        for (int i = 0; i < items; i++) {
            if (selections.contains(subItems.get(i))) {
                count++;
            }
            if (subItems.get(i) instanceof IExpandable && ((IExpandable) subItems.get(i)).getSubItems() != null) {
                count += countSelectedSubItems(selections, (T) subItems.get(i));
            }
        }
        return count;
    }

    /**
     * select or unselect all sub itmes underneath an expandable item
     *
     * @param adapter the adapter instance
     * @param header  the header who's children should be selected or deselected
     * @param select  the new selected state of the sub items
     */
    public static <T extends IItem & IExpandable> void selectAllSubItems(final FastAdapter adapter, T header, boolean select) {
        selectAllSubItems(adapter, header, select, false, null);
    }

    /**
     * select or unselect all sub itmes underneath an expandable item
     *
     * @param adapter      the adapter instance
     * @param header       the header who's children should be selected or deselected
     * @param select       the new selected state of the sub items
     * @param notifyParent true, if the parent should be notified about the changes of its children selection state
     * @param payload      payload for the notifying function
     */
    public static <T extends IItem & IExpandable> void selectAllSubItems(final FastAdapter adapter, T header, boolean select, boolean notifyParent, Object payload) {
        int subItems = header.getSubItems().size();
        int position = adapter.getPosition(header);
        if (header.isExpanded()) {
            for (int i = 0; i < subItems; i++) {
                if (((IItem) header.getSubItems().get(i)).isSelectable()) {
                    SelectExtension extension = (SelectExtension) adapter.getExtension(SelectExtension.class);
                    if (extension != null) {
                        if (select) {
                            extension.select(position + i + 1);
                        } else {
                            extension.deselect(position + i + 1);
                        }
                    }
                }
                if (header.getSubItems().get(i) instanceof IExpandable)
                    selectAllSubItems(adapter, header, select, notifyParent, payload);

            }
        } else {
            for (int i = 0; i < subItems; i++) {
                if (((IItem) header.getSubItems().get(i)).isSelectable()) {
                    ((IItem) header.getSubItems().get(i)).withSetSelected(select);
                }
                if (header.getSubItems().get(i) instanceof IExpandable)
                    selectAllSubItems(adapter, header, select, notifyParent, payload);
            }
        }

        // we must notify the view only!
        if (notifyParent && position >= 0) {
            adapter.notifyItemChanged(position, payload);
        }
    }

    /**
     * select or unselect an item with the given identifier
     * This will not handle the `only one selected` case. Please deselect all items first for this requirement.
     *
     * @param adapter    the adapter instance
     * @param identifier the identifier of the item to select / deselect
     * @param select     the new selected state of the sub items
     * @deprecated See {@link SelectExtension#selectByIdentifier(long, boolean, boolean)} ()} ()}
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static boolean selectItem(final FastAdapter adapter, final long identifier, final boolean select) {
        Triple<Boolean, IItem, Integer> res = adapter.recursive(new AdapterPredicate() {
            @Override
            public boolean apply(@NonNull IAdapter lastParentAdapter, int lastParentPosition, @NonNull IItem item, int position) {
                if (item.getIdentifier() == identifier) {
                    if (position != -1) {
                        SelectExtension extension = (SelectExtension) adapter.getExtension(SelectExtension.class);
                        if (extension != null) {
                            if (select) {
                                extension.select(position);
                            } else {
                                extension.deselect(position);
                            }
                        }
                    } else {
                        item.withSetSelected(select);
                    }
                    return true;
                }
                return false;
            }
        }, true);
        return res.first;
    }

    /**
     * deselects all items including all subitems
     *
     * @param adapter the adapter instance
     * @deprecated See {@link SelectExtension#deselect()} ()}
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static void deselect(final FastAdapter adapter) {
        adapter.recursive(new AdapterPredicate() {
            @Override
            public boolean apply(@NonNull IAdapter lastParentAdapter, int lastParentPosition, @NonNull IItem item, int position) {
                if (position != -1) {
                    SelectExtension extension = (SelectExtension) adapter.getExtension(SelectExtension.class);
                    if (extension != null) {
                        extension.deselect(position);
                    }
                } else {
                    item.withSetSelected(false);
                }
                return true;
            }
        }, false);
    }

    private static <T extends IExpandable & IItem> T getParent(IItem item) {
        if (item instanceof ISubItem) {
            return (T) ((ISubItem) item).getParent();
        }
        return null;
    }

    /**
     * deletes all selected items from the adapter respecting if the are sub items or not
     * subitems are removed from their parents sublists, main items are directly removed
     *
     * Alternatively you might consider also looking at: {@link SelectExtension#deleteAllSelectedItems()}
     *
     * @param deleteEmptyHeaders if true, empty headers will be removed from the adapter
     * @return List of items that have been removed from the adapter
     */
    public static List<IItem> deleteSelected(final FastAdapter fastAdapter, final ExpandableExtension expandableExtension, boolean notifyParent, boolean deleteEmptyHeaders) {
        List<IItem> deleted = new ArrayList<>();

        // we use a LinkedList, because this has performance advantages when modifying the listIterator during iteration!
        // Modifying list is O(1)
        LinkedList<IItem> selectedItems = new LinkedList<>(getSelectedItems(fastAdapter));

//        Log.d("DELETE", "selectedItems: " + selectedItems.size());

        // we delete item per item from the adapter directly or from the parent
        // if keepEmptyHeaders is false, we add empty headers to the selected items set via the iterator, so that they are processed in the loop as well
        IItem item, parent;
        int pos, parentPos;
        boolean expanded;
        ListIterator<IItem> it = selectedItems.listIterator();
        while (it.hasNext()) {
            item = it.next();

            pos = fastAdapter.getPosition(item);

            // search for parent - if we find one, we remove the item from the parent's subitems directly
            parent = getParent(item);
            if (parent != null) {
                parentPos = fastAdapter.getPosition(parent);
                boolean success = ((IExpandable) parent).getSubItems().remove(item);
//                Log.d("DELETE", "success=" + success + " | deletedId=" + item.getIdentifier() + " | parentId=" + parent.getIdentifier() + " (sub items: " + ((IExpandable) parent).getSubItems().size() + ") | parentPos=" + parentPos);

                // check if parent is expanded and notify the adapter about the removed item, if necessary (only if parent is visible)
                if (parentPos != -1 && ((IExpandable) parent).isExpanded()) {
                    expandableExtension.notifyAdapterSubItemsChanged(parentPos, ((IExpandable) parent).getSubItems().size() + 1);
                }

                // if desired, notify the parent about its changed items (only if parent is visible!)
                if (parentPos != -1 && notifyParent) {
                    expanded = ((IExpandable) parent).isExpanded();
                    fastAdapter.notifyAdapterItemChanged(parentPos);
                    // expand the item again if it was expanded before calling notifyAdapterItemChanged
                    if (expanded) {
                        expandableExtension.expand(parentPos);
                    }
                }

                deleted.add(item);

                if (deleteEmptyHeaders && ((IExpandable) parent).getSubItems().size() == 0) {
                    it.add(parent);
                    it.previous();
                }
            } else if (pos != -1) {
                // if we did not find a parent, we remove the item from the adapter
                IAdapter adapter = fastAdapter.getAdapter(pos);
                boolean success = false;
                if (adapter instanceof IItemAdapter) {
                    success = ((IItemAdapter) adapter).remove(pos) != null;
                }
                boolean isHeader = item instanceof IExpandable && ((IExpandable) item).getSubItems() != null;
//                Log.d("DELETE", "success=" + success + " | deletedId=" + item.getIdentifier() + "(" + (isHeader ? "EMPTY HEADER" : "ITEM WITHOUT HEADER") + ")");
                deleted.add(item);
            }
        }

//        Log.d("DELETE", "deleted (incl. empty headers): " + deleted.size());

        return deleted;
    }

    /**
     * deletes all items in identifiersToDelete collection from the adapter respecting if there are sub items or not
     * subitems are removed from their parents sublists, main items are directly removed
     *
     * @param fastAdapter         the adapter to remove the items from
     * @param identifiersToDelete ids of items to remove
     * @param notifyParent        if true, headers of removed items will be notified about the change of their child items
     * @param deleteEmptyHeaders  if true, empty headers will be removed from the adapter
     * @return List of items that have been removed from the adapter
     */
    public static List<IItem> delete(final FastAdapter fastAdapter, final ExpandableExtension expandableExtension, Collection<Long> identifiersToDelete, boolean notifyParent, boolean deleteEmptyHeaders) {
        List<IItem> deleted = new ArrayList<>();
        if (identifiersToDelete == null || identifiersToDelete.size() == 0) {
            return deleted;
        }

        // we use a LinkedList, because this has performance advantages when modifying the listIterator during iteration!
        // Modifying list is O(1)
        LinkedList<Long> identifiers = new LinkedList<>(identifiersToDelete);

        // we delete item per item from the adapter directly or from the parent
        // if keepEmptyHeaders is false, we add empty headers to the selected items set via the iterator, so that they are processed in the loop as well
        IItem item, parent;
        int pos, parentPos;
        boolean expanded;
        Long identifier;
        ListIterator<Long> it = identifiers.listIterator();
        while (it.hasNext()) {
            identifier = it.next();

            pos = fastAdapter.getPosition(identifier);
            item = fastAdapter.getItem(pos);

            // search for parent - if we find one, we remove the item from the parent's subitems directly
            parent = getParent(item);
            if (parent != null) {
                parentPos = fastAdapter.getPosition(parent);
                boolean success = ((IExpandable) parent).getSubItems().remove(item);
//                Log.d("DELETE", "success=" + success + " | deletedId=" + item.getIdentifier() + " | parentId=" + parent.getIdentifier() + " (sub items: " + ((IExpandable) parent).getSubItems().size() + ") | parentPos=" + parentPos);

                // check if parent is expanded and notify the adapter about the removed item, if necessary (only if parent is visible)
                if (parentPos != -1 && ((IExpandable) parent).isExpanded()) {
                    expandableExtension.notifyAdapterSubItemsChanged(parentPos, ((IExpandable) parent).getSubItems().size() + 1);
                }

                // if desired, notify the parent about it's changed items (only if parent is visible!)
                if (parentPos != -1 && notifyParent) {
                    expanded = ((IExpandable) parent).isExpanded();
                    fastAdapter.notifyAdapterItemChanged(parentPos);
                    // expand the item again if it was expanded before calling notifyAdapterItemChanged
                    if (expanded) {
                        expandableExtension.expand(parentPos);
                    }
                }

                deleted.add(item);

                if (deleteEmptyHeaders && ((IExpandable) parent).getSubItems().size() == 0) {
                    it.add(parent.getIdentifier());
                    it.previous();
                }
            } else if (pos != -1) {
                // if we did not find a parent, we remove the item from the adapter
                IAdapter adapter = fastAdapter.getAdapter(pos);
                boolean success = false;
                if (adapter instanceof IItemAdapter) {
                    success = ((IItemAdapter) adapter).remove(pos) != null;
                    if (success) {
                        fastAdapter.notifyAdapterItemRemoved(pos);
                    }
                }
                boolean isHeader = item instanceof IExpandable && ((IExpandable) item).getSubItems() != null;
//                Log.d("DELETE", "success=" + success + " | deletedId=" + item.getIdentifier() + "(" + (isHeader ? "EMPTY HEADER" : "ITEM WITHOUT HEADER") + ")");
                deleted.add(item);
            }
        }

//        Log.d("DELETE", "deleted (incl. empty headers): " + deleted.size());

        return deleted;
    }

    /**
     * notifies items (incl. sub items if they are currently extended)
     *
     * @param adapter     the adapter
     * @param identifiers set of identifiers that should be notified
     */
    public static <Item extends IItem & IExpandable> void notifyItemsChanged(final FastAdapter adapter, ExpandableExtension expandableExtension, Set<Long> identifiers) {
        notifyItemsChanged(adapter, expandableExtension, identifiers, false);
    }

    /**
     * notifies items (incl. sub items if they are currently extended)
     *
     * @param adapter              the adapter
     * @param identifiers          set of identifiers that should be notified
     * @param restoreExpandedState true, if expanded headers should stay expanded
     */
    public static <Item extends IItem & IExpandable> void notifyItemsChanged(final FastAdapter adapter, ExpandableExtension expandableExtension, Set<Long> identifiers, boolean restoreExpandedState) {
        int i;
        IItem item;
        for (i = 0; i < adapter.getItemCount(); i++) {
            item = adapter.getItem(i);
            if (item instanceof IExpandable) {
                notifyItemsChanged(adapter, expandableExtension, (Item) item, identifiers, true, restoreExpandedState);
            } else if (identifiers.contains(item.getIdentifier())) {
                adapter.notifyAdapterItemChanged(i);
            }
        }
    }

    /**
     * notifies items (incl. sub items if they are currently extended)
     *
     * @param adapter              the adapter
     * @param header               the expandable header that should be checked (incl. sub items)
     * @param identifiers          set of identifiers that should be notified
     * @param checkSubItems        true, if sub items of headers items should be checked recursively
     * @param restoreExpandedState true, if expanded headers should stay expanded
     */
    public static <Item extends IItem & IExpandable> void notifyItemsChanged(final FastAdapter adapter, final ExpandableExtension expandableExtension, Item header, Set<Long> identifiers, boolean checkSubItems, boolean restoreExpandedState) {
        int subItems = header.getSubItems().size();
        int position = adapter.getPosition(header);
        boolean expanded = header.isExpanded();
        // 1) check header itself
        if (identifiers.contains(header.getIdentifier())) {
            adapter.notifyAdapterItemChanged(position);
        }
        // 2) check sub items, recursively
        IItem item;
        if (header.isExpanded()) {
            for (int i = 0; i < subItems; i++) {
                item = (IItem) header.getSubItems().get(i);
                if (identifiers.contains(item.getIdentifier())) {
//                    Log.d("NOTIFY", "Position=" + position + ", i=" + i);
                    adapter.notifyAdapterItemChanged(position + i + 1);
                }
                if (checkSubItems && item instanceof IExpandable) {
                    notifyItemsChanged(adapter, expandableExtension, (Item) item, identifiers, true, restoreExpandedState);
                }
            }
        }
        if (restoreExpandedState && expanded) {
            expandableExtension.expand(position);
        }
    }

    public interface IPredicate<T> {
        boolean apply(T data);
    }
}