package com.pabitrarista.chatdialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AskScreen2Activity extends AppCompatActivity implements View.OnClickListener {

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

    TextView textView1, textView2, textView3, textView4, textView5, textView6, textView7, textView8, textView9;
    LinearLayout linearLayout;

    ArrayList<String> arrayList;
    ArrayAdapter arrayAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_screen2);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar2);
        getSupportActionBar().setElevation(0);
        View view = getSupportActionBar().getCustomView();

        ImageView back = view.findViewById(R.id.custom_action_bar2_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in2 = new Intent(AskScreen2Activity.this, MainActivity.class);
                startActivity(in2);
                finish();
            }
        });

        init();
        linearLayout.setVisibility(View.GONE);

        Query bucket2Personality = db.collection(XPERT_MASTER_KEY)
                .document(A_R_RAHMAN_KEY)
                .collection(RESPONSE_KEY)
                .whereEqualTo(BUCKET_1_KEY, "exp")
                .whereEqualTo(BUCKET_2_KEY, "personality");

        Query bucket2Opinion = db.collection(XPERT_MASTER_KEY)
                .document(SACHIN_BANSAL)
                .collection(RESPONSE_KEY)
                .whereEqualTo(BUCKET_1_KEY, "exp")
                .whereEqualTo(BUCKET_2_KEY, "opinion");

        Query bucket2Journey = db.collection(XPERT_MASTER_KEY)
                .document(SACHIN_BANSAL)
                .collection(RESPONSE_KEY)
                .whereEqualTo(BUCKET_1_KEY, "exp")
                .whereEqualTo(BUCKET_2_KEY, "journey");

        Query bucket2Pro = db.collection(XPERT_MASTER_KEY)
                .document(SACHIN_BANSAL)
                .collection(RESPONSE_KEY)
                .whereEqualTo(BUCKET_1_KEY, "exp")
                .whereEqualTo(BUCKET_2_KEY, "pro");

        bucket2Personality.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String[] bucket3Content = new String[60];
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (i == 0) {
                            linearLayout.setVisibility(View.VISIBLE);
                            bucket3Content[i] = document.getString(BUCKET_3_KEY);
                            setTxt(i, bucket3Content[i]);
                            i++;
                        } else {
                            bucket3Content[i] = document.getString(BUCKET_3_KEY);
                            if (!bucket3Content[i].equals(bucket3Content[i - 1])) {
                                setTxt(i, bucket3Content[i]);
                                i++;
                            }
                        }
                        //Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                }
            }
        });


//        db.collection(XPERT_MASTER_KEY)
//                .document(A_R_RAHMAN_KEY)
//                .collection(RESPONSE_KEY)
//                .document("exp_journey_first_day")
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        String bucket2 = documentSnapshot.getString("bucket1");
//                        textView.setText(bucket2 + "");
//                    }
//                });
    }

    private void init() {
        db = FirebaseFirestore.getInstance();

        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);
        textView7 = findViewById(R.id.textView7);
        textView8 = findViewById(R.id.textView8);
        textView9 = findViewById(R.id.textView9);
        linearLayout = findViewById(R.id.ask_screen2_linear_layout_1);
        listView = findViewById(R.id.listView);

        textView1.setOnClickListener(this);
        textView2.setOnClickListener(this);
        textView3.setOnClickListener(this);
        textView4.setOnClickListener(this);
        textView5.setOnClickListener(this);
        textView6.setOnClickListener(this);
        textView7.setOnClickListener(this);
        textView8.setOnClickListener(this);
        textView9.setOnClickListener(this);
    }

    private void setTxt(int i, String str) {
        switch (i) {
            case 0:
                textView1.setText(str);
                break;
            case 1:
                textView2.setText(str);
                break;
            case 2:
                textView3.setText(str);
                break;
            case 3:
                textView4.setText(str);
                break;
            case 4:
                textView5.setText(str);
                break;
            case 5:
                textView6.setText(str);
                break;
            case 6:
                textView7.setText(str);
                break;
            case 7:
                textView8.setText(str);
                break;
            case 8:
                textView9.setText(str);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        String bucket3 = null;

        switch (view.getId()) {
            case R.id.textView1:
                bucket3 = textView1.getText().toString();
                break;
            case R.id.textView2:
                bucket3 = textView2.getText().toString();
                break;
            case R.id.textView3:
                bucket3 = textView3.getText().toString();
                break;
            case R.id.textView4:
                bucket3 = textView4.getText().toString();
                break;
            case R.id.textView5:
                bucket3 = textView5.getText().toString();
                break;
            case R.id.textView6:
                bucket3 = textView6.getText().toString();
                break;
            case R.id.textView7:
                bucket3 = textView7.getText().toString();
                break;
            case R.id.textView8:
                bucket3 = textView8.getText().toString();
                break;
            case R.id.textView9:
                bucket3 = textView9.getText().toString();
                break;
        }

        if (bucket3 != null) {
            arrayList = new ArrayList<>();

            Query bucket3Q = db.collection(XPERT_MASTER_KEY)
                    .document(A_R_RAHMAN_KEY)
                    .collection(RESPONSE_KEY)
                    .whereEqualTo(BUCKET_1_KEY, "exp")
                    .whereEqualTo(BUCKET_2_KEY, "personality")
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
                                Toast.makeText(getApplicationContext(), i + "", Toast.LENGTH_SHORT).show();
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
