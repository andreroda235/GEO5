package pt.unl.fct.di.www.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.PendingResult;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MapActivity extends AppCompatActivity {

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String TAG = "MapActivity";

    private static String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;


    private static final float DEFAULT_ZOOM = 15f;
    private static final float TRACKING_ZOOM = 15f;
    private static final int WALKING_INTERVAL = 3 * 1000;
    private static final float RADIUS = 5*1000;
    private static final float MINIMUM_DIST = 25;


    //widgets
    private EditText mFromSearchText;
    private EditText mToSearchText;
    private ImageView mGps;
    private Button mGoBtn;
    private RelativeLayout mSimplePathLayout;
    private RelativeLayout mWaypointLayout;
    private EditText mWaypoint;
    private Button mAddMarker;
    private Button mCreatePath;
    private RelativeLayout mPlayStopBtn;
    private RelativeLayout mPauseResumeBtn;
    private RelativeLayout mChooseIntervalBtn;
    private LinearLayout mPathTrackingLayout;
    private ImageView mDrawTrackSrc;
    private ImageView mPauseResumeSrc;
    private LinearLayout mContentView;
    private RelativeLayout mPathScrollView;
    private Button mCloseHistoryMenuView;
    private Button mWaypointInputBtn;
    private Button mSimpleInputBtn1;
    private Button mSimpleInputBtn2;
    private RelativeLayout mNewPathBtn;
    private RelativeLayout mPathHistoryBtn;
    private LinearLayout pathForm1;
    private LinearLayout pathForm2;
    private RelativeLayout pathFormLayout;
    private EditText mPathTitleInput;
    private EditText mPathDescInput;
    private RelativeLayout mMenuLayout;
    private RadioGroup radioGroup;
    private RelativeLayout mTimeIntervalForm;
    private NumberPicker mSecondsPicker;
    private Button mSecondsPickerBtn;
    private RelativeLayout mOperationsLayout;


    //variables
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Marker mTempPlace1;
    private Marker mTempPlace2;
    private List<Marker> mTempComplexRoute;
    private Marker mCurrentWaypointMarker;
    private List<Marker> mTempGPSRoute;
    private List<Polyline> mTempGPSLines;
    private PolylineOptions mFinalGSPPolyOptions;
    private GeoApiContext mGeoApiContext;
    private boolean mStartTracking;
    private boolean mPause;
    private Timer timer;
    private TimerTask timerTask;
    private Map<String, RouteData> routes;
    private RouteData mTempRouteData;
    private int radioBtnId;
    private PathFormData pathFData;
    private Map<String, GeoSpotData> geoSpotData;
    private Map<String, Marker> geoSpots;
    private String myUsername;
    private Timer timerWalk;
    private TimerTask timerTaskWalk;
    private boolean walkMode;
    private KmlLayer kmllayer;
    private Circle circleRadius;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        SharedPreferences authentication = getSharedPreferences("AUTHENTICATION", 0);
        myUsername = authentication.getString("username", null);

        getLocationPermission();
        if(isServicesOK()){
            initMap();
        }
        mOperationsLayout = findViewById(R.id.operations_layout);
        mFromSearchText = (EditText) findViewById(R.id.from_layout_search_input);
        mToSearchText = (EditText) findViewById(R.id.to_layout_search_input);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        mGoBtn = (Button) findViewById(R.id.go_button);
        mSimplePathLayout = (RelativeLayout) findViewById(R.id.simple_path_layout);
        mWaypointLayout = (RelativeLayout) findViewById(R.id.waypoint_layout);
        mTempComplexRoute = new ArrayList<>();
        mWaypoint = (EditText) findViewById(R.id.waypoint_layout_search_input);
        mAddMarker = (Button) findViewById(R.id.add_marker);
        mCreatePath = (Button) findViewById(R.id.create_path);
        mPlayStopBtn = (RelativeLayout) findViewById(R.id.play_stop);
        mPauseResumeBtn = (RelativeLayout) findViewById(R.id.pause_resume);
        mPathTrackingLayout = (LinearLayout) findViewById(R.id.path_tracking_layout);
        mDrawTrackSrc = (ImageView) findViewById(R.id.track_draw_src);
        mPauseResumeSrc = (ImageView) findViewById(R.id.pause_resume_src);
        mStartTracking = false;
        mContentView = (LinearLayout) findViewById(R.id.path_scroll_menu_content);
        mPathScrollView = (RelativeLayout) findViewById(R.id.path_menu_layout);
        mCloseHistoryMenuView = (Button) findViewById(R.id.path_close_menu);
        routes = new HashMap<String, RouteData>();
        mTempRouteData = null;
        mWaypointInputBtn = (Button) findViewById(R.id.waypoint_add_marker);
        mSimpleInputBtn1 = (Button) findViewById(R.id.simple_add_marker_1);
        mSimpleInputBtn2 = (Button) findViewById(R.id.simple_add_marker_2);
        mNewPathBtn = findViewById(R.id.ic_new_general_path_btn_layout);
        mPathHistoryBtn = findViewById(R.id.ic_my_paths_btn_layout);
        radioBtnId = -1;
        pathFData = null;
        pathForm1 = findViewById(R.id.path_form_1);
        pathForm2 = findViewById(R.id.path_form_2);
        pathFormLayout = findViewById(R.id.path_form_layout);
        mPathTitleInput = (EditText) findViewById(R.id.path_title_input);
        mPathDescInput = (EditText) findViewById(R.id.path_description_input);
        mPathDescInput.setMovementMethod(new ScrollingMovementMethod());
        mMenuLayout = findViewById(R.id.menu_items_layout);
        radioGroup = findViewById(R.id.radio_group);
        geoSpots = new HashMap<String, Marker>();
        geoSpotData = new HashMap<String, GeoSpotData>();
        mChooseIntervalBtn = findViewById(R.id.choose_time);
        mTimeIntervalForm = findViewById(R.id.time_picker_layout);
        mSecondsPicker = findViewById(R.id.time_picker);
        mSecondsPickerBtn = findViewById(R.id.time_picker_confirm_btn);
        walkMode = false;
        mSecondsPicker.setMinValue(1);
        mSecondsPicker.setMaxValue(60);
        circleRadius = null;

    }


    private void attemptSimplePath() {

        hideMenu();
        showSimplePath();

    }

    private void attemptWaypoints() {

        hideMenu();
        showWaypoints();
    }

    private void attemptPathTracking() {

        hideMenu();
        showPathTracking();
    }

    private void attemptToAddKML() {
        hideMenu();
        if (kmllayer==null)
            try {
                kmllayer = new KmlLayer(mMap, R.raw.parques, MapActivity.this);
            } catch (Exception e) {
                e.toString();
            }
        else if (!kmllayer.isLayerOnMap()) {
            Log.i(TAG, "KML layer added");
            try {
                kmllayer.addLayerToMap();
            } catch (Exception e) {
                e.toString();
            }
        } else if (kmllayer.isLayerOnMap()) {
            if (kmllayer.getMap() != null){
                Log.i(TAG, "KML layer present");
                kmllayer.removeLayerFromMap();
            }
        }
    }

    private void hideSimplePath(){
        mSimplePathLayout.setVisibility(View.GONE);
    }

    private void showSimplePath(){
        mSimplePathLayout.setVisibility(View.VISIBLE);
        mWaypointLayout.setVisibility(View.GONE);
        mPathTrackingLayout.setVisibility(View.GONE);
        mPathScrollView.setVisibility(View.GONE);
    }

    private void hideWaypoints(){
        mWaypointLayout.setVisibility(View.GONE);
    }

    private void showWaypoints(){
        mSimplePathLayout.setVisibility(View.GONE);
        mWaypointLayout.setVisibility(View.VISIBLE);
        mPathTrackingLayout.setVisibility(View.GONE);
        mPathScrollView.setVisibility(View.GONE);
    }

    private void hidePathTracking(){
        mPathTrackingLayout.setVisibility(View.GONE);
    }

    private void showPathTracking(){
        mSimplePathLayout.setVisibility(View.GONE);
        mWaypointLayout.setVisibility(View.GONE);
        mPathTrackingLayout.setVisibility(View.VISIBLE);
        mPathScrollView.setVisibility(View.GONE);
    }

    public void pathFormClicks(View view){

        int id = view.getId();
        radioBtnId = radioGroup.getCheckedRadioButtonId();
        Log.d(TAG, "pathFormClicks: " + radioBtnId + " " + R.id.radio_1 + " " + id);
        switch (id){
            case R.id.next_form_button: {

                if( radioBtnId != R.id.radio_1 &&
                    radioBtnId != R.id.radio_2 &&
                    radioBtnId != R.id.radio_3 ){

                    Toast.makeText(MapActivity.this, "Please select an option before proceeding.", Toast.LENGTH_SHORT).show();
                }else{
                    pathFData = new PathFormData(radioBtnId);
                    pathForm1.setVisibility(View.GONE);
                    pathForm2.setVisibility(View.VISIBLE);
                    RadioButton r = (RadioButton) findViewById(pathFData.getSelectionID());
                    r.setChecked(false);
                }
                break;
            }
            case R.id.cancel_form_button: {

                pathFormLayout.setVisibility(View.GONE);
                pathForm1.setVisibility(View.VISIBLE);
                pathForm2.setVisibility(View.GONE);
                pathFData = null;
                radioBtnId = -1;

                break;
            }
            case R.id.confirm_form_button: {

                String title = mPathTitleInput.getText().toString();
                String description = mPathDescInput.getText().toString();

                if(title.length() == 0) {
                    mPathTitleInput.setError(getString(R.string.error_field_required));
                    mPathTitleInput.requestFocus();

                    Toast.makeText(MapActivity.this, "Please add title.", Toast.LENGTH_SHORT).show();

                } else {
                    switch (pathFData.getSelectionID()){
                        case R.id.radio_1 :{
                            attemptSimplePath();
                            break;
                        }
                        case R.id.radio_2 :{
                            attemptWaypoints();
                            break;
                        }
                        case R.id.radio_3 :{
                            attemptPathTracking();
                            break;
                        }
                    }

                    pathFData.setPathTitle(title);
                    pathFData.setPathDescription(description);
                    pathFormLayout.setVisibility(View.GONE);
                    pathForm1.setVisibility(View.VISIBLE);
                    pathForm2.setVisibility(View.GONE);
                    radioBtnId = -1;
                    mPathTitleInput.setText("");
                    mPathDescInput.setText("");
                }
                break;
            }
            case R.id.cancel_form_button2: {

                pathFormLayout.setVisibility(View.GONE);
                pathForm1.setVisibility(View.VISIBLE);
                pathForm2.setVisibility(View.GONE);
                radioBtnId = -1;
                pathFData = null;
                mPathTitleInput.setText("");
                mPathDescInput.setText("");

                break;
            }
        }

    }

    public void walkMode(View v){

        walkMode = !walkMode;

        if(walkMode){
            CircleOptions circleOptions = new CircleOptions()
                    .center(new com.google.android.gms.maps.model.LatLng(-33.87365, 151.20689))
                    .radius(RADIUS)
                    .strokeColor(Color.RED)
                    .visible(true);
            circleRadius = mMap.addCircle(circleOptions);
            v.getBackground().setColorFilter(Color.parseColor("#46C47E"), PorterDuff.Mode.SRC_ATOP);
            mOperationsLayout.setVisibility(View.GONE);
            timerWalk = new Timer();
            timerTaskWalk = new TimerTask() {
                @Override
                public void run() {
                    showGeoSpots();
                }
            };
            timerWalk.schedule(timerTaskWalk,0, WALKING_INTERVAL);
            Toast.makeText(MapActivity.this, "Walk Mode: On.", Toast.LENGTH_SHORT).show();
        }else{
            timerTaskWalk.cancel();
            timerWalk.cancel();
            Toast.makeText(MapActivity.this, "Walk Mode: Off.", Toast.LENGTH_SHORT).show();
            v.getBackground().setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
            mOperationsLayout.setVisibility(View.VISIBLE);
            for(Map.Entry<String, Marker> entry: geoSpots.entrySet()){
                Marker geospot = entry.getValue();
                geospot.setVisible(true);
            }
            circleRadius.remove();
        }

    }

    private void openGeoSpot(Marker gspt){

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        if (currentLocation != null) {

                                Location geospotLocation = new Location("temp");
                                geospotLocation.setLatitude(gspt.getPosition().latitude);
                                geospotLocation.setLongitude(gspt.getPosition().longitude);
                                float distance = currentLocation.distanceTo(geospotLocation);

                                if (distance < MINIMUM_DIST) {
                                    if(gspt.getTag() != null){
                                        GeoSpotData toDisplay = geoSpotData.get(gspt.getTag());
                                        Log.d(TAG, "onMarkerClick: " + toDisplay.getGeoSpotName() + " ," + toDisplay.getLocation()
                                                + " ," + toDisplay.getDescription() + " ," + toDisplay.getTags());
                                        Intent details = new Intent(MapActivity.this, DisplayGeoSpotActivity.class);
                                        details.putExtra("geoSpotData", toDisplay);
                                        startActivity(details);
                                    }
                                }
                        }
                    } else {
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException " + e.getMessage());
        }

    }

    private void showGeoSpots (){

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        if(currentLocation != null){
                            com.google.android.gms.maps.model.LatLng currentCoords =
                                    new com.google.android.gms.maps.model.LatLng(currentLocation.getLatitude(),
                                            currentLocation.getLongitude());
                            circleRadius.setCenter(currentCoords);
                            for(Map.Entry<String, Marker> entry: geoSpots.entrySet()){

                                Marker geospot = entry.getValue();
                                Location geospotLocation = new Location("temp");
                                geospotLocation.setLatitude(geospot.getPosition().latitude);
                                geospotLocation.setLongitude(geospot.getPosition().longitude);
                                float distance = currentLocation.distanceTo(geospotLocation);

                                if(distance < RADIUS)
                                    geospot.setVisible(true);
                                else
                                    geospot.setVisible(false);
                            }
                        }
                    } else {
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException " + e.getMessage());
        }

    }



    private void hideMenu(){
        mMenuLayout.setVisibility(View.GONE);
    }

    private void showMenu(){
        hideSimplePath();
        hideWaypoints();
        hidePathTracking();
        mMenuLayout.setVisibility(View.VISIBLE);
    }


    private void init() {
        Log.d(TAG, "init: initializing");

        loadAllData();


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragStart..." + arg0.getPosition().latitude + "..." + arg0.getPosition().longitude);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragEnd..." + arg0.getPosition().latitude + "..." + arg0.getPosition().longitude);

                mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                Log.i("System out", "onMarkerDrag...");
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!walkMode){
                    if(marker.getTag() != null){

                        GeoSpotData toDisplay = geoSpotData.get(marker.getTag());
                        Log.d(TAG, "onMarkerClick: " + toDisplay.getGeoSpotName() + " ," + toDisplay.getLocation() + " ," + toDisplay.getDescription() + " ," + toDisplay.getTags());
                        Intent details = new Intent(MapActivity.this, DisplayGeoSpotActivity.class);
                        details.putExtra("geoSpotData", toDisplay);
                        startActivity(details);

                    }
                }else{
                    openGeoSpot(marker);
                }
                return false;
            }
        });

        //---------------------------------Menu Listeners-----------------------------

        mNewPathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pathFormLayout.setVisibility(View.VISIBLE);
                pathForm1.setVisibility(View.VISIBLE);
            }
        });

        mPathHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPathScrollView.setVisibility(View.VISIBLE);
            }
        });

        //---------------------------------Simple Path Listeners-----------------------

        mSimpleInputBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFromSearchText.getText().toString().length() == 0) {
                    MarkerOptions options = new MarkerOptions()
                            .position(mMap.getCameraPosition().target)
                            .draggable(true)
                            .title("" + mMap.getCameraPosition().target);

                    if(mTempPlace1 == null)
                        mTempPlace1 = mMap.addMarker(options);
                    else
                        mTempPlace1.setPosition(options.getPosition());

                    moveCamera(mTempPlace1.getPosition(), DEFAULT_ZOOM, mTempPlace1.getTitle());
                } else {

                    Address address = geoLocate(mFromSearchText);
                    if (address != null) {
                        MarkerOptions options = new MarkerOptions()
                                .position(new com.google.android.gms.maps.model
                                        .LatLng(address.getLatitude(), address.getLongitude()))
                                .draggable(true)
                                .title(address.getUrl());
                        if(mTempPlace1 == null)
                            mTempPlace1 = mMap.addMarker(options);
                        else
                            mTempPlace1.setPosition(options.getPosition());

                        moveCamera(mTempPlace1.getPosition(), DEFAULT_ZOOM, mTempPlace1.getTitle());

                    } else {
                        Toast.makeText(MapActivity.this, "Adress Not Found.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onEditorAction: address 1 not found.");
                        mFromSearchText.requestFocus();
                    }
                }
            }
        });

        mSimpleInputBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mToSearchText.getText().toString().length() == 0) {
                    MarkerOptions options = new MarkerOptions()
                            .position(mMap.getCameraPosition().target)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            .title("" + mMap.getCameraPosition().target);


                    if(mTempPlace2 == null)
                        mTempPlace2 = mMap.addMarker(options);
                    else
                        mTempPlace2.setPosition(options.getPosition());

                    moveCamera(mTempPlace2.getPosition(), DEFAULT_ZOOM, mTempPlace2.getTitle());
                } else{

                    Address address = geoLocate(mToSearchText);
                    if (address != null) {
                        MarkerOptions options = new MarkerOptions()
                                .position(new com.google.android.gms.maps.model
                                        .LatLng(address.getLatitude(), address.getLongitude()))
                                .draggable(true)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                .title(address.getUrl());
                        if(mTempPlace2 == null)
                            mTempPlace2 = mMap.addMarker(options);
                        else
                            mTempPlace2.setPosition(options.getPosition());

                        moveCamera(mTempPlace2.getPosition(), DEFAULT_ZOOM, mTempPlace2.getTitle());

                    } else {
                        Toast.makeText(MapActivity.this, "Adress Not Found.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onEditorAction: address 2 not found.");
                        mToSearchText.requestFocus();
                    }
                }


            }
        });



        mGoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTempPlace1 == null) {
                    mFromSearchText.requestFocus();
                    return;
                }
                if (mTempPlace2 == null) {
                    mToSearchText.requestFocus();
                    return;
                }

                mFromSearchText.clearFocus();
                mToSearchText.clearFocus();

                LatLng origin = new LatLng(mTempPlace1.getPosition().latitude, mTempPlace1.getPosition().longitude);
                LatLng destination = new LatLng(mTempPlace2.getPosition().latitude, mTempPlace2.getPosition().longitude);

                List<Marker> markers = new ArrayList<>();
                markers.add(mTempPlace1);
                markers.add(mTempPlace2);

                RouteData newRoute = new RouteData(genPathID(), pathFData.pathTitle, markers, pathFData.pathDescription, "WALKING", false, myUsername);
                routes.put(newRoute.getPath_id(),newRoute);

                calcDirectionsV_2(newRoute.getPath_id(), origin, null, destination);
                setPathCamera(mTempPlace1.getPosition(), mTempPlace2.getPosition());

                newPath(newRoute);
                savePath(newRoute);

                mTempPlace1.setDraggable(false);
                mTempPlace2.setDraggable(false);
                mTempPlace1 = null;
                mTempPlace2 = null;
                mFromSearchText.setText("");
                mToSearchText.setText("");
                showMenu();
            }
        });

        //---------------------------------Waypoints Listeners-------------------------


        mWaypointInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTempComplexRoute.size() < 25) {
                    Log.d(TAG, "waypointBtn onClick: '" + mWaypoint.getText() + "'");
                    if (mWaypoint.getText().toString().length() == 0) {
                        MarkerOptions options = new MarkerOptions()
                                .position(mMap.getCameraPosition().target)
                                .draggable(true)
                                .title("" + mMap.getCameraPosition().target);

                        if (mCurrentWaypointMarker == null)
                            mCurrentWaypointMarker = mMap.addMarker(options);
                        else
                            mCurrentWaypointMarker.setPosition(options.getPosition());

                        moveCamera(mCurrentWaypointMarker.getPosition(), DEFAULT_ZOOM, mCurrentWaypointMarker.getTitle());
                    } else {

                        Address address = geoLocate(mWaypoint);
                        if (address != null) {//might be wrong assertion
                            mWaypoint.clearFocus();
                            MarkerOptions options = new MarkerOptions()
                                    .position(new com.google.android.gms.maps.model
                                            .LatLng(address.getLatitude(), address.getLongitude()))
                                    .draggable(true)
                                    .title(address.getUrl());
                            if (mCurrentWaypointMarker == null)
                                mCurrentWaypointMarker = mMap.addMarker(options);
                            else {
                                mCurrentWaypointMarker.setPosition(options.getPosition());
                            }

                            moveCamera(mCurrentWaypointMarker.getPosition(), DEFAULT_ZOOM, mCurrentWaypointMarker.getTitle());
                        } else {
                            Toast.makeText(MapActivity.this, "Adress Not Found.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onEditorAction: address 2 not found.");
                            mWaypoint.requestFocus();
                        }
                    }
                } else {
                    Toast.makeText(MapActivity.this, "Maximum waypoints reached. Please Create Path.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAddMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (mCurrentWaypointMarker != null) {
                        mTempComplexRoute.add(mCurrentWaypointMarker);
                        mWaypoint.setText("");
                        mCurrentWaypointMarker = null;
                        Toast.makeText(MapActivity.this, "Marker Saved! Pick Next Marker.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MapActivity.this, "Marker Already Saved!", Toast.LENGTH_SHORT).show();
                    }
                }
        });

        mCreatePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTempComplexRoute.size() >= 3) {

                    RouteData newRoute = new RouteData(genPathID(), pathFData.pathTitle, mTempComplexRoute, pathFData.pathDescription, "WALKING", false, myUsername);
                    routes.put(newRoute.getPath_id(), newRoute);

                    Marker tempOri = mTempComplexRoute.remove(0);
                    LatLng origin = new LatLng(tempOri.getPosition().latitude, tempOri.getPosition().longitude);
                    Marker tempDes = mTempComplexRoute.remove(mTempComplexRoute.size() - 1);
                    LatLng destination = new LatLng(tempDes.getPosition().latitude, tempDes.getPosition().longitude);

                    LatLng[] waypoints = new LatLng[mTempComplexRoute.size()];
                    for (int i = 0; i < mTempComplexRoute.size(); i++) {
                        Marker marker = mTempComplexRoute.get(i);
                        waypoints[i] = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                    }

                    tempOri.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    tempDes.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                    //calculateWaypointsDirections(origin, waypoints, destination);

                    calcDirectionsV_2(newRoute.getPath_id(), origin, waypoints, destination);
                    setPathCamera(tempOri.getPosition(), tempDes.getPosition());

                    newPath(newRoute);
                    savePath(newRoute);

                    mTempComplexRoute = new ArrayList<Marker>();
                    mWaypoint.setText("");
                    showMenu();

                } else {
                    Toast.makeText(MapActivity.this, "Error: A Waypoint Path requires a minimum of 3 markers!", Toast.LENGTH_SHORT).show();
                    mWaypoint.requestFocus();
                }
            }
        });

        //---------------------------------Path Tracking Listeners-------------------------

        mPlayStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked play button");

                mStartTracking = !mStartTracking;

                if(mStartTracking){
                    mTimeIntervalForm.setVisibility(View.VISIBLE);
                    mTempGPSRoute = new ArrayList<>();
                    mTempGPSLines = new ArrayList<>();
                    mFinalGSPPolyOptions = new PolylineOptions();

                }else{
                    mDrawTrackSrc.setImageResource(R.drawable.ic_draw_track);
                    mPauseResumeBtn.setVisibility(View.GONE);
                    mChooseIntervalBtn.setVisibility(View.GONE);
                    stopTrackingUser();
                    mPause = false;
                    showMenu();
                }
            }
        });

        mPauseResumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPause = !mPause;

                if(mPause){
                    mPauseResumeSrc.setImageResource(R.drawable.ic_resume);
                    mChooseIntervalBtn.setVisibility(View.VISIBLE);
                    //stop
                    timerTask.cancel();
                    timer.cancel();
                }else{
                    //resume
                    mChooseIntervalBtn.setVisibility(View.GONE);
                    int interval = mSecondsPicker.getValue() * 1000;
                    timer = new Timer();
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            lastLocation();
                        }
                    };
                    timer.schedule(timerTask,interval, interval);
                    mPauseResumeSrc.setImageResource(R.drawable.ic_pause);
                }
            }
        });

        mChooseIntervalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimeIntervalForm.setVisibility(View.VISIBLE);
            }
        });

        mSecondsPickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mStartTracking && !mPause){
                    int interval = mSecondsPicker.getValue() * 1000;
                    mDrawTrackSrc.setImageResource(R.drawable.ic_stop);
                    trackUser(interval);
                    mPauseResumeBtn.setVisibility(View.VISIBLE);
                }

                TextView secondsText = findViewById(R.id.choose_time_text);
                secondsText.setText(mSecondsPicker.getValue() + "s");
                mTimeIntervalForm.setVisibility(View.GONE);

            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        //---------------------------------Path History Listeners-------------------------
        mCloseHistoryMenuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPathScrollView.setVisibility(View.GONE);
            }
        });

        hideSoftKeyboard();
    }

    //<--------------------------------ROUTE MANAGER------------------------------------------------------------------->
    public void newPath(RouteData data){

        final View mPathView = getLayoutInflater().inflate(R.layout.path_row_item,null,false);

        TextView mTitle = (TextView) mPathView.findViewById(R.id.path_item_title);
        mTitle.setText(data.getTitle());
        mTitle.setMovementMethod(new ScrollingMovementMethod());

        TextView mDate = (TextView) mPathView.findViewById(R.id.path_item_date);
        DateTime date = DateTime.now();
        String ddmmyyyy = date.toString("dd/M/yyyy");
        mDate.setText(ddmmyyyy);

        if(data.getMarkers().size() > 2 && !data.isTracked())
            mPathView.setBackgroundColor(getResources().getColor(R.color.washed_blue));
        else if(data.isTracked())
            mPathView.setBackgroundColor(getResources().getColor(R.color.washed_green));


        Button mInfoBtn = mPathView.findViewById(R.id.path_item_info);
        mInfoBtn.setTag(data.getPath_id());
        mInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RouteData toDisplay = routes.get(view.getTag());
                Intent details = new Intent(MapActivity.this, PathDetailsActivity.class);
                details.putExtra("routeID", toDisplay.getPath_id());
                details.putExtra("title", toDisplay.getTitle());
                details.putExtra("description", toDisplay.getDescription());
                details.putExtra("owner", toDisplay.getOwner());
                Log.d(TAG, "onClick: " + toDisplay.getOwner());
                startActivity(details);

            }
        });

        Button mGoToBtn = mPathView.findViewById(R.id.path_item_goto);
        mGoToBtn.setTag(data.getPath_id());
        mGoToBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RouteData data = routes.get(view.getTag());
                List<Marker> markers = data.getMarkers();
                for(Marker mk : markers){
                    Log.d(TAG, "mGOTOBTN onClick: " + mk.getPosition());
                }
                com.google.android.gms.maps.model.LatLng start =
                        markers.get(0).getPosition();
                com.google.android.gms.maps.model.LatLng finish =
                        markers.get(markers.size()-1).getPosition();

                setPathCamera(start,finish);
            }
        });

        Button mDeleteBtn = mPathView.findViewById(R.id.path_item_delete);
        mDeleteBtn.setTag(data.getPath_id());
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(MapActivity.this)
                        .setMessage("Are you sure you want to delete this path?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                RouteData data = routes.get(view.getTag());
                                List<Marker> markers = data.getMarkers();
                                for (Marker marker : markers)
                                    marker.remove();
                                data.getPath().remove();
                                routes.remove(view.getTag());
                                mContentView.removeView(mPathView);

                                if(data.getOwner().equals(myUsername)) {
                                    //TODO: delete from server
                                }
                            }
                        })
                        .setNegativeButton("No", null).show();
            }
        });

        Button mVisibileBtn = mPathView.findViewById(R.id.path_item_visibile);
        mVisibileBtn.setTag(data.getPath_id());
        mVisibileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RouteData data = routes.get(view.getTag());
                List<Marker> markers = data.getMarkers();
                for(Marker marker: markers)
                    marker.setVisible(false);
                data.getPath().setVisible(false);

                view.setVisibility(View.GONE);
                ((LinearLayout) ((ViewGroup) view.getParent()))
                        .findViewById(R.id.path_item_invisibile).setVisibility(View.VISIBLE);

            }
        });

        Button mInvisibileBtn = mPathView.findViewById(R.id.path_item_invisibile);
        mInvisibileBtn.setTag(data.getPath_id());
        mInvisibileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RouteData data = routes.get(view.getTag());
                List<Marker> markers = data.getMarkers();
                for(Marker marker: markers)
                    marker.setVisible(true);
                data.getPath().setVisible(true);

                view.setVisibility(View.GONE);
                ((LinearLayout) ((ViewGroup) view.getParent()))
                        .findViewById(R.id.path_item_visibile).setVisibility(View.VISIBLE);

            }
        });

        mContentView.addView(mPathView);

    }

    private void loadAllData(){

        PullUserRoutesTask pullRoutesTask = new PullUserRoutesTask(false);
        pullRoutesTask.execute((Void) null);

        //TODO:Areas

        PullGeoSpotsTask pullGeoSpotsTask = new PullGeoSpotsTask();
        pullGeoSpotsTask.execute((Void) null);
    }

    private void loadGeoSpots(List<GeoSpotData> pulledGeoSpots){

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                BitmapDescriptor icon = bitmapDescriptorFromVector(MapActivity.this, R.drawable.geospot_icon_definitive);

                for(GeoSpotData pulledGeoSpot: pulledGeoSpots){

                    PointerData coord = pulledGeoSpot.getLocation();

                    MarkerOptions options = new MarkerOptions()
                            .position(new com.google.android.gms.maps.model
                            .LatLng(Double.parseDouble(coord.lat), Double.parseDouble(coord.lng)))
                            .draggable(false)
                            .title(pulledGeoSpot.getGeoSpotName())
                            .icon(icon);
                    Marker geoSpotMk = mMap.addMarker(options);

                    geoSpotMk.setTag(pulledGeoSpot.getGeoSpotName());
                    geoSpotData.put(pulledGeoSpot.getGeoSpotName(), pulledGeoSpot);
                    geoSpots.put(pulledGeoSpot.getGeoSpotName(), geoSpotMk);
                }
            }
        });

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void loadUserPaths(List<UserRoutePullData> pullData){

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                List<UserRoutePullData> userRoutes = pullData;
                for (UserRoutePullData route: userRoutes) {

                    List<Marker> temp = new ArrayList<>();
                    LatLng origin = null;
                    LatLng destination = null;
                    LatLng[] wayp = null;
                    List<com.google.android.gms.maps.model.LatLng> points = points = new ArrayList<>();;


                    origin = new LatLng(Double.parseDouble(route.origin.lat), Double.parseDouble(route.origin.lng));
                    Marker originMk = mMap.addMarker( new MarkerOptions().position(new com.google.android.gms.maps.model
                            .LatLng(origin.lat, origin.lng))
                            .draggable(false)
                            .title(origin.lat + " " + origin.lng));

                    temp.add(originMk);
                    points.add(new com.google.android.gms.maps.model.LatLng(origin.lat,origin.lng));

                    Set<PointerData> waypoints = route.getIntermidiatePoints();
                    if(waypoints != null){
                        if(route.isTracked){
                            for(PointerData mkCoords: waypoints){
                                points.add(new com.google.android.gms.maps.model.LatLng(Double.parseDouble(mkCoords.lat),Double.parseDouble(mkCoords.lng)));
                            }
                        }else {
                            wayp = new LatLng[waypoints.size()];
                            int counter = 0;
                            for (PointerData mkCoords : waypoints) {
                                LatLng point = new LatLng(Double.parseDouble(mkCoords.lat), Double.parseDouble(mkCoords.lng));
                                Marker tempMk = mMap.addMarker(new MarkerOptions().position(new com.google.android.gms.maps.model
                                        .LatLng(point.lat, point.lng))
                                        .draggable(false)
                                        .title(point.lat + " " + point.lng));
                                temp.add(tempMk);
                                wayp[counter++] = point;
                            }
                        }
                    }

                    destination = new LatLng(Double.parseDouble(route.destination.lat), Double.parseDouble(route.destination.lng));
                    Marker destinationMk = mMap.addMarker( new MarkerOptions().position(new com.google.android.gms.maps.model
                            .LatLng(destination.lat,destination.lng))
                            .draggable(false)
                            .title(destination.lat + " " + destination.lng));

                    temp.add(destinationMk);
                    points.add(new com.google.android.gms.maps.model.LatLng(destination.lat,destination.lng));


                    Log.d(TAG, "LoadUserPath-run: origin " + origin +  " destination " + destination);

                    RouteData pulledRoute = new RouteData(route.getId(), route.getTitle(), temp, route.getDescription(), route.getTravelMode(), route.isTracked, route.getUsername());
                    routes.put(route.getId(), pulledRoute);

                    if(route.isTracked){
                        PolylineOptions options = new PolylineOptions();
                        options.addAll(points);
                        Polyline finalLine = mMap.addPolyline(options);
                        routes.get(route.getId()).setPath(finalLine);

                    }else {
                        calcDirectionsV_2(pulledRoute.getPath_id(), origin, wayp, destination);
                    }

                    newPath(pulledRoute);

                }
            }
        });

    }

    private void savePath(RouteData data){

        JSONObject json = new JSONObject();
        try {
            SharedPreferences authentication = getSharedPreferences("AUTHENTICATION", 0);
            String username = authentication.getString("username", null);
            String token = authentication.getString("token", null);

            json.accumulate("id", data.getPath_id());
            json.accumulate("username", username);
            json.accumulate("title", data.getTitle());
            json.accumulate("description", data.getDescription());
            json.accumulate("travelMode", data.getTravelMode());
            List<Marker> markers = data.getMarkers();
            Marker originMk = markers.get(0);

            JSONObject origin = new JSONObject();
            origin.accumulate("lat",Double.toString(originMk.getPosition().latitude));
            origin.accumulate("lng",Double.toString(originMk.getPosition().longitude));
            json.accumulate("origin", origin);

            Marker destinMk = markers.get(markers.size()-1);
            JSONObject destination = new JSONObject();
            destination.accumulate("lat",Double.toString(destinMk.getPosition().latitude));
            destination.accumulate("lng",Double.toString(destinMk.getPosition().longitude));
            json.accumulate("destination", destination);


            JSONArray intermidiates = null;
            if (markers.size() > 2) {
                intermidiates = new JSONArray();
                for (int i = 1; i < markers.size() - 1; i++) {
                    Marker temp = markers.get(i);
                    JSONObject j = new JSONObject();
                    j.accumulate("lat", Double.toString(temp.getPosition().latitude));
                    j.accumulate("lng", Double.toString(temp.getPosition().longitude));

                    intermidiates.put(j);
                }
            }

            json.put("intermidiatePoints", intermidiates);
            json.accumulate("isTracked", data.isTracked());

            PostUserRouteTask mPostRouteTask = new PostUserRouteTask(json, token);
            mPostRouteTask.execute((Void) null);
        } catch (Exception e) {
            Log.d(TAG, "PostPath-doInBackground: exception:" + e.getMessage());
            e.printStackTrace();
        }

    }

    private void setPathCamera(com.google.android.gms.maps.model.LatLng start,
                               com.google.android.gms.maps.model.LatLng finish) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(start);
        builder.include(finish);

        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }

    //do in the background
    private void trackUser(int interval) {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                lastLocation();
            }
        };
        timer.schedule(timerTask, 0, interval);

    }

    private void stopTrackingUser() {
        timerTask.cancel();
        timer.cancel();
        lastLocation();

        Polyline realPolyline = mMap.addPolyline(mFinalGSPPolyOptions);
        for(Polyline line: mTempGPSLines){
            line.remove();
        }

        Marker origin = mTempGPSRoute.remove(0);
        Marker destination = mTempGPSRoute.remove(mTempGPSRoute.size()-1);

        setPathCamera(origin.getPosition(), destination.getPosition());
        RouteData TrackedRoute = new RouteData(genPathID(), pathFData.pathTitle, mTempGPSRoute, pathFData.pathDescription, "WALKING", true, myUsername);
        TrackedRoute.setPath(realPolyline);
        routes.put(TrackedRoute.getPath_id(), TrackedRoute);

        newPath(TrackedRoute);
        savePath(TrackedRoute);
    }

    private void lastLocation() {
        Log.d(TAG, "getDeviceLocation: getting the device's current location for tracking");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();
                            if(currentLocation != null) {
                                com.google.android.gms.maps.model.LatLng currentCoord =
                                        new com.google.android.gms.maps.model.LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                                MarkerOptions options = new MarkerOptions()
                                        .position(currentCoord)
                                        .title("Path Tracking Marker : " + currentCoord);
                                Marker newMarker = mMap.addMarker(options);
                                mTempGPSRoute.add(newMarker);
                                mFinalGSPPolyOptions.add(newMarker.getPosition());

                                if (mTempGPSRoute.size() > 1) {
                                    //need to save polyline and markers for later;
                                    com.google.android.gms.maps.model.LatLng lastMarkerPos = mTempGPSRoute.get(mTempGPSRoute.size()-1).getPosition();
                                    Polyline polyline = mMap.addPolyline(new PolylineOptions().add(lastMarkerPos, currentCoord));
                                    mTempGPSLines.add(polyline);
                                }
                                moveCamera(newMarker.getPosition(), TRACKING_ZOOM, "My Location");
                            }
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException " + e.getMessage());
        }
    }

    //possibly use only this
    private void calcDirectionsV_2(String pathID, LatLng origin, LatLng[] waypoints, LatLng destination){

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
        directions.mode(TravelMode.WALKING);//check this
        directions.origin(origin);

        if(waypoints != null)
            directions.waypoints(waypoints);
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        for (DirectionsLeg leg : result.routes[0].legs) {
                            PolylineOptions lineOptions = new PolylineOptions();
                            List<com.google.android.gms.maps.model.LatLng> points = new ArrayList<>();

                            for (int i = 0; i < leg.steps.length; i++) {
                                DirectionsStep step = leg.steps[i];
                                List<LatLng> decodedPath = PolylineEncoding.decode(step.polyline.getEncodedPath());

                                for (LatLng coords : decodedPath) {
                                    points.add(new com.google.android.gms.maps.model.LatLng(coords.lat, coords.lng));
                                }
                            }

                            lineOptions.addAll(points);
                            lineOptions.width(20);
                            lineOptions.color(R.color.colorPrimaryDark);
                            Polyline finalPolyline = mMap.addPolyline(lineOptions);
                            finalPolyline.setVisible(true);

                            RouteData route = routes.get(pathID);
                            route.setPath(finalPolyline);
                            route.setDuration(result.routes[0].legs[0].duration.toString());
                            route.setPathLength(result.routes[0].legs[0].distance.toString());
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: calcDirectionsV_2: Failed to get directions: " + e.getMessage());
            }
        });

    }

    private Address geoLocate(TextView searchView) {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = searchView.getText().toString() + ", Portugal";

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException " + e.getMessage());
        }
        Address address = null;
        if (list.size() > 0) {
            address = list.get(0);

            Log.d(TAG, "geoLocate: found a location " + address.toString());
            //maybe change this
            //moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }

        return address;

    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the device's current location");

        if (haveGPSPPermissions()) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            try {
                    Task location = mFusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: found location");
                                Location currentLocation = (Location) task.getResult();

                                Log.d(TAG, "onComplete: location: " + currentLocation);

                                if(currentLocation != null){
                                    //move camera
                                    moveCamera(new com.google.android.gms.maps.model.LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                            DEFAULT_ZOOM, "My Location");
                                }
                            } else {
                                Log.d(TAG, "onComplete: current location is null");
                                Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

            } catch (SecurityException e) {
                Log.e(TAG, "getDeviceLocation: SecurityException " + e.getMessage());
            }

        } else {
            buildAlertMessageNoGps();
        }
    }

    private void moveCamera(com.google.android.gms.maps.model.LatLng latlng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latlng.latitude + ", lng: " + latlng.longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));

        hideSoftKeyboard();
    }

    private String genPathID(){
        SharedPreferences authentication = getSharedPreferences("AUTHENTICATION", 0);
        String username = authentication.getString("username", null);
        String pathID = username + UUID.randomUUID().toString();
        return pathID;
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "onMapReady: Map is ready");
                Toast.makeText(MapActivity.this, "Map is ready.", Toast.LENGTH_SHORT).show();
                mMap = googleMap;

                if (mLocationPermissionsGranted) {
                    getDeviceLocation();

                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);

                    init();

                    if(mGeoApiContext == null){
                        mGeoApiContext = new GeoApiContext.Builder()
                        .apiKey(getString(R.string.google_maps_api_key))
                        .build();
                    }
                }
            }
        });
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean haveGPSPPermissions(){
        return mLocationPermissionsGranted && isGPSEnabled();
    }

    public boolean isGPSEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isMapsEnabled(){

        if ( !isGPSEnabled()) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        Log.d(TAG,"getLocationPermission: getting location permissions");

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "getLocationPermission: granted");
                mLocationPermissionsGranted = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            Log.e(TAG, "getLocationPermission: not granted");
            ActivityCompat.requestPermissions(this,
                    permissions,
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
            Log.d(TAG, "isServicesOK: checking google services version");

            int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapActivity.this);

            if(available == ConnectionResult.SUCCESS){
                //everything is fine and the user can make map requests
                Log.d(TAG, "isServicesOK: Google Play Services is working");
                return true;
            }
            else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
                //an error occured but we can resolve it
                Log.d(TAG, "isServicesOK: an error occured but we can fix it");
                Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapActivity.this, available, ERROR_DIALOG_REQUEST);
                dialog.show();
            }else{
                Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
            }
            return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionsGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");

        if (requestCode == PERMISSIONS_REQUEST_ENABLE_GPS) {
            Log.d(TAG, "onActivityResult: mLocationPermissionsGranted: " + mLocationPermissionsGranted);
            if (!haveGPSPPermissions()) {
                Toast.makeText(this, "can't use map without gps", Toast.LENGTH_SHORT).show();
                finish();
            }else{
               getDeviceLocation();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnUserArea = new Intent(MapActivity.this, UserAreaActivity.class);
        startActivity(returnUserArea);
        super.onBackPressed();
    }

    //<----------------------------------------ASYNC TASKS----------------------------------------->

    public class PostUserRouteTask extends AsyncTask<Void, Void, String> {

        private final JSONObject data;
        private final String token;

        PostUserRouteTask(JSONObject data, String token) {
            this.data = data;
            this.token = token;
        }

        @Override
        protected String doInBackground(Void... params) {

            Log.d(TAG, "POSTuserroute-doInBackground: data" + data.toString() + " token: " + token );
            //get token here instead of save path
            try {
                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/route/submit"),data, token);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
            }

        @Override
        protected void onPostExecute(final String success) {

            Log.d(TAG, "onPostExecute: result: " + success);
            if (success != null) {
                Toast.makeText(MapActivity.this, "Path Saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MapActivity.this, "Path Not Saved.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(MapActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }

    }

    public class PullUserRoutesTask extends AsyncTask<Void, Void, String> {

        private boolean publicRoutes;

        PullUserRoutesTask(boolean publicRoutes){
            this.publicRoutes = publicRoutes;
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONObject json = new JSONObject();
            try {
                SharedPreferences authentication = getSharedPreferences("AUTHENTICATION", 0);
                String token = authentication.getString("token",null);

                if(publicRoutes)
                    return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/route/listActive"),null, token);
                else
                    return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/route/user"),null, token);
            } catch (Exception e) {
                Log.d(TAG, "PullPaths-doInBackground: exception:" + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String success) {
            Log.d(TAG, "PullPaths-onPostExecute: success: " + success);
            if (success != null && !success.equals("[]")) {
                try {
                    Gson gson = new Gson();

                    Type routeDataListType = new TypeToken<ArrayList<UserRoutePullData>>(){}.getType();
                    ArrayList<UserRoutePullData> pullData = gson.fromJson(success, routeDataListType);

                    loadUserPaths(pullData);

                } catch (Exception e) {
                    Log.e("PullUserRoutes", e.toString());
                }
            } else {
                Toast.makeText(MapActivity.this, "You have no saved paths.", Toast.LENGTH_SHORT).show();
            }
            if(!publicRoutes){
                PullUserRoutesTask pullPublicRoutesTask = new PullUserRoutesTask(true);
                pullPublicRoutesTask.execute((Void) null);
            }
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(MapActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }

    }

    public class PullGeoSpotsTask extends AsyncTask<Void, Void, String>{

        PullGeoSpotsTask(){ }

        @Override
        protected String doInBackground(Void... params) {

            SharedPreferences authentication = getSharedPreferences("AUTHENTICATION", 0);
            String token = authentication.getString("token",null);

            try {
                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/geoSpot/listActive"),null, token);
            } catch (IOException e) {
                Log.d(TAG, "PullGeoSpots-doInBackground: exception:" + e.getMessage());
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(String success) {

            if (success != null && !success.equals("[]")) {
                try {
                    Gson gson = new Gson();

                    Type geoSpotListType = new TypeToken<ArrayList<GeoSpotData>>(){}.getType();
                    ArrayList<GeoSpotData> pulledGeoSpotData = gson.fromJson(success, geoSpotListType);

                    loadGeoSpots(pulledGeoSpotData);
                } catch (Exception e) {
                    Log.e("PullGeoSpots", e.toString());
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MapActivity.this, "There are no GeoSpots at the moment.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public class DeleteUserRouteTask extends AsyncTask<Void, Void, String>{

        DeleteUserRouteTask(){ }

        @Override
        protected String doInBackground(Void... params) {

            SharedPreferences authentication = getSharedPreferences("AUTHENTICATION", 0);
            String token = authentication.getString("token",null);

            try {
                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/geoSpot/listActive"),null, token);
            } catch (IOException e) {
                Log.d(TAG, "PullGeoSpots-doInBackground: exception:" + e.getMessage());
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(String success) {

            if (success != null && !success.equals("[]")) {
                try {
                    Gson gson = new Gson();

                    Type geoSpotListType = new TypeToken<ArrayList<GeoSpotData>>(){}.getType();
                    ArrayList<GeoSpotData> pulledGeoSpotData = gson.fromJson(success, geoSpotListType);

                    loadGeoSpots(pulledGeoSpotData);
                } catch (Exception e) {
                    Log.e("PullGeoSpots", e.toString());
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MapActivity.this, "There are no GeoSpots at the moment.", Toast.LENGTH_SHORT).show();
            }

        }
    }


}

