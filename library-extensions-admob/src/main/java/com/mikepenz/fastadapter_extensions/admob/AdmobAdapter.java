package com.mikepenz.fastadapter_extensions.admob;

import android.content.Context;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.mikepenz.fastadapter_extensions.adapters.AbstractWrapAdapter;
import com.mikepenz.fastadapter_extensions.admob.items.AdBannerItem;
import com.mikepenz.fastadapter_extensions.admob.items.AdContentItem;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mikepenz on 06.03.16.
 */
public class AdmobAdapter extends AbstractWrapAdapter implements AdmobFetcher.AdmobListener {

    protected int mAdCount;
    protected AdmobFetcher mAdFetcher;


    public AdmobAdapter(List<AdContentItem> items) {
        super(items);
    }

    public static AdmobAdapter fromAdCount(Context ctx, int adCount) {
        LinkedList ads = new LinkedList();
        for (int i = 0; i < adCount; i++) {
            ads.add(new AdBannerItem());
        }

        AdmobAdapter adMobAdapter = new AdmobAdapter(ads);
        adMobAdapter.mAdCount = adCount;

        /*
        adMobAdapter.mAdFetcher = new AdmobFetcher();
        adMobAdapter.mAdFetcher.setTestDeviceId("DEBC2FA2BFD4A2460CC0BE330DFFCFD5");
        adMobAdapter.mAdFetcher.addListener(adMobAdapter);
        // Start prefetching ads
        adMobAdapter.mAdFetcher.prefetchAds(ctx.getApplicationContext());
*/

        return adMobAdapter;
    }

    @Override
    public boolean shouldInsertItemAtPosition(int position) {
        if (getAdapter().getItemCount() > 0 && getItems().size() > 0) {
            int itemsInBetween = (getAdapter().getItemCount() + getItems().size()) / (getItems().size() + 1) + 1;
            return (position + 1) % itemsInBetween == 0;
        }
        return false;
    }

    @Override
    public int itemInsertedBeforeCount(int position) {
        if (getAdapter().getItemCount() > 0 && getItems().size() > 0) {
            int itemsInBetween = (getAdapter().getItemCount() + getItems().size()) / (getItems().size() + 1) + 1;
            return position / itemsInBetween;
        }
        return 0;
    }

    @Override
    public void onAdCountChanged() {
        List<AdContentItem> adContentItems = new LinkedList<>();
        NativeAd[] ads = (NativeAd[]) mAdFetcher.getAdMapAtIndex().values().toArray();

        int count = Math.min(mAdCount, ads.length);
        for (int i = 0; i < count; i++) {
            adContentItems.add(new AdContentItem().withAd((NativeContentAd) ads[i]));
        }

        setItems(adContentItems);
    }
}
