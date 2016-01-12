#FastAdapter  [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.mikepenz/fastadapter/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.mikepenz/fastadapter) [![Join the chat at https://gitter.im/mikepenz/fastadapter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/mikepenz/fastadapter?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

> The RecyclerView is one of the most used widgets in the Android world, and with it you have to implement an Adapter which provides the items for the view. Most use cases require the same base logic, but require you to write everything again and again.

The FastAdapter is here to simplify this process. You don't have to worry about the adapter anymore. Just write the logic for how your view/item should look like, and you are done.
This library has a fast and highly optimized core which provides core functionality, most apps require. It also prevents common mistakes by taking away those steps from the devs. 
Beside being blazing fast, minimizing the code you need to write, it is also really easy to extend. Just provide another Adapter implementation, hook into the adapter chain, custom select / deselection behaviors. Everything is possible.

##A quick overview:
- Click / Long-Click listeners
- Selection / Multi-Selection
- Expandable items
- Write less code, get better results
- Headers
- Footers
- Highly optimized code
- Includes suggestions from the Android Team
- Easily extensible
- Chain other Adapters
- Comes with useful Helpers
 - ActionModeHelper
 - More to come...

#Preview
##Screenshots

#Include in your project
##Using Maven
```javascript
compile('com.mikepenz:fastadapter:0.7.1@aar') {
	transitive = true
}
```

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
    public void bindView(RecyclerView.ViewHolder holder) {
        Context ctx = holder.itemView.getContext();
        //get our viewHolder
        ViewHolder viewHolder = (ViewHolder) holder;

        //set the item selected if it is
        viewHolder.itemView.setSelected(isSelected());

        //set the text for the name
        viewHolder.name.setText(name);
        //set the text for the description or hide
        viewHolder.description.setText(description);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected View view;
        protected TextView name;
        protected TextView description;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.name = (TextView) view.findViewById(com.mikepenz.materialdrawer.R.id.material_drawer_name);
            this.description = (TextView) view.findViewById(com.mikepenz.materialdrawer.R.id.material_drawer_description);
        }
    }
}

```

###2. Set the Adapter to the RecyclerView
```java
//create our FastAdapter which will manage everything
FastAdapter fastAdapter = new FastAdapter();

//create our ItemAdapter which will host our items
ItemAdapter itemAdapter = new ItemAdapter();

//find the RecyclerView
RecyclerView rv = (RecyclerView) findViewById(R.id.rv);

//set our adapters to the RecyclerView
//we wrap our FastAdapter inside the ItemAdapter -> This allows us to chain adapters for more complex useCases
rv.setAdapter(itemAdapter.wrap(fastAdapter));

//GENERATE or FETCH YOUR ITEMS...

//set the items to your ItemAdapter
itemAdapter.add(ITEMS);
```

###3. Libs used in sample app:
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
