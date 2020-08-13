package com.iwangzhe.adlibrary;

import com.iwangzhe.adlibrary.adv.AdSystemMain;
import com.iwangzhe.baselibrary.IRouter;
import com.iwangzhe.baselibrary.IoKvdb;
import com.iwangzhe.baselibrary.NetHttp;

/**
 * author : 亚辉
 * e-mail : 2372680617@qq.com
 * date   : 2020/8/1113:46
 * desc   :
 */
public class AdApplication {
    private static AdApplication mAdApplication = null;

    public static AdApplication getInstance() {
        synchronized (AdApplication.class) {
            if (mAdApplication == null) {
                mAdApplication = new AdApplication();
            }
        }
        return mAdApplication;
    }

    public NetHttp mNetHttp;
    private IoKvdb mIoKvdb;
    private IRouter mIRouter;

    public void init(NetHttp netHttp, IoKvdb ioKvdb, IRouter iRouter) {
        this.mNetHttp = netHttp;
        this.mIoKvdb = ioKvdb;
        this.mIRouter = iRouter;
        AdSystemMain.getInstance().born();
    }

    public NetHttp getmNetHttp() {
        return mNetHttp;
    }

    public IRouter getmIRouter() {
        return mIRouter;
    }

    public IoKvdb getmIoKvdb() {
        return mIoKvdb;
    }

}
