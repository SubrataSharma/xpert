package com.pabitrarista.chatdialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.pabitrarista.chatdialog.recyclerview.XpertViewAdapter;
import com.pabitrarista.chatdialog.recyclerview.XpertViewData;

import java.util.ArrayList;

public class XpertListActivity extends AppCompatActivity {

    RecyclerView xpertRecyclerView;
    final ArrayList<XpertViewData> xpertViewDataArrayList = new ArrayList<>();
    XpertViewAdapter xpertViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xpert_list);

        init();

        setXpertList();
    }

    private void init() {
        xpertRecyclerView = findViewById(R.id.xpert_list_recycler_view);
        xpertRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        xpertViewAdapter = new XpertViewAdapter(XpertListActivity.this, xpertViewDataArrayList);
        xpertRecyclerView.setAdapter(xpertViewAdapter);
    }

    private void setXpertList() {
        XpertViewData xpertViewData = new XpertViewData("Mr. Premji", "");
        xpertViewDataArrayList.add(xpertViewData);

        xpertViewData = new XpertViewData("Shah Rukh", "");
        xpertViewDataArrayList.add(xpertViewData);

        xpertViewData = new XpertViewData("Mr. Bachchan", "");
        xpertViewDataArrayList.add(xpertViewData);

        xpertViewData = new XpertViewData("Imran", "");
        xpertViewDataArrayList.add(xpertViewData);

        xpertViewData = new XpertViewData("Virat", "");
        xpertViewDataArrayList.add(xpertViewData);

        xpertViewData = new XpertViewData("Mr. Rushdie", "");
        xpertViewDataArrayList.add(xpertViewData);

        xpertViewData = new XpertViewData("Rahul Gandhi", "");
        xpertViewDataArrayList.add(xpertViewData);

        xpertViewData = new XpertViewData("Virat", "");
        xpertViewDataArrayList.add(xpertViewData);

        xpertViewData = new XpertViewData("Mr. Rushdie", "");
        xpertViewDataArrayList.add(xpertViewData);

        xpertViewData = new XpertViewData("Rahul Gandhi", "");
        xpertViewDataArrayList.add(xpertViewData);
    }

    public void showXpertChat() {
        Intent in = new Intent(XpertListActivity.this, MainActivity.class);
        startActivity(in);
    }
}
