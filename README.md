#FastAdapter  [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.mikepenz/fastadapter/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.mikepenz/fastadapter) [![Join the chat at https://gitter.im/mikepenz/fastadapter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/mikepenz/fastadapter?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

> The RecyclerView is one of the most used widgets in the Android world, and with it you have to implement an Adapter which provides the items for the view. Most use cases require the same base logic, but require you to write everything again and again.

The FastAdapter is here to simplify this process. You don't have to worry about the adapter anymore. Just write the logic for how your view/item should look like, and you are done.
This library has a fast and highly optimized core which provides core functionality, most apps require. It also prevents common mistakes by taking away those steps from the devs. 
Beside being blazing fast, minimizing the code you need to write, it is also really easy to extend. Just provide another adapter implementation, hook into the adapter chain, custom select / deselection behaviors. Everything is possible.

##A quick overview:
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
- Split item view and model ([GenericItem](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/GenericItemActivity.java), [MultiTypeGenericItem](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/MultiTypeGenericItemActivity.java))
- Chain other Adapters ([SimpleItemListSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/SimpleItemListActivity.java), [StickyHeaderSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/StickyHeaderSampleActivity.java))
- Comes with useful Helpers
 - ActionModeHelper ([MultiselectSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/MultiselectSampleActivity.java))
 - UndoHelper ([MultiselectSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/MultiselectSampleActivity.java))
 - More to come...
- FastScroller (external lib) ([SimpleItemListSample](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/SimpleItemListActivity.java))

