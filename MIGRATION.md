###Upgrade Notes

#### v1.5.8 -> 1.6.0 
* the `IExpandable` interface has a new method `isAutoExpanding` which needs to be implemented (default value `true`). This allows to disable the auto toggling of `Expandable` items in the `FastAdapter` which is a problem for custom behaviors. Like issue #157

#### v1.x.x -> v1.4.0
* with v1.4.0 by default a FastAdapter is now `withSelectable(false)` (for normal lists) if you have selection enabled in your list, add `withSelectable(true)` to your `FastAdapter`, `FastItemAdapter` or `GenericFastItemAdapter``