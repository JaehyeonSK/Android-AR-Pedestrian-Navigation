package me.blog.cjh7163.tmaptest;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
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
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_SEARCH = 0x0001;

    private boolean selectionMode = false;
    private boolean navigationMode = false;
    private boolean arMode = false;
    private TMapPoint destination = null;

    private Drawable navIcon = null;

    // Views
    private FrameLayout frameMap;
    private TMapView mapView;
    private Button btnPath;
    private FloatingActionButton btnCurrent;
    private FloatingActionButton btnFlag;
    private FloatingActionButton btnDir;

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    // Manager Components
    private GpsManager gpsManager;
    private PathManager pathManager;
    private DirectionManager directionManager;

    private SensorManager sensorManager;
    private Sensor accSensor, magSensor;

    // Update Timer
    private Timer timer;
    private TimerTask timerTask;

    private FrameLayout arSurface;
    private FloatingActionButton btnAR;
    private SwitchCompat swAR;

    @Override
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

            sensorManager.registerListener(directionManager.sensorEventListener, accSensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(directionManager.sensorEventListener, magSensor, SensorManager.SENSOR_DELAY_UI);

            navIcon = toolbar.getNavigationIcon();

            // move to current location
            moveToCurrentLocation();
        } catch (Exception ex) {
            Log.d("Exception:", "can't initialize. " + ex.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            sensorManager.registerListener(directionManager.sensorEventListener, accSensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(directionManager.sensorEventListener, magSensor, SensorManager.SENSOR_DELAY_UI);
        } catch(Exception ex) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            sensorManager.unregisterListener(directionManager.sensorEventListener);
        } catch(Exception ex) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            timer.cancel();
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

        btnPath = (Button)findViewById(R.id.btnPath);
        btnPath.setOnClickListener(btnPathClicked);

        btnCurrent = (FloatingActionButton)findViewById(R.id.btnCurrent);
        btnCurrent.setOnClickListener(btnCurrentClicked);

        btnFlag = (FloatingActionButton)findViewById(R.id.btnFlag);
        btnFlag.setOnClickListener(btnFlagClicked);

        //Drawer Layout
        drawerLayout = (DrawerLayout)findViewById(R.id.activity_main);
        toolbar = (Toolbar)findViewById(R.id.toolbar);

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
                } else {
                    toolbar.setNavigationIcon(navIcon);
                    arSurface.setVisibility(View.INVISIBLE);
                }
            }
        });

        btnDir = (FloatingActionButton)findViewById(R.id.direction);
//        btnDir.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    Location location = gpsManager.getCurrentLocation();
//                    mapView.setLocationPoint(location.getLongitude(), location.getLatitude());
//                    if (navigationMode) {
//                        updateDirection();
//                    }
//                } catch (Exception ex) {
//                }
//            }
//        });

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
        float distThreshold = 3.0f;
        Vector nearestVector = null;

        try {
            if (nearestVector == null) {
                nearestVector = pathManager.getNearestVector();
            }

            double distance = nearestVector.getDistance();
            if(distance < distThreshold) {
                // in 3.0 meter
                if(pathManager.hasNext()) {
                    // Path has next point
                    nearestVector = pathManager.getNearestVector();
                } else {
                    // Navigation Complete !!!
                    nearestVector = null;
                    Toast.makeText(this, "목적지에 도착하였습니다.", Toast.LENGTH_SHORT).show();
                    setNavigationMode(false);
                }
            } else {
                // out of 3.0 meters
            }

            double direction = nearestVector.getDirection();

            btnDir.setRotation((float)direction);

            Log.d("Degree:", "" + nearestVector.getDirection());
            Log.d("Distance:", "" + nearestVector.getDistance());
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

                btnPath.setText("길찾기 종료");
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
            // clear map path
            mapView.removeTMapPath();

            // nav service timer stop
            try {
                timer.cancel();
            } catch (Exception ex) {
            }

            btnPath.setText("길찾기");
            swAR.setVisibility(View.INVISIBLE);
//            btnAR.setVisibility(View.INVISIBLE);
        }
    }

    private void setSelectionMode(boolean isSelectionMode) {
        this.selectionMode = isSelectionMode;

        if (isSelectionMode) {
            toolbar.setTitle("도착지를 설정하세요.");

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
            toolbar.setTitle("");
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
                case R.id.nav_history:
                    break;
                case R.id.nav_help:
                    break;
                case R.id.nav_send: {
                    Uri uri = Uri.parse("mailto:cjh7163@gmail.com");
                    Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
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

    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            try {
                Log.d("loc changed", "location changed!");
                mapView.setLocationPoint(location.getLongitude(), location.getLatitude());
                if (navigationMode) {
                    updateDirection();
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
