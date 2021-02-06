package com.mikepenz.fastadapter.dsl

import com.mikepenz.fastadapter.*
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.adapters.ModelAdapter

// Notify user that the DSL is currently experimental
@Experimental(level = Experimental.Level.WARNING)
annotation class ExperimentalFADSL

@DslMarker
annotation class FastAdapterDsl

@FastAdapterDsl
@ExperimentalFADSL
inline fun genericFastAdapter(block: (GenericFastAdapter).() -> Unit): GenericFastAdapter {
    return GenericFastAdapter().apply(block)
}

@FastAdapterDsl
@ExperimentalFADSL
inline operator fun <Item : GenericItem> FastAdapter.Companion.invoke(block: (FastAdapter<Item>).() -> Unit): FastAdapter<Item> {
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