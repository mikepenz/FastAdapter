package com.mikepenz.fastadapter.dsl

import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItemList
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.adapters.ModelAdapter

@DslMarker
annotation class FastAdapterDsl

inline fun genericfastadapter(block: (FastAdapter<GenericItem>).() -> Unit): FastAdapter<GenericItem> {
    return FastAdapter<GenericItem>().apply(block)
}

inline fun <Item : GenericItem> fastadapter(block: (FastAdapter<Item>).() -> Unit): FastAdapter<Item> {
    return FastAdapter<Item>().apply(block)
}

inline fun <ParentItem : GenericItem, ChildItem : ParentItem> FastAdapter<ParentItem>.itemAdapter(block: ItemAdapter<ChildItem>.() -> Unit) {
    addAdapters(listOf(ItemAdapter<ChildItem>().apply(block) as IAdapter<ParentItem>))
}

fun <Model, ParentItem : GenericItem, ChildItem : ParentItem> FastAdapter<ParentItem>.modelAdapter(interceptor: (element: Model) -> ChildItem?, block: ModelAdapter<Model, ChildItem>.() -> Unit) {
    addAdapters(listOf(ModelAdapter<Model, ChildItem>(interceptor).apply(block) as ModelAdapter<Model, ParentItem>))
}

fun <Model, ParentItem : GenericItem, ChildItem : ParentItem> FastAdapter<ParentItem>.modelAdapter(itemList: IItemList<ChildItem>, interceptor: (element: Model) -> ChildItem?, block: ModelAdapter<Model, ChildItem>.() -> Unit) {
    addAdapters(listOf(ModelAdapter(itemList, interceptor).apply(block) as ModelAdapter<Model, ParentItem>))
}