package com.example.kevin.emergencyproj;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
import java.util.ArrayList;
import java.util.List;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity {

    private static final String[] KEY_WORDS = {"earthquake", "flood", "wildfire", "tornado", "blizzard"};
    private static final double SEARCH_RADIUS = 1;

    private double currentLat = 30.615, currentLong = -96.342;

    List<Point> mockPoints = new ArrayList<>();
    List<Point> disasterPoints = new ArrayList<>();
    List<Point> responderPoints = new ArrayList<>();

    private boolean earthquakeFilter;
    private boolean floodingFilter;
    private boolean wildfireFilter;
    private boolean tornadoFilter;
    private boolean blizzardFilter;
    private boolean landslideFilter;

    @BindView(R.id.menu_earthquake)
    MenuItem earthquakeMenu;
    @BindView(R.id.menu_flooding)
    MenuItem floodingMenu;
    @BindView(R.id.menu_wildfire)
    MenuItem wildfireMenu;
    @BindView(R.id.menu_tornado)
    MenuItem tornadoMenu;
    @BindView(R.id.menu_blizzard)
    MenuItem blizzardMenu;
    @BindView(R.id.menu_landslide)
    MenuItem landslideMenu;

    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    /*
        CATEGORIES:
            - BLIZZARD
            - EARTHQUAKE
            - WILDFIRE
            - FLOOD
            - TORNADO
            - LANDSLIDE
     */

    // TEST DATA
    private static final LatLng PERTH = new LatLng(-31.952854, 115.857342);
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);

    List<Point> points = new ArrayList<>();

    private GoogleMap googleMap;

    private TwitterStream twitterStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTwitterStream();
        initMockPoints();
    }

    private void initMockPoints() {
        mockPoints.add(new Point(29.7449, -95.37141, Point.Type.FLOOD));
        mockPoints.add(new Point(29.76051082, -95.36164326, Point.Type.EARTHQUAKE));
        mockPoints.add(new Point(31.48889, -97.15737, Point.Type.LANDSLIDE));
        mockPoints.add(new Point(29.8421551, -97.9737673, Point.Type.TORNADO));
        mockPoints.add(new Point(32.288333339, -97.4166666, Point.Type.WILDFIRE));
        mockPoints.add(new Point(29.7252, -95.344, Point.Type.FLOOD));
        mockPoints.add(new Point(29.77564674, -95.81264056, Point.Type.BLIZZARD));
        mockPoints.add(new Point(29.8421551, -97.9737673, Point.Type.LANDSLIDE));
        mockPoints.add(new Point(29.7629, -95.3832, Point.Type.WILDFIRE));
        mockPoints.add(new Point(29.775746, -95.80937, Point.Type.FLOOD));
        mockPoints.add(new Point(29.78216, -95.80981, Point.Type.TORNADO));
        mockPoints.add(new Point(29.7603773, -95.361569, Point.Type.EARTHQUAKE));
        mockPoints.add(new Point(29.75217779, -95.35790357, Point.Type.FLOOD));
        responderPoints.add(new Point(30, -96, Point.Type.RESPONDER));
        responderPoints.add(new Point(29.5, -96.5, Point.Type.RESPONDER));
        responderPoints.add(new Point(30.1, -95.5, Point.Type.RESPONDER));
    }

    interface TwitterAuth {
        String CONSUMER_KEY = "1rnXwetkZ2z0K7RHeTmj5iXak";
        String CONSUMER_SECRET = "6U13PEUn8toF50LBeMhf7TRffIWMmZVRysLzswdwtkXWIUIagM";
        String ACCESS_TOKEN = "854167713681375232-ruF3NOVDc3EVKZkCMUzGcLWDRMFhljr";
        String ACCESS_TOKEN_SECRET = "T6RsmfVZ7cj0H5MoLB9At4dfUNhL7OPw8sCTOP9wzUryY";
    }

    private void initTwitterStream() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(TwitterAuth.CONSUMER_KEY)
                .setOAuthConsumerSecret(TwitterAuth.CONSUMER_SECRET)
                .setOAuthAccessToken(TwitterAuth.ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(TwitterAuth.ACCESS_TOKEN_SECRET);

        twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance();
        twitterStream.addListener(new StatusListener() {
            public void onStatus(Status status) {
                if (!mockPoints.isEmpty()) disasterPoints.add(mockPoints.remove(0));
                //Update markers
            }

            @Override
            public void onException(Exception ex) {}

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {}

            @Override
            public void onStallWarning(StallWarning warning) {}
        });

        FilterQuery tweetFilterQuery = new FilterQuery();
        tweetFilterQuery.locations(
                new double[]{currentLong - SEARCH_RADIUS, currentLat - SEARCH_RADIUS},
                new double[]{currentLong + SEARCH_RADIUS, currentLat + SEARCH_RADIUS});
        tweetFilterQuery.language("en");
        twitterStream.filter(tweetFilterQuery);
    }

    private int urgencyLevel(Point p) {
        int nearbyResponders = 0;
        for (Point r : responderPoints) {
            double dist = Math.sqrt(
                    Math.pow((r.getLatitude() - p.getLatitude()), 2) +
                    Math.pow(r.getLongitude() - p.getLongitude(), 2));
            if (dist <= .5) nearbyResponders++;
        }
        return nearbyResponders/2;
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

            points.add(new Point(-31.952854, 115.857342, "earthquake"));
            points.add(new Point(-33.87365, 151.20689, "flood"));

            for (int i = 0; i < points.size(); i++) {
                BitmapDescriptor icon;
                switch (points.get(i).getType()) {
                    case "earthquake":
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(points.get(i).getLatitude(), points.get(i).getLongitude()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        break;
                    case "flood":
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(points.get(i).getLatitude(), points.get(i).getLongitude()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        break;
                    case "wildfire":
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(points.get(i).getLatitude(), points.get(i).getLongitude()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        break;
                    case "tornado":
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(points.get(i).getLatitude(), points.get(i).getLongitude()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        break;
                    case "blizzard":
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(points.get(i).getLatitude(), points.get(i).getLongitude()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        break;
                    case "landslide":
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(points.get(i).getLatitude(), points.get(i).getLongitude()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        break;
                }
            }

            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // and move the map's camera to the same location.
            LatLng userLoc = new LatLng(location.getLatitude(), location.getLongitude());

            googleMap.addMarker(new MarkerOptions().position(userLoc).title("Your Marker"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLoc));

        }
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

    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    private static Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return BitmapFactory.decodeResource(context.getResources(), drawableId);
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Update markers based on filters
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