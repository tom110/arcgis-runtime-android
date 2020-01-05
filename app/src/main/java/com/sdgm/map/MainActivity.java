package com.sdgm.map;

import android.os.Bundle;

import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;


import com.google.android.material.navigation.NavigationView;
import com.sdgm.map.ui.gallery.GalleryFragment;
import com.sdgm.map.ui.home.HomeFragment;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    private Fragment mapFragment;
    private Fragment layerManagerFragment;

    private String tag;

    private MapView mMapView;
    private LocationDisplay mLocationDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mapFragment= getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);


        tag="map";

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {

        if (mMapView != null) {
            mMapView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (mMapView != null) {
            mMapView.dispose();
        }
        super.onDestroy();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment;
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                if(tag=="map"){
                    break;
                }else {
                    fragment = getSupportFragmentManager().findFragmentByTag("layersManager");
                    if (fragment != null) {
                        getSupportFragmentManager().beginTransaction().hide(fragment).show(mapFragment)
                                .commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().show(mapFragment)
                                .commit();
                    }

                    Log.i(TAG, "map");
                    tag="map";
                    break;
                }
            case R.id.nav_gallery:
                if(tag=="layersManager"){
                    break;
                }else {
                    if (layerManagerFragment == null) {
                        layerManagerFragment = new GalleryFragment();
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.nav_host_fragment,layerManagerFragment, "layersManager").commit();
                    } else {
                        fragment = getSupportFragmentManager().findFragmentByTag("layersManager");
                        getSupportFragmentManager().beginTransaction()
                                .show(fragment).commit();
                    }
                    Log.i(TAG, "layersManager");
                    tag="layersManager";
                    break;
                }
            case R.id.nav_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_send:
                Toast.makeText(this, "Send", Toast.LENGTH_SHORT).show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
