package com.example.kevin.emergencyproj;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity {

    private boolean earthquakeFilter;
    private boolean floodingFilter;
    private boolean wildfireFilter;
    private boolean tornadoFilter;
    private boolean blizzardFilter;
    private boolean landslideFilter;

    @BindView(R.id.menu_earthquake) MenuItem earthquakeMenu;
    @BindView(R.id.menu_flooding) MenuItem floodingMenu;
    @BindView(R.id.menu_wildfire) MenuItem wildfireMenu;
    @BindView(R.id.menu_tornado) MenuItem tornadoMenu;
    @BindView(R.id.menu_blizzard) MenuItem blizzardMenu;
    @BindView(R.id.menu_landslide) MenuItem landslideMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_earthquake:
                // earthquake was selected
                item.setChecked(!item.isChecked());
                earthquakeFilter = item.isChecked();
                break;
            case R.id.menu_flooding:
                // flooding was selected
                item.setChecked(!item.isChecked());
                floodingFilter = item.isChecked();
                break;
            case R.id.menu_wildfire:
                // wildfire was selected
                item.setChecked(!item.isChecked());
                wildfireFilter = item.isChecked();
                break;
            case R.id.menu_tornado:
                // tornado was selected
                item.setChecked(!item.isChecked());
                tornadoFilter = item.isChecked();
                break;
            case R.id.menu_blizzard:
                // blizzard was selected
                item.setChecked(!item.isChecked());
                blizzardFilter = item.isChecked();
                break;
            case R.id.menu_landslide:
                // landslide was selected
                item.setChecked(!item.isChecked());
                landslideFilter = item.isChecked();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        item.setActionView(new View(this));
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return false;
            }
        });
        return false;
    }

    private boolean isChecked = false;

    public boolean onEarthquake(Menu menu) {
        earthquakeMenu.setChecked(isChecked);
        return true;
    }
    public boolean onFlooding(Menu menu) {
        floodingMenu.setChecked(isChecked);
        return true;
    }
    public boolean onWildfire(Menu menu) {
        wildfireMenu.setChecked(isChecked);
        return true;
    }
    public boolean onTornado(Menu menu) {
        tornadoMenu.setChecked(isChecked);
        return true;
    }
    public boolean onBlizzard(Menu menu) {
        blizzardMenu.setChecked(isChecked);
        return true;
    }
    public boolean onLandslide(Menu menu) {
        landslideMenu.setChecked(isChecked);
        return true;
    }

}

