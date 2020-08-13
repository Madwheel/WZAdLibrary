package com.iwangzhe.adlibrary.adv.model;


import com.iwangzhe.adlibrary.adv.AdSystemMain;
import com.iwangzhe.baselibrary.base.ModelApi;

import java.util.HashMap;
import java.util.Map;

/**
 * author : 亚辉
 * e-mail : 2372680617@qq.com
 * date   : 2020/7/3116:18
 * desc   :
 */
public class AdSystemModelApi extends ModelApi {

    private AdSystemMain mMain;
    private SplashAdInfo splashAdInfo;

    public AdSystemModelApi(AdSystemMain main) {
        super(main);
        mMain = main;
    }

    private static AdSystemModelApi mAdSystemModelApi = null;

    public static AdSystemModelApi getInstance(AdSystemMain main) {
        synchronized (AdSystemModelApi.class) {
            if (mAdSystemModelApi == null) {
                mAdSystemModelApi = new AdSystemModelApi(main);
            }
        }
        return mAdSystemModelApi;
    }

    private Map<String, JAdvInfo> advInfoMap;

    @Override
    public void born() {
        super.born();
        advInfoMap = new HashMap<>();
        splashAdInfo = new SplashAdInfo();
    }

    public Map<String, JAdvInfo> getAdvInfoMap() {
        return advInfoMap;
    }

    public SplashAdInfo getSplashAdInfo() {
        return splashAdInfo;
    }

    public void setSplashAdInfo(SplashAdInfo splashAdInfo) {
        this.splashAdInfo = splashAdInfo;
    }

    public void setAdvInfoMap(Map<String, JAdvInfo> advInfoMap) {
        this.advInfoMap = advInfoMap;
    }
}
