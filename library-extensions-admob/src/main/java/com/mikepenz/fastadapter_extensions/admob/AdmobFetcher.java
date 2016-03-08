/*
 * Copyright 2015 Yahoo Inc. All rights reserved.
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
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.mikepenz.library_extensions_admob.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdmobFetcher {

    private final String TAG = AdmobFetcher.class.getCanonicalName();

    /**
     * Maximum number of ads to prefetch.
     */
    private static final int PREFETCHED_ADS_SIZE = 2;
    /**
     * Maximum number of times to try fetch an ad after failed attempts.
     */
    private static final int MAX_FETCH_ATTEMPT = 4;

    private AdLoader adLoader;
    private List<AdmobListener> mAdNativeListeners = new ArrayList<AdmobListener>();
    private List<NativeAd> mPrefetchedAdList = new ArrayList<NativeAd>();
    private Map<Integer, NativeAd> adMapAtIndex = new HashMap<Integer, NativeAd>();
    private int mNoOfFetchedAds;
    private int mFetchFailCount;
    private WeakReference<Context> mContext = new WeakReference<Context>(null);
    AtomicBoolean lockFetch = new AtomicBoolean();

    private String admobReleaseUnitId;

    /*
    *Gets a release unit ID for admob banners. ID should be active, please check it in your Admob's account.
    * Be careful: don't set it or set to null if you still haven't deployed a Release.
    * Otherwise your Admob account could be banned
     */
    public String getAdmobReleaseUnitId() {
        return admobReleaseUnitId;
    }

    /*
   *Sets a release unit ID for admob banners. ID should be active, please check it in your Admob's account.
   * Be careful: don't set it or set to null if you still haven't deployed a Release.
   * Otherwise your Admob account could be banned
    */
    public void setAdmobReleaseUnitId(String admobReleaseUnitId) {
        this.admobReleaseUnitId = admobReleaseUnitId;
    }

    private String testDeviceId;

    /*
    *Gets a test device ID. Normally you don't have to set it
     */
    public String getTestDeviceId() {
        return testDeviceId;
    }

    /*
    *Sets a test device ID. Normally you don't have to set it
     */
    public void setTestDeviceId(String testDeviceId) {
        this.testDeviceId = testDeviceId;
    }

    /**
     * Adds an {@link AdmobListener} that would be notified for any changes to the native ads
     * list.
     *
     * @param listener the listener to be notified
     */
    public synchronized void addListener(AdmobListener listener) {
        mAdNativeListeners.add(listener);
    }


    public Map<Integer, NativeAd> getAdMapAtIndex() {
        return adMapAtIndex;
    }

    /**
     * Gets native ad at a particular index in the fetched ads list.
     *
     * @param index the index of ad in the fetched ads list
     * @return the native ad in the list
     * @see #getFetchedAdsCount()
     */
    public synchronized NativeAd getAdForIndex(final int index) {
        NativeAd adNative = adMapAtIndex.get(index);

        if (adNative == null && mPrefetchedAdList.size() > 0) {
            adNative = mPrefetchedAdList.remove(0);

            if (adNative != null) {
                adMapAtIndex.put(index, adNative);
            }
        }

        ensurePrefetchAmount(); // Make sure we have enough pre-fetched ads
        return adNative;
    }

    /**
     * Gets the number of ads that have been fetched so far.
     *
     * @return the number of ads that have been fetched
     */
    public synchronized int getFetchedAdsCount() {
        return mNoOfFetchedAds;
    }


    /**
     * Fetches a new native ad.
     *
     * @param context the current context.
     * @see #destroyAllAds()
     */
    public synchronized void prefetchAds(Context context) {
        mContext = new WeakReference<Context>(context);
        setupAds();
        fetchAd();
    }

    /**
     * Destroys ads that have been fetched, that are still being fetched and removes all resource
     * references that this instance still has. This should only be called when the Activity that
     * is showing ads is closing, preferably from the {@link android.app.Activity#onDestroy()}.
     * </p>
     * The converse of this call is {@link #prefetchAds(Context)}.
     */
    public synchronized void destroyAllAds() {
        mFetchFailCount = 0;
        adMapAtIndex.clear();

        mPrefetchedAdList.clear();
        mNoOfFetchedAds = 0;

        Log.i(TAG, "destroyAllAds adList " + adMapAtIndex.size() + " prefetched " +
                mPrefetchedAdList.size());
        mContext.clear();
        notifyObserversOfAdSizeChange();
    }

    /**
     * Destroys all the ads in Map to refresh it with new one
     */
    public synchronized void clearMapAds() {
        adMapAtIndex.clear();
    }

    /**
     * Notifies all registered {@link AdmobListener} of a change in ad data count.
     */
    private void notifyObserversOfAdSizeChange() {
        for (AdmobListener listener : mAdNativeListeners) {
            listener.onAdCountChanged();
        }
    }

    /**
     * Fetches a new native ad.
     */
    private synchronized void fetchAd() {
        Context context = mContext.get();

        if (context != null) {
            Log.i(TAG, "Fetching Ad now");
            if (lockFetch.getAndSet(true))
                return;
            adLoader.loadAd(getAdRequest()); //Fetching the ads item
        } else {
            mFetchFailCount++;
            Log.i(TAG, "Context is null, not fetching Ad");
        }
    }

    /**
     * Ensures that the necessary amount of prefetched native ads are available.
     */
    private synchronized void ensurePrefetchAmount() {
        if (mPrefetchedAdList.size() < PREFETCHED_ADS_SIZE &&
                (mFetchFailCount < MAX_FETCH_ATTEMPT)) {
            fetchAd();
        }
    }

    /**
     * Determines if the native ad can be used.
     *
     * @param adNative the native ad object
     * @return <code>true</code> if the ad object can be used, false otherwise
     */
    private boolean canUseThisAd(NativeAd adNative) {
        if (adNative != null) {
            NativeAd.Image logoImage = null;
            CharSequence header = null, body = null;
            if (adNative instanceof NativeContentAd) {
                NativeContentAd ad = (NativeContentAd) adNative;
                logoImage = ad.getLogo();
                header = ad.getHeadline();
                body = ad.getBody();
            } else if (adNative instanceof NativeAppInstallAd) {
                NativeAppInstallAd ad = (NativeAppInstallAd) adNative;
                logoImage = ad.getIcon();
                header = ad.getHeadline();
                body = ad.getBody();
            }

            if (!TextUtils.isEmpty(header)
                    && !TextUtils.isEmpty(body)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Subscribing to the native ads events
     */
    private synchronized void setupAds() {
        String admobUnitId = TextUtils.isEmpty(getAdmobReleaseUnitId()) ?
                mContext.get().getResources().getString(R.string.admob_unit_id)
                : getAdmobReleaseUnitId();
        adLoader = new AdLoader.Builder(mContext.get(), admobUnitId)
                .forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                    @Override
                    public void onAppInstallAdLoaded(NativeAppInstallAd appInstallAd) {
                        onAdFetched(appInstallAd);
                    }
                })
                .forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                    @Override
                    public void onContentAdLoaded(NativeContentAd contentAd) {
                        onAdFetched(contentAd);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Handle the failure by logging, altering the UI, etc.
                        Log.i(TAG, "onAdFailedToLoad " + errorCode);
                        lockFetch.set(false);
                        mFetchFailCount++;
                        ensurePrefetchAmount();
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
    }

    /**
     * A handler for received native ads
     */
    private synchronized void onAdFetched(NativeAd adNative) {
        Log.i(TAG, "onAdFetched");
        if (canUseThisAd(adNative)) {
            mPrefetchedAdList.add(adNative);
            mNoOfFetchedAds++;
        }
        lockFetch.set(false);
        mFetchFailCount = 0;
        ensurePrefetchAmount();
        notifyObserversOfAdSizeChange();
    }

    /**
     * Setup and get an ads request
     */
    private synchronized AdRequest getAdRequest() {
        AdRequest.Builder adBldr = new AdRequest.Builder();
        if (!TextUtils.isEmpty(getTestDeviceId()))
            adBldr.addTestDevice(getTestDeviceId());

        adBldr.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        return adBldr.build();
        //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        // All emulators are added by default as test devices
    }

    /**
     * Listener that is notified when changes to the list of fetched native ads are made.
     */
    public interface AdmobListener {
        /**
         * Raised when the number of ads have changed. Adapters that implement this class
         * should notify their data views that the dataset has changed.
         */
        void onAdCountChanged();
    }
}