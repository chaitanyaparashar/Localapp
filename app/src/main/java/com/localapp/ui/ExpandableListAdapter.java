package com.localapp.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.localapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 4 way on 15-04-2017.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private HashMap<String, List<String>> listDataChild;
    private HashMap<String, List<Integer>> listDrawableChild;
    private ArrayList<String> listDataHeader;

    // Hashmap for keeping track of our checkbox check states
    private HashMap<Integer, boolean[]> mChildCheckStates;

    // Our getChildView & getGroupView use the viewholder patter
    // Here are the viewholders defined, the inner classes are
    // at the bottom
    private ChildViewHolder childViewHolder;
    private GroupViewHolder groupViewHolder;

    /*
         *  For the purpose of this document, I'm only using a single
    *	textview in the group (parent) and child, but you're limited only
    *	by your XML view for each group item :)
   */
    private String groupText;
    private String childText;
    private Integer childDrawable;


    public ExpandableListAdapter(Context mContext) {
        this.mContext = mContext;

        prepareListData();

        // Initialize our hashmap containing our check states here
        mChildCheckStates = new HashMap<Integer, boolean[]>();
    }

    public int getNumberOfCheckedItemsInGroup(int mGroupPosition)
    {
        boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
        int count = 0;
        if(getChecked != null) {
            for (int j = 0; j < getChecked.length; ++j) {
                if (getChecked[j] == true) count++;
            }
        }
        return  count;
    }

    public String getItemAtPostion (int mGroupPosition) {
        boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
        String iteam = "";
        if(getChecked != null) {
            for (int j = 0; j < getChecked.length; ++j) {
                if (getChecked[j])
                    iteam = iteam+ (String) getChild(mGroupPosition, j)+",";
            }
        }

        return iteam;
//
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listDataChild.get(listDataHeader.get(groupPosition)).size();
    }

    /*
    * This defaults to "public object getGroup" if you auto import the methods
    * I've always make a point to change it from "object" to whatever item
    * I passed through the constructor
   */
    @Override
    public Object getGroup(int groupPosition) {
        return  listDataHeader.get(groupPosition);
    }

    /*
     * This defaults to "public object getChild" if you auto import the methods
     * I've always make a point to change it from "object" to whatever item
     * I passed through the constructor
    */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
    }


    public Object getChildDrawable(int groupPosition, int childPosition) {
        try {
            return listDrawableChild.get(listDataHeader.get(groupPosition)).get(childPosition);
        }catch (Exception e){
            return R.mipmap.ic_launcher;
        }

    }



    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        //  I passed a text string into an activity holding a getter/setter
        //  which I passed in through "ExpListGroupItems".
        //  Here is where I call the getter to get that text
        groupText = (String) getGroup(groupPosition);

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.profession_list_group, null);

            // Initialize the GroupViewHolder defined at the bottom of this document
            groupViewHolder = new GroupViewHolder();

            groupViewHolder.mGroupText = (TextView) convertView.findViewById(R.id.lblListHeader);

            convertView.setTag(groupViewHolder);
        } else {

            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder.mGroupText.setText(groupText);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final int mGroupPosition = groupPosition;
        final int mChildPosition = childPosition;

        //  I passed a text string into an activity holding a getter/setter
        //  which I passed in through "ExpListChildItems".
        //  Here is where I call the getter to get that text
        childText = (String) getChild(mGroupPosition, mChildPosition);
        childDrawable = (Integer) getChildDrawable(mGroupPosition, mChildPosition);

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.profession_list_items, null);

            childViewHolder = new ChildViewHolder();

            childViewHolder.mChildImageView = (ImageView) convertView
                    .findViewById(R.id.list_image);

            childViewHolder.mChildText = (TextView) convertView
                    .findViewById(R.id.lblListItem);

            childViewHolder.mCheckBox = (CheckBox) convertView
                    .findViewById(R.id.lstcheckBox);

            convertView.setTag(R.layout.profession_list_items, childViewHolder);

        } else {

            childViewHolder = (ChildViewHolder) convertView
                    .getTag(R.layout.profession_list_items);
        }

        childViewHolder.mChildText.setText(childText);
        childViewHolder.mChildImageView.setImageResource(childDrawable);

		/*
		 * You have to set the onCheckChangedListener to null
		 * before restoring check states because each call to
		 * "setChecked" is accompanied by a call to the
		 * onCheckChangedListener
		*/
        childViewHolder.mCheckBox.setOnCheckedChangeListener(null);

        if (mChildCheckStates.containsKey(mGroupPosition)) {
			/*
			 * if the hashmap mChildCheckStates<Integer, Boolean[]> contains
			 * the value of the parent view (group) of this child (aka, the key),
			 * then retrive the boolean array getChecked[]
			*/
            boolean getChecked[] = mChildCheckStates.get(mGroupPosition);

            // set the check state of this position's checkbox based on the
            // boolean value of getChecked[position]
            childViewHolder.mCheckBox.setChecked(getChecked[mChildPosition]);

        } else {

			/*
			 * if the hashmap mChildCheckStates<Integer, Boolean[]> does not
			 * contain the value of the parent view (group) of this child (aka, the key),
			 * (aka, the key), then initialize getChecked[] as a new boolean array
			 *  and set it's size to the total number of children associated with
			 *  the parent group
			*/
            boolean getChecked[] = new boolean[getChildrenCount(mGroupPosition)];

            // add getChecked[] to the mChildCheckStates hashmap using mGroupPosition as the key
            mChildCheckStates.put(mGroupPosition, getChecked);

            // set the check state of this position's checkbox based on the
            // boolean value of getChecked[position]
            childViewHolder.mCheckBox.setChecked(false);
        }

        childViewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                    getChecked[mChildPosition] = isChecked;
                    mChildCheckStates.put(mGroupPosition, getChecked);

                } else {

                    boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                    getChecked[mChildPosition] = isChecked;
                    mChildCheckStates.put(mGroupPosition, getChecked);
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


    public final class GroupViewHolder {

        TextView mGroupText;
    }

    public final class ChildViewHolder {

        ImageView mChildImageView;
        TextView mChildText;
        CheckBox mCheckBox;
    }


    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        listDrawableChild = new HashMap<>();

        // Adding child data
        listDataHeader.add("STUDENT");
        listDataHeader.add("PROFESSIONALS");
        listDataHeader.add("SKILLS");
        listDataHeader.add("HEALTH AND WELLNESS");
        listDataHeader.add("REPAIR AND MAINTENANCE");
        listDataHeader.add("WEDDING & EVENTS");
        listDataHeader.add("BEAUTY");
        listDataHeader.add("HOUSEWIFE");



        // Adding child data
        List<String> student = new ArrayList<String>();
        student.add("Student");

        List<Integer> studentIcon = new ArrayList<>();
        studentIcon.add(R.drawable.ic_student);


        List<String> housewife = new ArrayList<String>();
        housewife.add("Housewife");


        List<String> health = new ArrayList<String>();
        health.add("Dietician");
        health.add("Fitness Trainer");
        health.add("Nurse");
        health.add("Physiotherapy");
        health.add("Yoga Trainer");


        List<String> repair = new ArrayList<String>();
        repair.add("AC Repair");
        repair.add("Carpenter");
        repair.add("Construction & Repair");
        repair.add("Electrician");
        repair.add("House Painter");
        repair.add("Laptop Repair");
        repair.add("Loundry");
        repair.add("Plumber");

        List<String> professionals = new ArrayList<String>();
        professionals.add("Web Designer");
        professionals.add("Social Marketing");
        professionals.add("Lawyer");
        professionals.add("Real Estate");
        professionals.add("Insurance Agent");
        professionals.add("CCTV Camera Installation");
        professionals.add("CA");
        professionals.add("Finance");
        professionals.add("Operations");
        professionals.add("Software Engineer");
        professionals.add("Engineer");
        professionals.add("Sales Professionals");
        professionals.add("Writer");
        professionals.add("Interior Designer");
        professionals.add("Graphic Designer");
        professionals.add("Administrator");
        professionals.add("Human Resource");
        professionals.add("Security Guard");
        professionals.add("Driver");
        professionals.add("Doctor");
        professionals.add("Adviser");
        professionals.add("Architect");
        professionals.add("Marketeer");

        List<Integer> professionalsIcons = new ArrayList<>();


        List<String> wedding = new ArrayList<String>();
        wedding.add("Decor");
        wedding.add("DJ");
        wedding.add("Corporate Event Planer");
        wedding.add("Bartender");
        wedding.add("Photograph");
        wedding.add("Musician");
        List<String> skills = new ArrayList<String>();
        skills.add("Salsa");
        skills.add("Drum");
        skills.add("Keyboard Lesson");
        skills.add("Guitar");
        skills.add("Zumba");

        List<String> beauty = new ArrayList<String>();
        beauty.add("Saloon");
        beauty.add("Makeup Artist");
        beauty.add("Spa");

        listDataChild.put(listDataHeader.get(0), student); // Header, Child data
        listDataChild.put(listDataHeader.get(1), professionals);
        listDataChild.put(listDataHeader.get(2), skills);
        listDataChild.put(listDataHeader.get(3), health);
        listDataChild.put(listDataHeader.get(4), repair);
        listDataChild.put(listDataHeader.get(5), wedding);
        listDataChild.put(listDataHeader.get(6), beauty);
        listDataChild.put(listDataHeader.get(7), housewife);

        listDrawableChild.put(listDataHeader.get(0), studentIcon);
    }
}
