package com.example.tagtheplace;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    public SupportMapFragment mapFragment;
    public GoogleMap googleMap;
    public String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};
    public int DEFAULT_ZOOM = 15;
    public LatLng CITY_HALL = new LatLng(37.5662952, 126.97794509999994);
    public PlaceRepository placeRepository;

    @Override
    // U1-2, U2-2
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // U2-3
        mapFragment = SupportMapFragment.newInstance();
        // U2-4~8
        getSupportFragmentManager().beginTransaction().
                add(R.id.mapView, mapFragment).commit();
        // U1-3
        if (checkPermission()) {
            // U1-6
            mapFragment.getMapAsync(this);
        } else {
            // U1-7
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
            mapFragment.getMapAsync(this);
        }
        // U2-9
        //mapFragment.getMapAsync(this::onMapReady);
    }

    // U1-10
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // U1-11
        mapFragment.getMapAsync(this);
    }

    // U2-10
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // U2-11
        this.googleMap = googleMap;
        // U2-12
        googleMap.setOnInfoWindowClickListener(this);
        if (checkPermission()) {
            // U2-13
            googleMap.setMyLocationEnabled(true);
            // U2-14
            LatLng location = this.getMyLocation();
            // U2-15
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));
            // U6-3
            googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                // U6-4
                public void onMapLongClick(@NonNull LatLng latLng) {
                    // U6-5
                    View dialogView = View.inflate(MainActivity.this,
                            R.layout.alert_add_marker_layout, null);
                    // U6-6
                    EditText editName = dialogView.findViewById(R.id.edit_name);
                    // U6-7
                    EditText editTag = dialogView.findViewById(R.id.edit_tag);
                    // U6-8
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(MainActivity.this);
                    // U6-10
                    builder.setTitle("마커 추가하기");
                    // U6-11
                    builder.setView(dialogView);
                    // U6-12
                    builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // U6-13
                            builder.setNegativeButton("취소", null);
                            // U6-17
                            PlaceData placeData = new PlaceData();
                            // U6-18
                            LatLng location = getMyLocation();

                            if ((location.latitude - 0.003 <= latLng.latitude) &&
                                    (latLng.latitude <= location.latitude + 0.003) &&
                                    (location.longitude - 0.003 <= latLng.longitude) &&
                                    (latLng.longitude <= location.longitude + 0.003)) {

                                if (!editName.getText().toString().equals("") &&
                                        !editTag.getText().toString().equals("")) {
                                    // U6-19
                                    placeData.setLike(0);
                                    // U6-20
                                    placeData.setDislike(0);
                                    // U6-21
                                    placeData.setLat((float) latLng.latitude);
                                    // U6-22
                                    placeData.setLng((float) latLng.longitude);
                                    // U6-23
                                    placeData.setName(editName.getText().toString());
                                    // U6-24
                                    placeData.setTag(editTag.getText().toString());
                                    // U6-26
                                    PlaceRepository.insertDataToDB(placeData);
                                    // U6-27
                                    mapFragment.getMapAsync(MainActivity.this);
                                    // U6-28
                                    Toast.makeText(MainActivity.this,
                                            "마커가 추가되었습니다.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // U6-29
                                    Toast.makeText(MainActivity.this,
                                            "이름, 태그를 입력하세요.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // U6-30
                                Toast.makeText(MainActivity.this,
                                        "인근의 장소를 추가해 주세요.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    // U6-15
                    builder.show();
                }
            });
        } else {
            // U2-17
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CITY_HALL, DEFAULT_ZOOM));
        }
    }

    // U7-3
    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        // U7-4
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        // U7-5, U7-6
        boolean checked = sharedPreferences.
                getBoolean(String.valueOf(marker.getZIndex()), false);
        // U7-7, U7-8
        PlaceData placeData = PlaceRepository.getPlaceById((int) marker.getZIndex());
        // U7-9
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        // U7-10
        builder.setTitle(marker.getTitle());
        // U7-11
        builder.setMessage(marker.getSnippet()+" 좋아요: " +
                placeData.getLike() + " 싫어요: " + placeData.getDislike());
        // U7-17
        builder.setNegativeButton("돌아가기", null);
        AlertDialog dialog = builder.create();
        if (!checked) {
            // U7-12
            View dialogView = View.inflate(MainActivity.this,
                    R.layout.alert_eveluation_layout, null);
            // U7-13
            Button positiveButton = dialogView.findViewById(R.id.btn_positive);
            // U7-14
            Button negativeButton = dialogView.findViewById(R.id.btn_negative);
            // U7-16
            dialog.setView(dialogView);
            // U8-3
            SharedPreferences.Editor editor = sharedPreferences.edit();
            // U8-8
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // U8-9
                    PlaceRepository.increaseLikeById(placeData.getId());
                    // U8-10
                    editor.putBoolean(String.valueOf(marker.getZIndex()), true);
                    // U8-11
                    editor.apply();
                    // U8-12
                    dialog.dismiss();
                    // U8-13
                    onInfoWindowClick(marker);
                }
            });

            // U8-16
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // U8-17
                    PlaceRepository.increaseDislikeById(placeData.getId());
                    // U8-18
                    editor.putBoolean(String.valueOf(marker.getZIndex()), true);
                    // U8-19
                    editor.apply();
                    // U8-20
                    dialog.dismiss();
                    // U8-21
                    onInfoWindowClick(marker);
                }
            });
        }
        // U7-19
        dialog.show();
    }

    // U3-1
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // U3-2
        getMenuInflater().inflate(R.menu.search_menu, menu);
        // U3-3
        MenuItem searchItem = menu.findItem(R.id.search);
        // U3-5
        SearchView searchView = (SearchView) searchItem.getActionView();
        // U3-6
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // U3-8
            @Override
            public boolean onQueryTextSubmit(String query) {
                // U3-9
                googleMap.clear();
                // U3-10, // U5-2
                List<PlaceData> list = PlaceRepository.getPlaceByTag(query);
                for (int i = 0; i < list.size(); i++) {
                    // U5-3
                    MarkerOptions markerOptions = new MarkerOptions();
                    // U5-4~7
                    markerOptions.position(new LatLng(list.get(i).getLat(), list.get(i).getLng()));
                    markerOptions.title(list.get(i).getName());
                    markerOptions.snippet(list.get(i).getTag());
                    markerOptions.zIndex(list.get(i).getId());
                    // U5-9
                    Marker marker = googleMap.addMarker(markerOptions);
                }
                // U5-10
                mapFragment.getMapAsync(MainActivity.this);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        return true;
    }

    // U9-1
    public LatLng getMyLocation() {
        // U9-2
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        checkPermission();
        // U9-3
        Location lastKnownLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastKnownLocation == null) {
            // U9-5
            final double[] loc = new double[2];
            // U9-6
            LocationListener listener = new LocationListener() {
                // U9-7
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    // U9-8
                    loc[0] = location.getLatitude();
                    // U9-9
                    loc[1] = location.getLongitude();
                }
            };
            // U9-11
            manager.requestLocationUpdates(String.valueOf(manager.getBestProvider
                    (new Criteria(), true)), 1000, 0, listener);
            // U9-12
            return new LatLng(loc[0], loc[1]);
        }
        // U9-13
        return new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
    }

    public boolean checkPermission() {

        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                // U1-4
                return false;
            }
        }
        // U1-5
        return true;
    }
}