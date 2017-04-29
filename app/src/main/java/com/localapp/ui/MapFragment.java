package com.localapp.ui;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.localapp.R;
import com.localapp.appcontroller.AppController;
import com.localapp.camera.Camera2Activity;
import com.localapp.data.GetUsersRequestData;
import com.localapp.data.NoticeBoard;
import com.localapp.data.NoticeBoardMessage;
import com.localapp.data.Profile;
import com.localapp.login_session.SessionManager;
import com.localapp.request.CommonRequest;
import com.localapp.request.GetNearestNoticeBoardRequest;
import com.localapp.request.GetNoticeBoardMessageRequest;
import com.localapp.request.GetProfileRequest;
import com.localapp.request.GetUsersRequest;
import com.localapp.request.ImageSearchRequest;
import com.localapp.request.SubscribeUnsubscribeNoticeBoardRequest;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.localapp.request.helper.GetProfileByIdRequest;
import com.squareup.picasso.Picasso;
import com.localapp.util.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import static com.localapp.appcontroller.AppController.getAppContext;
import static com.localapp.util.utility.getProfessionList;


public class MapFragment extends Fragment implements OnMapReadyCallback, GetUsersRequest.GetUsersResponseCallback,
        ClusterManager.OnClusterClickListener<Profile>, ClusterManager.OnClusterInfoWindowClickListener<Profile>, ClusterManager.OnClusterItemClickListener<Profile>, ClusterManager.OnClusterItemInfoWindowClickListener<Profile>,
        ImageSearchRequest.ImageSearchResponseCallback, GetNearestNoticeBoardRequest.GetNearestNoticeBoardRequestCallback,GetNoticeBoardMessageRequest.GetNoticeBoardMessageRequestCallback,SubscribeUnsubscribeNoticeBoardRequest.SubscribeUnsubscribeNoticeBoardCallback,GetProfileByIdRequest.GetProfileByIdRequestCallback {



    public static String TAG = "MapFragment";
    private static final int REQUEST_LOCATION_CODE = 200;
    private static final int REQUEST_CAMERA_CODE = 201;
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CALL_PHONE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA};
    final static String[] CAMERA_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO};

    SessionManager session;

