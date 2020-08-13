package com.iwangzhe.adlibrary.adv.serv;

import com.iwangzhe.adlibrary.AdApplication;
import com.iwangzhe.adlibrary.adv.AdSystemMain;
import com.iwangzhe.adlibrary.adv.model.JAdvInfo;
import com.iwangzhe.baselibrary.base.CommonRes;
import com.iwangzhe.baselibrary.base.IResCallback;
import com.iwangzhe.baselibrary.base.JBase;
import com.iwangzhe.baselibrary.base.ServApi;
import com.snappydb.SnappydbException;

import java.util.HashMap;
import java.util.Map;

/**
 * author : 亚辉
 * e-mail : 2372680617@qq.com
 * date   : 2020/7/3115:17
 * desc   :
 */
public class AdSystemServApi extends ServApi {
    private AdSystemMain mMain;

    public AdSystemServApi(AdSystemMain main) {
        super(main);
        mMain = main;
    }

    private static AdSystemServApi mAdSystemServApi = null;

    public static AdSystemServApi getInstance(AdSystemMain main) {
        synchronized (AdSystemServApi.class) {
            if (mAdSystemServApi == null) {
                mAdSystemServApi = new AdSystemServApi(main);
            }
        }
        return mAdSystemServApi;
    }

    public void getAdverts(String pageKey, String posKey, final IResCallback<JAdvInfo> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("pageKey", pageKey);
        params.put("posKey", posKey);
        AdApplication.getInstance().getmNetHttp().reqGetResByWzApi(JAdvInfo.class, "adv/position/fetch/", params, new IResCallback<JAdvInfo>() {
            @Override
            public void onFinish(CommonRes<JAdvInfo> res) {
                callback.onFinish(res);
            }
        });
    }

    public void reportAdShow(int mapId, int pos, int total, final IResCallback<JBase> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("mapId", "" + mapId);
        params.put("pos", "" + pos);
        params.put("total", "" + total);
        AdApplication.getInstance().getmNetHttp().reqGetResByWzApi(JBase.class, "adv/position/show/", params, new IResCallback<JBase>() {
            @Override
            public void onFinish(CommonRes<JBase> res) {
                callback.onFinish(res);
            }
        });
    }

    public CommonRes<JAdvInfo> getJAdvInfoFromDb(String key) {
        JAdvInfo jAdvInfo;
        try {
            jAdvInfo = AdApplication.getInstance().getmIoKvdb().getObject(mMain.getModName() + ":" + key, JAdvInfo.class);
        } catch (SnappydbException e) {
            return new CommonRes<>(false);
        }
        if (jAdvInfo == null) {
            return new CommonRes<>(true, 10001);
        }
        return new CommonRes<>(true, 0, jAdvInfo);
    }

    public CommonRes<JAdvInfo> setAdvInfoToDb(String key, JAdvInfo item) {
        try {
            AdApplication.getInstance().getmIoKvdb().put(mMain.getModName() + ":" + key, item);
        } catch (SnappydbException e) {
            return new CommonRes<>(false);
        }
        return new CommonRes<>(true);
    }
}
