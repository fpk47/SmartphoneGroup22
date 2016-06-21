package nl.tudelft.inpoint;

import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import nl.tudelft.inpoint.activity.ActivityTrainingFragment;
import nl.tudelft.inpoint.localization.LocalizationFragment;
import nl.tudelft.inpoint.training.TrainingFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initializeGlobals();

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new LocalizationFragment()).commit();

        setTitle("Localization");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_localization) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new LocalizationFragment()).commit();
            setTitle("Localization");
        } else if (id == R.id.nav_training) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new TrainingFragment()).commit();
            setTitle("Training");
        } else if (id == R.id.nav_activity_training) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ActivityTrainingFragment()).commit();
            setTitle("Activity Training");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    private void initializeGlobals() {
        Resources resource = getResources();
        Globals.MAP_DEFAULT_COLOR = resource.getColor(R.color.mapDefault);
        Globals.MAP_SELECTED_COLOR = resource.getColor(R.color.mapSelected);
        Globals.WIFI_MANAGER = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        Globals.DATABASE = new SQLiteHelper(this);
        Globals.RESOURCES = getResources();
        Globals.PACKAGE_NAME = getPackageName();
    }
}
