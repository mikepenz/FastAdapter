package androidx.recyclerview.widget;

import androidx.recyclerview.widget.RecyclerView.LayoutManager;

/**
 * Why the class name? Because it guarantees zero naming conflicts!
 * <p>
 * With this really long name, I recommend `import static *.*` !
 * <p>
 * Created by jayson on 3/27/2016.
 */
public class com_mikepenz_fastadapter_extensions_scroll {

    public static boolean postOnRecyclerView(LayoutManager layoutManager, Runnable action) {
        if (layoutManager.mRecyclerView != null) {
            layoutManager.mRecyclerView.post(action);
            return true;
        }
        return false;
    }
}
