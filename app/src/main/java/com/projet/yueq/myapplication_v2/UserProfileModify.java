package com.projet.yueq.myapplication_v2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.leancloud.chatkit.LCChatKit;

/**
 * Created by delll on 2017/5/14.
 */

public class UserProfileModify extends AppCompatActivity {
    private int key;
    private String data;
    private String userId ,pwd;
    private final int KEY_NAME=1;
    private final int KEY_PWD=2;
    private MySqliteHelper helper;
    private final static String TABLE_NAME = "users";
    private final static String TABLE_FRI_NAME = "friends";

    private static final String EXTRA_KEY = "EXTRA_KEY";
    private static final String EXTRA_DATA = "EXTRA_DATA";

    private EditText newname,oldpwd,newpwd,newpwd1;
    private Button btn_confirm;

    public static final void start(Context context, int key,String data) {
        Intent intent = new Intent();
        intent.setClass(context, UserProfileModify.class);
        intent.putExtra(EXTRA_KEY, key);
        intent.putExtra(EXTRA_DATA, data);
        context.startActivity(intent);
    }

    public static final void start(Context context, int key) {
        Intent intent = new Intent();
        intent.setClass(context, UserProfileModify.class);
        intent.putExtra(EXTRA_KEY, key);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key = getIntent().getIntExtra(EXTRA_KEY, -1);
        data = getIntent().getStringExtra(EXTRA_DATA);
        helper=CustomUserProvider.getInstance().getHelper();
        if (key == KEY_NAME ) {
            setContentView(R.layout.name_modify);
            setTitle(R.string.name);
        } else if (key == KEY_PWD) {
            setContentView(R.layout.pwd_modify);
            setTitle(R.string.pwd);
        }
        btn_confirm=(Button)findViewById(R.id.confirm_button);
        btn_confirm.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (key == KEY_NAME ) {
                    EditName();
                }
                if (key == KEY_PWD) {
                    EditPwd();
                }
            }
        });
    }


    private void EditName(){
        newname=(EditText) findViewById(R.id.newname);

        if(newname.getText().toString().trim().equals("") ){
            Toast.makeText(this,"Can not be empty",Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            ContentValues values = new ContentValues();
            values.put("name",newname.getText().toString());
            helper.update(TABLE_NAME,values,"userId = ?", new String[]{LCChatKit.getInstance().getCurrentUserId()});
            Toast.makeText(this, "Modify username successfully", Toast.LENGTH_SHORT).show();
            //setContentView(R.layout.info_settings);
            finish();
        }
    }

    private void EditPwd(){
        oldpwd=(EditText) findViewById(R.id.oldpwd);
        newpwd=(EditText) findViewById(R.id.newpwd);
        newpwd1=(EditText) findViewById(R.id.newpwd1);
        if(newpwd.getText().toString().trim().equals("") || newpwd1.getText().toString().trim().equals("") ){
            Toast.makeText(this,"New password can not be empty",Toast.LENGTH_SHORT).show();
            return;
        }
        Cursor cursor = helper.query(TABLE_NAME);
        boolean flag=false;
        while (cursor.moveToNext()) {
            userId = cursor.getString(1);
            pwd = cursor.getString(3);
            if ((userId.equals(LCChatKit.getInstance().getCurrentUserId())) && (oldpwd.getText().toString().equals(pwd))){
                if(newpwd.getText().toString().equals(newpwd1.getText().toString())){
                    ContentValues values = new ContentValues();
                    values.put("pwd",newpwd.getText().toString());
                    helper.update(TABLE_NAME,values,"userId = ?", new String[]{LCChatKit.getInstance().getCurrentUserId()});
                    Toast.makeText(this, "Modify password successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    //setContentView(R.layout.info_settings);
                }
                else {
                    Toast.makeText(this, "New passwords are inconsistent,please enter again", Toast.LENGTH_SHORT).show();
                }
                flag=true;
                break;
            }
        }
        if(!flag){Toast.makeText(this, "Your password is wrong,please enter again", Toast.LENGTH_SHORT).show();}
    }

    protected <T extends View> T findView(int resId) {
        return (T) (findViewById(resId));
    }
}
