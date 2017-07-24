package com.localapp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.localapp.R;
import com.localapp.utils.Constants;

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
    public List<String> strings = new ArrayList<>();




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

        // Adding header data
        listDataHeader.add(Constants.PROFESSION_GROUP_STUDENT);
        listDataHeader.add(Constants.PROFESSION_GROUP_PROFESSIONALS);
        listDataHeader.add(Constants.PROFESSION_GROUP_SKILLS);
        listDataHeader.add(Constants.PROFESSION_GROUP_HEALTH);
        listDataHeader.add(Constants.PROFESSION_GROUP_REPAIR);
        listDataHeader.add(Constants.PROFESSION_GROUP_WEDDING);
        listDataHeader.add(Constants.PROFESSION_GROUP_BEAUTY);
        listDataHeader.add(Constants.PROFESSION_GROUP_HOUSEWIFE);



        // Adding child data


        List<Integer> housewifeIcon = new ArrayList<>();
        housewifeIcon.add(R.drawable.ic_housewifes);

        List<Integer> studentIcon = new ArrayList<>();
        studentIcon.add(R.drawable.ic_student);

        List<Integer> healhIcons = new ArrayList<>();
        healhIcons.add(R.drawable.ic_dietician);
        healhIcons.add(R.drawable.ic_fitness_trainer);
        healhIcons.add(R.drawable.ic_nurse);
        healhIcons.add(R.drawable.ic_physiotharepy);
        healhIcons.add(R.drawable.ic_yoga_trainer);


        List<Integer> repairIcons = new ArrayList<>();
        repairIcons.add(R.drawable.ic_ac_repaire);
        repairIcons.add(R.drawable.ic_carpenter);
        repairIcons.add(R.drawable.ic_construction_epair);
        repairIcons.add(R.drawable.ic_electrician);
        repairIcons.add(R.drawable.ic_house_painter);
        repairIcons.add(R.drawable.ic_laptop_repair);
        repairIcons.add(R.drawable.ic_loundry);
        repairIcons.add(R.drawable.ic_plumber);


        List<Integer> professionalsIcons = new ArrayList<>();
        professionalsIcons.add(R.drawable.ic_web_designer);
        professionalsIcons.add(R.drawable.ic_social_marketing);
        professionalsIcons.add(R.drawable.ic_lawyer);
        professionalsIcons.add(R.drawable.ic_realestate);
        professionalsIcons.add(R.drawable.ic_insurance_agent);
        professionalsIcons.add(R.drawable.ic_cctv);
        professionalsIcons.add(R.drawable.ic_ca);
        professionalsIcons.add(R.drawable.ic_graph);
        professionalsIcons.add(R.drawable.ic_operator);
        professionalsIcons.add(R.drawable.ic_software_engineer);
        professionalsIcons.add(R.drawable.ic_engineer);
        professionalsIcons.add(R.drawable.ic_sales_professional);
        professionalsIcons.add(R.drawable.ic_writer);
        professionalsIcons.add(R.drawable.ic_interior_design);
        professionalsIcons.add(R.drawable.ic_graphic_designer);
        professionalsIcons.add(R.drawable.ic_adminstrator);
        professionalsIcons.add(R.drawable.ic_hr);
        professionalsIcons.add(R.drawable.ic_security);
        professionalsIcons.add(R.drawable.ic_driver);
        professionalsIcons.add(R.drawable.ic_doctor);
        professionalsIcons.add(R.drawable.ic_advisor);
        professionalsIcons.add(R.drawable.ic_architect);
        professionalsIcons.add(R.drawable.ic_marketeer);


        List<Integer> weddingIcon = new ArrayList<>();
        weddingIcon.add(R.drawable.ic_decor);
        weddingIcon.add(R.drawable.ic_dj);
        weddingIcon.add(R.drawable.ic_cep);
        weddingIcon.add(R.drawable.ic_bartender);
        weddingIcon.add(R.drawable.ic_photograph);
        weddingIcon.add(R.drawable.ic_musician);



        List<Integer> skillsIcon = new ArrayList<>();
        skillsIcon.add(R.drawable.ic_salsa);
        skillsIcon.add(R.drawable.ic_drum);
        skillsIcon.add(R.drawable.ic_keyboard_lessons);
        skillsIcon.add(R.drawable.ic_guitar);
        skillsIcon.add(R.drawable.ic_zumba);


        List<Integer> beautyIcon = new ArrayList<>();
        beautyIcon.add(R.drawable.ic_saloon);
        beautyIcon.add(R.drawable.ic_makeupartist);
        beautyIcon.add(R.drawable.ic_spa);

        listDataChild.put(listDataHeader.get(0), Constants.PROFESSION_GROUP_STUDENT_LIST); // Header, Child data
        listDataChild.put(listDataHeader.get(1), Constants.PROFESSION_GROUP_PROFESSIONALS_LIST);
        listDataChild.put(listDataHeader.get(2), Constants.PROFESSION_GROUP_SKILLS_LIST);
        listDataChild.put(listDataHeader.get(3), Constants.PROFESSION_GROUP_HEALTH_LIST);
        listDataChild.put(listDataHeader.get(4), Constants.PROFESSION_GROUP_REPAIR_LIST);
        listDataChild.put(listDataHeader.get(5), Constants.PROFESSION_GROUP_WEDDING_LIST);
        listDataChild.put(listDataHeader.get(6), Constants.PROFESSION_GROUP_BEAUTY_LIST);
        listDataChild.put(listDataHeader.get(7), Constants.PROFESSION_GROUP_HOUSEWIFE_LIST);

        listDrawableChild.put(listDataHeader.get(0), studentIcon);
        listDrawableChild.put(listDataHeader.get(1), professionalsIcons);
        listDrawableChild.put(listDataHeader.get(2), skillsIcon);
        listDrawableChild.put(listDataHeader.get(3), healhIcons);
        listDrawableChild.put(listDataHeader.get(4), repairIcons);
        listDrawableChild.put(listDataHeader.get(5), weddingIcon);
        listDrawableChild.put(listDataHeader.get(6), beautyIcon);
        listDrawableChild.put(listDataHeader.get(7), housewifeIcon);
    }













}
