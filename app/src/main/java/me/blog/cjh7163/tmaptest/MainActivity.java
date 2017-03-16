package me.blog.cjh7163.tmaptest;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Views
    private FrameLayout frameMap;
    private TMapView mapView;
    private Button btnPath, btnCurrent;

    // Manager Components
    private GpsManager gpsManager;
    private PathManager pathManager;

    private TestClass T = new TestClass();
    class TestClass {
        public TMapPoint startPoint, endPoint;
        public TMapPolyLine polyLine;

        public TestClass() {
            startPoint = new TMapPoint(37.61605974, 127.01164517);
            endPoint = new TMapPoint(37.603247, 127.024911);
        }
    }

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

        btnCurrent = (Button)findViewById(R.id.btnCurrent);
        btnCurrent.setOnClickListener(btnCurrentClicked);
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

    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            try {
                // if (isFindPathMode)
                mapView.setLocationPoint(location.getLongitude(), location.getLatitude());
                updateDirection();
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
            TMapData mapData = new TMapData();
            mapData.findPathData(T.startPoint, T.endPoint, new TMapData.FindPathDataListenerCallback() {
                @Override
                public void onFindPathData(TMapPolyLine tMapPolyLine) {
                    mapView.addTMapPath(tMapPolyLine);

                    ArrayList<TMapPoint> points = tMapPolyLine.getLinePoint();

                    for (int i=0; i<points.size(); i++) {
                        TMapPoint point = points.get(i);
//                        Log.d(String.format("points[%d]:", i), point.getLongitude() + ", " + point.getLatitude());
                    }

                    pathManager.setPolyLine(tMapPolyLine);
                }
            });
        } catch (Exception ex) {
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
