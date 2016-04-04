###Upgrade Notes

#### v1.x.x -> v1.4.0
* with v1.4.0 by default a FastAdapter is now `withSelectable(false)` (for normal lists) if you have selection enabled in your list, add `withSelectable(true)` to your `FastAdapter`, `FastItemAdapter` or `GenericFastItemAdapter``