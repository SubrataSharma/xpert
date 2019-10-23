package chat.xpert.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;
import com.pabitrarista.chatdialog.R;

import chat.xpert.user.recyclerview.ChatViewAdapter;
import chat.xpert.user.recyclerview.ChatViewData;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    String TAG = "logtag";

    CardView cardView_4thBucket;
    ImageView imageView_4thBucket, image_four_squares;
    RelativeLayout relativeLayout;
    TextView textView, textView_4thBucket;
    HorizontalScrollView horizontalScrollView;
    EditText editText;
    LinearLayout linearLayout;
    RelativeLayout relativeLayoutUserIntro;
    TextView textViewIntro1, textViewIntro2, textViewIntro3;

    RecyclerView recyclerView;
    final List<ChatViewData> chatViewData = new ArrayList<>();
    ChatViewAdapter chatViewAdapter;
    int itemInsertPosition = 0;

    private SharedPreferences preferences;

    String xpertName, xpertImage, xpertId, Uid;
    int xpertIdChatCount;
    String sessionId = null;
    String userPhone, userFirstName;

    String userInterest = "null";
    String xpertInterest = "";

    Boolean typingImage = false;

    FirebaseAuth mAuth;

    FirebaseFirestore db;
    private static final String XPERT_MASTER_KEY = "xpert_master";
    private static final String USER_MASTER_KEY = "user_master";
    private static final String NAME_KEY = "name";
    private static final String PROFESSION_KEY = "profession";
    private static final String BUCKET_3_KEY = "bucket3";
    private static final String BUCKET_4_KEY = "bucket4";
    private static final String FIRST_NAME_KEY = "first_name";
    private static final String LAST_NAME_KEY = "last_name";
    private static final String PROFILE_PIC_KEY = "profile_pic";
    private static final String GENDER_KEY = "gender";
    private static final String SENDER_KEY = "sender";
    private static final String SENDER_ID_KEY = "sender_id";
    private static final String SESSION_ID_KEY = "session_id";
    private static final String LAST_CHAT_ON_KEY = "last_chat_on";
    private static final String STARTED_ON_KEY = "started_on";
    private static final String SOURCE_KEY = "source";
    private static final String USER_PERSONA_KEY = "user_persona";
    private static final String PROFESION_KEY = "profession";
    private static final String INTEREST_KEY = "interest";
    private static final String IMAGE_KEY = "image";
    private static final String XPERT_KEY = "xpert";
    private static final String XPERT_PROFILE_IMAGE_KEY = "xpert_profile_image";
    private static final String ACTIVE_KEY = "active";
    private static final String DATE_KEY = "date";
    private static final String START_CHAT_ON_KEY = "start_chat_on";
    private static final String NOTIFICATION_KEY = "notification";
    private static final String MESSAGE_TYPE_KEY = "message_type";
    private static final String MESSAGE_KEY = "message";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String LANGUAGE_KEY = "language";
    private static final String STATUS_KEY = "status";
    private static final String RESPONSE_DOC_ID_KEY = "response_doc_id";
    private static final String ANS_START_KEY = "ans_start";
    private static final String ANS_END_KEY = "ans_end";
    Query profession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userPhone = preferences.getString("userPhone", "null");
        userFirstName = preferences.getString("userFirstName", "null");

        xpertName = getIntent().getStringExtra("xpertName");
        xpertImage = getIntent().getStringExtra("xpertImage");
        xpertId = getIntent().getStringExtra("xpertId");

        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getUid();

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setElevation(0);
        View view = getSupportActionBar().getCustomView();

        TextView name = view.findViewById(R.id.custom_action_bar_name);
        name.setText(xpertName);
        CircleImageView image = view.findViewById(R.id.custom_action_bar_image);
        try {
            Picasso.get().load(xpertImage).into(image);
        } catch (Exception e) {
            Picasso.get().load(R.drawable.icon_dp).into(image);
        }
        ImageView back = view.findViewById(R.id.custom_action_bar_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        init();

        showUserIntro();

        set4thBucket();

        hideBucket();

        firestoreListener();
    }

    private void init() {
        cardView_4thBucket = findViewById(R.id.main_4thBucket_cardView);
        cardView_4thBucket.setVisibility(View.GONE);
        relativeLayout = findViewById(R.id.main_relative_layout);
        imageView_4thBucket = findViewById(R.id.main_4thBucket_imageView);
        image_four_squares = findViewById(R.id.main_image_four_squares);
        textView = findViewById(R.id.main_text_view);
        textView_4thBucket = findViewById(R.id.main_4thBucket_textView);
        horizontalScrollView = findViewById(R.id.main_horizontal_scroll_view);
        editText = findViewById(R.id.main_editText);
        linearLayout = findViewById(R.id.main_linear_layout);
        relativeLayoutUserIntro = findViewById(R.id.main_user_intro_relativeLayout);
        textViewIntro1 = findViewById(R.id.main_user_intro_1);
        textViewIntro2 = findViewById(R.id.main_user_intro_2);
        textViewIntro3 = findViewById(R.id.main_user_intro_3);

        recyclerView = findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create the data adapter with above data list.
        chatViewAdapter = new ChatViewAdapter(chatViewData);
        // Set data adapter to RecyclerView.
        recyclerView.setAdapter(chatViewAdapter);

        db = FirebaseFirestore.getInstance();
    }

    private void showUserIntro() {
        db.collection(USER_MASTER_KEY)
                .document(Uid)
                .collection("following")
                .document(xpertId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            userInterest = documentSnapshot.getString(USER_PERSONA_KEY);

                            if (userInterest != null) {
                                relativeLayoutUserIntro.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                linearLayout.setVisibility(View.VISIBLE);
                                createSessionId(userInterest);
                                return;
                            }

                            db.collection(XPERT_MASTER_KEY)
                                    .document(xpertId)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            try {
                                                String str = documentSnapshot.getString(PROFESSION_KEY);
                                                if (str != null) {
                                                    profession = db
                                                            .collection(PROFESION_KEY)
                                                            .whereEqualTo(PROFESION_KEY, str);

                                                    profession.get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        String prof, inters;
                                                                        try {
                                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                prof = document.getString(PROFESSION_KEY);
                                                                                inters = document.getString(INTEREST_KEY);
                                                                                prof = prof.replace('-', ' ');
                                                                                inters = inters.toLowerCase();
                                                                                addTextView(prof, inters);
                                                                            }
                                                                        } catch (Exception e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void addTextView(final String prof, final String inters) {
        textViewIntro1.setText("I am / want to be a " + prof);
        textViewIntro2.setText("I am interested in " + inters);
//        Toast.makeText(this, prof + "  " + inters, Toast.LENGTH_SHORT).show();

        relativeLayoutUserIntro.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);

        textViewIntro1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayoutUserIntro.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("userInterestAbout" + xpertId, prof);
                editor.apply();
                createSessionId(prof);
                uploadUserInterest(prof);

                String s1 = "Hi " + userFirstName;
                writeMsgInDB("xpert", "text", s1, "reply", 0, 0);
                String s2 = "Nice to meet a fellow " + prof;
                writeMsgInDB("xpert", "text", s2, "reply", 0, 0);
                String s3 = "Feel free to ask me about my journey, " + inters + " or anything else.";
                writeMsgInDB("xpert", "text", s3, "reply", 0, 0);
            }
        });
        textViewIntro2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayoutUserIntro.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("userInterestAbout" + xpertId, inters);
                editor.apply();
                createSessionId(inters);
                uploadUserInterest(inters);

                String s1 = "Hi " + userFirstName;
                writeMsgInDB("xpert", "text", s1, "reply", 0, 0);
                s1 = "Great to see you are interested in " + inters + ".";
                writeMsgInDB("xpert", "text", s1, "reply", 0, 0);
                s1 = "Feel free to ask me about " + inters + ", my opinions or anything else.";
                writeMsgInDB("xpert", "text", s1, "reply", 0, 0);
            }
        });
        textViewIntro3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayoutUserIntro.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("userInterestAbout" + xpertId, "fan");
                editor.apply();
                createSessionId("fan");
                uploadUserInterest("fan");

                String s1 = "Hi " + userFirstName;
                writeMsgInDB("xpert", "text", s1, "reply", 0, 0);
                String s2 = "Glad to finally meet you !";
                writeMsgInDB("xpert", "text", s2, "reply", 0, 0);
                String s3 = "Thank you for your support,\nHow are you doing today?";
                writeMsgInDB("xpert", "text", s3, "reply", 0, 0);
            }
        });
    }

    private void uploadUserInterest(final String userInterest) {

        Map<String, Object> docData = new HashMap<>();
        docData.put(XPERT_KEY, db.document(XPERT_MASTER_KEY + "/" + xpertId));
        docData.put(XPERT_PROFILE_IMAGE_KEY, null);
        docData.put(USER_PERSONA_KEY, userInterest);
        docData.put(ACTIVE_KEY, true);
        docData.put(DATE_KEY, FieldValue.serverTimestamp());
        docData.put(START_CHAT_ON_KEY, FieldValue.serverTimestamp());
        docData.put(SESSION_ID_KEY, sessionId);
        docData.put(NOTIFICATION_KEY, true);

        db.collection(USER_MASTER_KEY)
                .document(Uid)
                .collection("following")
                .document(xpertId)
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
    }

    private void set4thBucket() {
        db.collection(XPERT_MASTER_KEY)
                .document(xpertId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            String str = documentSnapshot.getString(PROFESSION_KEY);
                            if (str != null) {
                                profession = db
                                        .collection(PROFESION_KEY)
                                        .whereEqualTo(PROFESION_KEY, str);

                                profession.get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    String prof, inters, imageUrl;
                                                    try {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            prof = document.getString(PROFESSION_KEY);
                                                            inters = document.getString(INTEREST_KEY);
                                                            imageUrl = document.getString(IMAGE_KEY);
                                                            inters = inters.replace('-', ' ');
                                                            xpertInterest = inters;
                                                            inters = inters.toUpperCase();
                                                            textView_4thBucket.setText(inters);
                                                            Picasso.get().load(imageUrl).into(imageView_4thBucket);
                                                            cardView_4thBucket.setVisibility(View.VISIBLE);
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void hideBucket() {
        relativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = relativeLayout.getRootView().getHeight() - relativeLayout.getHeight();
                //Log.i(TAG, "onGlobalLayout: " + relativeLayout.getRootView().getHeight() + " " + relativeLayout.getHeight() + " " + heightDiff);
                if (heightDiff > 500) {
                    textView.setVisibility(View.GONE);
                    horizontalScrollView.setVisibility(View.GONE);
                    image_four_squares.setVisibility(View.VISIBLE);
                    //Log.i(TAG, "keyboard opened");
                } else {
                    textView.setVisibility(View.VISIBLE);
                    horizontalScrollView.setVisibility(View.VISIBLE);
                    image_four_squares.setVisibility(View.GONE);
                    //Log.i(TAG, "keyboard closed");
                }
            }
        });
    }

    private void createSessionId(String userInterest) {
        xpertIdChatCount = preferences.getInt(xpertId + "ChatCount", 0);
        xpertIdChatCount++;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(xpertId + "ChatCount", xpertIdChatCount);
        editor.apply();

        sessionId = xpertId + "|" + userPhone + "|" + userInterest + "|" + xpertIdChatCount;
        Log.i("sessionId", sessionId);
    }

    public void funFacts(View view) {
        Intent in = new Intent(MainActivity.this, AskScreen2Activity.class);
        in.putExtra("option", "personality");
        in.putExtra("xpertId", xpertId);
        in.putExtra("bucket", BUCKET_3_KEY);
        in.putExtra("title", "My Trivia");
        startActivity(in);
        findViewById(R.id.main_bucket_4_underline).setVisibility(View.VISIBLE);
    }

    public void opinions(View view) {
        Intent in = new Intent(MainActivity.this, AskScreen2Activity.class);
        in.putExtra("option", "opinion");
        in.putExtra("xpertId", xpertId);
        in.putExtra("bucket", BUCKET_3_KEY);
        in.putExtra("title", "My Opinions");
        startActivity(in);
        findViewById(R.id.main_bucket_3_underline).setVisibility(View.VISIBLE);
    }

    public void life(View view) {
        Intent in = new Intent(MainActivity.this, AskScreen2Activity.class);
        in.putExtra("option", "journey");
        in.putExtra("xpertId", xpertId);
        in.putExtra("bucket", BUCKET_4_KEY);
        in.putExtra("title", "My Life");
        startActivity(in);
        findViewById(R.id.main_bucket_1_underline).setVisibility(View.VISIBLE);
    }

    public void bucket4(View view) {
        Intent in = new Intent(MainActivity.this, AskScreen2Activity.class);
        in.putExtra("option", "pro");
        in.putExtra("xpertId", xpertId);
        in.putExtra("bucket", BUCKET_4_KEY);
        in.putExtra("title", xpertInterest);
        startActivity(in);
        findViewById(R.id.main_bucket_2_underline).setVisibility(View.VISIBLE);
    }

    public void sendMsg(View view) {
        final String msgEditText = editText.getText().toString().trim();
        if (!msgEditText.equals("")) {

            TranslateAPI translateAPI = new TranslateAPI(
                    Language.AUTO_DETECT,   //Source Language
                    Language.ENGLISH,         //Target Language
                    msgEditText);           //Query Text

            translateAPI.setTranslateListener(new TranslateAPI.TranslateListener() {
                @Override
                public void onSuccess(String translatedText) {
                    callDialogFlow(translatedText);
                }

                @Override
                public void onFailure(String ErrorText) {
                    callDialogFlow(msgEditText);
                }
            });

            writeMsgInDB("user", "text", msgEditText, "send", 0, 0);

            editText.setText("");
        }
    }

    private void callDialogFlow(String msg) {
    }

    @Override
    protected void onResume() {
        super.onResume();

        String question_content = preferences.getString("question_content", null);
        final String response = preferences.getString("response", null);
        String response_type = preferences.getString("response_type", null);
        final int video_start = preferences.getInt("video_start", -1);
        final int video_end = preferences.getInt("video_end", -1);

        if (response_type != null && response != null && !response.equals("")) {
            writeMsgInDB("user", "text", question_content, "ignore", 0, 0);

            if (response_type.equals("youtube")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        writeMsgInDB("xpert", "youtube", response, "ignore", video_start, video_end);
                    }
                }, 3000);
            } else if (response_type.equals("text")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        writeMsgInDB("xpert", "text", response, "ignore", 0, 0);
                    }
                }, 3000);
            }

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("question_content", null);
            editor.putString("response_type", null);
            editor.putString("response", null);
            editor.putInt("video_start", -1);
            editor.putInt("video_end", -1);
            editor.apply();
        }
    }

    public void writeMsgInDB(String sender, String message_type, String message, String status_key, int ans_start, int ans_end) {

        Date date = new Date();
        long time = date.getTime(); //Time in Milliseconds
        Timestamp ts = new Timestamp(time);
        Log.i("timestamp", ts.toString());

        Map<String, Object> docData = new HashMap<>();
        docData.put(SENDER_KEY, sender);                // user|xpert
        docData.put(MESSAGE_TYPE_KEY, message_type);    //text|image|youtube
        docData.put(MESSAGE_KEY, message);
        docData.put(TIMESTAMP_KEY, ts);
        docData.put(LANGUAGE_KEY, "english");
        docData.put(STATUS_KEY, status_key);    // send|reply|ignore
        docData.put(RESPONSE_DOC_ID_KEY, null);
        docData.put(ANS_START_KEY, ans_start);
        docData.put(ANS_END_KEY, ans_end);

        db.collection(USER_MASTER_KEY)
                .document(Uid)
                .collection("following")
                .document(xpertId)
                .collection("chat_transcript")
                .document()
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
    }

    private void firestoreListener() {

        Query query = db.collection(USER_MASTER_KEY)
                .document(Uid)
                .collection("following")
                .document(xpertId)
                .collection("chat_transcript")
                .orderBy(TIMESTAMP_KEY);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

//                for (QueryDocumentSnapshot doc : value) {
//                    if (doc.get("message") != null) {
//                        Log.i("message", doc.getString("message"));
//                    }
//                }

                for (DocumentChange doc : value.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        if (doc.getDocument().get(SENDER_KEY).toString().equals("user")) {
                            Log.i("message", doc.getDocument().getString(MESSAGE_KEY));
                            showUserText(doc.getDocument().getString("message"));
                        }
                        if (doc.getDocument().get(SENDER_KEY).toString().equals("xpert")) {
                            Log.i("message", doc.getDocument().getString(MESSAGE_KEY));
                            if (doc.getDocument().getString(MESSAGE_TYPE_KEY).equals("text"))
                                showText(doc.getDocument().getString(MESSAGE_KEY));
                            else if (doc.getDocument().getString(MESSAGE_TYPE_KEY).equals("image"))
                                showImage(doc.getDocument().getString(MESSAGE_KEY));
                            else if (doc.getDocument().getString(MESSAGE_TYPE_KEY).equals("youtube"))
                                showVideo(doc.getDocument().getString(MESSAGE_KEY), doc.getDocument().getLong(ANS_START_KEY).intValue(), doc.getDocument().getLong(ANS_END_KEY).intValue());
                        }
                    }
                }

            }
        });
    }

    private void showUserText(String text) {
        if (typingImage) {
            final ChatViewData msgTemp = new ChatViewData("", "");
            updateRVAdapter(msgTemp);
        }

        ChatViewData msg = new ChatViewData(ChatViewData.MSG_TYPE_SENT, text);
        recyclerView.smoothScrollToPosition(chatViewAdapter.addChatData(msg));
        itemInsertPosition = this.chatViewAdapter.addChatData(new ChatViewData(ChatViewData.MSG_TYPE_PACEHOLDER, ""));
        recyclerView.smoothScrollToPosition(itemInsertPosition);
        typingImage = true;
    }

    private void showText(String text) {
        final ChatViewData msgTemp = new ChatViewData(ChatViewData.MSG_TYPE_RECEIVED, text);
        updateRVAdapter(msgTemp);
        typingImage = false;
    }

    private void showImage(String url) {
        final ChatViewData msgTemp = new ChatViewData(ChatViewData.MSG_TYPE_IMAGE, url);
        updateRVAdapter(msgTemp);
        typingImage = false;
    }

    private void showVideo(String url, int startTime, int stopTime) {
        final ChatViewData msgTemp = new ChatViewData(ChatViewData.MSG_TYPE_VIDEO, url);
        msgTemp.setStartSeconds(startTime);
        msgTemp.setEndSeconds(stopTime);
        updateRVAdapter(msgTemp);
        typingImage = false;
    }

    private void updateRVAdapter(ChatViewData msg) {
        if (itemInsertPosition == 0)
            itemInsertPosition = chatViewAdapter.addChatData(new ChatViewData("", ""));
        chatViewAdapter.updateItemAtPos(msg, itemInsertPosition);
        recyclerView.smoothScrollToPosition(itemInsertPosition);
        itemInsertPosition = chatViewAdapter.addChatData(new ChatViewData("", ""));
    }

    public void openBucket(View view) {
        View view2 = this.getCurrentFocus();

        if (view2 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
        }
    }
}