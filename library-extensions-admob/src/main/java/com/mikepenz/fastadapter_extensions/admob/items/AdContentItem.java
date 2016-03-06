package com.mikepenz.fastadapter_extensions.admob.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.mikepenz.library_extensions_admob.R;

/**
 * Created by mikepenz on 28.12.15.
 */
public class AdContentItem extends AbstractItem<AdContentItem, AdContentItem.ViewHolder> {
    //the static ViewHolderFactory which will be used to generate the ViewHolder for this Item
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    NativeContentAd ad;

    public NativeContentAd getAd() {
        return ad;
    }

    public AdContentItem withAd(NativeContentAd ad) {
        this.ad = ad;
        return this;
    }

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    @Override
    public int getType() {
        return R.id.ad_content_item_id;
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    @Override
    public int getLayoutRes() {
        return R.layout.adcontent_item;
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param viewHolder the viewHolder of this item
     */
    @Override
    public void bindView(ViewHolder viewHolder) {
        super.bindView(viewHolder);

        // Locate the view that will hold the headline, set its text, and call the
        // NativeContentAdView's setHeadlineView method to register it.
        viewHolder.tvHeader.setText(ad.getHeadline());
        viewHolder.view.setHeadlineView(viewHolder.tvHeader);

        viewHolder.tvDescription.setText(ad.getBody());
        viewHolder.view.setBodyView(viewHolder.tvDescription);

        if (ad.getLogo() != null) {
            viewHolder.ivLogo.setImageDrawable(ad.getLogo().getDrawable());
        }
        viewHolder.view.setLogoView(viewHolder.ivLogo);

        viewHolder.btnAction.setText(ad.getCallToAction());
        viewHolder.view.setCallToActionView(viewHolder.btnAction);

        viewHolder.tvAdvertiser.setText(ad.getAdvertiser());
        viewHolder.view.setAdvertiserView(viewHolder.tvAdvertiser);

        if (ad.getImages() != null && ad.getImages().size() > 0) {
            viewHolder.ivImage.setImageDrawable(ad.getImages().get(0).getDrawable());
            viewHolder.ivImage.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivImage.setVisibility(View.GONE);
        }
        viewHolder.view.setImageView(viewHolder.ivImage);

        // Call the NativeContentAdView's setNativeAd method to register the
        // NativeAdObject.
        viewHolder.view.setNativeAd(ad);
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


    /**
     * our ViewHolder
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected NativeContentAdView view;
        TextView tvHeader;
        TextView tvDescription;
        ImageView ivLogo;
        Button btnAction;
        TextView tvAdvertiser;
        ImageView ivImage;

        public ViewHolder(View view) {
            super(view);
            this.view = (NativeContentAdView) view;

            tvHeader = (TextView) view.findViewById(R.id.tvHeader);
            tvDescription = (TextView) view.findViewById(R.id.tvDescription);
            ivLogo = (ImageView) view.findViewById(R.id.ivLogo);
            btnAction = (Button) view.findViewById(R.id.btnAction);
            tvAdvertiser = (TextView) view.findViewById(R.id.tvAdvertiser);
            ivImage = (ImageView) view.findViewById(R.id.ivImage);
        }
    }
}
