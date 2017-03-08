package com.fourway.localapp.ui;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.fourway.localapp.R;
import com.fourway.localapp.data.GetUsersRequestData;
import com.fourway.localapp.data.Profile;
import com.fourway.localapp.login_session.SessionManager;
import com.fourway.localapp.request.CommonRequest;
import com.fourway.localapp.request.GetUsersRequest;
import com.fourway.localapp.request.helper.VolleySingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;


public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, GetUsersRequest.GetUsersResponseCallback,
        ClusterManager.OnClusterClickListener<Profile>, ClusterManager.OnClusterInfoWindowClickListener<Profile>, ClusterManager.OnClusterItemClickListener<Profile>, ClusterManager.OnClusterItemInfoWindowClickListener<Profile> {

    SessionManager session;
    private static final int REQUEST_LOCATION_CODE = 200;
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    MapView mMapView;
    Location mCurrentLocation, mLastLocation;
    LocationManager mLocationManager;
    ArrayList<Profile> profileList;
    ArrayList<Integer> markerID;

    ImageView medicalBtn, officeBtn,
            carBtn, emergencyBtn,
            entertainmentBtn;
    ImageView searchBtn;

    RelativeLayout uDetailLayout;

    AutoCompleteTextView searchBoxView;

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

        session = new SessionManager(getActivity());
        profileList = new ArrayList<>();
        markerID = new ArrayList<>();
        officeBtn = (ImageView) view.findViewById(R.id.office_iv);
        medicalBtn = (ImageView) view.findViewById(R.id.medical_iv);
        carBtn = (ImageView) view.findViewById(R.id.car_iv);
        emergencyBtn = (ImageView) view.findViewById(R.id.emergency_iv);
        entertainmentBtn = (ImageView) view.findViewById(R.id.entertainment_iv);
        searchBtn = (ImageView) view.findViewById(R.id.search_btn);
        searchBoxView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);

        uDetailLayout = (RelativeLayout) view.findViewById(R.id.user_detail_rl);

        searchBoxView.addTextChangedListener(textWatcherforSearchBox);

        searchBtn.setOnClickListener(searchOnClickListener);
        officeBtn.setOnClickListener(filterClickListener);
        medicalBtn.setOnClickListener(filterClickListener);
        carBtn.setOnClickListener(filterClickListener);
        emergencyBtn.setOnClickListener(filterClickListener);
        entertainmentBtn.setOnClickListener(filterClickListener);

        mMapView = (MapView) view.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        /*
            for testing only
         */


        // Create an instance of GoogleAPIClient.
//        ConnectToGooglePlayServices();


        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isPermissionGrated()) {
            requestPermissions(PERMISSIONS, REQUEST_LOCATION_CODE);
        } else requestLocation();

        if (!isLocationEnabled()) {
            showAlertForLocationSetting(1);
        }

        Log.v(TAG, "View ready");
        // Inflate the layout for this fragment
        return view;
    }

    /**
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.setOnMarkerClickListener(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //User has previously accepted this permission
            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            //Not in api-23, no need to prompt
            mMap.setMyLocationEnabled(true);
        }


        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mClusterManager = new ClusterManager<Profile>(getContext(), mMap);
        mClusterManager.setRenderer(new ProfileRenderer());

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);

        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        request(new LatLng(28.545623, 77.330507));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(28.545623, 77.330507), 9.5f));
        if (HomeActivity.mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HomeActivity.mLastKnownLocation, 14f));
        }
//        addItems();
//        mClusterManager.cluster();

/*
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (mMap.getMyLocation()!=null) {
                    Toast.makeText(getContext(), "ctn clicked" + mMap.getMyLocation(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });*/
