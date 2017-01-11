package com.dagger.trucktrack;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dagger.trucktrack.api.ApiBuilder;
import com.dagger.trucktrack.model.TruckObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    static String deviceID;
    private GoogleMap mMap;
    List<TruckObject> truckObjects;
    LatLng sydney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        truckObjects = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_log_out) {
            showDialog();
            return true;
        }
        return true;
    }

    private void makeNetworkRequest(String device) {
        ApiBuilder.getInstance().getLocation(device).enqueue(new Callback<List<TruckObject>>() {
            @Override
            public void onResponse(Call<List<TruckObject>> call, Response<List<TruckObject>> response) {
                truckObjects = response.body();
                if (truckObjects.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Probably the device name entered isn't correct",Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDialog();
                        }
                    }).show();
                }
                displayCurrentLocation();
            }

            @Override
            public void onFailure(Call<List<TruckObject>> call, Throwable t) {
                Log.e("Network Req Failed", t.getLocalizedMessage());
                Snackbar.make(findViewById(android.R.id.content),"Probably the device name entered isn't correct",Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog();
                    }
                }).show();
            }

        });
    }

    private void showDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.add_dialog, null);
        final EditText editText = (EditText) v.findViewById(R.id.deviceID);
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Add a new device to Track")
                .customView(v, true)
                .positiveText("Ok")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deviceID = editText.getText().toString();
                        makeNetworkRequest(deviceID);
                    }
                })
                .build();
        dialog.show();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        sydney = new LatLng(0, 0);
        displayCurrentLocation();
    }

    public void displayCurrentLocation() {
        if (truckObjects.size() > 0)
            sydney = new LatLng(truckObjects.get(0).getLat(), truckObjects.get(0).getLong());
        mMap.addMarker(new MarkerOptions().position(sydney).title("Lorem ipsum"));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
    }
}
