package com.pabitrarista.chatdialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pabitrarista.chatdialog.recyclerview.XpertViewAdapter;

import java.util.ArrayList;

public class XpertListActivity extends AppCompatActivity {

    RecyclerView xpertRecyclerView;
    ArrayList<String> xpertName;
    ArrayList<String> xpertImage;
    XpertViewAdapter xpertViewAdapter;

    FirebaseFirestore db;
    private static final String XPERT_MASTER_KEY = "xpert_master";
    private static final String NAME_KEY = "name";
    private static final String PROFILE_IMAGE_KEY = "profile_image";
    Query xpertDetails;

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
        xpertName = new ArrayList<>();
        xpertImage = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
    }

    private void setXpertList() {
        xpertDetails = db.collection(XPERT_MASTER_KEY);

        xpertDetails.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String name, image;
                    xpertName.clear();
                    xpertImage.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        name = document.getString(NAME_KEY);
                        image = document.getString(PROFILE_IMAGE_KEY);

                        xpertName.add(name);
                        xpertImage.add(image);
                    }

                    xpertViewAdapter = new XpertViewAdapter(XpertListActivity.this, xpertName, xpertImage);
                    xpertRecyclerView.setAdapter(xpertViewAdapter);
                }
            }
        });
    }

    public void showXpertChat(String xpertName, String xpertImage) {
        Intent in = new Intent(XpertListActivity.this, MainActivity.class);
        in.putExtra("xpertName", xpertName);
        in.putExtra("xpertImage", xpertImage);
        startActivity(in);
    }
}
