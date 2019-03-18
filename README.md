# FastAdapter  [![Download](https://api.bintray.com/packages/mikepenz/maven/com.mikepenz%3Afastadapter/images/download.svg?version=3.2.7) ](https://bintray.com/mikepenz/maven/com.mikepenz%3Afastadapter/3.2.7/link) [![Join the chat at https://gitter.im/mikepenz/fastadapter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/mikepenz/fastadapter?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

> The RecyclerView is one of the most used widgets in the Android world, and with it you have to implement an Adapter which provides the items for the view. Most use cases require the same base logic, but require you to write everything again and again.

The FastAdapter is here to simplify this process. You don't have to worry about the adapter anymore. Just write the logic for how your view/item should look like, and you are done.
This library has a fast and highly optimized core which provides core functionality, most apps require. It also prevents common mistakes by taking away those steps from the devs. 
Beside being blazing fast, minimizing the code you need to write, it is also really easy to extend. Just provide another adapter implementation, hook into the adapter chain, custom select / deselection behaviors. Everything is possible.

## A quick overview:
- Core module 100% in Kotlin
- Click / Long-Click listeners
- Selection / Multi-Selection ([MultiselectSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/MultiselectSampleActivity.java), [CheckBoxSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/CheckBoxSampleActivity.java), [RadioButtonSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/RadioButtonSampleActivity.java))
- Expandable items ([ExpandableSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/ExpandableSampleActivity.java), [IconGridSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/IconGridActivity.java) ,[AdvancedSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/AdvancedSampleActivity.java))
- Write less code, get better results
- Highly optimized code
- Simple Drag & Drop ([SimpleItemListSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/SimpleItemListActivity.java))
- Headers ([StickyHeaderSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/StickyHeaderSampleActivity.java), [AdvancedSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/AdvancedSampleActivity.java))
- Footers
- Filter ([SimpleItemListSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/SimpleItemListActivity.java))
- Includes suggestions from the Android Team
- Easily extensible
- Endless Scroll ([EndlessScrollSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/EndlessScrollListActivity.java))
- "Leave-Behind"-Pattern ([SwipeListSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/SwipeListActivity.java))
- Split item view and model ([ModelItem](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/ModelItemActivity.java), [MultiTypeModelItem](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/MultiTypeModelItemActivity.java))
- Chain other Adapters ([SimpleItemListSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/SimpleItemListActivity.java), [StickyHeaderSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/StickyHeaderSampleActivity.java))
- Comes with useful Helpers
 - ActionModeHelper ([MultiselectSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/MultiselectSampleActivity.java))
 - UndoHelper ([MultiselectSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/MultiselectSampleActivity.java))
 - More to come...
- FastScroller (external lib) ([SimpleItemListSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/SimpleItemListActivity.java))

