package com.projet.yueq.myapplication_v2;

import android.app.Activity;
import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.LCChatProfileProvider;
import cn.leancloud.chatkit.LCChatProfilesCallBack;

/**
 * Created by delll on 2017/4/4.
 * 实现自定义用户体系
 */

public class CustomUserProvider implements LCChatProfileProvider {

    private static CustomUserProvider customUserProvider;
    private MySqliteHelper helper;
    private final static String TABLE_NAME = "users";


    public synchronized static CustomUserProvider getInstance(MySqliteHelper helper) {
        if (null == customUserProvider) {
            customUserProvider = new CustomUserProvider(helper);
        }
        return customUserProvider;
    }

    public synchronized static CustomUserProvider getInstance() {
        if (null == customUserProvider) {
            customUserProvider = new CustomUserProvider();
        }
        return customUserProvider;
    }

    private CustomUserProvider(MySqliteHelper helper) {
        this.helper=helper;
        init();
    }

    private CustomUserProvider() {}


    // 此数据均为 fake，仅供参考
    public void init() {
        if(helper.isEmpty(TABLE_NAME)){
            helper.insert(new LCChatKitUser("Tom", "Tom", "http://www.avatarsdb.com/avatars/tom_and_jerry2.jpg"));
            helper.insert(new LCChatKitUser("Jerry", "Jerry", "http://www.avatarsdb.com/avatars/jerry.jpg"));
            helper.insert(new LCChatKitUser("Harry", "Harry", "http://www.avatarsdb.com/avatars/young_harry.jpg"));
            helper.insert(new LCChatKitUser("William", "William", "http://www.avatarsdb.com/avatars/william_shakespeare.jpg"));
            helper.insert(new LCChatKitUser("Bob", "Bob", "http://www.avatarsdb.com/avatars/bath_bob.jpg"));
        }

    }

    @Override
    public void fetchProfiles(List<String> list, LCChatProfilesCallBack callBack) {
        List<LCChatKitUser> userList = new ArrayList<LCChatKitUser>();
        Cursor cursor=helper.query(TABLE_NAME);
        for (String userId : list) {
            while(cursor.moveToNext()){
                if(cursor.getString(1).equals(userId)){
                    userList.add(helper.getUser(cursor));
                    break;
                }
            }
        }
        callBack.done(userList, null);
    }


    public List getAllUsers(){
        List<LCChatKitUser> partuser = new ArrayList<LCChatKitUser>();
        Cursor cursor=helper.query(TABLE_NAME);
        while (cursor.moveToNext()){
            if(!cursor.getString(1).equals(LCChatKit.getInstance().getCurrentUserId())) {
                partuser.add(helper.getUser(cursor));
            }
        }
        return partuser;
    }

    public MySqliteHelper getHelper(){
        return this.helper;
    }

}
