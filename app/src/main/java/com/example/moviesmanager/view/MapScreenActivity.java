package com.example.moviesmanager.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;

import com.example.moviesmanager.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapScreenActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final LatLng KINOPLEX = new LatLng(-22.848099, -47.064665);
    private static final LatLng CINEPOLIS_CAMPINAS_SHOPPING = new LatLng(-22.932023, -47.078455);
    private static final LatLng CINEPOLIS_GALLERIA = new LatLng(-22.863260, -47.023464);
    private static final LatLng CINEMARK = new LatLng(-22.892192, -47.027153);
    private static final LatLng CINE_ARAUJO = new LatLng(-22.924636, -47.128162);

    private LinearLayout buy_tickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);

        buy_tickets = findViewById(R.id.buy_tickets);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                         .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        setButtonClick();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.addMarker(new MarkerOptions()
                .position(KINOPLEX)
                .title("Kinoplex"));

        googleMap.addMarker(new MarkerOptions()
                .position(CINEPOLIS_CAMPINAS_SHOPPING)
                .title("Cinepolis"));

        googleMap.addMarker(new MarkerOptions()
                .position(CINEPOLIS_GALLERIA)
                .title("Cinepolis"));

        googleMap.addMarker(new MarkerOptions()
                .position(CINEMARK)
                .title("Cinemark"));

        googleMap.addMarker(new MarkerOptions()
                .position(CINE_ARAUJO)
                .title("Cine Araújo"));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-22.890031, -47.076944), 12.0f));
    }

    private void setButtonClick() {
        buy_tickets.setOnClickListener(v -> {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.ingresso.cinemas");

            if (launchIntent != null) {
                startActivity(launchIntent);
            } else {
                launchIntent = new Intent(Intent.ACTION_VIEW);
                launchIntent.setData(Uri.parse("http://www.ingresso.com"));
            }

            startActivity(launchIntent);
        });
    }
}
