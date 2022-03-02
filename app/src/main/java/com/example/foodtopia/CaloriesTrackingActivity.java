package com.example.foodtopia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.foodtopia.Model.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CaloriesTrackingActivity extends AppCompatActivity {

    BarChart chart;
    FloatingActionButton back_btn;
    FirebaseAuth fAuth;
    DatabaseReference reference;
    Query allRecordFromUser;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private final String TAG = "test";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calories_tracking);

        // back button
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // chart
        chart = findViewById(R.id.chart);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Query query = FirebaseDatabase.getInstance().getReference("Diets")
                .orderByChild("userid").equalTo(user.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HashMap<String, List<String>> map = new HashMap<>();
                for (DataSnapshot record : snapshot.getChildren()) {
//                    System.out.println(record.child("date").getValue().toString());
                    List<String> alternateList = map.get(record.child("date").getValue().toString());
                    if (alternateList == null) {
                        alternateList = new ArrayList<>();
                        map.put(record.child("date").getValue().toString(), alternateList);
                    }
                    alternateList.add(record.child("calories").getValue().toString());
                }
//                for(Map.Entry<String, List<String>> alternateEntry : map.entrySet()) {
//                    System.out.println(alternateEntry.getKey() + ": " +
//                            alternateEntry.getValue().toString());
//                }
                HashMap<String, Float> map2 = new HashMap<>();
                for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                    String key = entry.getKey();
                    List<String> value = entry.getValue();
                    float td_calories_tt = 0;
                    for (String aString : value) {
                        System.out.println("key : " + key + " value : " + aString);
                        td_calories_tt += Float.parseFloat(aString);
                    }
                    map2.put(key, td_calories_tt);
                }
                Map<String, Float> treeMap = new TreeMap(map2);
                for (Map.Entry<String, Float> entry : treeMap.entrySet()) {
                    System.out.println(entry.getKey() + "/" + entry.getValue());
                }
//
//                for (String str : treeMap.keySet()) {
//                    System.out.println(str);
//                }
////
//                for (Map.Entry<String, Float> entry : map2.entrySet()) {
//                    System.out.println(entry.getKey() + "/" + entry.getValue());
//                }

                ArrayList<Float> value = new ArrayList<Float>();
                ArrayList<BarEntry> entries = new ArrayList<>();
                String title = "Calories";



                int a=300;
                for (Map.Entry<String, Float> entry : treeMap.entrySet()) {
                        value.add(entry.getValue());
                        BarEntry barEntry = new BarEntry(a++, entry.getValue());
                        entries.add(barEntry);
                }
                BarDataSet barDataSet = new BarDataSet(entries, title);
                barDataSet.setColors(ColorTemplate.getHoloBlue());
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(16f);

                BarData data = new BarData(barDataSet);
                chart.setFitBars(true);
                chart.setData(data);
                chart.invalidate();
                chart.animateY(2000);


//                ArrayList<Double> value = new ArrayList<Double>();
//                ArrayList<BarEntry> entries = new ArrayList<>();
//                String title = "Title";
//
//                for (int i=0; i<6; i++) {
//                    value.add(i * 100.1);
//                }
//
//                for (int i=0; i<value.size(); i++) {
//                    BarEntry barEntry = new BarEntry(i+5, value.get(i).floatValue());
//                    entries.add(barEntry);
//                }
//
//                BarDataSet barDataSet = new BarDataSet(entries, title);
//                barDataSet.setColors(ColorTemplate.getHoloBlue());
//                barDataSet.setValueTextColor(Color.BLACK);
//                barDataSet.setValueTextSize(16f);
//
//                BarData data = new BarData(barDataSet);
//                chart.setFitBars(true);
//                chart.setData(data);
//                chart.invalidate();
//                chart.animateY(2000);


                ////---------------ver2
//                HashMap<String,List<String>> map=new HashMap<>();
//                for (DataSnapshot record : snapshot.getChildren()) {
////                    System.out.println(record.child("date").getValue().toString());
//                    List<String> alternateList = map.get(record.child("date").getValue().toString());
//                    if(alternateList==null) {
//                        alternateList=new ArrayList<>();
//                        map.put(record.child("date").getValue().toString(),alternateList);
//                    }
//                    alternateList.add(record.child("calories").getValue().toString());
//                }
//                for(Map.Entry<String, List<String>> alternateEntry : map.entrySet()) {
//                    System.out.println(alternateEntry.getKey() + ": " +
//                            alternateEntry.getValue().toString());
//                }
////---------------ver1
                //                for (DataSnapshot record : snapshot.getChildren()) {
//                    System.out.println(record.child("date").getValue().toString());
//                    List<String> name=new ArrayList<>();
//                    if(!map.containsKey(record.child("date").getValue().toString())){
//                        ArrayList<String> name=new ArrayList<>();
//                        name.add(record.child("calories").getValue().toString());
//                        map.put(record.child("date").getValue().toString(),name);
//                    }else{
//                        map.put(record.child("date").getValue().toString(),
//                                map.get(record.child("date").getValue().
//                                toString()));
//                    }
//                    //System.out.println(record.child("calories")+" "+record.child("userid")+" "+record.child("userid_date"));;
//                }
//                for (Map.Entry<String, ArrayList> entry : map.entrySet()) {
//                    System.out.println("555");
//                    System.out.println(entry.getKey() + "/" + entry.getValue());
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        // get user id
//        fAuth = FirebaseAuth.getInstance();
//        FirebaseUser firebaseUser = fAuth.getCurrentUser();
//        String userID = firebaseUser.getUid();
//
//        reference = FirebaseDatabase.getInstance().getReference("Diets");
//        allRecordFromUser = reference.orderByChild("userid").equalTo(userID);
//
//
//
//
//        // get user data
//        allRecordFromUser.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                float total = 0;
//                for (DataSnapshot record : snapshot.getChildren()) {
////                    String calories = record.child("calories").getValue(String.class);
//                    float calories = Float.valueOf(record.child("calories").getValue(String.class));
//                    total += calories;
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        ArrayList<Double> value = new ArrayList<Double>();
//        ArrayList<BarEntry> entries = new ArrayList<>();
//        String title = "Title";
//
//        for (int i=0; i<10; i++) {
//            value.add(i * 100.1);
//        }
//
//        for (int i=0; i<10; i++) {
//            BarEntry barEntry = new BarEntry(i, value.get(i).floatValue());
//            entries.add(barEntry);
//        }
//
//        BarDataSet barDataSet = new BarDataSet(entries, title);
//        barDataSet.setColors(ColorTemplate.getHoloBlue());
//        barDataSet.setValueTextColor(Color.BLACK);
//        barDataSet.setValueTextSize(16f);
//
//
//        BarData data = new BarData(barDataSet);
//
//        chart.setFitBars(true);
//        chart.setData(data);
//        chart.invalidate();
//        chart.animateY(2000);
    }
}