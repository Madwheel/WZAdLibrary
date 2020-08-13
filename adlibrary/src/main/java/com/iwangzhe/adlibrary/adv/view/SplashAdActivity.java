package com.iwangzhe.adlibrary.adv.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.iwangzhe.adlibrary.AdApplication;
import com.iwangzhe.adlibrary.R;
import com.iwangzhe.adlibrary.adv.AdSystemMain;
import com.iwangzhe.adlibrary.adv.model.SplashAdInfo;
import com.iwangzhe.baselibrary.WZBaseActivity;
import com.iwangzhe.baselibrary.tool.BaseToolsMain;

/**
 * author : 亚辉
 * e-mail : 2372680617@qq.com
 * date   : 2020/8/1214:06
 * desc   :
 */
public class SplashAdActivity extends WZBaseActivity implements AdCountDownView.OnCountDownFinishListener {
    private AdCountDownView cpb_countdown;//倒计时进度条
    private boolean isCancleADCountdown = false;//是否取消广告倒计时结束跳转主页
    private RelativeLayout rl_ad_whole;
    private AdSystemMain mMain;
    private RelativeLayout rl_ad;
    private ImageView iv_ad;
    private ImageView iv_full_screen;
    private ImageView iv_ad_logo;
    private String adUrl;
    private String pageKey;
    private String posKey;
    private String logo;
    private SplashAdInfo splashAdInfo;
    private String adTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_adsystem);
        mMain = AdSystemMain.getInstance();
        Intent intent = getIntent();
        pageKey = intent.getStringExtra("pageKey");
        posKey = intent.getStringExtra("posKey");
        logo = intent.getStringExtra("logo");
        splashAdInfo = mMain.getControl().getSplashAdInfo(pageKey, posKey);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        // 跳过广告
        cpb_countdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCancleADCountdown = true;
                jumpToMain();
                finish();
            }
        });
        rl_ad_whole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(adUrl)) {
                    isCancleADCountdown = true;
                    AdApplication.getInstance().getmIRouter().startWebview(adUrl, adTitle, false);
                    finish();
                }
            }
        });
    }

    private void initData() {
        adUrl = splashAdInfo.getUrl();
        adTitle = splashAdInfo.getTitle();
        RequestOptions options = new RequestOptions().dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL);
        iv_ad_logo.setImageResource(BaseToolsMain.getInstance().getControl().getImgFromStr(this, logo));
        if (!TextUtils.isEmpty(adUrl)) {
            cpb_countdown.setVisibility(View.VISIBLE);
            rl_ad_whole.setClickable(true);
        } else {
            cpb_countdown.setVisibility(View.GONE);
            rl_ad_whole.setClickable(false);
        }
        String imgUrl = splashAdInfo.getImg_1080_1920();
        String fullScreenImg = imgUrl;
        if (BaseToolsMain.getInstance().getControl().isNormalWindow(SplashAdActivity.this)) {
            String url = splashAdInfo.getImg_1080_1600();
            if (!TextUtils.isEmpty(url)) {
                imgUrl = url;
            }
        } else {
            fullScreenImg = splashAdInfo.getImg_1125_2436();
        }
        if (!TextUtils.isEmpty(imgUrl)) {
            rl_ad.setVisibility(View.VISIBLE);
            iv_full_screen.setVisibility(View.GONE);
            Glide.with(SplashAdActivity.this)
                    .load(imgUrl)
                    .apply(options)
                    .into(iv_ad);
        } else {
            rl_ad.setVisibility(View.GONE);
            iv_full_screen.setVisibility(View.VISIBLE);
            Glide.with(SplashAdActivity.this)
                    .load(fullScreenImg)
                    .apply(options)
                    .into(iv_full_screen);
        }
        cpb_countdown.startCountDown();
        cpb_countdown.setOnCountDownFinishListener(this);
    }

    private void initView() {
        cpb_countdown = findViewById(R.id.cpb_countdown);
        rl_ad_whole = findViewById(R.id.rl_ad_whole);
        rl_ad = findViewById(R.id.rl_ad);
        iv_ad = findViewById(R.id.iv_ad);
        iv_full_screen = findViewById(R.id.iv_full_screen);
        iv_ad_logo = findViewById(R.id.iv_ad_logo);
    }

    @Override
    public void countDownFinished() {
        if (!isCancleADCountdown) {
            jumpToMain();
            finish();
        }
    }

    private void jumpToMain() {
        AdApplication.getInstance().getmIRouter().jumpToMain(null, null, false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        jumpToMain();
        finish();
        return super.onKeyDown(keyCode, event);
    }

}
