package com.iwangzhe.adlibrary.adv.control;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.iwangzhe.adlibrary.AdApplication;
import com.iwangzhe.adlibrary.adv.AdSystemMain;
import com.iwangzhe.adlibrary.adv.model.AdvertplanList;
import com.iwangzhe.adlibrary.adv.model.IAdListener;
import com.iwangzhe.adlibrary.adv.model.JAdvInfo;
import com.iwangzhe.adlibrary.adv.model.OnAdClickListener;
import com.iwangzhe.adlibrary.adv.model.OnSlideShowListener;
import com.iwangzhe.adlibrary.adv.model.SplashAdInfo;
import com.iwangzhe.adlibrary.adv.view.SliderView;
import com.iwangzhe.adlibrary.adv.view.SplashAdActivity;
import com.iwangzhe.baselibrary.CornerTransform;
import com.iwangzhe.baselibrary.base.CommonRes;
import com.iwangzhe.baselibrary.base.ControlApp;
import com.iwangzhe.baselibrary.base.IResCallback;
import com.iwangzhe.baselibrary.base.JBase;
import com.iwangzhe.baselibrary.tool.BaseToolsMain;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author : 亚辉
 * e-mail : 2372680617@qq.com
 * date   : 2020/7/3115:10
 * desc   :
 */
public class AdSystemControlApp extends ControlApp {

    private AdSystemMain mMain;
    private IAdListener adListener;
    private Map<String, Long> reportMap;

    public AdSystemControlApp(AdSystemMain main) {
        super(main);
        mMain = main;
    }

    private static AdSystemControlApp mAdSystemControlApp = null;

    public static AdSystemControlApp getInstance(AdSystemMain main) {
        synchronized (AdSystemControlApp.class) {
            if (mAdSystemControlApp == null) {
                mAdSystemControlApp = new AdSystemControlApp(main);
            }
        }
        return mAdSystemControlApp;
    }

    @Override
    public void born() {
        super.born();
        reportMap = new HashMap<>();
    }

    /**
     * 初始化广告信息
     *
     * @param pageKey
     * @param posKey
     */
    public void initAdverts(final String pageKey, final String posKey) {
        mMain.pServ.getAdverts(pageKey, posKey, new IResCallback<JAdvInfo>() {
            @Override
            public void onFinish(CommonRes<JAdvInfo> res) {
                if (res.isOk()) {
                    JAdvInfo resObj = res.getResObj();
                    Map<String, JAdvInfo> advInfoMap = mMain.pModel.getAdvInfoMap();
                    advInfoMap.put(pageKey + posKey, resObj);
                    mMain.pModel.setAdvInfoMap(advInfoMap);
                    mMain.pServ.setAdvInfoToDb(pageKey + posKey, resObj);
                    if (adListener != null) {
                        adListener.onSuccess(pageKey, posKey);
                    }
                }
            }
        });
    }