//    private ImageLoader mImageLoader;
    private GoogleMap mMap;
    private MapView mMapView;
    Location mCurrentLocation, mLastLocation;
    private LocationManager mLocationManager;
    public ArrayList<Profile> profileList;
    public ArrayList<Profile> noticeBoardProfileList;

    private ImageView professionalBtn, studentBtn,
            repairBtn, emergencyBtn,
            notice_boardBtn,hobbiesBtn;
    private ImageView searchBtn,searchCameraBtn;
    private RelativeLayout uDetailLayout;
    private AutoCompleteTextView searchBoxView;
    private Snackbar closeAppSnackbar;

    private ArrayAdapter<String> autoCompleteAdapter;
    private List<String> searchContaintList;


    DialogNoticeBoardMessageAdapter messageAdapter;





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


        Bundle extras = getActivity().getIntent().getExtras();

        //for notification

        try {
            if(extras != null){
                String notificationUserID = extras.getString("userId");
                profileRequest(notificationUserID);
            }
        }catch (Exception e){
            e.printStackTrace();
        }



        session = new SessionManager(getActivity());
        profileList = new ArrayList<>();
        noticeBoardProfileList = new ArrayList<>();
        searchContaintList = new ArrayList<>();
        autoCompleteAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, searchContaintList);

        setupView(view);    //initialization view object

        mMapView = (MapView) view.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isPermissionGrated()) {
            requestPermissions(PERMISSIONS, REQUEST_LOCATION_CODE);
        } else {
            requestLocation(); //requesting for location update
        }

        if (!isLocationEnabled()) {
            showAlertForLocationSetting(1);
        }

        Log.v(TAG, "View ready");
        // Inflate the layout for this fragment
        return view;
    }

    /**
     * initialization view objects
     * @param view
     */
    private void setupView(View view) {


        studentBtn = (ImageView) view.findViewById(R.id.student_iv);
        hobbiesBtn = (ImageView) view.findViewById(R.id.hobbies_iv);
        professionalBtn = (ImageView) view.findViewById(R.id.professionals_iv);
        repairBtn = (ImageView) view.findViewById(R.id.repair_iv);
        emergencyBtn = (ImageView) view.findViewById(R.id.emergency_iv);
        notice_boardBtn = (ImageView) view.findViewById(R.id.notice_board_iv);
        searchBtn = (ImageView) view.findViewById(R.id.search_btn);
        searchCameraBtn= (ImageView) view.findViewById(R.id.search_camera_btn);
        searchBoxView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
        uDetailLayout = (RelativeLayout) view.findViewById(R.id.user_detail_rl);


        searchBoxView.addTextChangedListener(textWatcherForSearchBox);
        searchBoxView.setOnKeyListener(onKeyListener);
        searchBoxView.setAdapter(autoCompleteAdapter);
        searchBtn.setOnClickListener(searchOnClickListener);
        searchCameraBtn.setOnClickListener(searchOnClickListener);
        studentBtn.setOnClickListener(filterClickListener);
        professionalBtn.setOnClickListener(filterClickListener);
        repairBtn.setOnClickListener(filterClickListener);
        emergencyBtn.setOnClickListener(filterClickListener);
        notice_boardBtn.setOnClickListener(filterClickListener);
        hobbiesBtn.setOnClickListener(filterClickListener);

    }



    /**
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

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



//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(28.545623, 77.330507), 9.5f));
        if (HomeActivity.mLastKnownLocation != null) {
            request(HomeActivity.mLastKnownLocation);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HomeActivity.mLastKnownLocation, 16.2f));
        }
//        addItems();
//        mClusterManager.cluster();
        setMyLocationButton(); //adjust my location button
        Log.v(TAG, "Map Ready");
    }

    /**
     *
     * @return GoogleMap
     */
    protected GoogleMap getMap() {
        return mMap;
    }


    private void addItems() {
        /*************** data for testing ***********/
        for (int i =0 ;i <10; i++) {
            mClusterManager.addItem(new Profile(position(), "Walter"));
        }
        for (int i =0 ;i <10; i++) {
            mClusterManager.addItem(new Profile(position(), "Gran"));
        }
        for (int i =0 ;i <10; i++) {
            mClusterManager.addItem(new Profile(position(), "Ruth"));
        }
        for (int i =0 ;i <10; i++) {
            mClusterManager.addItem(new Profile(position(), "Stefan"));
        }
        for (int i =0 ;i <10; i++) {
            mClusterManager.addItem(new Profile(position(), "Yeats"));
        }

        for (int i =0 ;i <10; i++) {
            mClusterManager.addItem(new Profile(position(), "Mechanic"));
        }

        for (int i =0 ;i <10; i++) {
            mClusterManager.addItem(new Profile(position(), "John"));
        }

        for (int i =0 ;i <10; i++) {
            mClusterManager.addItem(new Profile(position(), "Teach"));
        }



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


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        AppController.activityResumed();


        mMapView.onResume();

        try {
            getView().setFocusableInTouchMode(true);
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
        getView().requestFocus();
        getView().setOnKeyListener(onKeyListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
        AppController.activityPaused();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }


    /**
     * filter map marker
     */
    boolean isSelectedFilterButton = false;
    View.OnClickListener filterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (uDetailLayout.getVisibility() != View.VISIBLE) {
                ArrayList<Integer> indexs;
                initFilterButtonSelection();
                isSelectedFilterButton = true;
                switch (v.getId()) {
                    case R.id.emergency_iv:
                        emergencyBtn.setImageResource(R.drawable.ic_health_selected);
                        indexs = filterIndexByProfession(profileList, ExpandableListAdapter.PROFESSION_GROUP_HEALTH);
                        if (indexs != null && indexs.size() > 0) {
                            addMarkerByProfile(true, indexs);
                        }

                        break;
                    case R.id.student_iv:
                        studentBtn.setImageResource(R.drawable.ic_student_selected);
                        indexs = filterIndexByProfession(profileList, ExpandableListAdapter.PROFESSION_GROUP_STUDENT);
                        if (indexs != null && indexs.size() > 0) {
                            addMarkerByProfile(true, indexs);
                        }
                        break;
                    case R.id.professionals_iv:
                        professionalBtn.setImageResource(R.drawable.ic_professionals_selected);
                        indexs = filterIndexByProfession(profileList, ExpandableListAdapter.PROFESSION_GROUP_PROFESSIONALS);
                        if (indexs != null && indexs.size() > 0) {
                            addMarkerByProfile(true, indexs);
                        }
                        break;
                    case R.id.repair_iv:
                        repairBtn.setImageResource(R.drawable.ic_repair_selected);
                        indexs = filterIndexByProfession(profileList, ExpandableListAdapter.PROFESSION_GROUP_REPAIR);
                        if (indexs != null && indexs.size() > 0) {
                            addMarkerByProfile(true, indexs);
                        }
                        break;
                    case R.id.notice_board_iv:
                        notice_boardBtn.setImageResource(R.drawable.ic_notice_selected);
                        requestForNearbyNoticeBoard();
                        break;

                    case R.id.hobbies_iv:
                        hobbiesBtn.setImageResource(R.drawable.ic_hobby_selected);
                        indexs = filterIndexByProfession(profileList, ExpandableListAdapter.PROFESSION_GROUP_SKILLS);
                        if (indexs != null && indexs.size() > 0) {
                            addMarkerByProfile(true, indexs);
                        }
                        break;

                }
            }

        }
    };

    private void initFilterButtonSelection (){
        emergencyBtn.setImageResource(R.drawable.ic_health);
        studentBtn.setImageResource(R.drawable.ic_student);
        professionalBtn.setImageResource(R.drawable.ic_professionals);
        repairBtn.setImageResource(R.drawable.ic_repair);
        notice_boardBtn.setImageResource(R.drawable.ic_notice);
        hobbiesBtn.setImageResource(R.drawable.ic_hobby);
        isSelectedFilterButton = false;
    }

    /**
     * locationListener
     */
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.v(TAG, "onLocationChanged");

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            addMarkerAtLocation(latLng);
            HomeActivity.mLastKnownLocation = latLng;
