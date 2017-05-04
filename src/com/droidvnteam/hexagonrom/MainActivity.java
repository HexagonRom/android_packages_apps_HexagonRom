package com.droidvnteam.hexagonrom;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.System;

import com.droidvnteam.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String INTENT_EXTRA_INIT_FRAGMENT = "init_fragment";
    public static final String INIT_FRAGMENT_HALO = "halo";

    private static final String NAV_ITEM_ID = "navItemId";
    static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private int id;
    private boolean firstPage = true;
    private boolean restartRequired = false;

    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mView = (View) findViewById(R.id.drawer_layout);

        Fragment fragment;
        String title = null;
        String fragmentExtra = getIntent().getStringExtra(INTENT_EXTRA_INIT_FRAGMENT);
        /*if (INIT_FRAGMENT_HALO.equals(fragmentExtra)) {
            fragment = new HaloFragment();
            title = getString(R.string.halo_settings_title);
        } else {
            if (title != null) {
                Log.w(TAG, "Unknown init fragment: " + fragmentExtra);
            }*/
            fragment = new AboutFragment();
        //}

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.content_main, fragment);
        tx.commit();
        if (title != null) {
            setTitle(title);
        }

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent emailIntent =
                        new Intent(Intent.ACTION_SEND);
                String[] recipients = new String[]{"davor@losinj.com", "",};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "HEXAGON talk");
                emailIntent.setType("text/plain");
                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail_intent)));
                finish();
            }
        });*/

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = setupDrawerToggle();
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

   }

    @Override
    public void onResume() {
        super.onResume();

        if (restartRequired) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            /*
            **If back was pressed after changing to a new fragment,
            **first return to about page before closing
            */
            if(firstPage) {
                super.onBackPressed();
            } else {
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                // Uncheck previous selected item
                MenuItem oldItem = navigationView.getMenu().findItem(id);
                if (oldItem != null) {
                    oldItem.setChecked(false);
                }
                MenuItem newItem = navigationView.getMenu().findItem(R.id.nav_about);
                onNavigationItemSelected(newItem);
                newItem.setChecked(false);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        // update highlighted item in the navigation menu
        item.setChecked(true);
        id = item.getItemId();
        Fragment fragment = null;

        Class fragmentClass;

        switch (id) {
            case R.id.nav_display_animations:
                fragmentClass = DisplayAnimationsActivity.class;
                break;
            case R.id.nav_statusbar:
                fragmentClass = StatusBarFragment.class;
                break;
            case R.id.nav_notif_drawer:
                fragmentClass = NotificationsFragment.class;
                break;
            case R.id.nav_quick_settings:
                fragmentClass = QuickSettingsFragment.class;
                break;
            case R.id.nav_headsup:
                fragmentClass = HeadsUpSettingsFragment.class;
                break;
            case R.id.nav_recents:
                fragmentClass = RecentsPanelFragment.class;
                break;
            case R.id.nav_lockscreen:
                fragmentClass = LockscreenFragment.class;
                break;
            case R.id.nav_extensions:
                fragmentClass = ExtensionsFragment.class;
                break;
            case R.id.nav_transparency_porn:
                fragmentClass = TransparencyPornFragment.class;
                break;
            case R.id.nav_blur_ui:
                fragmentClass = BlurUIFragment.class;
                break;
            case R.id.advanced:
                fragmentClass = VariousShitFragment.class;
                break;
            case R.id.nav_about:
                fragmentClass = AboutFragment.class;
                break;
            default:
                fragmentClass = AboutFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RelativeLayout contentMain = (RelativeLayout) findViewById(R.id.content_main);
        contentMain.removeAllViewsInLayout();
        contentMain.invalidate();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();

        firstPage = id == R.id.nav_about;

        // Highlight the selected item, update the title, and close the drawer
        item.setChecked(true);
        setTitle(item.getTitle());
        mDrawer.closeDrawers();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
