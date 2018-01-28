package com.example.kevin.emergencyproj;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

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

    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {

            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // and move the map's camera to the same location.
            LatLng collegeStation = new LatLng(location.getLatitude(), location.getLongitude());

            googleMap.addMarker(new MarkerOptions().position(collegeStation).title("Your Marker"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(collegeStation));

        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Write your message here.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= 23 &&
                            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    // and move the map's camera to the same location.
                    LatLng collegeStation = new LatLng(location.getLatitude(), location.getLongitude());

                    googleMap.addMarker(new MarkerOptions().position(collegeStation).title("Your Marker"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(collegeStation));

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
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

