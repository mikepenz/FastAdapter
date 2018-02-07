### Upgrade Notes

#### v3.2.4
* Adjusted the `set(int position, Item item, int preItemCount)` to include the `preItemCount` to corretly notify the adapter about the changed element.

#### v3.2.3
* The `ActionModeHelper` requires a `FastAdapter` with the `SelectExtension` applied. This is done in current versions via `withSelectable(true)`. Make sure this is called before creating the `ActionModeHelper`.

#### v3.2.1
* `AdapterPredicate` was adjusted to include the `IAdapter<Item> lastParentAdapter`, `int lastParentPosition` to allow more advanced recursive operations on the items
* removed `select(Item item, int position, boolean fireEvent, boolean considerSelectableFlag)` and replaced with `select(IAdapter<Item> adapter, Item item, int position, boolean fireEvent, boolean considerSelectableFlag)`
  * this was done to make the `Adapter` `NonNull`

#### v3.0.5
* The `ItemFilter`s `itemsFiltered` callback method will no longer be called after `onReset` (FIX bug making it impossible to detect when filtering was done)

#### v3.0.3
* Remove the `R.id.fastadapter_item` Tag from the `ViewHolder` in favour of `FastAdapter.getHolderAdapterItem()` as this will make sure we always get the newest reference to our item

#### v3.0.0
v3 is a major new release of the `FastAdapter` library and comes with a big refactor. In most cases the upgrade should be still really straight-forward.
A core part of the v3 refactor was to eliminate the `GenericItemAdapter`. It's functionality is now bundled inside the `ModelAdapter` and should be seen, as the default adapter.
* `itemAdapter.wrap(...)` was removed in favor of `FastAdapter.with(IItemAdapter, ...)`
* `GenericItemAdapter` -> `ModelAdapter`
* `GenericAbstractItem` -> `ModelAbstractItem`
* all `setModel`-methods of the `ModelAdapter` do not longer exist -> use the normal `set`-methods instead (The ModelAdapter will just keep the item list (not the original model list))
* `ItemAdapter extends ModelAdapter`: as mentioned above the `ModelAdapter` is now the default and the base
* The `ModelAdapter` and `ItemAdapter` no longer extend `RecyclerView.Adapter` and are really lightweight now.
* The `FooterAdapter` and `HeaderAdapter` were removed. Simply use multiple `ItemAdapter` (the order in which they are passed to the `FastAdapter` defines at which position their items show up)
* The initial page of the `EndlessRecyclerOnScrollListener` is now starting also with `0` (like the `EndlessRecyclerOnTopScrollListener`)
* The logic of the `Predicate.filter` method was reversed to follow the API of RxJava http://reactivex.io/documentation/operators/filter.html (`true` means the item stays, `false` it will be removed)
* by default the `FastAdapter` no longer contains expandable functionality, please add the `expandable` module (as described) in the readme
```java
expandableExtension = new ExpandableExtension<>();
fastAdapter.addExtension(expandableExtension);
```
* moved all previous internal `FastAdapter.*Listener` to their own interfaces

**SOMETHING MISSING?** Please open a issue and let me know. Or open a PR and add missing migration notes

#### v2.6.0
* the `ItemFilter` was moved to it's own class
* the `ItemFilterListener` was moved to it's own class
  * in addition the interface was adjusted to provide more flexibility
```java
public interface ItemFilterListener<Item> {
    void itemsFiltered(@Nullable CharSequence constraint, @Nullable List<Item> results);
    void onReset();
}
```
* the `withFilterPredicate` and `withItemFilterListener` are no longer available on the `ItemAdapter`. Use `getItemFilter()` on the adapter to get the `ItemFilter` and call the methods on this one
* we no longer allow any filter extending `Filter`, you have to extend `ItemFilter` now to provide your custom implementation
* `ItemFilter`, and `ItemFilterListener` and all related methods are now typed.
* the `Item` itself is no longer set via the general `Tag` on the `viewHolder.itemView`, but instead you can now retrieve it via:
```java
(IItem) viewHolder.itemView.getTag(R.id.fastadapter_item)
```
* in addition the `itemView` now gets a tag set which has the reference to the current `FastAdapter`, this is necessary for the `ClickListenerHelper` to correctly retrieve the item, even if we have a shared `RecyclerViewPool`
* the `FastAdapter` now directly handles setting the `Tag` with the `Item` and with the `FastAdapter` references. This is no longer necessary inside the `AbstractItem`, or any custom implementation
* the method `onEvent` inside the `CustomEventHook` was renamed to `attachEvent` as it seems a bit more meaningful
* the `attachEvent` no longer passes the `FastAdapter` in, please use the provided method `getFastAdapter()` && `getItem()` to get the proper element in your event from the `ViewHolder`
* the `ClickListenerHelper` was renamed to `EventHookUtil` and moved to the utils package. It is now a util as it no longer needs an instance and has just 3 public static methods.
* `withItemEvent` is now deprecated in favor of the new naming `withEventHook`


