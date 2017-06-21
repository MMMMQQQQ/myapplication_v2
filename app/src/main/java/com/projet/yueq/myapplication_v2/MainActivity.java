package com.projet.yueq.myapplication_v2;

import android.app.Application;

import cn.leancloud.chatkit.LCChatKit;
/**
 * Created by delll on 2017/4/4.
 */

public class MainActivity extends Application {

    // appId、appKey 可以在「LeanCloud  控制台 > 设置 > 应用 Key」获取
    private final String APP_ID = "pXhe2R2qtiA38PBEW8A7XFv9-gzGzoHsz";
    private final String APP_KEY = "FJsXNGnMuC6aPnoC9Xe1VdUN";

    @Override
    public void onCreate() {
        super.onCreate();
        // 关于 CustomUserProvider 可以参看后面的文档
        MySqliteHelper helper=new MySqliteHelper(getApplicationContext());
        LCChatKit.getInstance().setProfileProvider(CustomUserProvider.getInstance(helper));
        LCChatKit.getInstance().init(getApplicationContext(), APP_ID, APP_KEY);
    }
}