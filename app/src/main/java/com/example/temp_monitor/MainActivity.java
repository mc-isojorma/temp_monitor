package com.example.temp_monitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity";
    //FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mConditionRef = mRootRef.child("Leppavaara/temp/cur_temp");
    DatabaseReference mConditionRef2 = mRootRef.child("Leppavaara/humi/cur_humi");
    TextView tvhumidity, tvhumi;
    Button bhistory;
    Intent historyintent, mapintent;
    ArrayList<String> mylist = new ArrayList<String>();
    MyListFragment myListFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvhumidity = (TextView) findViewById(R.id.tvhumidity);
        tvhumi = (TextView) findViewById(R.id.tvhumi);
        bhistory = (Button) findViewById(R.id.bhistory);
        myListFragment = (MyListFragment) getSupportFragmentManager().findFragmentById(R.id.lvfragment);
        getAllValues();
        historyintent = new Intent(getApplicationContext(),HistoryActivity.class);
        mapintent = new Intent(getApplicationContext(), MapsActivity.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Read from the database
        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
                tvhumidity.setText(value.toString()+" C°");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        mConditionRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
                tvhumi.setText(value.toString()+" %");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        bhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mapintent);
            }
        });
    }
    private void getAllValues(){
        DatabaseReference mRef = mRootRef.child("Leppavaara/temperature");
        mRef.orderByChild("date").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Weather weather = dataSnapshot.getValue(Weather.class);
                mylist.add(weather.date);
                Log.d(TAG," date " + weather.date + " time " +weather.time+ " value "+weather.value);
                buildIntent();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

    }
    private void buildIntent(){
        Log.d(TAG, "mylist size"+ mylist.size());
        // removing duplicates from the list
        Set<String> hs = new LinkedHashSet<>();
        hs.addAll(mylist);
        Log.d(TAG, "hs = " + hs.toString());
        mylist.clear();
        mylist.addAll(hs);
        historyintent.putStringArrayListExtra("mylist", mylist);
        myListFragment.getList(mylist);
    }
}
