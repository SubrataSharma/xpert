package com.pabitrarista.chatdialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pabitrarista.chatdialog.recyclerview.BucketViewAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AskScreen2Activity extends AppCompatActivity implements View.OnClickListener {

    String option = null;
    private SharedPreferences preferences;

    FirebaseFirestore db;
    private static final String XPERT_MASTER_KEY = "xpert_master";
    private static final String A_R_RAHMAN_KEY = "a-r-rahman";
    private static final String SACHIN_BANSAL = "sachin-bansal";
    private static final String RESPONSE_KEY = "responses";
    private static final String BUCKET_1_KEY = "bucket1";
    private static final String BUCKET_2_KEY = "bucket2";
    private static final String BUCKET_3_KEY = "bucket3";
    private static final String ANSWER_STATUS_KEY = "answer_status";
    private static final String PHRASE_KEY = "phrase"; //for answer_status = default
    private static final String QUESTION_KEY = "question"; //for answer_status = custom
    private static final String ANS_FORMAT_KEY = "ans_format";
    private static final String ANS_TEXT_KEY = "ans_text";
    private static final String ANS_VIDEO_ID_KEY = "ans_video_id";
    private static final String ANS_START_KEY = "ans_start";

    RecyclerView bucketRecyclerView;
    ArrayList<String> bucketArrayList;
    BucketViewAdapter bucketViewAdapter;

    ArrayList<String> arrayList;
    ArrayAdapter arrayAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_screen2);

        option = getIntent().getStringExtra("option");

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar2);
        getSupportActionBar().setElevation(0);
        View view = getSupportActionBar().getCustomView();

        ImageView back = view.findViewById(R.id.custom_action_bar2_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        init();

        setBucket();
    }

    private void init() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        db = FirebaseFirestore.getInstance();

        bucketRecyclerView = findViewById(R.id.ask_screen2_recycler_view);
        bucketRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        bucketArrayList = new ArrayList<>();

        listView = findViewById(R.id.listView);
    }

    private void setBucket() {
        Query bucket2 = db.collection(XPERT_MASTER_KEY)
                .document(A_R_RAHMAN_KEY)
                .collection(RESPONSE_KEY)
                .whereEqualTo(BUCKET_1_KEY, "exp")
                .whereEqualTo(BUCKET_2_KEY, option);

        bucket2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String str;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        str = document.getString(BUCKET_3_KEY);
                        bucketArrayList.add(str);
                    }

                    Set<String> set = new HashSet<>(bucketArrayList);
                    bucketArrayList.clear();
                    bucketArrayList.addAll(set);

                    bucketViewAdapter = new BucketViewAdapter(AskScreen2Activity.this, bucketArrayList);
                    bucketRecyclerView.setAdapter(bucketViewAdapter);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        String bucket3 = null;

        if (bucket3 != null) {
            arrayList = new ArrayList<>();

            final Query bucket3Q = db.collection(XPERT_MASTER_KEY)
                    .document(A_R_RAHMAN_KEY)
                    .collection(RESPONSE_KEY)
                    .whereEqualTo(BUCKET_1_KEY, "exp")
                    .whereEqualTo(BUCKET_2_KEY, option)
                    .whereEqualTo(BUCKET_3_KEY, bucket3)
                    .whereEqualTo(ANSWER_STATUS_KEY, "custom");

            bucket3Q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        String phrase;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            phrase = document.getString(QUESTION_KEY);
                            arrayList.add(phrase);
                        }
                        arrayAdapter = new ArrayAdapter(getApplication(), android.R.layout.simple_list_item_1, arrayList) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView textView = view.findViewById(android.R.id.text1);
                                /*YOUR CHOICE OF COLOR*/
                                textView.setTextColor(Color.BLACK);
                                return view;
                            }
                        };
                        listView.setAdapter(arrayAdapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                TextView tv = (TextView) view;
                                tv.setTextColor(Color.RED);
                                // tv.getText();
                                // arrayList.get(i);
                                Query question = bucket3Q.whereEqualTo(QUESTION_KEY, tv.getText().toString());
                                question.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            String ans_format = null, ans = null;
                                            int ans_start = -1;
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                ans_format = document.getString(ANS_FORMAT_KEY);
                                                if (ans_format.equals("text")) {
                                                    ans = document.getString(ANS_TEXT_KEY);
                                                } else if (ans_format.equals("video")) {
                                                    ans = document.getString(ANS_VIDEO_ID_KEY);
                                                    ans_start = document.getLong(ANS_START_KEY).intValue();
                                                }
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("ans_format", ans_format);
                                                editor.putString("ans", ans);
                                                editor.putInt("ans_start", ans_start);
                                                editor.apply();
                                                onBackPressed();
//                                                Toast.makeText(AskScreen2Activity.this, ans, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
//                                Toast.makeText(getApplicationContext(), i + "", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
