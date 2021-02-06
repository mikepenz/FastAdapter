package androidx.recyclerview.widget

import androidx.recyclerview.widget.RecyclerView.LayoutManager

/**
 * Why the class name? Because it guarantees zero naming conflicts!
 *
 * With this really long name, I recommend `import static *.*` !
 *
 * Created by jayson on 3/27/2016.
 */
@Suppress("all")
object com_mikepenz_fastadapter_extensions_scroll {

    @JvmStatic fun postOnRecyclerView(layoutManager: LayoutManager, action: Runnable): Boolean {
        if (layoutManager.mRecyclerView != null) {
            layoutManager.mRecyclerView.post(action)
            return true
        }
        return false
    }
}
