package com.pabitrarista.chatdialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pabitrarista.chatdialog.recyclerview.BucketViewAdapter;
import com.pabitrarista.chatdialog.recyclerview.QuestionViewAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AskScreen2Activity extends AppCompatActivity {

    String option = null;
    private SharedPreferences preferences;

    FirebaseFirestore db;
    private static final String XPERT_MASTER_KEY = "xpert_master";
    private static final String A_R_RAHMAN_KEY = "a-r-rahman";
    private static final String SACHIN_BANSAL = "sachin-bansal";
    private static final String RESPONSES_KEY = "responses";
    private static final String BUCKET_1_KEY = "bucket1";
    private static final String BUCKET_2_KEY = "bucket2";
    private static final String BUCKET_3_KEY = "bucket3";
    private static final String ANSWER_STATUS_KEY = "answer_status";
    private static final String PHRASE_KEY = "phrase"; //for answer_status = default
    private static final String QUESTION_KEY = "question"; //for answer_status = custom
    private static final String RESPONSE_TYPE_KEY = "response_type"; //text || url || video
    private static final String RESPONSE_KEY = "response";
    private static final String ANS_START_KEY = "ans_start";
    Query bucket2, bucket3, question;

    RecyclerView bucketRecyclerView;
    ArrayList<String> bucketArrayList;
    BucketViewAdapter bucketViewAdapter;

    RecyclerView questionRecyclerView;
    ArrayList<String> questionArrayList;
    QuestionViewAdapter questionViewAdapter;

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

        questionRecyclerView = findViewById(R.id.ask_screen2_recycler_view_2);
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionArrayList = new ArrayList<>();
    }

    private void setBucket() {
        bucket2 = db.collection(XPERT_MASTER_KEY)
                .document(A_R_RAHMAN_KEY)
                .collection(RESPONSES_KEY)
                .whereEqualTo(BUCKET_1_KEY, "exp")
                .whereEqualTo(BUCKET_2_KEY, option)
                .whereEqualTo(ANSWER_STATUS_KEY, "custom");

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

    public void setQuestion(String bucket3Content) {
        bucket3 = bucket2
                .whereEqualTo(BUCKET_3_KEY, bucket3Content);

        bucket3.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String str;
                    questionArrayList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        str = document.getString(QUESTION_KEY);
                        questionArrayList.add(str);
                    }

                    questionViewAdapter = new QuestionViewAdapter(AskScreen2Activity.this, questionArrayList);
                    questionRecyclerView.setAdapter(questionViewAdapter);
                }
            }
        });
    }

    public void setAnswer(String questionContent) {
        question = bucket3
                .whereEqualTo(QUESTION_KEY, questionContent);

        question.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String response_type = null, response = null;
                    int video_start = -1;

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        response_type = document.getString(RESPONSE_TYPE_KEY);
                        if (response_type.equals("text")) {
                            response = document.getString(RESPONSE_KEY);
                        } else if (response_type.equals("url")) {
                            response = document.getString(RESPONSE_KEY);
                        } else if (response_type.equals("video")) {
                            response = document.getString(RESPONSE_KEY);
                            video_start = document.getLong(ANS_START_KEY).intValue();
                        }
                    }

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("response_type", response_type);
                    editor.putString("response", response);
                    editor.putInt("video_start", video_start);
                    editor.apply();
                    onBackPressed();
                }
            }
        });
    }
}
