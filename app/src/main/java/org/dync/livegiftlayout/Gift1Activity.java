package org.dync.livegiftlayout;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.dync.giftlibrary.util.GiftPanelControl;
import org.dync.giftlibrary.widget.GiftControl;
import org.dync.giftlibrary.widget.GiftFrameLayout;
import org.dync.giftlibrary.widget.GiftModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Gift1Activity extends AppCompatActivity {

    private GiftFrameLayout giftFrameLayout1;
    private GiftFrameLayout giftFrameLayout2;

    private LinearLayout giftLayout;
    private LinearLayout ll_portrait;
    private LinearLayout ll_landscape;
    private TextView tvGiftNum;
    private ImageView btnGift;
    private ViewPager mViewpager;
    private LinearLayout mDotsLayout;

    private String mGifturl = "";
    private String mGiftName = "";
    private String mGiftPrice = "";
    private RecyclerView mRecyclerView;
    private GiftControl giftControl;
    private RecyclerView recyclerView;
    private GiftMsgAdapter adapter;
    private GiftModel giftModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift1);
        giftFrameLayout1 = (GiftFrameLayout) findViewById(R.id.gift_layout1);
        giftFrameLayout2 = (GiftFrameLayout) findViewById(R.id.gift_layout2);

        showGiftMsgList();

        initGiftLayout();

        List<GiftBean.GiftListBean> giftListBeen = fromNetData();//来自网络礼物图片
        List<GiftModel> giftModels = toGiftModel(giftListBeen);//转化为发送礼物的集合

        GiftPanelControl giftPanelControl = new GiftPanelControl(this, mViewpager, mRecyclerView, mDotsLayout);
        giftPanelControl.init(giftModels);//这里如果为null则加载本地礼物图片
        giftPanelControl.setGiftListener(new GiftPanelControl.GiftListener() {
            @Override
            public void getGiftInfo(String giftPic, String giftName, String giftPrice) {
                mGifturl = giftPic;
                mGiftName = giftName;
                mGiftPrice = giftPrice;
            }
        });
        giftControl = new GiftControl(giftFrameLayout1, giftFrameLayout2);
        tvGiftNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showGiftDialog();
            }
        });
        btnGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mGiftName)) {
                    Toast.makeText(getApplication(), "你还没选择礼物呢", Toast.LENGTH_SHORT).show();
                } else {
                    String numStr = tvGiftNum.getText().toString();
                    if (!TextUtils.isEmpty(numStr)) {
                        int giftnum = Integer.parseInt(numStr);
                        if (giftnum == 0) {
                            return;
                        } else {
                            //这里最好不要直接new对象
                            giftModel = new GiftModel(mGiftName, "礼物名字", giftnum, mGifturl, "1234", "吕靓茜", "", System.currentTimeMillis());
                            giftControl.loadGift(giftModel);
                            adapter.add(mGiftName);
                        }
                    }
                }
            }
        });
        findViewById(R.id.action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (giftLayout.getVisibility() == View.VISIBLE) {
                    giftLayout.setVisibility(View.GONE);
                } else {
                    giftLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //模拟从网络获取礼物url的集合
    private List<GiftBean.GiftListBean> fromNetData(){
        List<GiftBean.GiftListBean> list = new ArrayList<>();
        try {
            InputStream in= getAssets().open("gift.json");
            InputStreamReader json=new InputStreamReader(in);
            Gson gson = new Gson();
            GiftBean giftBean = gson.fromJson(json, GiftBean.class);
            list = giftBean.getGiftList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<GiftModel> toGiftModel(List<GiftBean.GiftListBean> datas){
        List<GiftModel> giftModels = new ArrayList<>();
        GiftModel giftModel;
        for (int i = 0; i < datas.size(); i++){
            GiftBean.GiftListBean giftListBean = datas.get(i);
            giftModel = new GiftModel(giftListBean.getGiftName(), giftListBean.getGiftPic(), giftListBean.getGiftPrice());
            giftModels.add(giftModel);
        }
        return giftModels;
    }

    private void showGiftMsgList() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GiftMsgAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void initGiftLayout() {
        ll_portrait = (LinearLayout) findViewById(R.id.ll_portrait);
        ll_landscape = (LinearLayout) findViewById(R.id.ll_landscape);
        giftLayout = (LinearLayout) findViewById(R.id.giftLayout);
        tvGiftNum = (TextView) findViewById(R.id.toolbox_tv_gift_num);
        btnGift = (ImageView) findViewById(R.id.toolbox_iv_face);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_gift);
        mViewpager = (ViewPager) findViewById(R.id.toolbox_pagers_face);
        mDotsLayout = (LinearLayout) findViewById(R.id.face_dots_container);

        giftLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里的作用是消费掉点击事件
            }
        });
    }

    private void showGiftDialog() {
        final GiftDialogFrament giftDialogFrament = new GiftDialogFrament();
        giftDialogFrament.show(getFragmentManager(), "GiftDialogFrament");
        giftDialogFrament.setGiftListener(new GiftDialogFrament.GiftListener() {
            @Override
            public void giftNum(String giftNum) {
                tvGiftNum.setText(giftNum);
                giftDialogFrament.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁动画
        if (giftControl != null) {
            giftControl.cleanAll();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {// 横屏
//            Log.e(TAG, "onConfigurationChanged: " + "横屏");
            onConfigurationLandScape();

        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Log.e(TAG, "onConfigurationChanged: " + "竖屏");
            onConfigurationPortrait();
        }
    }

    private void onConfigurationPortrait() {
        ll_portrait.setVisibility(View.VISIBLE);
        ll_landscape.setVisibility(View.GONE);
    }

    private void onConfigurationLandScape() {
        ll_portrait.setVisibility(View.GONE);
        ll_landscape.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (giftLayout.getVisibility() == View.VISIBLE) {
                    giftLayout.setVisibility(View.GONE);
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
