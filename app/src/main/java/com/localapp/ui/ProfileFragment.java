package com.localapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.localapp.R;
import com.localapp.appcontroller.AppController;
import com.localapp.data.Profile;
import com.squareup.picasso.Picasso;


/**
 * Created by 4 way on 02-03-2017.
 */

public class ProfileFragment extends Fragment {
    private CircularImageView userPic;
    private ImageButton camButton;
    private ImageView editPersonal,editAbout;
    private TextView uNmaeTextView,uNumberTextView,
            uEmailTextView,uProdessionTextView,uBreifInfo,
            uDetailTextView;



    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        setupView(view);



        return view;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible && getContext()!=null) {
//            startActivityForResult(new Intent(getContext(),SignUpActivity.class),0);
        }else {
            Log.v("ttttt","notVisible");
        }

    }

    public void setupView(View view) {
        userPic = (CircularImageView) view.findViewById(R.id.image_pic);
        camButton = (ImageButton) view.findViewById(R.id.cam_btn);
        editPersonal = (ImageView) view.findViewById(R.id.edit_personal_info);
        editAbout = (ImageView) view.findViewById(R.id.edit_about);
        uNmaeTextView = (TextView) view.findViewById(R.id._name);
        uNumberTextView = (TextView) view.findViewById(R.id._phone);
        uEmailTextView = (TextView) view.findViewById(R.id._email);
        uProdessionTextView = (TextView) view.findViewById(R.id._profession);
        uBreifInfo = (TextView) view.findViewById(R.id._brief_intro);
        uDetailTextView = (TextView) view.findViewById(R.id._details);
    }

    public void setProfileData(Profile profile) {
        Picasso.with(AppController.getAppContext()).load(profile.getuPictureURL());
        uNmaeTextView.setText(profile.getuName());
        uNumberTextView.setText(profile.getuMobile());
        uEmailTextView.setText(profile.getuEmail());
        uProdessionTextView.setText(profile.getProfession());
        uBreifInfo.setText(profile.getuSpeciality());
        uDetailTextView.setText(profile.getuNotes());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        HomeActivity.mViewPager.setCurrentItem(0);
    }
}
