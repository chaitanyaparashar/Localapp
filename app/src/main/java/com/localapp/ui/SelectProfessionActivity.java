package com.localapp.ui;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.localapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectProfessionActivity extends AppCompatActivity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    ArrayList<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    HashMap<String, List<Drawable>> listIconChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_profession);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
//        prepareListData();

        listAdapter = new ExpandableListAdapter(this);

        // setting list adapter
        expListView.setAdapter(listAdapter);


        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });




        /*button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int count =0;
                for(int mGroupPosition =0; mGroupPosition < listAdapter.getGroupCount(); mGroupPosition++)
                {
                    count = count +  listAdapter.getNumberOfCheckedItemsInGroup(mGroupPosition);

                }
                textView.setText(""+count);
            }
        });*/

      /*  button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String items = "";
                for(int mGroupPosition =0; mGroupPosition < listAdapter.getGroupCount(); mGroupPosition++)
                {
                    items = items +  listAdapter.getItemAtPostion(mGroupPosition);

                }
                textView.setText(""+items.substring(0,items.length()-1));
            }
        });
*/

    }


    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        listIconChild = new HashMap<String, List<Drawable>>();

        // Adding child data
        listDataHeader.add("STUDENT");
        listDataHeader.add("HOUSEWIFE");
        listDataHeader.add("HEALTH AND WELLNESS");
        listDataHeader.add("REPAIR AND MAINTENANCE");
        listDataHeader.add("PROFESSIONALS");
        listDataHeader.add("WEDDING & EVENTS");
        listDataHeader.add("SKILLS");
        listDataHeader.add("BEAUTY");

        // Adding child data
        List<String> student = new ArrayList<String>();
        student.add("Student");

        /*List<Drawable> studentIcon = new ArrayList<>();
        studentIcon.add(R.drawable.st)*/


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
        listDataChild.put(listDataHeader.get(1), housewife);
        listDataChild.put(listDataHeader.get(2), health);
        listDataChild.put(listDataHeader.get(3), repair);
        listDataChild.put(listDataHeader.get(4), professionals);
        listDataChild.put(listDataHeader.get(5), wedding);
        listDataChild.put(listDataHeader.get(6), skills);
        listDataChild.put(listDataHeader.get(7), beauty);
    }
}
