###Upgrade Notes

#### v1.8.0
* This release bring a breaking interface change. Your items now have to implement `bindView(ViewHolder holder, List payloads)` instead of `bindView(VH holder)`. 
 * The additional payload can be used to implement a more performant view updating when only parts of the item have changed. Please also refer to the `DiffUtils` which may provide the payload.

#### v1.7.0
* **Dropping support for API < 14. New `MinSdkVersion` is 14**

#### v1.5.8 -> 1.6.0 
* the `IExpandable` interface has a new method `isAutoExpanding` which needs to be implemented (default value `true`). This allows to disable the auto toggling of `Expandable` items in the `FastAdapter` which is a problem for custom behaviors. Like issue #157

#### v1.x.x -> v1.4.0
* with v1.4.0 by default a FastAdapter is now `withSelectable(false)` (for normal lists) if you have selection enabled in your list, add `withSelectable(true)` to your `FastAdapter`, `FastItemAdapter` or `GenericFastItemAdapter``