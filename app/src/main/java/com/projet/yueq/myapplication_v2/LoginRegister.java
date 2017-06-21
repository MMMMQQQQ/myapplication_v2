package com.projet.yueq.myapplication_v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;

import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.LCChatKitUser;

/**
 * Created by delll on 2017/4/4.
 */

public class LoginRegister extends AppCompatActivity implements View.OnClickListener{

    private Button btn_reg,btn_log;
    private EditText edit_userId,edit_pwd;
    private MySqliteHelper helper;
    private CheckBox checkbox;
    private String userId ,pwd;
    private final static String TABLE_NAME = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        checkIsRemenber();

        Toolbar toolbar_login = (Toolbar) findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar_login);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    //判断记住密码是否选中
    private void checkIsRemenber() {
        SharedPreferences sp = getSharedPreferences("user", 0);
        boolean isRemenber = sp.getBoolean("isCheck", true);
        if(isRemenber){
            checkbox.setChecked(true);
            edit_userId.setText(sp.getString("userId",""));
            edit_pwd.setText(sp.getString("pwd",""));
        }
    }

    //初始化View
    private void initView() {
        helper=CustomUserProvider.getInstance().getHelper();
        btn_reg = (Button) findViewById(R.id.btn_reg);
        btn_log = (Button) findViewById(R.id.btn_log);
        edit_userId = (EditText) findViewById(R.id.edit_userId);
        edit_pwd = (EditText) findViewById(R.id.edit_pwd);
        //记住密码
        checkbox = (CheckBox) findViewById(R.id.checkBox);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences sp = getSharedPreferences("user", 0);
                SharedPreferences.Editor edit = sp.edit();
                if(isChecked){
                    edit.putBoolean("isCheck",true);
                    edit.commit();
                }else{
                    edit.putBoolean("isCheck",false);
                    edit.commit();
                }
            }
        });
        btn_reg.setOnClickListener(this);
        btn_log.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //注册
            case R.id.btn_reg:
                register();
                break;
            //登录
            case R.id.btn_log:
                login();;
                break;
        }
    }

    //注册
    private void register(){
        //查询用户名是否重复
        Cursor cursor = helper.query(TABLE_NAME);
        boolean flag=false;

        if(edit_userId.getText().toString().trim().equals("")||
                edit_pwd.getText().toString().trim().equals("")) {
            Toast.makeText(this,"Can not be empty",Toast.LENGTH_SHORT).show();
        }else{
            while(cursor.moveToNext()){
                userId = cursor.getString(1);
                pwd = cursor.getString(3);
                if(edit_userId.getText().toString().equals(userId)){
                    Toast.makeText(this,"Already exist,please register again",Toast.LENGTH_SHORT).show();
                    flag=true;
                    break;
                }
            }
            if(!flag){
                helper.insert(new LCChatKitUser(edit_userId.getText().toString(),edit_userId.getText().toString(),""),edit_pwd.getText().toString());
                Toast.makeText(this, "Register successfully", Toast.LENGTH_SHORT).show();
                edit_userId.setText("");
                edit_pwd.setText("");
            }
        }
    }

    //登录
    public void login() {
        Cursor cursor = helper.query(TABLE_NAME);
        boolean flag=false;
        while (cursor.moveToNext()) {
            //第一列为id
            userId = cursor.getString(1); //获取第2列的值,第一列的索引从0开始
            pwd = cursor.getString(3);//获取第4列的值
            flag=checkNameWithPwd(userId,pwd);
            if(flag) break;
        }
        if(!flag){Toast.makeText(this, "User Id or password is wrong,please enter again", Toast.LENGTH_SHORT).show();}
        cursor.close();
        // UserInfoCache.getHelper().close();
        //Toast.makeText(this, "已经关闭数据库", Toast.LENGTH_SHORT).show();
    }

    //判断用户名与密码是否匹配
    private boolean checkNameWithPwd(String id, String pwd) {
        if ((edit_userId.getText().toString().equals(id)) && (edit_pwd.getText().toString().equals(pwd))) {
            SharedPreferences sp = getSharedPreferences("user",0);
            SharedPreferences.Editor edit = sp.edit();
            if(checkbox.isChecked()){//勾选记住密码
                edit.putBoolean("isCheck",true);
                edit.putString("userId",id);
                edit.putString("pwd",pwd);
                edit.commit();
            }else{
                edit.clear();
                edit.commit();
            }

            LCChatKit.getInstance().open(userId, new AVIMClientCallback() {
                @Override
                public void done(AVIMClient avimClient, AVIMException e) {
                    if (null == e) {
                        Toast.makeText(LoginRegister.this, "Login successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(LoginRegister.this, MapsActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginRegister.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return true;
        } else{ return false;}

    }

}
