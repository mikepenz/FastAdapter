/*
 *  Copyright 2015 Yahoo Inc. All rights reserved.
 * Copyright 2015 Clockbyte LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mikepenz.fastadapter_extensions.admob;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.mikepenz.library_extensions_admob.R;

/**
 * Adapter that has common functionality for any adapters that need to show ads in-between
 * other data.
 */
public class AdmobAdapterWrapper extends BaseAdapter implements AdmobFetcher.AdmobListener {

    private final String TAG = AdmobAdapterWrapper.class.getCanonicalName();

    private BaseAdapter mAdapter;

    public BaseAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(BaseAdapter adapter) {
        mAdapter = adapter;
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
            }

            @Override
            public void onInvalidated() {
                notifyDataSetInvalidated();
            }
        });
    }

    AdmobFetcher adFetcher;
    Context mContext;

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_AD_CONTENT = 1;
    private static final int VIEW_TYPE_AD_INSTALL = 2;

    private final static int DEFAULT_NO_OF_DATA_BETWEEN_ADS = 10;
    private final static int DEFAULT_LIMIT_OF_ADS = 3;

    private int mNoOfDataBetweenAds;

    /*
    * Gets the number of your data items between ad blocks, by default it equals to 10.
    * You should set it according to the Admob's policies and rules which says not to
    * display more than one ad block at the visible part of the screen
    * so you should choose this parameter carefully and according to your item's height and screen resolution of a target devices
    */
    public int getNoOfDataBetweenAds() {
        return mNoOfDataBetweenAds;
    }

    /*
    * Sets the number of your data items between ad blocks, by default it equals to 10.
    * You should set it according to the Admob's policies and rules which says not to
    * display more than one ad block at the visible part of the screen
    * so you should choose this parameter carefully and according to your item's height and screen resolution of a target devices
    */
    public void setNoOfDataBetweenAds(int mNoOfDataBetweenAds) {
        this.mNoOfDataBetweenAds = mNoOfDataBetweenAds;
    }

    private int mLimitOfAds;

    /*
    * Gets the max count of ad blocks per dataset, by default it equals to 3 (according to the Admob's policies and rules)
    */
    public int getLimitOfAds() {
        return mLimitOfAds;
    }

    /*
    * Sets the max count of ad blocks per dataset, by default it equals to 3 (according to the Admob's policies and rules)
    */
    public void setLimitOfAds(int mLimitOfAds) {
        this.mLimitOfAds = mLimitOfAds;
    }

    private int mContentAdsLayoutId;

    /*
    * Gets the res layout id for published content ads {@link https://support.google.com/admob/answer/6240809}
    */
    public int getContentAdsLayoutId() {
        return mContentAdsLayoutId;
    }

    /*
    * Sets the res layout id for published content ads {@link https://support.google.com/admob/answer/6240809}
    */
    public void setContentAdsLayoutId(int mContentAdsLayoutId) {
        this.mContentAdsLayoutId = mContentAdsLayoutId;
    }

    private int mInstallAdsLayoutId;

    /*
    * Gets the res layout id for published install app ads {@link https://support.google.com/admob/answer/6240809}
    */
    public int getInstallAdsLayoutId() {
        return mInstallAdsLayoutId;
    }

    /*
    * Sets the res layout id for published install app ads {@link https://support.google.com/admob/answer/6240809}
    */
    public void setInstallAdsLayoutId(int mInstallAdsLayoutId) {
        this.mInstallAdsLayoutId = mInstallAdsLayoutId;
    }

    /*
    *Sets a test device ID. Normally you don't have to set it
    */
    public void setTestDeviceId(String testDeviceId) {
        adFetcher.setTestDeviceId(testDeviceId);
    }

    /*
    *Sets a release unit ID for admob banners. ID should be active, please check it in your Admob's account.
    * Be careful: don't set it or set to null if you still haven't deployed a Release.
    * Otherwise your Admob account could be banned
    */
    public void setAdmobReleaseUnitId(String admobReleaseUnitId) {
        adFetcher.setAdmobReleaseUnitId(admobReleaseUnitId);
    }

    public AdmobAdapterWrapper(Context context) {
        setNoOfDataBetweenAds(DEFAULT_NO_OF_DATA_BETWEEN_ADS);
        setLimitOfAds(DEFAULT_LIMIT_OF_ADS);
        setContentAdsLayoutId(R.layout.adcontent_item);
        setInstallAdsLayoutId(R.layout.adinstall_item);
        mContext = context;

        adFetcher = new AdmobFetcher();
        adFetcher.addListener(this);
        // Start prefetching ads
        adFetcher.prefetchAds(context.getApplicationContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        switch (getItemViewType(position)) {
            case VIEW_TYPE_AD_INSTALL:
                NativeAppInstallAdView lvi1;
                NativeAppInstallAd ad1 = (NativeAppInstallAd) getItem(position);
                if (convertView == null) {
                    lvi1 = getInstallAdView(parent, ad1);
                } else {
                    lvi1 = (NativeAppInstallAdView) convertView;
                    bindInstallAdView(lvi1, ad1);
                }
                return lvi1;
            case VIEW_TYPE_AD_CONTENT:
                NativeContentAdView lvi2;
                NativeContentAd ad2 = (NativeContentAd) getItem(position);
                if (convertView == null) {
                    lvi2 = getContentAdView(parent, ad2);
                } else {
                    lvi2 = (NativeContentAdView) convertView;
                    bindContentAdView(lvi2, ad2);
                }
                return lvi2;
            default:
                int origPos = getOriginalContentPosition(position);
                return mAdapter.getView(origPos, convertView, parent);
        }
    }

    private NativeContentAdView getContentAdView(ViewGroup parent, NativeContentAd ad) {
        // Inflate a layout and add it to the parent ViewGroup.
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        NativeContentAdView adView = (NativeContentAdView) inflater
                .inflate(getContentAdsLayoutId(), parent, false);

        bindContentAdView(adView, ad);

        return adView;
    }

    private void bindContentAdView(NativeContentAdView adView, NativeContentAd ad) {
        if (adView == null || ad == null) return;

        // Locate the view that will hold the headline, set its text, and call the
        // NativeContentAdView's setHeadlineView method to register it.
        TextView tvHeader = (TextView) adView.findViewById(R.id.tvHeader);
        tvHeader.setText(ad.getHeadline());
        adView.setHeadlineView(tvHeader);

        TextView tvDescription = (TextView) adView.findViewById(R.id.tvDescription);
        tvDescription.setText(ad.getBody());
        adView.setBodyView(tvDescription);

        ImageView ivLogo = (ImageView) adView.findViewById(R.id.ivLogo);
        if (ad.getLogo() != null)
            ivLogo.setImageDrawable(ad.getLogo().getDrawable());
        adView.setLogoView(ivLogo);

        Button btnAction = (Button) adView.findViewById(R.id.btnAction);
        btnAction.setText(ad.getCallToAction());
        adView.setCallToActionView(btnAction);

        TextView tvAdvertiser = (TextView) adView.findViewById(R.id.tvAdvertiser);
        tvAdvertiser.setText(ad.getAdvertiser());
        adView.setAdvertiserView(tvAdvertiser);

        ImageView ivImage = (ImageView) adView.findViewById(R.id.ivImage);
        if (ad.getImages() != null && ad.getImages().size() > 0) {
            ivImage.setImageDrawable(ad.getImages().get(0).getDrawable());
            ivImage.setVisibility(View.VISIBLE);
        } else ivImage.setVisibility(View.GONE);
        adView.setImageView(ivImage);

        // Call the NativeContentAdView's setNativeAd method to register the
        // NativeAdObject.
        adView.setNativeAd(ad);
    }

    private NativeAppInstallAdView getInstallAdView(ViewGroup parent, NativeAppInstallAd ad) {
        // Inflate a layout and add it to the parent ViewGroup.
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        NativeAppInstallAdView adView = (NativeAppInstallAdView) inflater
                .inflate(getInstallAdsLayoutId(), parent, false);

        bindInstallAdView(adView, ad);

        return adView;
    }

    private void bindInstallAdView(NativeAppInstallAdView adView, NativeAppInstallAd ad) {
        if (adView == null || ad == null) return;

        // Locate the view that will hold the headline, set its text, and call the
        // NativeContentAdView's setHeadlineView method to register it.
        TextView tvHeader = (TextView) adView.findViewById(R.id.tvHeader);
        tvHeader.setText(ad.getHeadline());
        adView.setHeadlineView(tvHeader);

        TextView tvDescription = (TextView) adView.findViewById(R.id.tvDescription);
        tvDescription.setText(ad.getBody());
        adView.setBodyView(tvDescription);

        ImageView ivLogo = (ImageView) adView.findViewById(R.id.ivLogo);
        if (ad.getIcon() != null)
            ivLogo.setImageDrawable(ad.getIcon().getDrawable());
        adView.setIconView(ivLogo);

        Button btnAction = (Button) adView.findViewById(R.id.btnAction);
        btnAction.setText(ad.getCallToAction());
        adView.setCallToActionView(btnAction);

        TextView tvStore = (TextView) adView.findViewById(R.id.tvStore);
        tvStore.setText(ad.getStore());
        adView.setStoreView(tvStore);

        TextView tvPrice = (TextView) adView.findViewById(R.id.tvPrice);
        tvPrice.setText(ad.getPrice());
        adView.setPriceView(tvPrice);

        ImageView ivImage = (ImageView) adView.findViewById(R.id.ivImage);
        if (ad.getImages() != null && ad.getImages().size() > 0) {
            ivImage.setImageDrawable(ad.getImages().get(0).getDrawable());
            ivImage.setVisibility(View.VISIBLE);
        } else ivImage.setVisibility(View.GONE);
        adView.setImageView(ivImage);

        // Call the NativeContentAdView's setNativeAd method to register the
        // NativeAdObject.
        adView.setNativeAd(ad);
    }

    /**
     * <p>Gets the count of all data, including interspersed ads.</p>
     * <p/>
     * <p>If data size is 10 and an ad is to be showed after every 5 items starting at the index 0, this method
     * will return 12.</p>
     *
     * @return the total number of items this adapter can show, including ads.
     * @see AdmobAdapterWrapper#setNoOfDataBetweenAds(int)
     * @see AdmobAdapterWrapper#getNoOfDataBetweenAds()
     */
    @Override
    public int getCount() {

        if (mAdapter != null) {
            /*
            No of currently fetched ads, as long as it isn't more than no of max ads that can
            fit dataset.
             */
            int noOfAds = getAdsCountToPublish();
            return mAdapter.getCount() > 0 ? mAdapter.getCount() + noOfAds : 0;
        } else {
            return 0;
        }
    }

    public int getAdsCountToPublish() {
        int noOfAds = Math.min(adFetcher.getFetchedAdsCount(),
                mAdapter.getCount() / getNoOfDataBetweenAds());
        return Math.min(noOfAds, getLimitOfAds());
    }

    /**
     * Gets the item in a given position in the dataset. If an ad is to be returned,
     * a {@link NativeAd} object is returned.
     *
     * @param position the adapter position
     * @return the object or ad contained in this adapter position
     */
    @Override
    public Object getItem(int position) {

        if (canShowAdAtPosition(position)) {
            int adPos = getAdIndex(position);
            return adFetcher.getAdForIndex(adPos);
        } else {
            int origPos = getOriginalContentPosition(position);
            return mAdapter.getItem(origPos);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT + getAdapter().getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (canShowAdAtPosition(position)) {
            int adPos = getAdIndex(position);
            NativeAd ad = adFetcher.getAdForIndex(adPos);
            return ad instanceof NativeAppInstallAd ? VIEW_TYPE_AD_INSTALL : VIEW_TYPE_AD_CONTENT;
        } else {
            int origPos = getOriginalContentPosition(position);
            return mAdapter.getItemViewType(origPos);
        }
    }

    /**
     * Translates an adapter position to an actual position within the underlying dataset.
     *
     * @param position the adapter position
     * @return the original position that the adapter position would have been without ads
     */
    protected int getOriginalContentPosition(int position) {
        int noOfAds = getAdsCountToPublish();
        // No of spaces for ads in the dataset, according to ad placement rules
        int adSpacesCount = position / (getNoOfDataBetweenAds() + 1);
        return position - Math.min(adSpacesCount, noOfAds);
    }

    /**
     * Determines if an ad can be shown at the given position. Checks if the position is for
     * an ad, using the preconfigured ad positioning rules; and if a native ad object is
     * available to place in that position.
     *
     * @param position the adapter position
     * @return <code>true</code> if ads can
     */
    protected boolean canShowAdAtPosition(int position) {

        // Is this a valid position for an ad?
        // Is an ad for this position available?
        return isAdPosition(position) && isAdAvailable(position);
    }

    /**
     * Gets the ad index for this adapter position within the list of currently fetched ads.
     *
     * @param position the adapter position
     * @return the index of the ad within the list of fetched ads
     */
    private int getAdIndex(int position) {
        return (position / getNoOfDataBetweenAds()) - 1;
    }

    /**
     * Checks if adapter position is an ad position.
     *
     * @param position the adapter position
     * @return {@code true} if an ad position, {@code false} otherwise
     */
    private boolean isAdPosition(int position) {
        return (position + 1) % (getNoOfDataBetweenAds() + 1) == 0;
    }

    /**
     * Checks if an ad is available for this position.
     *
     * @param position the adapter position
     * @return {@code true} if an ad is available, {@code false} otherwise
     */
    private boolean isAdAvailable(int position) {
        int adIndex = getAdIndex(position);
        return position >= getNoOfDataBetweenAds()
                && adIndex >= 0
                && adIndex < getLimitOfAds()
                && adFetcher.getFetchedAdsCount() > adIndex;
    }

    /**
     * Destroys all currently fetched ads
     */
    public void destroyAds() {
        adFetcher.destroyAllAds();
    }

    /**
     * Clears all currently displaying ads to update them
     */
    public void requestUpdateAd() {
        adFetcher.clearMapAds();
    }

    @Override
    public void onAdCountChanged() {

        notifyDataSetChanged();
    }
}