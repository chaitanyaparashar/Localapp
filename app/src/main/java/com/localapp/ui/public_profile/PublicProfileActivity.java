package com.localapp.ui.public_profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.localapp.R;
import com.localapp.appcontroller.AppController;
import com.localapp.data.Profile;
import com.localapp.request.CommonRequest;
import com.localapp.request.GetProfileByIdRequest;
import com.localapp.ui.HomeActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.localapp.util.ColorUtils.getDominantColor1;
import static com.localapp.util.utility.calcDistance;

public class PublicProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, GetProfileByIdRequest.GetProfileByIdRequestCallback, Target{
    private static String TAG = "PublicProfileActivity";

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Bind(R.id.toolbar_header_view)
    protected HeaderView toolbarHeaderView;

    @Bind(R.id.float_header_view)
    protected HeaderView floatHeaderView;

    @Bind(R.id.appbar)
    protected AppBarLayout appBarLayout;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.image)
    protected ImageView mProfileImageView;

    @Bind(R.id._brief_intro)
    protected TextView mSpeciality;

    @Bind(R.id._details)
    protected TextView mNotes;

    @Bind(R.id._profession)
    protected TextView mProfession;

    @Bind(R.id._mobile)
    protected TextView mMobile;

    @Bind(R.id._email)
    protected TextView mEmail;

    @Bind(R.id.card_brief_intro)
    protected CardView cardIntro;

    @Bind(R.id.card_details)
    protected CardView cardDetails;

    @Bind(R.id.card_profession)
    protected CardView cardProfession;

    @Bind(R.id.mobile_card)
    protected CardView mobileCardView;

    private boolean isHideToolbarView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);


        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initUi();

        String uid = getIntent().getStringExtra("action_id");

        if (uid != null)
        profileRequest(uid);

//        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.colorAccent));


    }

    private void initUi() {
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }

    private void profileRequest(String profileID) {
        Profile mProfile = new Profile(profileID);

        GetProfileByIdRequest request = new GetProfileByIdRequest(this, mProfile, this);
        request.executeRequest();
    }

    @Override
    public void onProfileIdResponse(CommonRequest.ResponseCode responseCode, Profile mProfile) {
        if (responseCode == CommonRequest.ResponseCode.COMMON_RES_SUCCESS) {
            setProfileData(mProfile);
        }
    }


    private void setProfileData(Profile mProfile){
        String uName = mProfile.getuName();
        String uEmail = mProfile.getuEmail();
        String uMobile = mProfile.getuMobile();
        String uPictureURL = mProfile.getuPictureURL();
        String uSpeciality = mProfile.getuSpeciality();
        String uNotes = mProfile.getuNotes();
        String uPrivacy = mProfile.getuPrivacy();
        String profession = mProfile.getProfession();
        LatLng mLatLng = mProfile.getuLatLng();
        String distance = calcDistance(HomeActivity.mLastKnownLocation,mLatLng,"km",false);

        Picasso.with(AppController.getAppContext()).load(uPictureURL).placeholder(R.drawable.ic_user).into(mProfileImageView);
        Picasso.with(AppController.getAppContext()).load(uPictureURL).placeholder(R.drawable.ic_user).into(this);



        if (distance != null) {
            toolbarHeaderView.bindTo(uName, distance + " km");
            floatHeaderView.bindTo(uName, distance + " km");
        }else {
            toolbarHeaderView.bindTo(uName, "");
            floatHeaderView.bindTo(uName, "");
        }

        if (uSpeciality != null && !uSpeciality.equals("null") && !uSpeciality.isEmpty()) {
            mSpeciality.setText(uSpeciality);
        }else {
            cardIntro.setVisibility(View.GONE);
        }

        if (uNotes != null && !uNotes.equals("null") && !uNotes.isEmpty()) {
            mNotes.setText(uNotes);
        }else {
            cardDetails.setVisibility(View.GONE);
        }

        if (profession != null && !profession.equals("null") && !profession.isEmpty()){
            mProfession.setText(profession);
        }else {
            cardProfession.setVisibility(View.GONE);
        }

        if (uMobile != null && !uMobile.equals("null" ) && !uMobile.isEmpty() && !uPrivacy.equals("1")) {
            mMobile.setText(uMobile);
        }else {
            mobileCardView.setVisibility(View.GONE);
        }

        mEmail.setText(uEmail);



    }




    private void setPallet(final Bitmap bitmap) {
        Palette.from(bitmap)
                .generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch textSwatch = palette.getVibrantSwatch();
                        if (textSwatch != null) {
                            collapsingToolbarLayout.setContentScrimColor(textSwatch.getRgb());
                            Log.d(TAG,"rgb set");
                        }else {
                            Log.d(TAG,"rgb null");
                            new DoInBackground().execute(bitmap);
                        }

                        Palette.Swatch textSwatch1 = palette.getDarkVibrantSwatch();

                        if (textSwatch1 != null) {
                            setStatuseBarColor(textSwatch1.getRgb());
                            Log.d(TAG,"rgb set");
                        }/*else {
                            Log.d(TAG,"rgb null");
                            new DoInBackground().execute(bitmap);
                        }*/

                    }


                });
    }

    private void setStatuseBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }



    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        Log.d(TAG,"onBitmapLoaded");
        setPallet(bitmap);
    }


    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        Log.d(TAG,"onBitmapFailed");
    }


    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }


    private class DoInBackground extends AsyncTask<Bitmap,Void,Integer> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Integer doInBackground(Bitmap... params) {

            return getDominantColor1(params[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            collapsingToolbarLayout.setContentScrimColor(integer);
            Log.d(TAG,"Dominant set");
        }
    }



}
