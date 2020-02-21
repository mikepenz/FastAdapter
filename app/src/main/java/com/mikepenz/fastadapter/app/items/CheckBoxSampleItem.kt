package com.mikepenz.fastadapter.app.items

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.app.R
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.materialdrawer.holder.StringHolder

/**
 * Created by mikepenz on 28.12.15.
 */
class CheckBoxSampleItem : AbstractItem<CheckBoxSampleItem.ViewHolder>() {

    var header: String? = null
    var name: StringHolder? = null
    var description: StringHolder? = null

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    override val type: Int
        get() = R.id.fastadapter_checkbox_sample_item_id

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    override val layoutRes: Int
        get() = R.layout.checkbox_sample_item

    fun withHeader(header: String): CheckBoxSampleItem {
        this.header = header
        return this
    }

    fun withName(Name: String): CheckBoxSampleItem {
        this.name = StringHolder(Name)
        return this
    }

    fun withName(@StringRes NameRes: Int): CheckBoxSampleItem {
        this.name = StringHolder(NameRes)
        return this
    }

    fun withDescription(description: String): CheckBoxSampleItem {
        this.description = StringHolder(description)
        return this
    }

    fun withDescription(@StringRes descriptionRes: Int): CheckBoxSampleItem {
        this.description = StringHolder(descriptionRes)
        return this
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param holder the viewHolder of this item
     */
    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)

        holder.checkBox.isChecked = isSelected

        //set the text for the name
        StringHolder.applyTo(name, holder.name)
        //set the text for the description or hide
        StringHolder.applyToOrHide(description, holder.description)
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.name.text = null
        holder.description.text = null
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    /**
     * our ViewHolder
     */
    class ViewHolder(protected var view: View) : RecyclerView.ViewHolder(view) {
        var checkBox: CheckBox = view.findViewById(R.id.checkbox)
        internal var name: TextView = view.findViewById(R.id.material_drawer_name)
        internal var description: TextView = view.findViewById(R.id.material_drawer_description)
    }

    class CheckBoxClickEvent : ClickEventHook<CheckBoxSampleItem>() {
        override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
            return if (viewHolder is ViewHolder) {
                viewHolder.checkBox
            } else null
        }

        override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<CheckBoxSampleItem>, item: CheckBoxSampleItem) {
            val selectExtension: SelectExtension<CheckBoxSampleItem> = fastAdapter.requireExtension()
            selectExtension.toggleSelection(position)
        }
    }
}
