package com.example.birtdayapp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import adapter.adapter;
import helper.dbHelper;
import helper.modelHelper;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private adapter contactAdapter;
    private ArrayList<modelHelper> contactArrayList;
    private TextView tvHidden;
    private dbHelper dbHelper;

    //gweh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rview);
//        tvHidden = findViewById(R.id.tv_hidden);
        contactAdapter = new adapter(this);
        dbHelper = new dbHelper(this);
        contactArrayList = dbHelper.getAll();
        FloatingActionButton fab = findViewById(R.id.fab);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);

        contactAdapter.setContactArrayList(contactArrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(contactAdapter);





        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                startActivity(intent);
            }
        });
    }
}