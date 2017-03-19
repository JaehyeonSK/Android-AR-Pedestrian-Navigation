package me.blog.cjh7163.tmaptest;

import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private boolean selectionMode = false;
    private boolean navigationMode = false;
    private TMapPoint destination = null;

    // Views
    private FrameLayout frameMap;
    private TMapView mapView;
    private Button btnPath;
    private FloatingActionButton btnCurrent;
    private FloatingActionButton btnFlag;

    // Manager Components
    private GpsManager gpsManager;
    private PathManager pathManager;

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

            // move to current location
            moveToCurrentLocation();
        } catch (Exception ex) {
            Log.d("Exception:", "can't initialize. " + ex.getMessage());
        }
    }

    private void initViews() {
        frameMap = (FrameLayout)findViewById(R.id.frameMap);

        mapView = new TMapView(this);
        mapView.setSKPMapApiKey(getString(R.string.tmap_api_key));
        mapView.setIconVisibility(true);
        mapView.setZoomLevel(16);
//        mapView.setMapType(TMapView.MAPTYPE_STANDARD);
//        mapView.setCompassMode(true);
        mapView.setTrackingMode(true);

        frameMap.addView(mapView);

        btnPath = (Button)findViewById(R.id.btnPath);
        btnPath.setOnClickListener(btnPathClicked);

        btnCurrent = (FloatingActionButton)findViewById(R.id.btnCurrent);
        btnCurrent.setOnClickListener(btnCurrentClicked);

        btnFlag = (FloatingActionButton)findViewById(R.id.btnFlag);
        btnFlag.setOnClickListener(btnFlagClicked);
    }

    private void moveToCurrentLocation() {
        try {
            Location currentLocation = gpsManager.getCurrentLocation();
            mapView.setLocationPoint(currentLocation.getLongitude(), currentLocation.getLatitude());
        } catch (Exception ex) {
            Toast.makeText(this, "현재 위치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDirection() {
        Vector nearestVector = pathManager.getNearestVector();
        double direction = nearestVector.getDirection();
        double distance = nearestVector.getDistance();

    }

    private void setNavigationMode(boolean isNavigationMode) {
        this.navigationMode = isNavigationMode;

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

                btnPath.setText("길찾기 종료");

                Toast.makeText(this, "길 안내를 시작합니다.", Toast.LENGTH_SHORT).show();

                moveToCurrentLocation();
                // nav service on

            } catch (Exception ex) {
                Log.d("Exception:", ex.getMessage());
                setNavigationMode(false);
            }

        } else {
            btnFlag.setVisibility(View.VISIBLE);
            btnCurrent.setVisibility(View.VISIBLE);

            // enable User Scroll & Zoom
            mapView.setUserScrollZoomEnable(false);
            // clear map path
            mapView.removeTMapPath();

            btnPath.setText("길찾기");
        }
    }

    private void setSelectionMode(boolean isSelctionMode) {
        this.selectionMode = isSelctionMode;

        if (isSelctionMode) {
            Toast.makeText(this, "도착지를 선택하세요.", Toast.LENGTH_SHORT).show();

            mapView.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
                @Override
                public void onLongPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, final TMapPoint tMapPoint) {
                (new AlertDialog.Builder(MainActivity.this))
                    .setTitle("안내")
                    .setMessage("도착지로 설정하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                mapView.removeMarkerItem("도착지");
                            } catch(Exception ex) {
                            }

                            destination = tMapPoint;

                            // add destination marker on TMap View
                            TMapMarkerItem marker = new TMapMarkerItem();
                            marker.setID("도착지");
                            marker.setTMapPoint(tMapPoint);

                            mapView.addMarkerItem("도착지", marker);

                            // turn off selection mode
                            setSelectionMode(false);
                        }
                    })
                    .setNegativeButton("아니오", null).show();
                }
            });
        } else {
            Toast.makeText(this, "도착지 선택 해제", Toast.LENGTH_SHORT).show();
            mapView.setOnLongClickListenerCallback(null);
        }
    }

    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            try {

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