# Preview
## Demo
You can try it out here [Google Play](https://play.google.com/store/apps/details?id=com.mikepenz.fastadapter.app) (or download the latest release from GitHub)

## Screenshots
![Image](https://raw.githubusercontent.com/mikepenz/FastAdapter/develop/DEV/github/screenshots1.jpg)


# Include in your project
## Latest releases

- Kotlin | [v4.0.0-rc03](https://github.com/mikepenz/FastAdapter/tree/v4.0.0-rc3)
- Java && AndroidX | [v3.3.1](https://github.com/mikepenz/FastAdapter/tree/v3.3.1)
- Java && AppCompat | [v3.2.9](https://github.com/mikepenz/FastAdapter/tree/v3.2.9)

## Using Maven

The library is split up into core, commons, and extensions. The core functions are included in the following dependency.
```gradle
implementation 'com.mikepenz:fastadapter:${latestFastAdapterRelease}'
implementation "androidx.appcompat:appcompat:${androidX}"
implementation "androidx.recyclerview:recyclerview:${androidX}"
```

Expandable support is included and can be added via this
```gradle
implementation 'com.mikepenz:fastadapter-extensions-expandable:${latestFastAdapterRelease}'
//The tiny Materialize library used for its useful helper classes
implementation 'com.mikepenz:materialize:${latestVersion}' // at least 1.2.0
```

Many helper classes are included in the following dependency.
```gradle
implementation 'com.mikepenz:fastadapter-extensions-diff:${latestFastAdapterRelease}' // diff util helpers
implementation 'com.mikepenz:fastadapter-extensions-drag:${latestFastAdapterRelease}' // drag support
implementation 'com.mikepenz:fastadapter-extensions-scroll${latestFastAdapterRelease}' // scroll helpers
implementation 'com.mikepenz:fastadapter-extensions-swipe:${latestFastAdapterRelease}' // swipe support
implementation 'com.mikepenz:fastadapter-extensions-ui:${latestFastAdapterRelease}' // pre-defined ui components
implementation 'com.mikepenz:fastadapter-extensions-utils:${latestFastAdapterRelease}' // needs the `expandable`, `drag` and `scroll` extension.

// required for the ui components and the utils
implementation "com.google.android.material:material:${androidX}"
//The tiny Materialize library used for its useful helper classes
implementation 'com.mikepenz:materialize:${latestVersion}' // at least 1.2.0
```

## v4.x.y
> Major release, migrates fully to Kotlin. No migration notes yet available. Stay tuned!

## v3.3.x
> Upgrades to use androidX dependencies. Use a version smaller than 3.3.x to use with appCompat dependencies.

## v3.x.x
> v3 is a huge new release and comes with a big set of new changes. If you previously used the `FastAdapter` and head over to the [MIGRATION GUIDE](https://github.com/mikepenz/FastAdapter/blob/develop/MIGRATION.md) on how to get started with v3.
> In case you are searching [v2.x head over here to it here](https://github.com/mikepenz/FastAdapter/tree/v2.6.3).

## How to use
### 1. Implement your item (the easy way)
Just create a class which extends the `AbstractItem` as shown below. Implement the methods, and your item is ready.
```java
public class SimpleItem extends AbstractItem<SimpleItem.ViewHolder> {
    public String name;
    public String description;

    // The unique ID for this type of item
    // The ID has to be unique per item type.
    // This is important as it is used by the RV for viewHolder reusing.
    @Override
    public int getType() {
        return R.id.fastadapter_sampleitem_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.sample_item;
    }

    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    /**
     * our ViewHolder
     */
    protected static class ViewHolder extends FastAdapter.ViewHolder<SimpleItem> {
        @BindView(R.id.material_drawer_name)
        TextView name;
        @BindView(R.id.material_drawer_description)
        TextView description;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void bindView(SimpleItem item, List<Object> payloads) {
            StringHolder.applyTo(item.name, name);
            StringHolder.applyToOrHide(item.description, description);
        }

        @Override
        public void unbindView(SimpleItem item) {
            name.setText(null);
            description.setText(null);
        }
    }
}
```

### 2. Set the Adapter to the RecyclerView
```java
//create the ItemAdapter holding your Items
ItemAdapter itemAdapter = new ItemAdapter();
//create the managing FastAdapter, by passing in the itemAdapter
FastAdapter fastAdapter = FastAdapter.with(itemAdapter);

//set our adapters to the RecyclerView
recyclerView.setAdapter(fastAdapter);

//set the items to your ItemAdapter
itemAdapter.add(ITEMS);
```

### 3. Extensions

By default the `FastAdapter` only provides basic functionality, which comes with the abstraction of items as `Item` and `Model`. 
And the general functionality of adding/removing/modifying elements. To enable *selections*, or *expandables* the provided extensions need to be activated.

#### 3.1. SelectExtension

```java
// Gets (or creates and attaches if not yet existing) the extension from the given `FastAdapter`
SelectExtension selectExtension = mFastAdapter.getOrCreateExtension(SelectExtension.class);
// configure as needed
selectExtension.setSelectable(true);
selectExtension.setMultiSelect(true);
selectExtension.setSelectOnLongClick(false);  
// see the API of this class for more options.
```

#### 3.2. ExpandableExtension

> This requires the `fastadapter-extensions-expandable` extension.

```java
// Gets (or creates and attaches if not yet existing) the extension.
ExpandableExtension expandableExtension = fastItemAdapter.getOrCreateExtension(ExpandableExtension.class);
// configure as needed
expandableExtension.setOnlyOneExpandedItem(true);
```

For further details scroll down to the `ExpandableItems` (under advanced usage) section.
 
### 3. Click listener
```java
fastAdapter.setOnClickListener((view, adapter, item, position) -> {
    // Handle click here
    return false;
});
```

### 4. Click listeners for views inside your item
```java
//just add an `EventHook` to your `FastAdapter` by implementing either a `ClickEventHook`, `LongClickEventHook`, `TouchEventHook`, `CustomEventHook`
fastAdapter.addEventHook(new ClickEventHook<SimpleItem>() {
    @Nullable
    @Override
    public View onBind(@NotNull RecyclerView.ViewHolder viewHolder) {
        //return the views on which you want to bind this event
        if (viewHolder instanceof SimpleItem.ViewHolder) {
            return ((SimpleItem.ViewHolder) viewHolder).viewWhichReactsOnClick;
        }
        return null;
    }

    @Override
    public void onClick(@NotNull View v, int position, @NotNull FastAdapter<SimpleItem> fastAdapter, @NotNull SimpleItem item) {
        //react on the click event
    }
});
```

### 5. Filter 

```java
// Call this in onQueryTextSubmit() & onQueryTextChange() when using SearchView
itemAdapter.filter("yourSearchTerm");
itemAdapter.getItemFilter().setFilterPredicate((item, constraint) -> {
    return item.getName().startsWith(String.valueOf(constraint));
});
```
`filter()` should return true for items to be retained and false for items to be removed.

### 6. Drag and drop

> This requires the `fastadapter-extensions-drag` extension.

First, attach `ItemTouchHelper` to RecyclerView.

```java
SimpleDragCallback dragCallback = new SimpleDragCallback();
ItemTouchHelper touchHelper = new ItemTouchHelper(dragCallback);
touchHelper.attachToRecyclerView(recyclerView);
```

Implement `ItemTouchCallback` interface in your Activity, and override the `itemTouchOnMove()` method.

```java
@Override
public boolean itemTouchOnMove(int oldPosition, int newPosition) {
    DragDropUtil.onMove(fastAdapter.getItemAdapter(), oldPosition, newPosition);  // change position
    return true;
}
```

### 7. Using different ViewHolders (like HeaderView)

Start by initializing your adapters:

```java
// Head is a model class for your header
ItemAdapter<Header> headerAdapter = new ItemAdapter<>();
```

Initialize a Model FastAdapter:

```java
ItemAdapter<IItem> itemAdapter = new ItemAdapter<>();
```

Finally, set the adapter:

```java
FastAdapter fastAdapter = FastAdapter.with(headerAdapter, itemAdapter); //the order defines in which order the items will show up
// alternative the super type of both item adapters can be used. e.g.:
// FastAdapter<IItem> fastAdapter = FastAdapter.with(headerAdapter, itemAdapter); 
recyclerView.setAdapter(fastAdapter);
```

### 8. Infinite (endless) scrolling

Create a FooterAdapter. We need this to display a loading ProgressBar at the end of our list. (Don't forget to pass it into `FastAdapter.with(..)`)

```java
ItemAdapter<ProgressItem> footerAdapter = new ItemAdapter<>();
```
Keep in mind that ProgressItem is provided by FastAdapter’s extensions.
```java
recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(footerAdapter) {
    @Override
    public void onLoadMore(int currentPage) {
	footerAdapter.clear();
	footerAdapter.add(new ProgressItem().withEnabled(false));
	// Load your items here and add it to FastAdapter
	fastAdapter.add(NEWITEMS);
    }
});
```

For the complete tutorial and more features such as multi-select and CAB check out the [sample app](https://github.com/mikepenz/FastAdapter/tree/develop/app).

## Advanced Usage
### Proguard
* As of v2.5.0 there are no more known requirements to use the `FastAdapter` with Proguard

### ExpandableItems
The `FastAdapter` comes with support for expandable items. After adding the dependency set up the `Expandable` extension via:

```java
expandableExtension = new ExpandableExtension<>();
fastAdapter.addExtension(expandableExtension);
```

Expandable items have to implement the `IExpandable` interface, and the sub items the `ISubItem` interface. This allows better support.
The sample app provides sample implementations of those. (Those in the sample are kept Model which allows them to be used with different parent / subitems)

As of the way how `SubItems` and their state are handled it is highly recommended to use the `identifier` based `StateManagement`. Just add `withPositionBasedStateManagement(false)` to your `FastAdapter` setup.

A simple item just needs to extend from the `AbstractExpandableItem` and provide the `ViewHolder` as type.
```java
public class SimpleSubExpandableItem extends AbstractExpandableItem<SimpleSubExpandableItem.ViewHolder> {

    /**
     * BASIC ITEM IMPLEMENTATION
     */
}
```
// See the `SimpleSubExpandableItem.java` of the sample application for more details.


## Articles
- [RecyclerView Adapter made easy](http://blog.grafixartist.com/recyclerview-adapter-android-made-fast-easy/) (FastAdapter v2.x)

## Libs used in sample app:
Mike Penz:
- AboutLibraries https://github.com/mikepenz/AboutLibraries
- Android-Iconics https://github.com/mikepenz/Android-Iconics
- ItemAnimators https://github.com/mikepenz/ItemAnimators
- MaterialDrawer https://github.com/mikepenz/MaterialDrawer

Other Libs:
- Butterknife https://github.com/JakeWharton/butterknife
- Glide https://github.com/bumptech/glide
- MaterialScrollBar https://github.com/krimin-killr21/MaterialScrollBar
- StickyRecyclerHeadersAdapter https://github.com/timehop/sticky-headers-recyclerview


# Developed By

* Mike Penz 
  * [mikepenz.com](http://mikepenz.com) - <mikepenz@gmail.com>
  * [paypal.me/mikepenz](http://paypal.me/mikepenz)

* Fabian Terhorst
  * [github.com/FabianTerhorst](https://github.com/FabianTerhorst) - <fabian.terhorst@gmail.com>
  * [paypal.me/fabianterhorst](http://paypal.me/fabianterhorst)


# Contributors

This free, open source software was also made possible by a group of volunteers that put many hours of hard work into it. See the [CONTRIBUTORS.md](CONTRIBUTORS.md) file for details.

## Special mentions

I want to give say thanks to some special contributors who provided some huge PRs and many changes to improve this great library. 

* **[MFlisar](https://github.com/MFlisar)**

# License

    Copyright 2017 Mike Penz

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
