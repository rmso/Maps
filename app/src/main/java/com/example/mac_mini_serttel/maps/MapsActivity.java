package com.example.mac_mini_serttel.maps;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, LocationListener {

    private GoogleMap mMap;
    static ArrayList<LatLng> localizacoes;

    LocationManager locationManager;
    String provider;
    int localizacao = -1;

    String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        PermissionUtils.validate(this, 0, permissoes);

        localizacoes = new ArrayList<>();
        localizacoes.add(new LatLng(0 , 0));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapLongClickListener(this);

        if (localizacao != -1 && localizacao != 0){
            locationManager.removeUpdates(this);

            mMap.addMarker(new MarkerOptions().position(MapsActivity.localizacoes.get(localizacao)).title("aqui"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MapsActivity.localizacoes.get(localizacao), 10));

        }else {
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result : grantResults){
            if (result == PackageManager.PERMISSION_DENIED){
                //alguma permissão foi negada
                alertAndFinish();
                return;
            }
        }
        //se chegou até aqui está ok
    }

    private void alertAndFinish(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name).setMessage("Para utilizar este aplicativo, você precisa aceitar as permissões");
        //add the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onMapLongClick(LatLng point) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String marcador = new Date().toString();
        try{
            List<Address> listaLocais = geocoder.getFromLocation(point.latitude, point.longitude, 1);
            if (listaLocais != null && listaLocais.size() > 0){
                marcador = listaLocais.get(0).getAddressLine(0);
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        MapsActivity.localizacoes.add(point);

        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title(marcador)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
    }


    @Override
    public void onLocationChanged(Location localizacaoUsuario) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(localizacaoUsuario.getLatitude(), localizacaoUsuario.getLongitude()), 10));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
