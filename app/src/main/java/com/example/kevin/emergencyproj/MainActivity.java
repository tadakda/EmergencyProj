package com.example.kevin.emergencyproj;

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
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String[] KEY_WORDS = {"earthquake", "flood", "wildfire", "tornado", "blizzard"};
    private static final double SEARCH_RADIUS = 2;

    private double currentLat = 30.615, currentLong = -96.342;

    List<Point> mockPoints = new ArrayList<>();
    List<Point> disasterPoints = new ArrayList<>();
    List<Point> responderPoints = new ArrayList<>();
    Map<Point, Marker> markerTracker = new HashMap<>();

    private boolean earthquakeFilter;
    private boolean floodingFilter;
    private boolean wildfireFilter;
    private boolean tornadoFilter;
    private boolean blizzardFilter;
    private boolean landslideFilter;

    MenuItem earthquakeMenu;
    MenuItem floodingMenu;
    MenuItem wildfireMenu;
    MenuItem tornadoMenu;
    MenuItem blizzardMenu;
    MenuItem landslideMenu;

    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    private GoogleMap googleMap;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initTwitterStream();
        initMockPoints();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initMockPoints() {
        double centerLat = 30.615;
        double centerLng = -96.342;
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100,centerLng - .005 + Math.random() / 100, Point.Type.FLOOD));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100,centerLng - .005 + Math.random() / 100, Point.Type.EARTHQUAKE));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100,centerLng - .005 + Math.random() / 100, Point.Type.LANDSLIDE));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100,centerLng - .005 + Math.random() / 100, Point.Type.TORNADO));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100,centerLng - .005 + Math.random() / 100, Point.Type.WILDFIRE));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100,centerLng - .005 + Math.random() / 100, Point.Type.FLOOD));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100,centerLng - .005 + Math.random() / 100, Point.Type.BLIZZARD));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100,centerLng - .005 + Math.random() / 100, Point.Type.LANDSLIDE));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100,centerLng - .005 + Math.random() / 100, Point.Type.WILDFIRE));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100,centerLng - .005 + Math.random() / 100, Point.Type.FLOOD));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100,centerLng - .005 + Math.random() / 100, Point.Type.TORNADO));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100,centerLng - .005 + Math.random() / 100, Point.Type.EARTHQUAKE));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100,centerLng - .005 + Math.random() / 100, Point.Type.FLOOD));
        responderPoints.add(new Point(centerLat - .005 + Math.random() / 100,centerLng - .005 + Math.random() / 100, Point.Type.RESPONDER));
        responderPoints.add(new Point(centerLat - .005 + Math.random() / 100,centerLng - .005 + Math.random() / 100, Point.Type.RESPONDER));
        responderPoints.add(new Point(centerLat - .005 + Math.random() / 100,centerLng - .005 + Math.random() / 100, Point.Type.RESPONDER));
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

        TwitterStream twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance();
        twitterStream.addListener(new StatusListener() {
            public void onStatus(Status status) {
                if (!mockPoints.isEmpty()) {
                    disasterPoints.add(mockPoints.remove(0));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            redrawMarkers();
                        }
                    });
                }
            }

            @Override
            public void onException(Exception ex) {
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
            }

            @Override
            public void onStallWarning(StallWarning warning) {
            }
        });

        FilterQuery tweetFilterQuery = new FilterQuery();
        tweetFilterQuery.locations(
                new double[]{currentLong - SEARCH_RADIUS, currentLat - SEARCH_RADIUS},
                new double[]{currentLong + SEARCH_RADIUS, currentLat + SEARCH_RADIUS});
        tweetFilterQuery.language("en");
        twitterStream.filter(tweetFilterQuery);
    }

    private int urgencyLevel(Point p) {
        double maxDist = .007;
        int nearbyResponders = 0;
        for (Point r : responderPoints) {
            double dist = Math.sqrt(
                    Math.pow((r.getLatitude() - p.getLatitude()), 2) +
                            Math.pow(r.getLongitude() - p.getLongitude(), 2));
            if (dist <= maxDist) nearbyResponders++;
        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            currentLat = loc.getLatitude();
            currentLong = loc.getLongitude();
            double dist = Math.sqrt(
                    Math.pow((currentLat - p.getLatitude()), 2) +
                            Math.pow(currentLong - p.getLongitude(), 2));
            if (dist <= maxDist) nearbyResponders++;
        }

        return nearbyResponders / 2;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng userLoc = new LatLng(location.getLatitude(), location.getLongitude());

            currentLat = location.getLatitude();
            currentLong = location.getLongitude();

            googleMap.addMarker(new MarkerOptions().position(userLoc).title("Your Marker"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLoc));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= 23 &&
                            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    LatLng collegeStation = new LatLng(location.getLatitude(), location.getLongitude());

                    googleMap.addMarker(new MarkerOptions().position(collegeStation).title("Your Marker"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(collegeStation));
                }
            }
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
        earthquakeMenu = menu.findItem(R.id.menu_earthquake);
        floodingMenu = menu.findItem(R.id.menu_flooding);
        wildfireMenu = menu.findItem(R.id.menu_wildfire);
        tornadoMenu = menu.findItem(R.id.menu_tornado);
        blizzardMenu = menu.findItem(R.id.menu_blizzard);
        landslideMenu = menu.findItem(R.id.menu_landslide);

        earthquakeMenu.setChecked(!earthquakeFilter);
        floodingMenu.setChecked(!floodingFilter);
        wildfireMenu.setChecked(!wildfireFilter);
        tornadoMenu.setChecked(!tornadoFilter);
        blizzardMenu.setChecked(!blizzardFilter);
        landslideMenu.setChecked(!landslideFilter);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_earthquake:
                item.setChecked(!item.isChecked());
                earthquakeFilter = !item.isChecked();
                break;
            case R.id.menu_flooding:
                item.setChecked(!item.isChecked());
                floodingFilter = !item.isChecked();
                break;
            case R.id.menu_wildfire:
                item.setChecked(!item.isChecked());
                wildfireFilter = !item.isChecked();
                break;
            case R.id.menu_tornado:
                item.setChecked(!item.isChecked());
                tornadoFilter = !item.isChecked();
                break;
            case R.id.menu_blizzard:
                item.setChecked(!item.isChecked());
                blizzardFilter = !item.isChecked();
                break;
            case R.id.menu_landslide:
                item.setChecked(!item.isChecked());
                landslideFilter = !item.isChecked();
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
        redrawMarkers();
        return false;
    }

    private void redrawMarkers() {
        for (Point p : responderPoints) {
            if (!markerTracker.containsKey(p)) {
                markerTracker.put(p,
                        googleMap.addMarker(new MarkerOptions()
                        .title("First Responder")
                        .position(new LatLng(p.getLatitude(), p.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));
            } else {
                markerTracker.get(p).remove();
                markerTracker.remove(p);
            }
        }
        for (Point p : disasterPoints) {
            BitmapDescriptor icon;
            String desc;
            switch (urgencyLevel(p)) {
                case 0:
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    desc = "High Priority";
                    break;
                case 1:
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                    desc = "Medium Priority";
                    break;
                default:
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    desc = "Low Priority";
                    break;
            }
            boolean isFiltered;
            String title;
            switch (p.getType()) {
                case Point.Type.EARTHQUAKE:
                    isFiltered = earthquakeFilter;
                    title = "Earthquake";
                    break;
                case Point.Type.FLOOD:
                    isFiltered = floodingFilter;
                    title = "Flooding";
                    break;
                case Point.Type.WILDFIRE:
                    isFiltered = wildfireFilter;
                    title = "Wildfire";
                    break;
                case Point.Type.TORNADO:
                    isFiltered = tornadoFilter;
                    title = "Tornado";
                    break;
                case Point.Type.BLIZZARD:
                    isFiltered = blizzardFilter;
                    title = "Blizzard";
                    break;
                case Point.Type.LANDSLIDE:
                    isFiltered = landslideFilter;
                    title = "LandSlide";
                    break;
                default:
                    isFiltered = false;
                    title = "";
            }
            if (!isFiltered && !markerTracker.containsKey(p)) {
                googleMap.addMarker(new MarkerOptions()
                        .title(title)
                        .snippet(desc)
                        .position(new LatLng(p.getLatitude(), p.getLongitude()))
                        .icon(icon));
            } else if (isFiltered && markerTracker.containsKey(p)) {
                markerTracker.get(p).remove();
                markerTracker.remove(p);
            }
        }
    }
}
