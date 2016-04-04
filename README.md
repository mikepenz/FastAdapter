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

The library is split up into core, and extensions. The core functions are included in the following dependency.
```gradle
compile('com.mikepenz:fastadapter:1.4.0@aar') {
	transitive = true
}
```

All additions are included in the following dependency.
```gradle
compile 'com.mikepenz:fastadapter-extensions:1.4.0@aar'
```

> If you upgrade from < 1.4.0 follow the [MIGRATION GUIDE](https://github.com/mikepenz/FastAdapter/blob/develop/MIGRATION.md)


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
    public void bindView(ViewHolder viewHolder) {
    	//call super so the selection is already handled for you
    	super.bindView(viewHolder);
    	
    	//bind our data
        //set the text for the name
        viewHolder.name.setText(name);
        //set the text for the description or hide
        viewHolder.description.setText(description);
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

##Advanced Usage
###Proguard
If you use the `FastAdapter` and enabled **Proguard** you have to implement a `ViewHolderFactory` for you `Item`. ([SampleItem#L143](https://github.com/mikepenz/FastAdapter/blob/develop/app/src/main/java/com/mikepenz/fastadapter/app/items/SampleItem.java#L143))
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
