package com.sinnus.bible.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.sinnus.bible.R;
import com.sinnus.bible.adapter.GridViewAdapter;
import com.sinnus.bible.adapter.MyFragmentPagerAdapter;
import com.sinnus.bible.bean.Bible;
import com.sinnus.bible.bean.Book;
import com.sinnus.bible.bean.Chapter;
import com.sinnus.bible.bean.Section;
import com.sinnus.bible.fragment.MainFragment;
import com.sinnus.bible.fragment.SettingsFragment;
import com.sinnus.bible.util.AutoRefreshMap;
import com.sinnus.bible.util.ColorUtil;
import com.sinnus.bible.util.ThemeUtil;
import com.sinnus.bible.util.TimeUtil;
import com.umeng.update.UmengUpdateAgent;

import org.w3c.dom.Text;

import java.net.SecureCacheResponse;
import java.util.Random;

public class MainActivity extends BaseActivity implements MainFragment.OnFragmentInteractionListener, AdapterView.OnItemClickListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private View mNavigationHeader;
    private TextView navSectionContent;
    private TextView navSectionLocation;
    private Toolbar mToolbar;
    private ViewPager mViewPager;

    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private SlidingDrawer mSlidingDrawer;
    public LinearLayout slidingDrawerContent = null;
    public GridView gridViewOld;
    public GridView gridViewNew;
    public Button sp;
    public GridView gridViewChapter;
    public static int LOCATE_BOARD_TAG = 0;

    private Menu mMenu;

    public int current_book_id;
    public int current_chapter_id;
    public Book current_book = null;

    public static String STRING_BOOK_ID = "book_id";
    public static String STRING_CHAPTER_ID = "chapter_id";
    public static String STRING_HAS_LEARNED_FAB = "has_learned_fab";
    public static String STRING_IS_LIGHT = "is_light";

    private static boolean IS_LIGHT;
    private static boolean HAS_LEARNED_FAB;

    private AutoRefreshMap autoRefreshMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //调用baseActivity并且载入主题
        loadHistory();
        autoRefreshMap = new AutoRefreshMap(this.current_book_id, this);//自动初始化 curbook，net_book,
        //和prev_book,并且自动更新
        setContentView(R.layout.activity_main);
        setImmersedStatusBar();
        initDrawerLayout();
        initFloatingActionButton();
        initMainView();
        UmengUpdateAgent.update(this);
