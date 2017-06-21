package com.projet.yueq.myapplication_v2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;



/**
 * Created by Administrator on 2017/6/18 0018.
 */

public class About extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_contacts);

        Toolbar toolbar_about = (Toolbar) findViewById(R.id.toolbar_about);
        setSupportActionBar(toolbar_about);

        final int versionCode = BuildConfig.VERSION_CODE;
        final String versionName = BuildConfig.VERSION_NAME;
        final String ok = "OK";
        final String thisIsVersionNumber = "This is version number";
        final String thisIsBuildNumber = "This is build number";
        final String hereIsWhatsNewInThisVersion = "Here is whats new in this version:";
        final String newInThisVersion = getString(R.string.new_in_this_version);

        setVersionNameText();

        TextView versionNameDisplay;
        versionNameDisplay = (TextView) findViewById(R.id.display_version_name);
        versionNameDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), thisIsVersionNumber + "" + versionCode + "", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), thisIsBuildNumber + "" + versionName + "", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(About.this);
                builder.setTitle(hereIsWhatsNewInThisVersion)
                        .setMessage(newInThisVersion)
                        .setNeutralButton(ok, null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void setVersionNameText() {
        final String version = getString(R.string.version);
        String versionName = BuildConfig.VERSION_NAME;
        TextView displayVersionName = (TextView) findViewById(R.id.display_version_name);
        displayVersionName.setText(version + "" + versionName);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_about_contacts,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_my_contacts:
                FriendManager.start(About.this);
                return true;
            case R.id.action_settings:
                SettingsActivity.start(About.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
