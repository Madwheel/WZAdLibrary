package com.iwangzhe.adlibrary.adv.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.iwangzhe.adlibrary.AdManager;
import com.iwangzhe.adlibrary.R;

/**
 * author : 亚辉
 * e-mail : 2372680617@qq.com
 * date   : 2020/8/1116:12
 * desc   :
 */
public class AdView extends RelativeLayout {
    private String pageKey;
    private String posKey;

    public AdView(Context context) {
        this(context, null);
    }

    public AdView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.AdView);
        pageKey = type.getString(R.styleable.AdView_pageKey);
        posKey = type.getString(R.styleable.AdView_posKey);
        AdManager adManager = AdManager.getInstance(pageKey, posKey);
        adManager.loadAd(this, context);
        type.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