//            Toast.makeText(getApplicationContext(), "" + latLng, Toast.LENGTH_SHORT).show();
//            if (isActivityVisible()) {
                session.saveLastLocation(latLng);
                request(latLng);
//            }

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

    /**
     * request for update location based on distance and time
     */
    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = mLocationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(provider, 1000*5, 100, locationListener);
        Log.v(TAG, "requestLocation");
    }

    /**
     * check location provider enabled or not
     * @return
     */
    private boolean isLocationEnabled() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * check all required permission is granted or not
     * @return
     */
    private boolean isPermissionGrated() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED || getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) && getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                    && getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)  {
                Log.v("", "Permission is grated");
                return true;
            } else {
                Log.v("", "Permission not grated");
                return false;
            }
        }
        return false;
    }

    /**
     * Alert dialog for if your location setting is off
     * @param status
     */
    private void showAlertForLocationSetting(final int status) {
        String msg, title, btnText;
        if (status == 1) {
            msg = getString(R.string.alert_msg_location_setting);
            title = getString(R.string.enable_location);
            btnText = getString(R.string.location_Settings);
        }else {
            msg = getString(R.string.alert_msg_access_location);
            title = getString(R.string.permission_access);
            btnText = getString(R.string.grant);
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
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });

        dialog.show();
    }



    AlertDialog dialog;
    public void showNoticeBoardDialog(final NoticeBoard noticeBoard, final boolean hasSubscribed) {

//        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());



        View view = LayoutInflater.from(getContext()).inflate(R.layout.notice_board_dialog,null);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll_notice_dialog);
        Button subButton = (Button) view.findViewById(R.id.subscribe_btn);

        if (HomeActivity.mUserId != null && HomeActivity.mUserId.equals(noticeBoard.getAdminId())) {
            subButton.setVisibility(View.GONE);
        }else {
            linearLayout.setVisibility(View.GONE);
        }




        TextView noticeName = (TextView)view.findViewById(R.id.notice_board_name_textView);
        RecyclerView messageRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerViewDialog);

        final EditText messageEditText = (EditText) view.findViewById(R.id._input_notice_message);
        final ImageButton postBtn = (ImageButton)  view.findViewById(R.id._notice_post_btn);

        noticeName.setText(noticeBoard.getName());


        if (hasSubscribed) {
            subButton.setText(R.string.unsubscribe);
        }else {
            subButton.setText(R.string.subscribe);
        }


        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HomeActivity.mUserId !=null && !HomeActivity.mUserId.equals("")) {
                    CommonRequest.RequestType type;
                    if (hasSubscribed) {
                        type = CommonRequest.RequestType.COMMON_REQUEST_UNSUBSCRIBE_NOTICE_BOARD;

                    } else {
                        type = CommonRequest.RequestType.COMMON_REQUEST_SUBSCRIBE_NOTICE_BOARD;
                    }

                    requestSubscribeAndUnsub(noticeBoard, type);
                }else {
                    Toast.makeText(getContext(), R.string.login_first, Toast.LENGTH_SHORT).show();
                }

                if (dialog!=null)
                dialog.dismiss();

            }
        });



        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageEditText.getText().toString().trim();
                if (!msg.isEmpty()) {
                    NoticeBoardMessage message = new NoticeBoardMessage(msg);
                    message.setAdminId(noticeBoard.getId());

                    /*PostNoticeBoardMessageRequest postNoticeBoardMessageRequest = new PostNoticeBoardMessageRequest(getContext(),message,MapFragment.this);
                    postNoticeBoardMessageRequest.executeRequest();
                    noticeBoard.getMessagesList().add(message);
                    messageAdapter.notifyDataSetChanged();
                    messageEditText.setText("");*/
                }
            }
        });



        messageAdapter = new DialogNoticeBoardMessageAdapter(getContext(),noticeBoard);

        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        messageRecyclerView.setAdapter(messageAdapter);


