package me.blog.cjh7163.tmaptest;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.blog.cjh7163.tmaptest.Augmented.GLClearRenderer;
import me.blog.cjh7163.tmaptest.Settings.Preference;
import me.blog.cjh7163.tmaptest.Settings.SettingsActivity;
import me.blog.cjh7163.tmaptest.Utils.LocationUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_SEARCH = 0x0001;
    private static final int REQUEST_HISTORY = 0x0002;

    private long backPressedTime = 0;

    private boolean selectionMode = false;
    private boolean navigationMode = false;
    private boolean arMode = false;
    private TMapPoint destination = null;

    private Drawable navIcon = null;

    // Views
    private FrameLayout frameMap;
    private TMapView mapView;
//    private Button btnPath;
    private FloatingActionButton btnPath;
    private FloatingActionButton btnCurrent;
    private FloatingActionButton btnFlag;
    private FloatingActionButton btnDir;

    private Toolbar toolbar;
    private TextView tvToolbarTitle;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    // OpenGL
    private GLSurfaceView glSurfaceView;
    private GLClearRenderer renderer;

    // Manager Components
    private GpsManager gpsManager;
    private PathManager pathManager;
    private DirectionManager directionManager;

    private SensorManager sensorManager;
    private Sensor accSensor, magSensor, oriSensor;

    // Update Timer
    private Timer timer;
    private TimerTask timerTask;

    private FrameLayout arSurface;
    private SwitchCompat swAR;

    // Marker
    private ArrayList<String> markerIdList = new ArrayList<>();

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        try {
            initViews();

            // init GPS Manager
            GpsManager.init(this);
            gpsManager = GpsManager.getInstance();
            gpsManager.setOnLocationListener(locationListener);

            // init Path Manager
            pathManager = PathManager.getInstance();

            // init Direction Manager
            directionManager = DirectionManager.getInstance();

            // init Sensor Manager
            sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
            accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            oriSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

            sensorManager.registerListener(directionManager.sensorEventListener, accSensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(directionManager.sensorEventListener, magSensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(directionManager.sensorEventListener, oriSensor, SensorManager.SENSOR_DELAY_UI);

            navIcon = toolbar.getNavigationIcon();

            // move to current location
            moveToCurrentLocation();
        } catch (Exception ex) {
            Log.d("Exception:", "can't initialize. " + ex.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (arMode) {
            swAR.setChecked(false);
        } else if (navigationMode) {
            setNavigationMode(false);
        } else if (selectionMode) {
            setSelectionMode(false);
        } else {
            if (!arMode && !navigationMode) {
                long currentTime = System.currentTimeMillis();

                if (currentTime - backPressedTime < 2000) {

                    finish();
                } else {
                    backPressedTime = currentTime;
                    Toast.makeText(this, "종료하려면 뒤로가기 버튼을 누르세요.", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            sensorManager.registerListener(directionManager.sensorEventListener, accSensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(directionManager.sensorEventListener, magSensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(directionManager.sensorEventListener, oriSensor, SensorManager.SENSOR_DELAY_UI);
            gpsManager.start();
        } catch(Exception ex) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            sensorManager.unregisterListener(directionManager.sensorEventListener);
            gpsManager.stop();
        } catch(Exception ex) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            timer.cancel();
            gpsManager.stop();
        } catch(Exception ex) {
        }
    }

    private void initViews() {
        frameMap = (FrameLayout)findViewById(R.id.frameMap);

        mapView = new TMapView(this);
        mapView.setSKPMapApiKey(getString(R.string.tmap_api_key));
        mapView.setIconVisibility(true);
        mapView.setZoomLevel(16);
//        mapView.setMapType(TMapView.MAPTYPE_STANDARD);
        mapView.setCompassMode(true);
//        mapView.setTrackingMode(true);


        frameMap.addView(mapView);

//        btnPath = (Button)findViewById(R.id.btnPath);
        btnPath = (FloatingActionButton)findViewById(R.id.btnPath);
        btnPath.setOnClickListener(btnPathClicked);

        btnCurrent = (FloatingActionButton)findViewById(R.id.btnCurrent);
        btnCurrent.setOnClickListener(btnCurrentClicked);

        btnFlag = (FloatingActionButton)findViewById(R.id.btnFlag);
        btnFlag.setOnClickListener(btnFlagClicked);

        //Drawer Layout
        drawerLayout = (DrawerLayout)findViewById(R.id.activity_main);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        tvToolbarTitle = (TextView)findViewById(R.id.tvTitle);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        toggle.syncState();

        navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        arSurface = (FrameLayout)findViewById(R.id.arSurface);
        //btnAR = (FloatingActionButton)findViewById(R.id.btnAR);
        //btnAR.setOnClickListener(btnARClicked);
        swAR = (SwitchCompat)findViewById(R.id.swAR);
        swAR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                arMode = checked;
                if (arMode) {
                    toolbar.setNavigationIcon(null);
                    arSurface.setVisibility(View.VISIBLE);
                    btnDir.setVisibility(View.GONE);
                    startAR();
                } else {
                    toolbar.setNavigationIcon(navIcon);
                    arSurface.setVisibility(View.INVISIBLE);
                    btnDir.setVisibility(View.VISIBLE);
                    stopAR();
                }
            }
        });

        btnDir = (FloatingActionButton)findViewById(R.id.direction);

    }

    private void startAR() {
        try {
            FrameLayout content = (FrameLayout) findViewById(R.id.arSurface);

            renderer = new GLClearRenderer();

            glSurfaceView = new GLSurfaceView(this);
            glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            // set format as translucent
            glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            glSurfaceView.setRenderer(renderer);
            glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                boolean touchDown = false;
                float deltaX, deltaY;
                float x1, y1, x2, y2;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            touchDown = true;
                            x1 = event.getX();
                            y1 = event.getY();
                            break;
                        }
                        case MotionEvent.ACTION_MOVE:
                            if (touchDown) {
                                x2 = event.getX();
                                y2 = event.getY();

                                deltaX = (x2-x1)/150.0f;
                                deltaY = (y2-y1)/150.0f;

                                // length(x): 4.1
                                // length(y): 7.1
                                // -1.7 <= x <= 1.7
                                renderer.setX(Math.max(-1.7f, Math.min(1.8f, renderer.getX() + deltaX)));
                                // -3.9 <= y <= 2.5
                                renderer.setY(Math.max(-2.9f, Math.min(2.9f, renderer.getY() - deltaY)));

                                Log.d("pos:", "x:" + renderer.getX() + ", y:" + renderer.getY());

                                x1 = x2;
                                y1 = y2;
                                break;
                            }
                        case MotionEvent.ACTION_UP: {
                            touchDown = false;
                            break;
                        }
                    }

                    return true;
                }
            });

            toolbar.setTitle("증강현실 모드");
            tvToolbarTitle.setText("증강현실 모드");

            content.addView(glSurfaceView);

            glSurfaceView.setZOrderMediaOverlay(true);
        } catch(Exception ex) {
            Log.d("Exception::", ex.getMessage());
        }
    }

    private void stopAR() {
        try {
            FrameLayout content = (FrameLayout) findViewById(R.id.arSurface);
            glSurfaceView.setZOrderMediaOverlay(false);

            toolbar.setTitle(null);
            tvToolbarTitle.setText(null);
            content.removeView(glSurfaceView);
        } catch(Exception ex) {
            Log.d("Exception::", ex.getMessage());
        } finally {
            renderer = null;
            glSurfaceView = null;
        }

    }

    private void moveToCurrentLocation() {
        try {
            Location currentLocation = gpsManager.getCurrentLocation();
            mapView.setLocationPoint(currentLocation.getLongitude(), currentLocation.getLatitude());
            mapView.setCenterPoint(currentLocation.getLongitude(), currentLocation.getLatitude());
        } catch (Exception ex) {
            Toast.makeText(this, "현재 위치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDirection() {
        float distThreshold = 12.0f; // 12 meter
//        Vector nearestVector = null;
        TMapPoint nearestPoint = null;

        try {
//            if (nearestVector == null) {
//                nearestVector = pathManager.getNearestVector();
//            }

            nearestPoint = pathManager.getNearestPoint();
//            Log.d("nearest:", "" + pathManager.getNearestIndex());

//            double distance = nearestVector.getDistance();
            Location currentLocation = gpsManager.getCurrentLocation();
            Location nearestLocation = new Location(LocationManager.GPS_PROVIDER);
            nearestLocation.setLongitude(nearestPoint.getLongitude());
            nearestLocation.setLatitude(nearestPoint.getLatitude());

            double distance = LocationUtils.distanceBetween(currentLocation, nearestLocation);
//            Log.d("distance:", "" + distance);
            if(distance < distThreshold) {
                int nearestIndex = pathManager.getNearestIndex();

                // remove nearest marker and marker id
                String targetMarkerId = markerIdList.get(nearestIndex);
                mapView.removeMarkerItem(targetMarkerId);
                markerIdList.remove(nearestIndex);

                if(pathManager.hasNext()) {
                    // Path has next point
//                    nearestVector = pathManager.getNearestVector();
                    nearestPoint = pathManager.getNearestPoint();
                } else {
                    // Navigation Complete !!!
//                    nearestVector = null;
                    nearestPoint = null;

                    Toast.makeText(this, "목적지에 도착하였습니다.", Toast.LENGTH_SHORT).show();
                    setNavigationMode(false);
                    return;
                }
            } else {
                // out of 3.0 meters
            }

//            double direction = nearestVector.getDirection();
            nearestLocation.setLongitude(nearestPoint.getLongitude());
            nearestLocation.setLatitude(nearestPoint.getLatitude());
            double direction = LocationUtils.directionBetween(currentLocation, nearestLocation);

            btnDir.setRotation((float)direction);
            if (renderer != null) {
                // to correct gps position error
                if(Preference.getInstance().isCorrect) {
                    int nearestIndex = pathManager.getNearestIndex();
                    if (nearestIndex + 1 < markerIdList.size()) {
                        TMapMarkerItem nextMarker = mapView.getMarkerItemFromID(markerIdList.get(nearestIndex + 1));
                        Location nextLocation = new Location(LocationManager.GPS_PROVIDER);
                        nextLocation.setLongitude(nextMarker.longitude);
                        nextLocation.setLatitude(nextMarker.latitude);

                        direction = (direction + LocationUtils.directionBetween(nearestLocation, nextLocation)) / 2;
                    }
                }

                // to stabilize direction mark
                if(Preference.getInstance().isStabilize) {
                    direction = Math.round(direction * 0.1) * 10.0;
                }

                // angle z is reversed
                renderer.setAngleZ((float)-direction);

                // angle x range in [-90.0, 90.0]
                renderer.setAngleX(Math.min(Math.max(directionManager.getPitch(), -90.0f), +90.0f));

                // angle y
                renderer.setAngleY(directionManager.getRoll());
            }

//            Log.d("Degree:", "" + nearestVector.getDirection());
//            Log.d("Distance:", "" + nearestVector.getDistance());
        } catch(Exception ex) {
            Log.d("Exception:", ex.getMessage());
        }
    }

    private void setNavigationMode(boolean isNavigationMode) {
        this.navigationMode = isNavigationMode;

        btnDir.setRotation(0);
        if (isNavigationMode) {
            try {
                if (destination == null) {
                    Toast.makeText(this, "먼저 도착지를 선택하세요.", Toast.LENGTH_SHORT).show();
                    setNavigationMode(false);
                    setSelectionMode(true);
                    return;
                }

                TMapData mapData = new TMapData();

                Location currentLocation = gpsManager.getCurrentLocation();
                TMapPoint startPoint = new TMapPoint(currentLocation.getLatitude(), currentLocation.getLongitude());

                mapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startPoint, destination, new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine tMapPolyLine) {
                        // add Path from current to destination on TMap View
                        mapView.addTMapPath(tMapPolyLine);

                        // Line Point ArrayList
                        ArrayList<TMapPoint> linePoints = tMapPolyLine.getLinePoint();
                        markerIdList.clear();

                        int i = 0;
                        // Add Markers on TMapView
                        for (TMapPoint p : linePoints) {
                            TMapMarkerItem markerItem = new TMapMarkerItem();
                            markerItem.setTMapPoint(p);

                            String id = "l" + (i++);
                            markerItem.setID(id);
                            mapView.addMarkerItem(id, markerItem);
                            markerIdList.add(id);
                        }

                        // pass the path to Path Manager
                        pathManager.setPolyLine(tMapPolyLine);
                    }
                });

                // initialize navigation mode
                btnFlag.setVisibility(View.INVISIBLE);
                btnCurrent.setVisibility(View.INVISIBLE);
                setSelectionMode(false);

                // disable User Scroll & Zoom
                mapView.setUserScrollZoomEnable(true);

                // enable compass mode
                mapView.setCompassMode(true);

//                btnPath.setText("길찾기 종료");
                btnPath.setImageResource(R.drawable.flag);

                swAR.setVisibility(View.VISIBLE);
                //btnAR.setVisibility(View.VISIBLE); // set AR button visible

                Toast.makeText(this, "길 안내를 시작합니다.", Toast.LENGTH_SHORT).show();

                moveToCurrentLocation();

                // nav service timer start
                timer = new Timer(true);
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        updateDirection();
                    }
                };
                timer.schedule(timerTask, 200, 1);

            } catch (Exception ex) {
                Log.d("Exception:", ex.getMessage());
                setNavigationMode(false);
            }

        } else {
            btnFlag.setVisibility(View.VISIBLE);
            btnCurrent.setVisibility(View.VISIBLE);

            // enable User Scroll & Zoom
            mapView.setUserScrollZoomEnable(false);
            mapView.setCompassMode(true);

            // clear map path & markers
            mapView.removeTMapPath();
            mapView.removeAllMarkerItem();

            // nav service timer stop
            try {
                timer.cancel();
            } catch (Exception ex) {
            }

//            btnPath.setText("길찾기");
            btnPath.setImageResource(R.drawable.ic_stat_name);

            swAR.setVisibility(View.INVISIBLE);
//            btnAR.setVisibility(View.INVISIBLE);
        }
    }

    private void setSelectionMode(boolean isSelectionMode) {
        this.selectionMode = isSelectionMode;

        if (isSelectionMode) {
            toolbar.setTitle("도착지를 설정하세요.");
            tvToolbarTitle.setText("도착지를 설정하세요.");

            mapView.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
                @Override
                public void onLongPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, final TMapPoint tMapPoint) {
                (new AlertDialog.Builder(MainActivity.this))
                    .setTitle("안내")
                    .setMessage("도착지로 설정하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // set selected point as destination and mark it on TMap View
                            setDestination(tMapPoint);

                            // turn off selection mode
                            setSelectionMode(false);
                        }
                    })
                    .setNegativeButton("아니오", null).show();
                }
            });
        } else {
            toolbar.setTitle(null);
            tvToolbarTitle.setText(null);
            mapView.setOnLongClickListenerCallback(null);
        }
    }

    private void setDestination(TMapPoint destination) {
        // clear previous destination
        try {
            mapView.removeMarkerItem("도착지");
        } catch(Exception ex) {
        }

        this.destination = destination;

        // add destination marker on TMap View
        TMapMarkerItem marker = new TMapMarkerItem();
        marker.setID("도착지");
        marker.setTMapPoint(destination);

        mapView.addMarkerItem("도착지", marker);
        Log.d("Info::", "도착지 설정 완료");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        try {
            switch (id) {
                case R.id.nav_map:
                    break;
                case R.id.nav_search: {
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivityForResult(intent, REQUEST_SEARCH);
                    break;
                }
                case R.id.nav_history: {
                    Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                    startActivityForResult(intent, REQUEST_HISTORY);
                    break;
                }
                case R.id.nav_help:
                    break;
                case R.id.nav_send: {
                    Uri uri = Uri.parse("mailto:cjh7163@gmail.com");
                    Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                    startActivity(intent);
                    break;
                }
                case R.id.nav_settings: {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    break;
                }
            }
        } catch (Exception ex) {
            Log.d("Exception:", ex.getMessage());
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SEARCH: {
                    setNavigationMode(false);

                    String name = data.getStringExtra("POI");
                    double longitude = data.getDoubleExtra("LON", 0.0);
                    double latitude = data.getDoubleExtra("LAT", 0.0);

                    TMapPoint mapPoint = new TMapPoint(latitude, longitude);
                    setDestination(mapPoint);
                    mapView.setCenterPoint(longitude, latitude);

                    // 기록을 파일에 저장
                    BufferedWriter bw;
                    try {
                        bw = new BufferedWriter(new FileWriter(new File(getFilesDir(), "history.txt"), true));
                        bw.append(String.format("%s %f %f", name, longitude, latitude));
                        bw.newLine();
                        bw.close();
                    } catch(Exception ex) {
                        Log.d("FileWriteException:", ex.getMessage());
                    }

                    break;
                }
                case REQUEST_HISTORY: {
                    setNavigationMode(false);

                    String name = data.getStringExtra("POI");
                    double longitude = data.getDoubleExtra("LON", 0.0);
                    double latitude = data.getDoubleExtra("LAT", 0.0);

                    TMapPoint mapPoint = new TMapPoint(latitude, longitude);
                    setDestination(mapPoint);
                    mapView.setCenterPoint(longitude, latitude);

                    break;
                }
                default: {
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    ////////////////////////////////////////////////////////////////
    //
    //  Listeners
    ////////////////////////////////////////////////////////////////

    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            try {
                if (GpsManager.isBetterLocation(location, gpsManager.getLastLocation())
                        && (location.distanceTo(gpsManager.getLastLocation()) < 8.0f)) {
                    gpsManager.setLastLocation(location);

//                    Log.d("loc changed", "location changed!");
                    mapView.setLocationPoint(location.getLongitude(), location.getLatitude());
                    if (navigationMode) {
                        moveToCurrentLocation();
//                        updateDirection();
                    }
                }

            } catch (Exception ex) {
            }
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }
        @Override
        public void onProviderEnabled(String s) { }
        @Override
        public void onProviderDisabled(String s) { }
    };

    private View.OnClickListener btnPathClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                setNavigationMode(!navigationMode);
            } catch (Exception ex) {
                Log.d("Exception:", ex.getMessage());
            }

        }
    };

    private View.OnClickListener btnFlagClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                // toggle selection mode
                setSelectionMode(!selectionMode);
            } catch(Exception ex) {
                Log.d("Exception:", ex.getMessage());
            }
        }
    };

    private View.OnClickListener btnCurrentClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            moveToCurrentLocation();
        }
    };

}
