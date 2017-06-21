package com.projet.yueq.myapplication_v2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.activity.LCIMConversationActivity;
import cn.leancloud.chatkit.activity.LCIMConversationListFragment;
import cn.leancloud.chatkit.utils.LCIMConstants;

/**
 * Created by delll on 2017/4/4.
 */

public class App extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    /**
     * 上一次点击 back 键的时间
     * 用于双击退出的判断
     */
    private static long lastBackTime = 0;

    /**
     * 当双击 back 键在此间隔内是直接触发 onBackPressed
     */
    private final int BACK_INTERVAL = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager)findViewById(R.id.pager);
        tabLayout = (TabLayout)findViewById(R.id.tablayout);
        setTitle(R.string.App_Window);
        setSupportActionBar(toolbar);
        initTabLayout();
        // CustomUserProvider.getInstance().getFriHelper().insert(LCChatKit.getInstance().getCurrentUserId(),"Tom");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.create_team:
                gotoSquareConversation();
                break;
            case R.id.fri_manage:
                FriendManager.start(App.this);
                break;
            case R.id.Settings:
                SettingsActivity.start(App.this);
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.create_team)
            gotoSquareConversation();

        return super.onOptionsItemSelected(item);
    }*/


    private void initTabLayout() {
        String[] tabList = new String[]{"Conversation", "Contacts"};
        final Fragment[] fragmentList = new Fragment[] {new LCIMConversationListFragment(),
                new ContactFragment()};

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        for (int i = 0; i < tabList.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabList[i]));
        }

        TabFragmentAdapter adapter = new TabFragmentAdapter(getSupportFragmentManager(),
                Arrays.asList(fragmentList), Arrays.asList(tabList));
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (0 == position) {
//          EventBus.getDefault().post(new ConversationFragmentUpdateEvent());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackTime < BACK_INTERVAL) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Double-click back to exit", Toast.LENGTH_SHORT).show();
        }
        lastBackTime = currentTime;
    }

    private void gotoSquareConversation() {
        List<LCChatKitUser> userList = CustomUserProvider.getInstance().getHelper().getAllFri(LCChatKit.getInstance().getCurrentUserId());
        List<String> idList = new ArrayList<>();
        for (LCChatKitUser user : userList) {
            idList.add(user.getUserId());
        }
        LCChatKit.getInstance().getClient().createConversation(
                idList, getString(R.string.square), null, false, true, new AVIMConversationCreatedCallback() {
                    @Override
                    public void done(AVIMConversation avimConversation, AVIMException e) {
                        Intent intent = new Intent(App.this, LCIMConversationActivity.class);
                        intent.putExtra(LCIMConstants.CONVERSATION_ID, avimConversation.getConversationId());
                        startActivity(intent);
                    }
                });
    }
}
