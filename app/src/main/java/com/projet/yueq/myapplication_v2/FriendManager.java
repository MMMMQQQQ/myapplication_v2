package com.projet.yueq.myapplication_v2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.LCChatKitUser;


/**
 * Created by delll on 2017/5/10.
 */

public class FriendManager extends AppCompatActivity{
    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private List<LCChatKitUser> frilist;
    private List<Map<String, Object>> datas=new ArrayList<Map<String, Object>>();


    public static final void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, FriendManager.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_list);
        setTitle(R.string.contacts_list);

        Toolbar toolbar_FriendManager = (Toolbar) findViewById(R.id.toolbar_FriendManager);
        setSupportActionBar(toolbar_FriendManager);

        listView=(ListView)findViewById(R.id.listview);
        frilist=CustomUserProvider.getInstance().getHelper().getAllFri(LCChatKit.getInstance().getCurrentUserId());
        if(frilist.size()>=1){
            for(LCChatKitUser user:frilist){
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("_id",user.getUserName());
                if (!TextUtils.isEmpty(user.getAvatarUrl())) {
                    data.put("img", returnBitmap(user.getAvatarUrl()));
                } else {
                    data.put("img", R.drawable.lcim_default_avatar_icon);
                }
                datas.add(data);
            }
        }
        simpleAdapter=new SimpleAdapter(this,datas,R.layout.common_user_item,new String[]{"_id","img"},new int[]{R.id.tv_friend_name,R.id.img_friend_avatar});
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView iv = (ImageView) view;
                    iv.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        listView.setAdapter(simpleAdapter);//设置配置器
        registerForContextMenu(listView);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 0, 0, "delete this contact");
        //menu.add(0,1,1,"delete all contacts");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        if (item.getItemId()==0) DeleteOneFriend(datas.get(position).get("_id").toString());
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friend_manager_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.add_buddy)
            AddFriendActivity.start(FriendManager.this);

        return super.onOptionsItemSelected(item);
    }

    private void DeleteOneFriend(String id){
        CustomUserProvider.getInstance().getHelper().delete(LCChatKit.getInstance().getCurrentUserId(),id);
        Toast.makeText(this, "Delete successfully", Toast.LENGTH_SHORT).show();
    }

    private Bitmap returnBitmap(String url) {
        URL fileUrl = null;
        Bitmap bitmap = null;

        try {
            fileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) fileUrl
                    .openConnection();
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
}