//        ColorUtil.getPixColor(this, R.drawable.t);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.mMenu = menu;
        locateTo(current_book_id, current_chapter_id);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_exit) {
            this.finish();
            return true;
        } else if (id == R.id.action_chapter) {
            if (!mSlidingDrawer.isOpened()) {//关闭的，就打开
                setLocateBoardView(1);
                mSlidingDrawer.animateToggle();
            } else if (mSlidingDrawer.isOpened() && LOCATE_BOARD_TAG == 0) {
                setLocateBoardView(1);
            } else if (mSlidingDrawer.isOpened() && LOCATE_BOARD_TAG == 1) {
                mSlidingDrawer.animateToggle();
            }

        } else if (id == R.id.action_book) {
            if (!mSlidingDrawer.isOpened()) {
                setLocateBoardView(0);
                mSlidingDrawer.animateToggle();
            } else if (mSlidingDrawer.isOpened() && LOCATE_BOARD_TAG == 1) {
                setLocateBoardView(0);
            } else if (mSlidingDrawer.isOpened() && LOCATE_BOARD_TAG == 0) {
                mSlidingDrawer.animateToggle();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setImmersedStatusBar() {
        int status_bar_height_id = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int status_bar_height = getResources().getDimensionPixelSize(status_bar_height_id);
//        View view = findViewById(R.id.main);
//        view.setPadding(0, status_bar_height, 0, 0);

        //获取root view然后设置padding也行，但是效果不太好，因为下面有一点的阴影，不太好看
//        this.getWindow().getDecorView().setPadding(0, status_bar_height, 0, 0);
//        this.getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.black));
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    public void initDrawerLayout() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
//        mNavigationHeader = mNavigationView.inflateHeaderView(R.layout.navigation_header);
        navSectionContent = (TextView) findViewById(R.id.nav_section_content);
        navSectionLocation = (TextView) findViewById(R.id.nav_section_location);
        Section section = autoRefreshMap.obtainRandomSection();
        navSectionContent.setText(section.getContent());
        navSectionLocation.setText(section.getLocationString());
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        int id = menuItem.getItemId();
                        int delay = 0;
                        if (id == R.id.nav_history) {
                            final Intent intent = new Intent(MainActivity.this, TrackActivity.class);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(intent);
                                }
                            }, delay);
                        }
                        if (id == R.id.nav_setting) {
                            final Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(intent);
                                }
                            }, delay);
                        }
                        if (id == R.id.nav_about) {
                            final Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(intent);
                                }
                            }, delay);
                        }
                        if (id == R.id.nav_exit) {
                            MainActivity.this.finish();
                        }

                        return true;
                    }
                });

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                mToolbar,            /* nav drawer image to replace 'Up' caret */
                R.string.app_name,  /* "open drawer" description for accessibility */
                R.string.app_name    /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                updateHeaderView();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerToggle.syncState(); //动画效果
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void initFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!HAS_LEARNED_FAB) {
                    Snackbar.make(view, "长按选择章节，单击至灵修", Snackbar.LENGTH_LONG)
                            .setAction("不再提示", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(
                                            MainActivity.this,
                                            "已经关闭",
                                            Toast.LENGTH_SHORT).show();
                                    HAS_LEARNED_FAB = true;
                                }
                            }).show();
                } else {
                    myFragmentPagerAdapter.currentFragment.changeMode();
                }
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mSlidingDrawer.animateToggle();
                return true;
            }
        });
    }

    public void initMainView() {

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        myFragmentPagerAdapter =
                new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(myFragmentPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                System.out.println("on page selected");
                updateBesideBooks(position);
                updateMenu(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        mSlidingDrawer = (SlidingDrawer) findViewById(R.id.sliding_drawer);
        slidingDrawerContent = (LinearLayout) findViewById(R.id.sliding_drawer_content);


        gridViewNew = (GridView) findViewById(R.id.grid_view_new);
        gridViewOld = (GridView) findViewById(R.id.grid_view_old);
        sp = (Button) findViewById(R.id.button_sp);
        gridViewChapter = (GridView) findViewById(R.id.grid_view_chapter);

        gridViewNew.setAdapter(GridViewAdapter.getNewAdapter(this));
        gridViewOld.setAdapter(GridViewAdapter.getOldAdapter(this));
        gridViewChapter.setAdapter(GridViewAdapter.getChapterAdapter(this, current_book_id));

        gridViewNew.setOnItemClickListener(this);
        gridViewOld.setOnItemClickListener(this);
        gridViewChapter.setOnItemClickListener(this);
    }

    @Override
    public void onDestroy() {
        saveHistory();
        super.onDestroy();
    }

    public void loadHistory() {
        SharedPreferences sps = getSharedPreferences("history", 0);
        current_book_id = sps.getInt(STRING_BOOK_ID, 43);
        current_chapter_id = sps.getInt(STRING_CHAPTER_ID, 1);
        HAS_LEARNED_FAB = sps.getBoolean(STRING_HAS_LEARNED_FAB, false);
        IS_LIGHT = sps.getBoolean(STRING_IS_LIGHT, false);
    }

    public void saveHistory() {
        SharedPreferences sps = getSharedPreferences("history", 0);
        sps.edit().remove(STRING_BOOK_ID).commit();
        sps.edit().remove(STRING_CHAPTER_ID).commit();
        sps.edit().remove(STRING_HAS_LEARNED_FAB).commit();
        sps.edit().putInt(STRING_BOOK_ID, current_book_id).commit();
        sps.edit().putInt(STRING_CHAPTER_ID, current_chapter_id).commit();
        sps.edit().putBoolean(STRING_HAS_LEARNED_FAB, HAS_LEARNED_FAB).commit();
        sps.edit().putBoolean(STRING_IS_LIGHT, IS_LIGHT).commit();
    }

    @Override
    public Chapter onFragmentInteraction(int bookId, int chapterId) { //返回数据
        return this.autoRefreshMap.obtainChapter(bookId, chapterId);
    }

    public void locateTo(int bookId, int chapterId) { //直接跳转切换，载入book数据，但是并不显示，
        current_book_id = bookId;
        autoRefreshMap.load(bookId);
        current_chapter_id = chapterId;
        updateHeaderView();//切换书卷后，更新headerview中的section
        //viewpager跳转到bookId和chapterId对应的那个fragmentId的fragment,
        // 在fragment中 根据bookID和chapterId
        //来调用onFragmentInteraction来从activity中找到数据chapter再在fragment中显示
        mViewPager.setCurrentItem(Bible.getMainFragmentIdByInfo(
                current_book_id, current_chapter_id), false);

    }

    public void updateBesideBooks(int position) {  //当跳转到一个新的书卷时 需要加载别的书卷
        int a[] = Bible.getRelativeInfoById(position);
        int bookId = a[0];
        if (bookId == current_book_id) {
            return;
        } else {
            current_book_id = bookId;
            autoRefreshMap.loadBesideBooks(bookId);
        }
    }


    public void updateHeaderView(){
        Section section = autoRefreshMap.obtainRandomSection();
        navSectionContent.setText(section.getContent());
        navSectionLocation.setText(section.getLocationString());
    }
    public void updateMenu(int position) {
        int a[] = Bible.getRelativeInfoById(position);
        int bookId = a[0];
        int chapterId = a[1];
        current_book_id = bookId;
        current_chapter_id = chapterId;
        MainActivity.this.mMenu.findItem(R.id.action_book).setTitle(Bible.getBookName(bookId));

        MainActivity.this.mMenu.findItem(R.id.action_chapter).
                setTitle("" + chapterId);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String text = (String) ((TextView) view).getText();

        int bookId = Bible.getBookIdBySimpleName(text);
        if (bookId == 0) {//说明是文本内容是章节数字，不是书名
            locateTo(current_book_id, position + 1);
            mSlidingDrawer.animateToggle();//动画关闭slidingDrawer
        } else {              //点击了书名，这时
            //载入书
            locateTo(bookId, 1);
            gridViewChapter.setAdapter(GridViewAdapter.getChapterAdapter(this, bookId));
            setLocateBoardView(1);
        }
    }

    public void setLocateBoardView(int tag) {
        LOCATE_BOARD_TAG = tag;
        if (tag == 1) {
            gridViewOld.setVisibility(View.GONE);
            gridViewNew.setVisibility(View.GONE);
            sp.setVisibility(View.GONE);
            gridViewChapter.setVisibility(View.VISIBLE);
        } else {
            gridViewOld.setVisibility(View.VISIBLE);
            gridViewNew.setVisibility(View.VISIBLE);
            sp.setVisibility(View.VISIBLE);
            gridViewChapter.setVisibility(View.GONE);
        }
    }

}
