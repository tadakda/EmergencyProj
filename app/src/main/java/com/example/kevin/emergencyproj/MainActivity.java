package com.example.kevin.emergencyproj;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

    private static final String[] KEY_WORDS = {"earthquake", "flood", "wildfire", "tornado", "blizzard", "landslide"};
    private static final double SEARCH_RADIUS = 1;

    private double currentLat = 30.615, currentLong = -96.342;

    List<Point> mockPoints = new ArrayList<>();
    List<Point> disasterPoints = new ArrayList<>();
    List<Point> responderPoints = new ArrayList<>();
    Map<Point, Marker> markerTracker = new HashMap<>();

    boolean isZoomedOut;
    List<Marker> zoomedOutMarkers = new ArrayList<>();

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
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100, centerLng - .005 + Math.random() / 100, Point.Type.FLOOD));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100, centerLng - .005 + Math.random() / 100, Point.Type.EARTHQUAKE));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100, centerLng - .005 + Math.random() / 100, Point.Type.LANDSLIDE));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100, centerLng - .005 + Math.random() / 100, Point.Type.TORNADO));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100, centerLng - .005 + Math.random() / 100, Point.Type.WILDFIRE));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100, centerLng - .005 + Math.random() / 100, Point.Type.FLOOD));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100, centerLng - .005 + Math.random() / 100, Point.Type.BLIZZARD));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100, centerLng - .005 + Math.random() / 100, Point.Type.LANDSLIDE));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100, centerLng - .005 + Math.random() / 100, Point.Type.WILDFIRE));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100, centerLng - .005 + Math.random() / 100, Point.Type.FLOOD));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100, centerLng - .005 + Math.random() / 100, Point.Type.TORNADO));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100, centerLng - .005 + Math.random() / 100, Point.Type.EARTHQUAKE));
        mockPoints.add(new Point(centerLat - .005 + Math.random() / 100, centerLng - .005 + Math.random() / 100, Point.Type.FLOOD));
        responderPoints.add(new Point(centerLat - .005 + Math.random() / 100, centerLng - .005 + Math.random() / 100, Point.Type.RESPONDER));
        responderPoints.add(new Point(centerLat - .005 + Math.random() / 100, centerLng - .005 + Math.random() / 100, Point.Type.RESPONDER));
        responderPoints.add(new Point(centerLat - .005 + Math.random() / 100, centerLng - .005 + Math.random() / 100, Point.Type.RESPONDER));
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
                            initNewDisasterMarkers();
                            initResponderMarkers();
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
        double maxDist = 50;
        int nearbyResponders = 0;
        for (Point r : responderPoints) {
            double dist = haversine(r.getLatitude(), r.getLongitude(),
                    p.getLatitude(), p.getLongitude());
            if (dist <= maxDist) nearbyResponders++;
        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            currentLat = loc.getLatitude();
            currentLong = loc.getLongitude();
            double dist = haversine(currentLat, currentLong,
                    p.getLatitude(), p.getLongitude());
            if (dist <= maxDist) nearbyResponders++;
        }

        return nearbyResponders / 2;
    }

    private double haversine(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371e3;
        double a1 = toRad(lat1);
        double a2 = toRad(lat2);
        double deltaA = toRad(lat2 - lat1);
        double deltaLambda = toRad(lng2 - lng1);

        double a = Math.sin(deltaA / 2) * Math.sin (deltaA / 2) +
                Math.cos(a1) * Math.cos(a2) *
                        Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
        double b = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * b;
    }

    private double toRad(double l) {
        return l * Math.PI / 180;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();
                    redrawMarkers();
                }

                @Override
                public void onProviderDisabled(String provider) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onStatusChanged(String provider, int status,
                                            Bundle extras) {}
            });
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng userLoc = new LatLng(location.getLatitude(), location.getLongitude());

            currentLat = location.getLatitude();
            currentLong = location.getLongitude();

            googleMap.addMarker(new MarkerOptions().position(userLoc).title("Your Marker").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLoc));
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {
                    if (isZoomedOut != (googleMap.getCameraPosition().zoom <= 12)) {
                        isZoomedOut = !isZoomedOut;
                        zoomedOutMarkers.clear();
                        for (Point p : markerTracker.keySet()) {
                            markerTracker.get(p).remove();
                        }
                        markerTracker.clear();
                        initMarkers();
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],@NonNull int[] grantResults) {
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
                    googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                    googleMap.setMyLocationEnabled(true);
                }
            }
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
        initNewDisasterMarkers();
        return false;
    }

    private void initMarkers() {
        if (isZoomedOut && zoomedOutMarkers.isEmpty()) {
            double avgLat = 0, avgLng = 0;
            int avgUrgency = 0;
            for (Point p : disasterPoints) {
                avgLat += p.getLatitude();
                avgLng += p.getLongitude();
                avgUrgency = urgencyLevel(p);
            }
            avgLat /= disasterPoints.size();
            avgLng /= disasterPoints.size();
            avgUrgency /= disasterPoints.size();

            BitmapDescriptor icon;
            switch (avgUrgency) {
                case 0:
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    break;
                case 1:
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                    break;
                default:
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
            }

            zoomedOutMarkers.add(googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(avgLat, avgLng))
                    .icon(icon)));
        } else {
            initResponderMarkers();
            initNewDisasterMarkers();
        }
    }

    private void initResponderMarkers() {
        for (Point p : responderPoints) {
            if (!markerTracker.containsKey(p)) {
                markerTracker.put(p,
                        googleMap.addMarker(new MarkerOptions()
                                .title("First Responder")
                                .position(new LatLng(p.getLatitude(), p.getLongitude()))
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.responder_icon))));
            }
        }
    }

    private void initNewDisasterMarkers() {
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
                    title = "Landslide";
                    break;
                default:
                    isFiltered = false;
                    title = "";
            }
            if (!isFiltered && !markerTracker.containsKey(p)) {
                markerTracker.put(p,
                        googleMap.addMarker(new MarkerOptions()
                                .title(title)
                                .snippet(desc)
                                .position(new LatLng(p.getLatitude(), p.getLongitude()))
                                .icon(icon)));
            } else if (isFiltered && markerTracker.containsKey(p)) {
                markerTracker.get(p).remove();
                markerTracker.remove(p);
            }
        }
    }

    private void redrawMarkers() {
        for (Point p : markerTracker.keySet()) {
            BitmapDescriptor icon;
            switch (urgencyLevel(p)) {
                case 0:
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    break;
                case 1:
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                    break;
                default:
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    break;
            }
            markerTracker.get(p).setIcon(icon);
        }
    }
}
