package com.projet.yueq.myapplication_v2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.leancloud.chatkit.LCChatKit;

/**
 * Created by delll on 2017/4/19.
 */

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private int KEY_NAME=1;
    private int KEY_PWD=2;

    // view
    private   RelativeLayout headLayout;
    private RelativeLayout idLayout;
    private RelativeLayout nameLayout;
    private RelativeLayout pwdLayout;
    private TextView idText;
    private TextView nameText;
    private TextView pwdText;
    private ImageView iv_photo;
    private Bitmap head;// 头像Bitmap
    private static String path =null;// sd路径
    private static String head_name="head.jpg";

    public static final void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_settings);
        setTitle(R.string.personal_info);

        Toolbar toolbar_settings = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar_settings);
        getSupportActionBar().setTitle("Personal Information");


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        findViews();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        idText.setText(LCChatKit.getInstance().getCurrentUserId());
        nameText.setText(CustomUserProvider.getInstance().getHelper().getUser(LCChatKit.getInstance().getCurrentUserId()).getUserName());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init(){
        path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/com.projet.yueq.myapplication_v2/myHead/";
        Bitmap bt = BitmapFactory.decodeFile(path + head_name);// 从SD卡中找头像，转换成Bitmap
        if (bt != null) {
            @SuppressWarnings("deprecation")
            Drawable drawable = new BitmapDrawable(bt);// 转换成drawable
            iv_photo.setImageDrawable(drawable);
        } else {
            /**
             * 如果SD里面没有则需要从服务器取头像，取回来的头像再保存在SD中
             *
             */
//            String url=CustomUserProvider.getInstance().getHelper().getUser(LCChatKit.getInstance().getCurrentUserId()).getAvatarUrl();
//            if (!TextUtils.isEmpty(url)) {
//                Bitmap bt=returnBitmap(url);
//                iv_photo.setImageDrawable(new BitmapDrawable(bt));
//            } else {
            iv_photo.setImageDrawable(getDrawable(R.drawable.lcim_default_avatar_icon));
            //     }
        }
    }

//    private void setPicToView(Bitmap mBitmap) {
//        String sdStatus = Environment.getExternalStorageState();
//        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
//            return;
//        }
//        FileOutputStream b = null;
//        File file = new File(path);
//        if (!file.exists()) {
//            try {
//                //按照指定的路径创建文件夹
//                file.mkdirs();
//            } catch (Exception e) {
//                // TODO: handle exception
//            }
//        }
//        String fileName = path + head_name;// 图片名字
//        try {
//            b = new FileOutputStream(fileName);
//            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
//            b.flush();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private Bitmap returnBitmap(String url) {
        URL fileUrl = null;
        Bitmap bitmap = null;
        try {
            fileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void findViews() {
        headLayout = findView(R.id.head_layout);
        idLayout = findView(R.id.userId_layout);
        nameLayout = findView(R.id.name_edit_layout);
        pwdLayout = findView(R.id.pwd_edit_layout);

        ((TextView) headLayout.findViewById(R.id.attribute)).setText(R.string.avatar);
        ((TextView) idLayout.findViewById(R.id.attribute)).setText(R.string.userId);
        ((TextView) nameLayout.findViewById(R.id.attribute)).setText(R.string.name);
        ((TextView) pwdLayout.findViewById(R.id.attribute)).setText(R.string.pwd);

        iv_photo = (ImageView) headLayout.findViewById(R.id.iv_photo);
        idText = (TextView) idLayout.findViewById(R.id.value);
        nameText = (TextView) nameLayout.findViewById(R.id.value);
        pwdText = (TextView) pwdLayout.findViewById(R.id.value);

        headLayout.setOnClickListener(this);
        nameLayout.setOnClickListener(this);
        pwdLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_layout:
                iv_photo.setDrawingCacheEnabled(true);
                Bitmap head=iv_photo.getDrawingCache();
                iv_photo.setDrawingCacheEnabled(false);
                AvatarEditActivity.start(SettingsActivity.this);
                break;
            case R.id.name_edit_layout:
                UserProfileModify.start(SettingsActivity.this, KEY_NAME,nameText.getText().toString());
                break;
            case R.id.pwd_edit_layout:
                UserProfileModify.start(SettingsActivity.this, KEY_PWD);
                break;
        }
    }

    protected <T extends View> T findView(int resId) {
        return (T) (findViewById(resId));
    }
}