#### v2.5.3
* the `ItemTouchCallback` has a new method `itemTouchDropped` just implement it and keep it empty, if you do not need it.
* If you implemented your own `Item` using the `IItem` interface instead the `AbstractItem` you will now also have to implement
  * `failedToRecycle`
* removed the `RecyclerViewCacheUtil` as it would no longer work with the latest RecyclerView

#### v2.5.0
**WARNING**
* If you use the MaterialDrawer or the AboutLibraries you will need a compatible release of those for it to work
* The FACTORY within the items is no longer required and replaced by a much simpler approach. Remove the old FACTORY and it's methods, and implement the `getViewHolder` method
```
@Override
public ViewHolder getViewHolder(View v) {
    return new ViewHolder(v);
}
```
* If you implemented your own `Item` using the `IItem` interface instead the `AbstractItem` you will now also have to implement
  * `attachToWindow` and
  * `detachFromWindow`
* The reflection based `GenericItemAdapter` CTOR (with the 2 classes as overload) was removed. Please use the CTOR using a `Function`

#### v2.1.0 
* This release brings minor changes to the `IItem` interface. Please update MaterialDrawer and AboutLibraries too if you use them.
* Starting with this version there is now the `core` and the `commons` dependency. Which make the `FastAdapter` even slimmer if you want to use the basics. 

**QUICK UPGRADE**
* The ` List payloads` param in the `onBindViewHolder` method was changed to `List<Object> payloads`
* The FastAdapter is now split into smaller dependencies
 * `com.mikepenz:fastadapter` : just contains the basics (no `FastItemAdapter` for example)
 * `com.mikepenz:fastadapter-commons` : this one contains the `FastItemAdapter`  and more useful common helper classes (please make sure to update the imports as these classes moved to `com.mikepenz.fastadapter.commons.*`)
 * `com.mikepenz:fastadapter-extensions` : comes with additional utils and helpers which bring allow more complex and advanced implementations

#### v2.0.0 
* This release brings new interfaces, and many changes to the expandable behavior. There is now also a `unbindView` method you have to implement for your `IItem`s.

**SHORT OVERVIEW**
* If you have items implemented by using the interface you have to implement the new methods (**unbindView**)
* If you have expandable items make sure to adjust the Model type definitions as metioned below. Check out the `AbstractExpandableItem` to simplify this for you
* If you use the `MaterialDrawer`, the `AboutLibraries` in your project, please make sure to update them so the changed interfaces do not cause conflicts

**DETAILED**
* New `unbindView` method was added to the `IItem` --> This method is called when the current item is no longer set and before the `ViewHolder` is used for the next item
 * You should move your view resetting logic here, or for example glide image loading canceling
* `IExpandable` Model types changes
 * it is now required to define the type which will be used for the subItems. This can be an implementation or `ISubItem`. We now require this, as we will keep the references between childs, and parents.
 * this allows more optimizations, and many additional usecases
* New `ISubItem` interface added 
 * items serving as subitems, have to implement this. 
* New `AbstractExpandableItem` added, which combines `IExpandable` and `ISubItem` with an `AbstractItem` to simplify your life
* A new `SubItemUtil` was introduced which simplifies some use cases when working with expandable / collapsing lists

**Extensions**
* new `EndlessRecyclerOnTopScrollListener` available
* new `IExtendedDraggable` and `DragDropUtil` introduced
* new `RangeSelectorHelper` introduced

#### v1.8.0
* This release bring a breaking interface change. Your items now have to implement `bindView(ViewHolder holder, List payloads)` instead of `bindView(VH holder)`. 
 * The additional payload can be used to implement a more performant view updating when only parts of the item have changed. Please also refer to the `DiffUtils` which may provide the payload.

#### v1.7.0
* **Dropping support for API < 14. New `MinSdkVersion` is 14**

#### v1.5.8 -> 1.6.0 
* the `IExpandable` interface has a new method `isAutoExpanding` which needs to be implemented (default value `true`). This allows to disable the auto toggling of `Expandable` items in the `FastAdapter` which is a problem for custom behaviors. Like issue #157

#### v1.x.x -> v1.4.0
* with v1.4.0 by default a FastAdapter is now `withSelectable(false)` (for normal lists) if you have selection enabled in your list, add `withSelectable(true)` to your `FastAdapter`, `FastItemAdapter` or `ModelFastItemAdapter``
