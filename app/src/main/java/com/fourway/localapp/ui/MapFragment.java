package com.fourway.localapp.ui;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fourway.localapp.R;
import com.fourway.localapp.data.GetUsersRequestData;
import com.fourway.localapp.data.Profile;
import com.fourway.localapp.request.CommonRequest;
import com.fourway.localapp.request.GetUsersRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener,GetUsersRequest.GetUsersResponseCallback{

    GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    MapView mMapView;
    Location mCurrentLocation, mLastLocation;
    LocationManager mLocationManager;
    ArrayList<Profile> profileList;
    ArrayList<Integer> markerID;

    ImageView medicalBtn,officeBtn,
            carBtn,emergencyBtn,
            entertainmentBtn;

    RelativeLayout uDetailLayout;

    public static String TAG = "MapFragment";

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        MapsInitializer.initialize(this.getActivity());


        profileList = new ArrayList<>();
        markerID = new ArrayList<>();
        officeBtn = (ImageView)view.findViewById(R.id.office_iv);
        medicalBtn = (ImageView)view.findViewById(R.id.medical_iv);
        carBtn = (ImageView)view.findViewById(R.id.car_iv);
        emergencyBtn = (ImageView)view.findViewById(R.id.emergency_iv);
        entertainmentBtn = (ImageView)view.findViewById(R.id.entertainment_iv);

        uDetailLayout = (RelativeLayout) view.findViewById(R.id.user_detail_rl);

        officeBtn.setOnClickListener(filterClickListener);
        medicalBtn.setOnClickListener(filterClickListener);
        carBtn.setOnClickListener(filterClickListener);
        emergencyBtn.setOnClickListener(filterClickListener);
        entertainmentBtn.setOnClickListener(filterClickListener);

        mMapView = (MapView) view.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        request(new LatLng(28.545623, 77.330507));

        // Create an instance of GoogleAPIClient.
        ConnectToGooglePlayServices();


        mLocationManager  = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, this);
        }catch (SecurityException se){
            Log.v(TAG, "SecurityException: "+se.getMessage());
        }

        Log.v(TAG, "View ready");
        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//        mMap.setOnMyLocationChangeListener(onMyLocationChangeListener);

        /*for (int i=0;i<mMapUsers.size();i++) {
            addMarkerAtLocation(mMapUsers.get(i));
        }*/
        setMyLocationButton();
        Log.v(TAG, "Map Ready");

    }

    void setMyLocationButton() {

        if (mMapView != null &&
                mMapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                locationButton.setElevation(10f);
            }
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 0, 200);
        }
    }



    private void updateMapUI(Location location)  {
//        mMap.clear();
        LatLng position = new LatLng(location.getLatitude(),location.getLongitude());
//        request(position);

//        mMap.addMarker(new MarkerOptions().position(position));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 14));

        Log.v(TAG, "Update UI");
    }

    /**
     *
     * @param latLng
     */
    private void addMarkerAtLocation(LatLng latLng, String tag) {
        mMap.addMarker(new MarkerOptions().position(latLng).title(tag));

        Log.v(TAG, "Add marker At: "+latLng);
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    View.OnClickListener filterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.office_iv:
                    Toast.makeText(getContext(), "office_iv", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.medical_iv:
                    Toast.makeText(getContext(), "medical_iv", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.car_iv:
                    Toast.makeText(getContext(), "car_iv", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.emergency_iv:
                    Toast.makeText(getContext(), "emergency_iv", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.entertainment_iv:
                    Toast.makeText(getContext(), "entertainment_iv", Toast.LENGTH_SHORT).show();
                    break;

            }

        }
    };

    GoogleMap.OnMyLocationChangeListener onMyLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            mCurrentLocation = location;
//            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            addMarkerAtLocation(latLng);
//            updateMapUI(location);
            Log.v(TAG, "onMyLocationChange");
        }
    };


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.v(TAG, "onLocationChanged");

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            addMarkerAtLocation(latLng);


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.v(TAG, "onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.v(TAG, "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.v(TAG, "onProviderDisabled");
        }
    };


    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "onLocationChanged");

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        addMarkerAtLocation(latLng);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.v(TAG, "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.v(TAG, "onProviderDisabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.v(TAG, "onProviderDisabled");
    }

    public void ConnectToGooglePlayServices(){
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(TAG, "onConnected");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            updateMapUI(mLastLocation);
            LatLng position = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
//            request(position);
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG, "onConnectionSuspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(TAG, "onConnectionFailed");

    }




