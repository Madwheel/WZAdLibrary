package com.iwangzhe.adlibrary;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.iwangzhe.adlibrary.adv.AdSystemMain;
import com.iwangzhe.adlibrary.adv.model.IAdListener;
import com.iwangzhe.adlibrary.adv.model.OnAdClickListener;

/**
 * author : 亚辉
 * e-mail : 2372680617@qq.com
 * date   : 2020/8/1113:43
 * desc   :
 */
public class AdManager {
    private String mPageKey;
    private String mPosKey;

    private static AdManager mAdManager = null;

    public static AdManager getInstance(String pageKey, String posKey) {
        synchronized (AdManager.class) {
            if (mAdManager == null) {
                mAdManager = new AdManager(pageKey, posKey);
            }
        }
        return mAdManager;
    }

    private AdManager(String pageKey, String posKey) {
        this.mPageKey = pageKey;
        this.mPosKey = posKey;
        initAd();
    }

    public void loadAd(final ViewGroup view, final Context context) {
        setView(view, context);
        setAdListener(new IAdListener() {
            @Override
            public void onSuccess(String pageKey, String posKey) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        setView(view, context);
                    }
                });
            }
        });
    }

    public void loadSplashAd(Activity activity, String logo) {
        AdSystemMain.getInstance().getControl().showSplashAd(mPageKey, mPosKey, logo, activity);
    }

    private void initAd() {
        AdSystemMain.getInstance().getControl().initAdverts(mPageKey, mPosKey);
    }

    private void setView(ViewGroup viewGroup, Context context) {
        View view = getView(context, getListener());
        viewGroup.removeAllViews();
        if (view == null) {
            viewGroup.setVisibility(View.GONE);
        } else {
            viewGroup.setVisibility(View.VISIBLE);
            viewGroup.addView(view);
        }
    }

    @NonNull
    private OnAdClickListener getListener() {
        return new OnAdClickListener() {
            @Override
            public void onItemClick(int position, String imgUrl, String url, String resourEntryName) {
                AdApplication.getInstance().getmIRouter().startWebview(url, null, false);
            }
        };
    }

    private View getView(Context context, OnAdClickListener listener) {
        return AdSystemMain.getInstance().getControl().getView(mPageKey, mPosKey, context, listener);
    }

    private void setAdListener(IAdListener adListener) {
        AdSystemMain.getInstance().getControl().setAdListener(adListener);
    }
}