//        dialog.setCancelable(false);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }


    /**
     * request for map data
     * @param latLng
     */
    void request(LatLng latLng) {
        GetUsersRequest usersRequest = new GetUsersRequest(getActivity(), latLng, HomeActivity.mLoginToken, MapFragment.this);
        usersRequest.executeRequest();
    }


    private void profileRequest(String profileID) {
        Profile mProfile = new Profile(profileID);

        GetProfileByIdRequest request = new GetProfileByIdRequest(getContext(),mProfile,this);
        request.executeRequest();
    }


    private void requestForNearbyNoticeBoard() {
        if (HomeActivity.mLastKnownLocation != null) {
            GetNearestNoticeBoardRequest nearestNoticeBoardRequest = new GetNearestNoticeBoardRequest(getContext(), this, HomeActivity.mLastKnownLocation);
            nearestNoticeBoardRequest.executeRequest();
        }else {
            Toast.makeText(getApplicationContext(), R.string.please_wait_getting_location, Toast.LENGTH_SHORT).show();
        }
    }

    private void requestForNoticeBoardMsg(NoticeBoard mNoticeBoard,boolean hasSubscribed) {
        GetNoticeBoardMessageRequest getNoticeBoardMessageRequest = new GetNoticeBoardMessageRequest(getContext(),mNoticeBoard, hasSubscribed,this);
        getNoticeBoardMessageRequest.executeRequest();
    }

    private void requestSubscribeAndUnsub(NoticeBoard mNoticeBoard, CommonRequest.RequestType requestType) {
        SubscribeUnsubscribeNoticeBoardRequest request = new SubscribeUnsubscribeNoticeBoardRequest(getContext(),mNoticeBoard.getId(),HomeActivity.mUserId,requestType,MapFragment.this);
        request.executeRequest();
    }





    @Override
    public void onGetUsersResponse(CommonRequest.ResponseCode res, GetUsersRequestData data) {
        switch (res) {
            case COMMON_RES_SUCCESS:
                if (profileList.size() > 0) {
                    profileList.clear();
                }
                profileList = data.getProfileList();
                if(uDetailLayout.getVisibility() != View.VISIBLE) {
                    mClusterManager.clearItems();
                    mClusterManager.addItems(profileList);
//                addMarkerByProfile(false, null);
//                addItems();
                    mClusterManager.cluster();
                }

                setSearchHintData(profileList);
                break;
            case COMMON_RES_CONNECTION_TIMEOUT:
                break;
            case COMMON_RES_FAILED_TO_CONNECT:
                Toast.makeText(getContext(), R.string.no_internet_msg, Toast.LENGTH_SHORT).show();
                break;
            case COMMON_RES_INTERNAL_ERROR:
                break;
            case COMMON_RES_SERVER_ERROR_WITH_MESSAGE:
                break;
        }
    }



    @Override
    public void GetNearestNoticeBoardResponse(CommonRequest.ResponseCode responseCode, List<NoticeBoard> mNoticeBoards) {
        if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {

            if (noticeBoardProfileList.size()>0) {
                noticeBoardProfileList.clear();
            }

            for (NoticeBoard noticeBoard: mNoticeBoards) {

                Profile profile = new Profile(noticeBoard.getId());
                profile.setuToken(noticeBoard.getAdminId());
                profile.setuLatLng(noticeBoard.getLocation());
                profile.setuSpeciality("nnnnnnnnnn");
                profile.setuName(noticeBoard.getName());
                noticeBoardProfileList.add(profile);

            }

            if (mClusterManager != null) {
                mClusterManager.clearItems();
                mClusterManager.addItems(noticeBoardProfileList);
                mClusterManager.cluster();
            }




            /*if (nearestNoticeBoardList.size()>0) {
                nearestNoticeBoardList.clear();
            }

            if (mNoticeBoards.size() != 0){
                nearestNoticeBoardList.addAll(mNoticeBoards);
                noticeAdapterNearYou.notifyDataSetChanged();
            }*/
        }
    }

    @Override
    public void GetNoticeBoardMessageResponse(CommonRequest.ResponseCode responseCode, NoticeBoard mNoticeBoard, boolean hasSubscribed) {
        if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {
            showNoticeBoardDialog(mNoticeBoard, hasSubscribed);
        }
    }

    @Override
    public void SubscribeUnsubscribeNoticeBoardResponse(CommonRequest.ResponseCode responseCode, String errorMsg) {
        if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {
            Toast.makeText(getContext(), R.string.success, Toast.LENGTH_SHORT).show();
        }else if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SERVER_ERROR_WITH_MESSAGE) {
            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * add marker by profile data
     */
    void addMarkerByProfile(boolean isFilter, ArrayList<Integer> filterIndex) {
        int filterIndexSize = 0;

        if (mClusterManager != null) {
            mClusterManager.clearItems();

            if (filterIndex != null) {
                filterIndexSize = filterIndex.size();
            }

            if (!isFilter) {
                mClusterManager.addItems(profileList);
            } else {
                for (int i = 0; i < filterIndexSize; i++) {
                    int profileIndex = filterIndex.get(i);
                    mClusterManager.addItem(profileList.get(profileIndex));
                }
            }
            mClusterManager.cluster();
            if (filterIndexSize == 1) {
                markerClickWindow(profileList.get(filterIndex.get(0)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(profileList.get(filterIndex.get(0)).getuLatLng(), 16.8f));
            }
        }
    }


    void showNotificationProfile(Profile mProfile) {

        mProfile.setuPrivacy("0");
        if (mClusterManager != null) {
            mClusterManager.clearItems();

            mClusterManager.addItem(mProfile);

            mClusterManager.cluster();

            markerClickWindow(mProfile);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mProfile.getuLatLng(), 16.8f));

        }
    }

    /**
     * filter index by search string
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
            String pProfession = profile.getProfession();

            if (pName.equals("null")) {
                pName = "";
            }

            if (pNotes == "null") {
                pNotes = "";
            }

            if (pSpeciality == "null") {
                pSpeciality = "";
            }
            if (pProfession == "null") {
                pProfession = "";
            }


            dataString = pName + " " + pNotes + " " + pSpeciality+ " " + pProfession;
            dataString = dataString.toLowerCase();

            StringTokenizer st = new StringTokenizer(dataString,", \n");
            boolean isFound = false;
            while (st.hasMoreTokens()) {
                if (st.nextToken().equals(searchString)) {
                    isFound = true;
                }
            }

            if (pName.toLowerCase().equals(searchString)){  //for full name
                isFound = true;
            }

            if (isFound) {
                profileIndex.add(i);
                isFound = false;
            }


        }

        return profileIndex;
    }


    public void setSearchHintData(List<Profile> mProfileList){
        String[] uSpeciality = new String[0];
        String[] uNotes = new String[0];
        String[] profession = new String[0];
        if (searchContaintList.size() > 0) {
            searchContaintList.clear();
        }
        for (Profile profile:mProfileList){

            try {
                uSpeciality = profile.getuSpeciality().split(" ");
                uNotes = profile.getuNotes().split(" ");
                profession = profile.getProfession().split(",");
            }catch (Exception e){
                e.printStackTrace();
            }

            if (!searchContaintList.contains(profile.getuName())){
                searchContaintList.add(profile.getuName());
            }

            for (String s:uSpeciality){
                if (!searchContaintList.contains(s)){
                    searchContaintList.add(s);
                }
            }

            for (String s:uNotes){
                if (!searchContaintList.contains(s)){
                    searchContaintList.add(s);
                }
            }

            for (String s:profession){
                if (!searchContaintList.contains(s)){
                    searchContaintList.add(s);
                }
            }


        }

        autoCompleteAdapter.notifyDataSetChanged();
    }

    /**
     * filter index by profession
     * @param profileList
     * @param professionGroup
     * @return
     */
    /*ArrayList<Integer> filterIndexByProfession(ArrayList<Profile> profileList, String profession) {
        profession = profession.toLowerCase();
        ArrayList<Integer> profileIndex = new ArrayList<>();

        int size = profileList.size();

        for (int i = 0; i < size; i++) {
            Profile profile = profileList.get(i);
            String profileProfession = profile.getProfession();

            if (profession.equals(profileProfession.toLowerCase())) {
                profileIndex.add(i);
            }
        }
        return profileIndex;
    }*/

    ArrayList<Integer> filterIndexByProfession(ArrayList<Profile> profileList, String professionGroup) {

        ArrayList<Integer> profileIndex = new ArrayList<>();

        int size = profileList.size();

        for (int i = 0; i < size; i++) {
            Profile profile = profileList.get(i);
            String[] profileProfession = profile.getProfession().split(",");

            for (String profession:profileProfession){
                try {
                    if (getProfessionList(professionGroup).contains(profession)) {
                        profileIndex.add(i);
                        break;
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

            }




        }
        return profileIndex;
    }



    /**
     * text watcher for search box
     */
    TextWatcher textWatcherForSearchBox = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (TextUtils.isEmpty(searchBoxView.getText()) && profileList != null) {
                addMarkerByProfile(false, null);
                searchCameraBtn.setVisibility(View.VISIBLE);

            }else {
                searchCameraBtn.setVisibility(View.GONE);
            }


        }


        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(searchBoxView.getText()) && profileList != null) {
                addMarkerByProfile(false, null);
                searchCameraBtn.setVisibility(View.VISIBLE);
                searchBoxView.clearFocus();

            }else {
                searchCameraBtn.setVisibility(View.GONE);
            }
        }
    };


    /**
     * search listener
     */
    View.OnClickListener searchOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.search_btn) {
                String searchQuery = searchBoxView.getText().toString().trim();
                if (!TextUtils.isEmpty(searchQuery)) {
                    ArrayList<Integer> indexs = search(profileList, searchQuery);
                    if (indexs != null && indexs.size() > 0) {
                        addMarkerByProfile(true, indexs);
                    }
                }
            }else {
                if (isCameraPermissionGrated()){
                    openCamera();
                }else {
                    requestPermissions(CAMERA_PERMISSIONS,REQUEST_CAMERA_CODE);
                }

            }

            searchBoxView.clearFocus();

        }
    };

    void openCamera(){
        Intent i = new Intent(getContext(), Camera2Activity.class);
        i.putExtra("requestCode", 20);
        startActivityForResult(i, 20);
    }

    boolean isCameraPermissionGrated(){
        return (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(),Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
    }

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
                        requestLocation();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), R.string.permission_denied, Toast.LENGTH_LONG).show();

                    searchBoxView.requestFocus();

                }
                return;

            case REQUEST_CAMERA_CODE:
                if (grantResults.length > 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }else {
                    Toast.makeText(getApplicationContext(), R.string.permission_denied, Toast.LENGTH_LONG).show();
                }

        }

    }

    protected Context getApplicationContext() {
        return getContext();
    }

    /**
     * show user profile when click on map profile
     * user can call and email from this window
     * @param profile
     */
    private void markerClickWindow(final Profile profile) {

            String pName = profile.getuName();
            String pTitle = profile.getProfession();
            String pPrivacy = profile.getuPrivacy();
            final String pEmail = profile.getuEmail();


            TextView textView = (TextView)getView().findViewById(R.id.user_name);
            TextView titleView = (TextView)getView().findViewById(R.id.user_title);
            ImageView actionEmail = (ImageView) getView().findViewById(R.id.action_email);
            ImageView actionCall = (ImageView) getView().findViewById(R.id.action_call);
        actionCall.setVisibility(View.VISIBLE);
            CircularImageView proPicNetworkImageView = (CircularImageView)getView().findViewById(R.id.user_pic);
            Picasso.with(AppController.getAppContext()).load(profile.getuPictureURL()).into(proPicNetworkImageView);
//            proPicNetworkImageView.setImageUrl(profile.getuPictureURL(), VolleySingleton.getInstance(getApplicationContext()).getImageLoader());
            if (pName != null) {
                textView.setText(pName);
            }
            if (pTitle != null) {
                titleView.setText(pTitle);
            }
            if (pPrivacy != null && !pPrivacy.equals("null") && pPrivacy.equals("1")) {
                actionCall.setVisibility(View.GONE);
            }
            actionCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mMobile = profile.getuMobile();
                    if (mMobile != "null") {
                        mMobile = "+91"+mMobile;
                        Intent callIntent = new Intent(Intent.ACTION_CALL,Uri.fromParts("tel", mMobile, null));
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

    }




    /**
     * Demonstrates heavy customisation of the look of rendered clusters.
     */

    /**
     * Draws profile photos inside markers (using IconGenerator).
     * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
     */

    private ClusterManager<Profile> mClusterManager;

    @Override
    public void onProfileIdResponse(CommonRequest.ResponseCode responseCode, Profile mProfile) {
        if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {
            showNotificationProfile(mProfile);
        }
    }


    private class ProfileRenderer extends DefaultClusterRenderer<Profile> {

        private IconGenerator mIconGenerator = new IconGenerator(getActivity().getApplication());
        private IconGenerator mClusterIconGenerator = new IconGenerator(getActivity().getApplication());
        private ImageView mImageView;
        private ImageView mImageViewC;
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
//            mImageLoader = VolleySingleton.getInstance(getApplicationContext()).getImageLoader();
//            mImageLoader.get(profile.getuPictureURL(),ImageLoader.getImageListener(mImageView,R.mipmap.ic_launcher,android.R.drawable.ic_dialog_alert));
//            mImageView.setImageResource(R.mipmap.ic_launcher);
//            mImageView.setImageUrl(profile.getuPictureURL(), mImageLoader);
            Picasso.with(AppController.getAppContext()).load(profile.getuPictureURL()).into(mImageView);
            mImageView.setTag(profile.getuEmail());

            if (profile.getuSpeciality().equals("nnnnnnnnnn")) {
                mImageView.setImageResource(R.drawable.ic_notice);
            }

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

                try {
                    Drawable drawable = null;
                    try {
                        mImageViewC = (ImageView) getView().findViewById(R.id.temp);
                        Picasso.with(getAppContext()).load(p.getuPictureURL()).placeholder(R.mipmap.ic_launcher).into(mImageViewC);
                        drawable = getResources().getDrawable(R.mipmap.ic_launcher);
                        drawable = mImageViewC.getDrawable();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        drawable = getResources().getDrawable(R.mipmap.ic_launcher);
                    }

                    if (p.getuSpeciality().equals("nnnnnnnnnn")) {
                        mImageViewC.setImageResource(R.drawable.ic_notice);
                        drawable = mImageViewC.getDrawable();
                    }
                /*if (p.getProfession() != null){
                    drawable = getResources().getDrawable(getClusterDrawable(p.getProfession()));
                }*/

//                Drawable drawable = getResources().getDrawable(p.profilePhoto);
                    drawable.setBounds(0, 0, width, height);
                    profilePhotos.add(drawable);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }


            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));

            if (cluster.getSize() == 1) {
                for (Profile p2 : cluster.getItems()) {
                    markerClickWindow(p2);
                }

            }
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
//        Toast.makeText(getContext(), cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

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
        searchBoxView.clearFocus();
        if (profile.getuSpeciality() != null &&!profile.getuSpeciality().equals("nnnnnnnnnn")) {
            markerClickWindow(profile);
        }else {
            NoticeBoard noticeBoard = new NoticeBoard();
            noticeBoard.setId(profile.getuId());
            noticeBoard.setName(profile.getuName());
            requestForNoticeBoardMsg(noticeBoard, false);
        }

        return true;
    }

    @Override
    public void onClusterItemInfoWindowClick(Profile profile) {
        // Does nothing, but you could go into the user's profile page, for example.

    }


    ProgressDialog mProgressDialog ;



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 20){
            Uri resultData = Uri.parse(data.getStringExtra("result"));
            ImageSearchRequest searchRequest = new ImageSearchRequest(getContext(),new File(resultData.getPath()),this);
            searchRequest.executeRequest();
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(getString(R.string.please_wait_msg));
            mProgressDialog.show();
        }
    }


    @Override
    public void ImageSearchResponse(CommonRequest.ResponseCode res, Profile uProfile, String errorMsg) {
        mProgressDialog.dismiss();
        switch (res) {
            case COMMON_RES_SUCCESS:
                ArrayList<Integer> indexs = new ArrayList<>();
                for(Profile p : profileList){
                    if(p.getuId() != null && p.getuId().contentEquals(uProfile.getuId())){
                        indexs.add(profileList.indexOf(p));
                        if (indexs.size() > 0) {
                            addMarkerByProfile(true, indexs);
                        }
                        break;
                    }
                    //something here
                }
                break;
            case COMMON_RES_CONNECTION_TIMEOUT:
                break;
            case COMMON_RES_FAILED_TO_CONNECT:
                Toast.makeText(getContext(),R.string.no_internet_msg, Toast.LENGTH_SHORT).show();
                break;
            case COMMON_RES_INTERNAL_ERROR:
                break;
            case COMMON_RES_SERVER_ERROR_WITH_MESSAGE:
                Toast.makeText(getContext(), "No data found for this image :(", Toast.LENGTH_SHORT).show();
                addMarkerByProfile(false, null);
                break;
        }
    }

    /**
     * for testing only
     * generate random location
     */
    private Random mRandom = new Random(1984);
    private LatLng position() {
        return new LatLng(random(28.545623, 28.28494009999999), random(77.330507, 76.3514683));
    }

    private double random(double min, double max) {
        return mRandom.nextDouble() * (max - min) + min;
    }

    int onlyOneTime = 0;
    View.OnKeyListener onKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction()!= KeyEvent.ACTION_DOWN && (uDetailLayout.getVisibility() == View.VISIBLE || isSelectedFilterButton)) {

                uDetailLayout.setVisibility(View.GONE);
                initFilterButtonSelection();
                addMarkerByProfile(false, null);
                searchBoxView.clearFocus();
                return true;
            }else {
                if (onlyOneTime == 0 && event.getAction()!= KeyEvent.ACTION_DOWN) {
                    closeAppSnackbar = Snackbar.make(getView(), R.string.close_app_msg, Snackbar.LENGTH_LONG);
                    closeAppSnackbar.show();
                    closeAppSnackbar.addCallback(snackbarBaseCallback);
                    searchBoxView.clearFocus();
                    onlyOneTime++;
                    return true;
                }
            }
            return false;
        }
    };

    BaseTransientBottomBar.BaseCallback<Snackbar> snackbarBaseCallback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            super.onDismissed(transientBottomBar, event);
            onlyOneTime = 0;
        }
    };



    class DialogNoticeBoardMessageAdapter extends RecyclerView.Adapter<DialogNoticeBoardMessageAdapter.ViewHolder>{
        private Context mContext;
        private NoticeBoard mNoticeBoard;

        public DialogNoticeBoardMessageAdapter(Context mContext, NoticeBoard mNoticeBoard) {
            this.mContext = mContext;
            this.mNoticeBoard = mNoticeBoard;
        }

        @Override
        public DialogNoticeBoardMessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.noticeboard_message_card, parent, false);
            return new DialogNoticeBoardMessageAdapter.ViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(DialogNoticeBoardMessageAdapter.ViewHolder holder, final int position) {
            final NoticeBoardMessage noticeBoardMessage = mNoticeBoard.getMessagesList().get(position);
            holder.noticeMessage.setText(noticeBoardMessage.getMsg());
            holder.timestamp.setText(utility.getTimeAndDate(noticeBoardMessage.getTimestamp()));

            if (HomeActivity.mUserId!=null && HomeActivity.mUserId.equals(mNoticeBoard.getAdminId())) {
                holder.deleteImageView.setVisibility(View.VISIBLE);
            }

            holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*requestDeleteNoticeBoardMessage(noticeBoardMessage);
                    mNoticeBoard.getMessagesList().remove(position);
                    messageAdapter.notifyDataSetChanged();*/
                }
            });
        }

        @Override
        public int getItemCount() {
            return mNoticeBoard.getMessagesList().size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView noticeMessage;
            public TextView timestamp;
            public ImageView deleteImageView;
            public ViewHolder(View itemView) {
                super(itemView);
                noticeMessage = (TextView) itemView.findViewById(R.id.notice_Msg_TextView);
                timestamp = (TextView) itemView.findViewById(R.id.notice_Msg_time_TextView);
                deleteImageView = (ImageView) itemView.findViewById(R.id.msg_delete);
                deleteImageView.setVisibility(View.GONE);
            }
        }
    }


}