#Preview
##Demo
You can try it out here [Google Play](https://play.google.com/store/apps/details?id=com.mikepenz.fastadapter.app) (or download the latest release from GitHub)

##Screenshots
![Image](https://raw.githubusercontent.com/mikepenz/FastAdapter/develop/DEV/github/screenshots1.jpg)

#Include in your project
##Using Maven

The library is split up into core, commons, and extensions. The core functions are included in the following dependency.
```gradle
compile('com.mikepenz:fastadapter:2.1.6@aar') {
	transitive = true
}
```

The commons package comes with some useful helpers (which are not needed in all cases) This one for example includes the `FastItemAdapter`
```gradle
compile 'com.mikepenz:fastadapter-commons:2.1.0@aar' 
```

All additions are included in the following dependency.
```gradle
compile 'com.mikepenz:fastadapter-extensions:2.1.0@aar'
//The tiny Materialize library used for its useful helper classes
compile 'com.mikepenz:materialize:1.0.0@aar'
```

> If you upgrade from < 2.1.0 follow the [MIGRATION GUIDE](https://github.com/mikepenz/FastAdapter/blob/develop/MIGRATION.md)


##How to use
###1. Implement your item (the easy way)
Just create a class which extends the `AbstractItem` as shown below. Implement the methods, and your item is ready.
```java
public class SampleItem extends AbstractItem<SampleItem, SampleItem.ViewHolder> {
    public String name;
    public String description;

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.fastadapter_sampleitem_id;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.sample_item;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
    	//call super so the selection is already handled for you
    	super.bindView(viewHolder, payloads);
    	
    	//bind our data
        //set the text for the name
        viewHolder.name.setText(name);
        //set the text for the description or hide
        viewHolder.description.setText(description);
    }

    //reset the view here (this is an optional method, but recommended)
    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.name.setText(null);
        holder.description.setText(null);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected TextView description;

        public ViewHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(com.mikepenz.materialdrawer.R.id.material_drawer_name);
            this.description = (TextView) view.findViewById(com.mikepenz.materialdrawer.R.id.material_drawer_description);
        }
    }
}
```

###2. Set the Adapter to the RecyclerView
```java
//create our FastAdapter which will manage everything
FastItemAdapter fastAdapter = new FastItemAdapter();

//set our adapters to the RecyclerView
//we wrap our FastAdapter inside the ItemAdapter -> This allows us to chain adapters for more complex useCases
recyclerView.setAdapter(fastAdapter);

//set the items to your ItemAdapter
fastAdapter.add(ITEMS);
```

###3. Click listener
```java
fastAdapter.withSelectable(true);
fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<Item>() {
            @Override
            public boolean onClick(View v, IAdapter<Item> adapter, Item item, int position) {
               // Handle click here
                return true;
            }
        });
```


###4. Click listeners for views inside your item
```java
//just add an `EventHook` to your `FastAdapter` by implementing either a `ClickEventHook`, `LongClickEventHook`, `TouchEventHook`, `CustomEventHook`
fastItemAdapter.withItemEvent(new ClickEventHook<SampleItem> {
    
    @Override
    public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
        //return the views on which you want to bind this event
        if (viewHolder instanceof SampleItem.ViewHolder) {
            return ((ViewHolder) viewHolder).view;
        }
        return null;
    }

    @Override
    public void onClick(View v, int position, FastAdapter<SampleItem> fastAdapter, SampleItem item) {
        //react on the click event
    }
});
```

###5. Filter 
```java
// Call this in onQueryTextSubmit() & onQueryTextChange() when using SearchView
fastAdapter.filter("yourSearchTerm");

fastAdapter.withFilterPredicate(new IItemAdapter.Predicate<Item>() {
            @Override
            public boolean filter(Item item, CharSequence constraint) {
                return item.getName().startsWith(String.valueOf(constraint));
            }
});
```
`filter()` will return true to indicate which items will be removed. Returning false indicates items that will be retained.

###6. Drag and drop
First, attach `ItemTouchHelper` to RecyclerView.
```java
SimpleDragCallback dragCallback = new SimpleDragCallback(this);
ItemTouchHelper touchHelper = new ItemTouchHelper(dragCallback);
touchHelper.attachToRecyclerView(recyclerView);
```
Implement `ItemTouchCallback` interface in your Activity, and override the `itemTouchOnMove()` method.
```java
@Override
   public boolean itemTouchOnMove(int oldPosition, int newPosition) {
       Collections.swap(fastAdapter.getAdapterItems(), oldPosition, newPosition); // change position
       fastAdapter.notifyAdapterItemMoved(oldPosition, newPosition);
       return true;
   }
```

###7. Using different ViewHolders (like HeaderView)
Start by initializing your adapters:
```java
FastItemAdapter fastAdapter = new FastItemAdapter<>();
// Head is a model class for your header
HeaderAdapter<Header> headerAdapter = new HeaderAdapter<>();
```
Initialize a generic FastAdapter:
```java
FastItemAdapter<IItem> fastAdapter = new FastItemAdapter<>();
```
Finally, set the adapter:
```java
recyclerView.setAdapter(headerAdapter.wrap(fastAdapter));
```
It is also possible to add in a third ViewHolder type by using the `wrap()` method again.
```java
recyclerView.setAdapter(thirdAdapter.wrap(headerAdapter.wrap(fastAdapter)));
```

###8. Infinite (endless) scrolling
Create a FooterAdapter. We need this to display a loading ProgressBar at the end of our list.
```java
FooterAdapter<ProgressItem> footerAdapter = new FooterAdapter<>();
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

For the complete tutorial and more features such as multi-select and CAB check out the [sample app](https://github.com/mikepenz/FastAdapter/tree/develop/app) or, read [blog post](http://blog.grafixartist.com/recyclerview-adapter-android-made-fast-easy/).

##Advanced Usage
###Proguard
If you use the `FastAdapter` and enabled **Proguard** you have to implement a `ViewHolderFactory` for your `Item`. ([SimpleItem#L145](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/items/SimpleItem.java#L145))
```java
//the static ViewHolderFactory which will be used to generate the ViewHolder for this Item
private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

/**
* our ItemFactory implementation which creates the ViewHolder for our adapter.
* It is highly recommended to implement a ViewHolderFactory as it is 0-1ms faster for ViewHolder creation,
* and it is also many many times more efficient if you define custom listeners on views within your item.
*/
protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
   public ViewHolder create(View v) {
       return new ViewHolder(v);
   }
}

/**
* return our ViewHolderFactory implementation here
*
* @return
*/
@Override
public ViewHolderFactory<? extends ViewHolder> getFactory() {
   return FACTORY;
}
```

Using the `GenericItemAdapter` with `proguard` requires you to use the non generic implementation by providing a `Function` to the following constructur: [GenericFastItemAdapter(Function<Model, Item> itemFactory) ](https://github.com/mikepenz/FastAdapter/blob/develop/library/src/main/java/com/mikepenz/fastadapter/commons/adapters/GenericFastItemAdapter.java#L29)

###ExpandableItems
The `FastAdapter` comes with native support for expandable items. These items have to implement the `IExpandable` interface, and the sub items the `ISubItem` interface. This allows better support. 
The sample app provides sample implementations of those. (Those in the sample are kept generic which allows them to be used with different parent / subitems)

As of the way how `SubItems` and their state are handled it is highly recommended to use the `identifier` based `StateManagement`. Just add `withPositionBasedStateManagement(false)` to your `FastAdapter` setup.

A simple item just needs to extend from the `AbstractExpandableItem` and provide the `Parent`, the `ViewHolder` and the `SubItem`s it will contain as type.
```java
public class SimpleSubExpandableItem extends AbstractExpandableItem<SimpleSubExpandableItem, SimpleSubExpandableItem.ViewHolder, SubItem> {

    /**
     * BASIC ITEM IMPLEMENTATION
     */
}
```




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


#Developed By

* Mike Penz 
 * [mikepenz.com](http://mikepenz.com) - <mikepenz@gmail.com>
 * [paypal.me/mikepenz](http://paypal.me/mikepenz)
 
#Contributors

This free, open source software was also made possible by a group of volunteers that put many hours of hard work into it. See the [CONTRIBUTORS.md](CONTRIBUTORS.md) file for details.

##Special mentions

I want to give say thanks to some special contributors who provided some huge PRs and many changes to improve this great library. 

* **[Fabian Terhorst](https://github.com/FabianTerhorst)**
* **[MFlisar](https://github.com/MFlisar)**

#License

    Copyright 2016 Mike Penz

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
