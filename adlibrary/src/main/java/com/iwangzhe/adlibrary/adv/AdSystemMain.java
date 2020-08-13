package com.iwangzhe.adlibrary.adv;


import com.iwangzhe.adlibrary.adv.control.AdSystemControlApp;
import com.iwangzhe.adlibrary.adv.model.AdSystemModelApi;
import com.iwangzhe.adlibrary.adv.serv.AdSystemServApi;
import com.iwangzhe.baselibrary.base.ModMain;

/**
 * author : 小辉
 * e-mail : 2372680617@qq.com
 * date   : 2020/6/249:54
 * desc   :
 */
public class AdSystemMain extends ModMain {

    @Override
    public String getModName() {
        return "AdvCommonViewMain";
    }

    private static AdSystemMain mAdSystemMain = null;

    public static AdSystemMain getInstance() {
        synchronized (AdSystemMain.class) {
            if (mAdSystemMain == null) {
                mAdSystemMain = new AdSystemMain();
            }
        }
        return mAdSystemMain;
    }

    public final AdSystemServApi pServ;
    public final AdSystemControlApp pControl;
    public final AdSystemModelApi pModel;

    public AdSystemMain() {
        pModel = AdSystemModelApi.getInstance(this);
        pServ = AdSystemServApi.getInstance(this);
        pControl = AdSystemControlApp.getInstance(this);
    }

    @Override
    public void born() {
        super.born();
        pModel.born();
        pServ.born();
        pControl.born();
    }

    public AdSystemControlApp getControl() {
        return pControl;
    }
}