    /**
     * 是否存在广告
     *
     * @param pageKey
     * @param posKey
     * @return
     */
    public boolean isExistAdv(String pageKey, String posKey) {
        CommonRes<JAdvInfo> jAdvInfoFromDb = mMain.pServ.getJAdvInfoFromDb(pageKey + posKey);
        if (jAdvInfoFromDb.isOk()) {
            JAdvInfo resObj = jAdvInfoFromDb.getResObj();
            if (resObj != null && resObj.getPlanList().size() > 0) {
                Map<String, JAdvInfo> advInfoMap = mMain.pModel.getAdvInfoMap();
                advInfoMap.put(pageKey + posKey, resObj);
                mMain.pModel.setAdvInfoMap(advInfoMap);
                return true;
            }
        }
        Map<String, JAdvInfo> advInfoMap = mMain.pModel.getAdvInfoMap();
        if (advInfoMap == null && advInfoMap.size() == 0) {
            return false;
        }
        JAdvInfo jAdvInfo = advInfoMap.get(pageKey + posKey);
        if (jAdvInfo != null && jAdvInfo.getPlanList().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 显示广告view
     *
     * @param advView
     * @param pageKey
     * @param posKey
     */
    public void showAdvView(SliderView advView, String pageKey, String posKey, OnSlideShowListener listener) {
        if (advView != null) {
            JAdvInfo jAdvInfo = mMain.pModel.getAdvInfoMap().get(pageKey + posKey);
            ArrayList<AdvertplanList> planList = jAdvInfo.getPlanList();
            if (planList.size() > 0) {
                //添加图片到图片列表里
                List<String> imageUrlList = new ArrayList<>();
                List<Integer> imageMapIdList = new ArrayList<>();
                List<String> jumpUrlList = new ArrayList<>();
                for (int i = 0; i < planList.size(); i++) {
                    jumpUrlList.add(planList.get(i).getJumpUrl());
                    JSONObject jsonObject = BaseToolsMain.getInstance().getControl().getJSONObject(planList.get(i).getPics());
                    String string = jsonObject.getString("img_" + jAdvInfo.getPositionInfo().getWidth() + "x" + jAdvInfo.getPositionInfo().getHeight());
                    imageUrlList.add(string);
                    imageMapIdList.add(planList.get(i).getMapId());
                }
                advView.bindData(jumpUrlList, imageUrlList, imageMapIdList, listener);
            } else {
                advView.bindData(new ArrayList<String>(), new ArrayList<String>(), new ArrayList<Integer>(), null);
            }

        }
    }

    public void setAdListener(IAdListener adListener) {
        this.adListener = adListener;
    }

    public void showSplashAd(String pageKey, String posKey, String logo, Activity activity) {
        if (isExistAdv(pageKey, posKey)) {
            Intent intent = new Intent(activity, SplashAdActivity.class);
            intent.putExtra("pageKey", pageKey);
            intent.putExtra("posKey", posKey);
            intent.putExtra("logo", logo);
            activity.startActivity(intent);
        } else {
            AdApplication.getInstance().getmIRouter().jumpToMain(null, null, false);
        }

    }

    /**
     * 广告位展示统计上报
     *
     * @param mapId (可选）广告素材id
     * @param pos   位置 0，1，2，。。
     * @param total 轮播图总数（多连图）
     */
    public void reportAdShow(int mapId, int pos, int total) {
        String key = "" + mapId + pos + total;
        if (reportMap != null && reportMap.size() > 0 && reportMap.containsKey(key)) {
            Long aLong = reportMap.get(key);
            if (System.currentTimeMillis() - aLong < 5 * 1000) {
                return;
            }
        }
        reportMap.put(key, System.currentTimeMillis());
        mMain.pServ.reportAdShow(mapId, pos, total, new IResCallback<JBase>() {
            @Override
            public void onFinish(CommonRes<JBase> res) {

            }
        });
    }

    public SplashAdInfo getSplashAdInfo(String pageKey, String posKey) {
        Map<String, JAdvInfo> advInfoMap = mMain.pModel.getAdvInfoMap();
        SplashAdInfo splashAdInfo = new SplashAdInfo();
        if (advInfoMap != null && advInfoMap.size() > 0 && advInfoMap.containsKey(pageKey + posKey)) {
            JAdvInfo jAdvInfo = advInfoMap.get(pageKey + posKey);
            ArrayList<AdvertplanList> planList = jAdvInfo.getPlanList();
            if (planList.size() > 0) {
                JSONObject jsonObject = BaseToolsMain.getInstance().getControl().getJSONObject(planList.get(0).getPics());
                //添加图片到图片列表里
                splashAdInfo.setUrl(planList.get(0).getJumpUrl());
                splashAdInfo.setTitle(planList.get(0).getTitle());
                splashAdInfo.setMid(planList.get(0).getMapId());
//                splashAdInfo.setImg_1080_1600(jsonObject.getString("img_" + jAdvInfo.getPositionInfo().getWidth() + "x" + jAdvInfo.getPositionInfo().getHeight()));
//                splashAdInfo.setImg_1080_1920(jsonObject.getString("img_" + jAdvInfo.getPositionInfo().getWidth() + "x" + jAdvInfo.getPositionInfo().getHeight()));
//                splashAdInfo.setImg_1080_1920(jsonObject.getString("img_" + jAdvInfo.getPositionInfo().getWidth() + "x" + jAdvInfo.getPositionInfo().getHeight()));
                splashAdInfo.setImg_1080_1600(jsonObject.getString("img_1080x1600"));
                splashAdInfo.setImg_1080_1920(jsonObject.getString("img_1142x2208"));
                splashAdInfo.setImg_1080_1920(jsonObject.getString("img_1125x2436"));
                mMain.pModel.setSplashAdInfo(splashAdInfo);
            }
        }
        return splashAdInfo;
    }

    public void setAdvInfoMap(Map<String, JAdvInfo> advInfoMap) {
        mMain.pModel.setAdvInfoMap(advInfoMap);
    }

    public View getView(String pageKey, String posKey, Context context, final OnAdClickListener listener) {
        boolean existAdv = isExistAdv(pageKey, posKey);
        if (!existAdv) {
            return null;
        }
        JAdvInfo jAdvInfo = mMain.pModel.getAdvInfoMap().get(pageKey + posKey);
        int advType = jAdvInfo.getPositionInfo().getAdvType();
        if (advType == 4) {
            return getSliderView(context, listener, jAdvInfo);
        }
//        else if (advType == 2) {//开屏广告
//        }
        return null;
    }

    @NonNull
    private View getSliderView(Context context, final OnAdClickListener listener, JAdvInfo jAdvInfo) {
        SliderView slideShowView = new SliderView(context);
        ArrayList<AdvertplanList> planList = jAdvInfo.getPlanList();
        if (planList.size() > 0) {
            //添加图片到图片列表里
            List<String> imageUrlList = new ArrayList<>();
            List<Integer> imageMapIdList = new ArrayList<>();
            List<String> jumpUrlList = new ArrayList<>();
            for (int i = 0; i < planList.size(); i++) {
                jumpUrlList.add(planList.get(i).getJumpUrl());
                JSONObject jsonObject = BaseToolsMain.getInstance().getControl().getJSONObject(planList.get(i).getPics());
                String string = jsonObject.getString("img_" + jAdvInfo.getPositionInfo().getWidth() + "x" + jAdvInfo.getPositionInfo().getHeight());
                imageUrlList.add(string);
                imageMapIdList.add(planList.get(i).getMapId());
            }
            slideShowView.bindData(jumpUrlList, imageUrlList, imageMapIdList, new OnSlideShowListener() {
                @Override
                public void onItemClick(int position, String imgUrl, String url, String resourEntryName) {
                    if (listener != null) {
                        listener.onItemClick(position, imgUrl, url, resourEntryName);
                    }
                }

                @Override
                public void displayImage(Context context, Object path, ImageView imageView) {
                    loadImage(context, path, imageView);
                }

                @Override
                public void onItemSelected(int mapId, int position, int total) {
                    reportAdShow(mapId, position, total);
                }
            });
        } else {
            slideShowView.bindData(new ArrayList<String>(), new ArrayList<String>(), new ArrayList<Integer>(), null);
        }
        return slideShowView;
    }

    private void loadImage(Context context, Object path, ImageView imageView) {
        CornerTransform transformation = new CornerTransform(context, BaseToolsMain.getInstance().getControl().dip2px(context, 5));
        transformation.setExceptCorner(false, false, false, false);
        Glide.with(context)
                .asBitmap()
                .load(path)
                .apply(RequestOptions.bitmapTransform(transformation).diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(imageView);
    }
}
