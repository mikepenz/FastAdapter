package com.mikepenz.fastadapter_extensions.admob.items;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.mikepenz.library_extensions_admob.R;

/**
 * Created by mikepenz on 28.12.15.
 */
public class AdBannerItem extends AbstractItem<AdBannerItem, AdBannerItem.ViewHolder> {
    //the static ViewHolderFactory which will be used to generate the ViewHolder for this Item
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    @Override
    public int getType() {
        return R.id.ad_banner_item_id;
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    @Override
    public int getLayoutRes() {
        return -1;
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param viewHolder the viewHolder of this item
     */
    @Override
    public void bindView(ViewHolder viewHolder) {
        super.bindView(viewHolder);
    }

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

    @Override
    public View generateView(Context ctx) {
        return getViewHolder(ctx).itemView;
    }

    @Override
    public View generateView(Context ctx, ViewGroup parent) {
        return getViewHolder(parent).itemView;
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup parent) {
        return getViewHolder(parent.getContext());
    }

    protected ViewHolder getViewHolder(Context ctx) {
        AdView adview = new AdView(ctx);
        adview.setAdSize(AdSize.BANNER);

        // this is the good adview
        adview.setAdUnitId(ctx.getString(R.string.admob_unit_id));

        float density = ctx.getResources().getDisplayMetrics().density;
        int height = Math.round(AdSize.BANNER.getHeight() * density);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, height);
        adview.setLayoutParams(params);

        // dont use below if testing on a device
        // follow https://developers.google.com/admob/android/quick-start?hl=en to setup testing device
        AdRequest request = new AdRequest.Builder().addTestDevice("8DDF41D178BD3C46F282CCFA8A4E710B").build();
        adview.loadAd(request);

        return new ViewHolder(adview);
    }

    /**
     * our ViewHolder
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected AdView view;

        public ViewHolder(View view) {
            super(view);
            this.view = (AdView) view;
        }
    }
}
