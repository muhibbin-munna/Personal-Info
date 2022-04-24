package com.indian.youthcareerinfo.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.indian.youthcareerinfo.R;
import com.indian.youthcareerinfo.ui.HomeFragment;
import com.indian.youthcareerinfo.ui.LogoutFragment;
import com.indian.youthcareerinfo.ui.NotificationFragment;
import com.indian.youthcareerinfo.ui.UploadDocumentFragment;
import com.indian.youthcareerinfo.ui.WelcomeFragment;

public class WelcomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Welcome");
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view_welcome_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new WelcomeFragment()).commit();
            navigationView.setNavigationItemSelectedListener(this);
        }
        navigationView.setCheckedItem(R.id.nav_welcome);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }
    public NavigationView getNavigationView(){
        return this.navigationView;
    }
    public Toolbar getToolbar(){
        return this.toolbar;
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(!navigationView.getMenu().findItem(R.id.nav_welcome).isChecked()){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new WelcomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_welcome);
            toolbar.setTitle("Welcome");
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_welcome:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new WelcomeFragment()).commit();
                break;
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                toolbar.setTitle("Home");
                break;
            case R.id.nav_notification:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new NotificationFragment()).commit();
                toolbar.setTitle("Notifications");
                break;
            case R.id.nav_upload_documents:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new UploadDocumentFragment()).commit();
                toolbar.setTitle("Upload Documents");
                break;
            case R.id.nav_logout:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LogoutFragment()).commit();
                toolbar.setTitle("Logout");
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}