//    void prepaireMapData() {
//        mMapUsers.add(new LatLng(28.545623, 77.330507));
//        mMapUsers.add(new LatLng(28.546066, 77.329938));
//        mMapUsers.add(new LatLng(28.544304, 77.331773));
//        mMapUsers.add(new LatLng(28.542834, 77.331312));
//        mMapUsers.add(new LatLng(28.544747, 77.332374));
//
//    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (uDetailLayout.getVisibility() != View.VISIBLE) {
            int index= Integer.parseInt(marker.getTitle());
            Profile profile = profileList.get(index);
            String pName = profile.getuName();
            final String pEmail = profile.getuEmail();
            final String mMobile = profile.getuMobile();

            TextView textView = (TextView)getView().findViewById(R.id.user_name);
            ImageView actionEmail = (ImageView) getView().findViewById(R.id.action_email);
            ImageView actionCall = (ImageView) getView().findViewById(R.id.action_call);
            if (pName != null) {
                textView.setText(pName);
            }
            actionCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse(mMobile));
                    startActivity(callIntent);
                }
            });

            actionEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent Email = new Intent(Intent.ACTION_SEND);
                    Email.setType("text/email");
                    Email.putExtra(Intent.EXTRA_EMAIL,
                            new String[]{pEmail});  //developer 's email

                    startActivity(Intent.createChooser(Email, "Send Email:"));
                }
            });

            uDetailLayout.setVisibility(View.VISIBLE);
        }else {
            uDetailLayout.setVisibility(View.GONE);
        }
        return true;
    }

    //test
    void request(LatLng latLng) {
        GetUsersRequest usersRequest = new GetUsersRequest(getActivity(),latLng,MapFragment.this);
        usersRequest.executeRequest();
    }


    @Override
    public void onGetUsersResponse(CommonRequest.ResponseCode res, GetUsersRequestData data) {
        switch (res) {
            case COMMON_RES_SUCCESS:
                if (profileList.size()>0){
                    profileList.clear();
                }
                profileList = data.getProfileList();
                addMarkerByProfile();
                ArrayList<Integer> indexs = search(profileList, "AB1");
                break;
        }
    }

    void addMarkerByProfile() {
        int size = profileList.size();
        if (markerID.size()>0) {
            markerID.clear();
        }
        for (int i = 0;i<size; i++) {
            LatLng lng = profileList.get(i).getuLatLng();
            if (lng != null) {
                addMarkerAtLocation(lng,""+i);
                markerID.add(i);
                Log.v(TAG, "addMarkerByProfile: "+lng);
            }
        }
    }


    ArrayList<Integer> search(ArrayList<Profile> profileList, String searchString) {
        ArrayList<Integer> profileIndex = new ArrayList<>();

        int size = profileList.size();

        for (int i = 0; i < size; i++) {
            Profile profile = profileList.get(i);
            String dataString = "";
            String pName = profile.getuName();
            String pNotes = profile.getuNotes();
            String pSpeciality  = profile.getuSpeciality();

            if (pName.equals("null")) {
                pName = "";
            }

            if (pNotes == "null") {
                pNotes = "";
            }

            if (pSpeciality == "null") {
                pSpeciality = "";
            }


            dataString = pName + " " + pNotes + " " + pSpeciality;

            StringTokenizer st = new StringTokenizer(dataString);
            boolean isFound = false;
            while (st.hasMoreTokens()) {
                if (st.nextToken().equals(searchString)) {
                    isFound = true;
                }
            }

            if (isFound){
                profileIndex.add(i);
                isFound = false;
            }


        }

        Toast.makeText(getContext(), ""+profileIndex.size(), Toast.LENGTH_SHORT).show();
        return profileIndex;
    }
}
