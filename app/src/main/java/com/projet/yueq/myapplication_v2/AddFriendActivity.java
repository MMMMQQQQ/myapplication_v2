package com.projet.yueq.myapplication_v2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.leancloud.chatkit.LCChatKit;

/**
 * Created by delll on 2017/4/19.
 */

public class AddFriendActivity extends AppCompatActivity{
    // private Toolbar toolbar;
    private EditText userId;
    private Button btn_add;
    private MySqliteHelper helper;
    private final static String TABLE_NAME = "users";

    public static final void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, AddFriendActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        // toolbar=(Toolbar)findViewById(R.id.toolbar);
        setTitle(R.string.add_buddy);

        Toolbar toolbar_AddFriend = (Toolbar) findViewById(R.id.toolbar_addFriend);
        setSupportActionBar(toolbar_AddFriend);

        userId = (EditText) findViewById(R.id.userId);
        helper=CustomUserProvider.getInstance().getHelper();
        initActionbar();
    }

    private void initActionbar() {
        btn_add=(Button) findViewById(R.id.add_button);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(userId.getText().toString())) {
                    Toast.makeText(AddFriendActivity.this, R.string.not_allow_empty, Toast.LENGTH_SHORT).show();
                } else if (userId.getText().toString().equals(LCChatKit.getInstance().getCurrentUserId())) {
                    Toast.makeText(AddFriendActivity.this, R.string.add_friend_self_tip, Toast.LENGTH_SHORT).show();
                } else {
                    add(userId.getText().toString());
                }
            }
        });
    }

    private void add(String id) {
        //final String account = username.getText().toString();
        boolean flag=false;
        Cursor cursor = helper.query(TABLE_NAME);
        while (cursor.moveToNext()) {
            if(cursor.getString(1).equals(id)){
                flag=true;
                break;
            }
        }
        if(!flag){
            Toast.makeText(AddFriendActivity.this, R.string.user_not_exsit, Toast.LENGTH_SHORT).show();
        }
        else {
            if(!helper.insert(LCChatKit.getInstance().getCurrentUserId(),id)){
                Toast.makeText(AddFriendActivity.this, R.string.friend_exsit, Toast.LENGTH_SHORT).show();
            }else Toast.makeText(AddFriendActivity.this, R.string.add_successful, Toast.LENGTH_SHORT).show();
        }
    }

}