//        mMap.setOnMyLocationChangeListener(onMyLocationChangeListener);

        /*for (int i=0;i<mMapUsers.size();i++) {
            addMarkerAtLocation(mMapUsers.get(i));
        }*/
        setMyLocationButton();
        Log.v(TAG, "Map Ready");

    }


    private void addItems() {

        /*************** data for testing ***********/

        // http://www.flickr.com/photos/sdasmarchives/5036248203/
        mClusterManager.addItem(new Profile(position(), "Walter"));
        mClusterManager.addItem(new Profile(position(), "Walter"));
        mClusterManager.addItem(new Profile(position(), "Walter"));
        mClusterManager.addItem(new Profile(position(), "Walter"));
        mClusterManager.addItem(new Profile(position(), "Walter"));
        mClusterManager.addItem(new Profile(position(), "Walter"));

        // http://www.flickr.com/photos/usnationalarchives/4726917149/
        mClusterManager.addItem(new Profile(position(), "Gran"));

        // http://www.flickr.com/photos/nypl/3111525394/
        mClusterManager.addItem(new Profile(position(), "Ruth"));

        // http://www.flickr.com/photos/smithsonian/2887433330/
        mClusterManager.addItem(new Profile(position(), "Stefan"));
        mClusterManager.addItem(new Profile(position(), "Stefan"));
        mClusterManager.addItem(new Profile(position(), "Stefan"));
        mClusterManager.addItem(new Profile(position(), "Stefan"));
        mClusterManager.addItem(new Profile(position(), "Stefan"));
        mClusterManager.addItem(new Profile(position(), "Stefan"));

        // http://www.flickr.com/photos/library_of_congress/2179915182/
        mClusterManager.addItem(new Profile(position(), "Mechanic"));
        mClusterManager.addItem(new Profile(position(), "Mechanic"));
        mClusterManager.addItem(new Profile(position(), "Mechanic"));
        mClusterManager.addItem(new Profile(position(), "Mechanic"));
        mClusterManager.addItem(new Profile(position(), "Mechanic"));
        mClusterManager.addItem(new Profile(position(), "Mechanic"));
        mClusterManager.addItem(new Profile(position(), "Mechanic"));
        mClusterManager.addItem(new Profile(position(), "Mechanic"));
        mClusterManager.addItem(new Profile(position(), "Mechanic"));

        // http://www.flickr.com/photos/nationalmediamuseum/7893552556/
        mClusterManager.addItem(new Profile(position(), "Yeats"));
        mClusterManager.addItem(new Profile(position(), "Yeats"));
        mClusterManager.addItem(new Profile(position(), "Yeats"));
        mClusterManager.addItem(new Profile(position(), "Yeats"));
        mClusterManager.addItem(new Profile(position(), "Yeats"));
        mClusterManager.addItem(new Profile(position(), "Yeats"));
        mClusterManager.addItem(new Profile(position(), "Yeats"));

        // http://www.flickr.com/photos/sdasmarchives/5036231225/
        mClusterManager.addItem(new Profile(position(), "John"));

        // http://www.flickr.com/photos/anmm_thecommons/7694202096/
        mClusterManager.addItem(new Profile(position(), "Trevor the Turtle"));

        // http://www.flickr.com/photos/usnationalarchives/4726892651/
        mClusterManager.addItem(new Profile(position(), "Teach"));
        mClusterManager.addItem(new Profile(position(), "Teach"));
        mClusterManager.addItem(new Profile(position(), "Teach"));
        mClusterManager.addItem(new Profile(position(), "Teach"));
        mClusterManager.addItem(new Profile(position(), "Teach"));
        mClusterManager.addItem(new Profile(position(), "Teach"));
        mClusterManager.addItem(new Profile(position(), "Teach"));
        mClusterManager.addItem(new Profile(position(), "Teach"));
        mClusterManager.addItem(new Profile(position(), "Teach"));
        mClusterManager.addItem(new Profile(position(), "Teach"));

        /* ========== For Real data ============*/
//        mClusterManager.addItems(profileList);
    }

    /**
     * set potion of myLocationButton
     */
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


    private void updateMapUI(Location location) {
//        mMap.clear();
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
//        request(position);

//        mMap.addMarker(new MarkerOptions().position(position));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 14));

        Log.v(TAG, "Update UI");
    }

    /**
     * add marker on map
     * @param latLng
     */
    private void addMarkerAtLocation(LatLng latLng, String tag) {
        mMap.addMarker(new MarkerOptions().position(latLng).title(tag));

        Log.v(TAG, "Add marker At: " + latLng);
    }

    @Override
    public void onLowMemory() {
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
//        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
//        mGoogleApiClient.disconnect();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }


    /**
     * filter map marker
     */
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


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.v(TAG, "onLocationChanged");

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            addMarkerAtLocation(latLng);
            Toast.makeText(getApplicationContext(), "" + latLng, Toast.LENGTH_SHORT).show();
            session.saveLastLocation(latLng);
            request(latLng);


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

    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = mLocationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(provider, 1000*5, 500, locationListener);
        Log.v(TAG, "requestLocation");
    }

    private boolean isLocationEnabled() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isPermissionGrated() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED || getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("", "Permission is grated");
                return true;
            } else {
                Log.v("", "Permission not grated");
                return false;
            }
        }
        return false;
    }

    private void showAlertForLocationSetting(final int status) {
        String msg, title, btnText;
        if (status == 1) {
            msg = "Your location Settings is set to 'OFF'. \nPlease Enable Location to " +
                    "use this app";
            title = "Enable Location";
            btnText = "Location Settings";
        }else {
            msg = "Please allow this app to access location!";
            title = "Permission access";
            btnText = "Grant";
        }

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setCancelable(false);
        dialog.setTitle(title)
        .setMessage(msg).setPositiveButton(btnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (status == 1) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }else {
                    requestPermissions(PERMISSIONS, REQUEST_LOCATION_CODE);
                }
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });

        dialog.show();
    }


    /**
     * google play service connection
     */
    public void ConnectToGooglePlayServices() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        /*Log.v(TAG, "onConnected");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            updateMapUI(mLastLocation);
            LatLng position = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
//            request(position);
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }*/

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

    /**
     * marker click listener
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        /*if (uDetailLayout.getVisibility() != View.VISIBLE) {
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
        }*/
        return false;
    }

    //test
    void request(LatLng latLng) {
        GetUsersRequest usersRequest = new GetUsersRequest(getActivity(), latLng, HomeActivity.mLoginToken, MapFragment.this);
        usersRequest.executeRequest();
    }


    @Override
    public void onGetUsersResponse(CommonRequest.ResponseCode res, GetUsersRequestData data) {
        switch (res) {
            case COMMON_RES_SUCCESS:
                if (profileList.size() > 0) {
                    profileList.clear();
                }
                profileList = data.getProfileList();
                mClusterManager.clearItems();
                mClusterManager.addItems(profileList);
//                addMarkerByProfile(false, null);
//                addItems();
                mClusterManager.cluster();
                break;
            case COMMON_RES_CONNECTION_TIMEOUT:
                break;
            case COMMON_RES_FAILED_TO_CONNECT:
                Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                break;
            case COMMON_RES_INTERNAL_ERROR:
                break;
            case COMMON_RES_SERVER_ERROR_WITH_MESSAGE:
                break;
        }
    }

    /**
     * add marker by profile data
     */
    void addMarkerByProfile(boolean isFilter, ArrayList<Integer> filterIndex) {
//        mMap.clear();
        int size = profileList.size();
        int filterIndexSize = 0;
        if (markerID.size() > 0) {
            markerID.clear();
        }

        mClusterManager.clearItems();

        if (filterIndex != null) {
            filterIndexSize = filterIndex.size();
        }

        if (!isFilter) {
            /*for (int i = 0; i < size; i++) {
                LatLng lng = profileList.get(i).getuLatLng();
                if (lng != null) {
                    addMarkerAtLocation(lng, "" + i);
                    markerID.add(i);
                    Log.v(TAG, "addMarkerByProfile: " + lng);
                }
            }*/
            mClusterManager.addItems(profileList);
        } else {
            for (int i = 0; i < filterIndexSize; i++) {
                int profileIndex = filterIndex.get(i);
                mClusterManager.addItem(profileList.get(profileIndex));
                /*LatLng lng = profileList.get(profileIndex).getuLatLng();
                if (lng != null) {
                    addMarkerAtLocation(lng, "" + i);
                    markerID.add(i);
                    Log.v(TAG, "addMarkerByProfile: " + lng);
                }*/
            }
        }
        mClusterManager.cluster();
    }

    /**
     *
     * @param profileList
     * @param searchString
     * @return
     */

    ArrayList<Integer> search(ArrayList<Profile> profileList, String searchString) {
        searchString = searchString.toLowerCase();
        ArrayList<Integer> profileIndex = new ArrayList<>();

        int size = profileList.size();

        for (int i = 0; i < size; i++) {
            Profile profile = profileList.get(i);
            String dataString = "";
            String pName = profile.getuName();
            String pNotes = profile.getuNotes();
            String pSpeciality = profile.getuSpeciality();

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
            dataString = dataString.toLowerCase();

            StringTokenizer st = new StringTokenizer(dataString);
            boolean isFound = false;
            while (st.hasMoreTokens()) {
                if (st.nextToken().equals(searchString)) {
                    isFound = true;
                }
            }

            if (isFound) {
                profileIndex.add(i);
                isFound = false;
            }


        }

        Toast.makeText(getContext(), "" + profileIndex.size(), Toast.LENGTH_SHORT).show();
        return profileIndex;
    }

    TextWatcher textWatcherforSearchBox = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(searchBoxView.getText()) && profileList != null) {
                addMarkerByProfile(false, null);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    View.OnClickListener searchOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String searchQuery = searchBoxView.getText().toString();
            if (!TextUtils.isEmpty(searchQuery)) {
                ArrayList<Integer> indexs = search(profileList, searchQuery);
                if (indexs != null && indexs.size() > 0) {
                    addMarkerByProfile(true, indexs);
                }
            }

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;

        }

    }

    protected Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }


    protected GoogleMap getMap() {
        return mMap;
    }

    private void markerClickWindow(Profile profile) {

        if (uDetailLayout.getVisibility() != View.VISIBLE) {
            String pName = profile.getuName();
            final String pEmail = profile.getuEmail();
            final String mMobile = profile.getuMobile();

            TextView textView = (TextView)getView().findViewById(R.id.user_name);
            ImageView actionEmail = (ImageView) getView().findViewById(R.id.action_email);
            ImageView actionCall = (ImageView) getView().findViewById(R.id.action_call);
            NetworkImageView proPicNetworkImageView = (NetworkImageView)getView().findViewById(R.id.user_pic);
            proPicNetworkImageView.setImageUrl(profile.getuPictureURL(), VolleySingleton.getInstance(getApplicationContext()).getImageLoader());
            if (pName != null) {
                textView.setText(pName);
            }
            actionCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMobile != "null") {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel " + mMobile));
                        startActivity(callIntent);
                    }
                }
            });

            actionEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent Email = new Intent(Intent.ACTION_SENDTO,Uri.fromParts(
                            "mailto",pEmail, null));

                    startActivity(Intent.createChooser(Email, "Send Email:"));
                }
            });

            uDetailLayout.setVisibility(View.VISIBLE);
        }else {
            uDetailLayout.setVisibility(View.GONE);
        }
    }


    /**
     * Demonstrates heavy customisation of the look of rendered clusters.
     */


    /**
     * Draws profile photos inside markers (using IconGenerator).
     * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
     */

    private ClusterManager<Profile> mClusterManager;


    private class ProfileRenderer extends DefaultClusterRenderer<Profile> {

        private IconGenerator mIconGenerator = new IconGenerator(getActivity().getApplication());
        private IconGenerator mClusterIconGenerator = new IconGenerator(getActivity().getApplication());
        private ImageView mImageView;
        private ImageView mClusterImageView;
        private int mDimension;

        public ProfileRenderer() {
            super(getApplicationContext(), getMap(), mClusterManager);

            View multiProfile = getActivity().getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.cluster_image);

            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(Profile profile, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
//            mImageView.setImageResource(profile.profilePhoto);
            mImageView.setImageResource(R.mipmap.ic_launcher);

            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(profile.getuName());
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Profile> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).

            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (Profile p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
//                Drawable drawable = getResources().getDrawable(p.profilePhoto);
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }

            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<Profile> cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<Profile> cluster) {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().getuName();
        Toast.makeText(getContext(), cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Profile> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(Profile profile) {
        // Does nothing, but you could go into the user's profile page, for example.
        markerClickWindow(profile);
        return true;
    }

    @Override
    public void onClusterItemInfoWindowClick(Profile profile) {
        // Does nothing, but you could go into the user's profile page, for example.

    }



    /**
     * For testing
     */

    private Random mRandom = new Random(1984);
    private LatLng position() {
        return new LatLng(random(28.545623, 28.28494009999999), random(77.330507, 76.3514683));
    }

    private double random(double min, double max) {
        return mRandom.nextDouble() * (max - min) + min;
    }
}
