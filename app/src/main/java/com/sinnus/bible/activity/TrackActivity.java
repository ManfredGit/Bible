package com.sinnus.bible.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.sinnus.bible.R;
import com.sinnus.bible.adapter.TrackAdapter;
import com.sinnus.bible.util.ThemeUtil;

import java.util.concurrent.TransferQueue;

public class TrackActivity extends BaseActivity {

    private ListView mListView;
    private TrackAdapter trackAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    public void initTheme(){
        ThemeUtil.setTheme(this, ThemeUtil.getCurrentTheme(this));
    }
    public void setImmersedStatusBar(){
//        int status_bar_height_id = getResources().getIdentifier("status_bar_height", "dimen", "android");
//        int status_bar_height = getResources().getDimensionPixelSize(status_bar_height_id);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mToolbar.setTitle("历史记录");

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrackActivity.this.finish();
            }
        });
    }

}
