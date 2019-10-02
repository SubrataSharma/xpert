package chat.xpert.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;
import com.pabitrarista.chatdialog.R;

import chat.xpert.user.recyclerview.ChatViewAdapter;
import chat.xpert.user.recyclerview.ChatViewData;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.RequestExtras;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.AIService;
import ai.api.model.AIContext;
import ai.api.model.AIError;
import ai.api.model.AIEvent;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements AIListener {

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

    AIService aiService;
    AIDataService aiDataService;

    private SharedPreferences preferences;

    String xpertName, xpertImage, xpertId;
    int xpertIdChatCount;
    String sessionId = null;
    String userPhone, userName;

    String userInterest = "null";
    String xpertInterest = "";

    FirebaseFirestore db;
    private static final String XPERT_MASTER_KEY = "xpert_master";
    private static final String NAME_KEY = "name";
    private static final String PROFESSION_KEY = "profession";
    private static final String BUCKET_3_KEY = "bucket3";
    private static final String BUCKET_4_KEY = "bucket4";
    private static final String FIRST_NAME_KEY = "first_name";
    private static final String LAST_NAME_KEY = "last_name";
    private static final String PROFILE_PIC_KEY = "profile_pic";
    private static final String GENDER_KEY = "gender";
    private static final String SENDER_ID_KEY = "sender_id";
    private static final String SESSION_ID_KEY = "session_id";
    private static final String LAST_CHAT_ON_KEY = "last_chat_on";
    private static final String STARTED_ON_KEY = "started_on";
    private static final String SOURCE_KEY = "source";
    private static final String USER_PERSONA_KEY = "user_persona";
    private static final String PROFESION_KEY = "profession";
    private static final String INTEREST_KEY = "interest";
    private static final String IMAGE_KEY = "image";
    Query content4thBucket, profession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userPhone = preferences.getString("userPhone", "null");
        userName = preferences.getString("userName", "null");

        xpertName = getIntent().getStringExtra("xpertName");
        xpertImage = getIntent().getStringExtra("xpertImage");
        xpertId = getIntent().getStringExtra("xpertId");

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

        final AIConfiguration config = new AIConfiguration("5a28a24679e6411699636dc44558724c",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        aiDataService = new AIDataService(this, config);

        db = FirebaseFirestore.getInstance();
    }

    private void showUserIntro() {
        userInterest = preferences.getString("userInterestAbout" + xpertId, "null");

        if (!userInterest.equals("null")) {
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

                String s1 = "Hi " + userName;
                String s2 = "Nice to meet a fellow " + prof;
                String s3 = "Feel free to ask me about my journey, " + inters + " or anything else.";

                ChatViewData msg = new ChatViewData(ChatViewData.MSG_TYPE_RECEIVED, s1);
                recyclerView.smoothScrollToPosition(chatViewAdapter.addChatData(msg));

                msg = new ChatViewData(ChatViewData.MSG_TYPE_RECEIVED, s2);
                recyclerView.smoothScrollToPosition(chatViewAdapter.addChatData(msg));

                msg = new ChatViewData(ChatViewData.MSG_TYPE_RECEIVED, s3);
                recyclerView.smoothScrollToPosition(chatViewAdapter.addChatData(msg));
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

                String s1 = "Hi " + userName;
                ChatViewData msg = new ChatViewData(ChatViewData.MSG_TYPE_RECEIVED, s1);
                recyclerView.smoothScrollToPosition(chatViewAdapter.addChatData(msg));

                s1 = "Great to see you are interested in " + inters + ".";
                msg = new ChatViewData(ChatViewData.MSG_TYPE_RECEIVED, s1);
                recyclerView.smoothScrollToPosition(chatViewAdapter.addChatData(msg));

                s1 = "Feel free to ask me about " + inters + ", my opinions or anything else.";
                msg = new ChatViewData(ChatViewData.MSG_TYPE_RECEIVED, s1);
                recyclerView.smoothScrollToPosition(chatViewAdapter.addChatData(msg));
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

                String s1 = "Hi " + userName;
                ChatViewData msg = new ChatViewData(ChatViewData.MSG_TYPE_RECEIVED, s1);
                recyclerView.smoothScrollToPosition(chatViewAdapter.addChatData(msg));

                String s2 = "Glad to finally meet you !";
                msg = new ChatViewData(ChatViewData.MSG_TYPE_RECEIVED, s2);
                recyclerView.smoothScrollToPosition(chatViewAdapter.addChatData(msg));

                String s3 = "Thank you for your support,\nHow are you doing today?";
                msg = new ChatViewData(ChatViewData.MSG_TYPE_RECEIVED, s3);
                recyclerView.smoothScrollToPosition(chatViewAdapter.addChatData(msg));
            }
        });
    }

    private void set4thBucket() {
//        content4thBucket = db.collection(XPERT_MASTER_KEY)
//                .whereEqualTo(NAME_KEY, xpertName);
//
//        content4thBucket.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    String str;
//                    try {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            str = document.getString(PROFESSION_KEY);
//                            str = str.replace('-', ' ');
//                            str = str.toUpperCase();
//                            textView_4thBucket.setText(str);
//                            set4thBucketImage(str);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

//        db.collection(XPERT_MASTER_KEY)
//                .document(xpertId)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        try {
//                            String str = documentSnapshot.getString(PROFESSION_KEY);
//                            str = str.replace('-', ' ');
//                            str = str.toUpperCase();
//                            textView_4thBucket.setText(str);
//                            set4thBucketImage(str);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });

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

        /*db.collection(XPERT_MASTER_KEY)
                .document(xpertId)
                .collection("chats")
                .document(userPhone)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String senderId = documentSnapshot.getString(SENDER_ID_KEY);

                        if (senderId == null) {
                            Map<String, Object> docData = new HashMap<>();
                            docData.put(FIRST_NAME_KEY, null);
                            docData.put(LAST_NAME_KEY, null);
                            docData.put(PROFILE_PIC_KEY, null);
                            docData.put(GENDER_KEY, null);
                            docData.put(SENDER_ID_KEY, userPhone);
                            docData.put(SESSION_ID_KEY, sessionId);
                            docData.put(LAST_CHAT_ON_KEY, FieldValue.serverTimestamp());
                            docData.put(STARTED_ON_KEY, FieldValue.serverTimestamp());
                            docData.put(SOURCE_KEY, "messenger");
                            docData.put(USER_PERSONA_KEY, "Fan");

                            db.collection(XPERT_MASTER_KEY)
                                    .document(xpertId)
                                    .collection("chats")
                                    .document(userPhone)
                                    .set(docData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    });
                        } else {
                            Map<String, Object> docData = new HashMap<>();
                            docData.put(LAST_CHAT_ON_KEY, FieldValue.serverTimestamp());

                            db.collection(XPERT_MASTER_KEY)
                                    .document(xpertId)
                                    .collection("chats")
                                    .document(userPhone)
                                    .update(docData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    });
                        }
                    }
                });*/
    }

    public void funFacts(View view) {
        Intent in = new Intent(MainActivity.this, AskScreen2Activity.class);
        in.putExtra("option", "personality");
        in.putExtra("xpertId", xpertId);
        in.putExtra("bucket", BUCKET_3_KEY);
        in.putExtra("title", "Trivia");
        startActivity(in);
    }

    public void opinions(View view) {
        Intent in = new Intent(MainActivity.this, AskScreen2Activity.class);
        in.putExtra("option", "opinion");
        in.putExtra("xpertId", xpertId);
        in.putExtra("bucket", BUCKET_3_KEY);
        in.putExtra("title", "Opinions");
        startActivity(in);
    }

    public void life(View view) {
        Intent in = new Intent(MainActivity.this, AskScreen2Activity.class);
        in.putExtra("option", "journey");
        in.putExtra("xpertId", xpertId);
        in.putExtra("bucket", BUCKET_4_KEY);
        in.putExtra("title", "Life");
        startActivity(in);
    }

    public void bucket4(View view) {
        Intent in = new Intent(MainActivity.this, AskScreen2Activity.class);
        in.putExtra("option", "pro");
        in.putExtra("xpertId", xpertId);
        in.putExtra("bucket", BUCKET_4_KEY);
        in.putExtra("title", xpertInterest);
        startActivity(in);
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

            // Add a new sent message to the list.
            ChatViewData msg = new ChatViewData(ChatViewData.MSG_TYPE_SENT, msgEditText);
            recyclerView.smoothScrollToPosition(chatViewAdapter.addChatData(msg));
//            chatViewData.add(msg);
//            int newMsgPosition = chatViewData.size() - 1;
            // Notify recycler view insert one new data.
//            chatViewAdapter.notifyItemInserted(newMsgPosition);
            // Scroll RecyclerView to the last message.
//            recyclerView.scrollToPosition(newMsgPosition);

            editText.setText("");
        }
    }

    public void callDialogFlow(String msg) {
        int itemInsertPosition = this.chatViewAdapter.addChatData(new ChatViewData(ChatViewData.MSG_TYPE_PACEHOLDER, ""));
        recyclerView.smoothScrollToPosition(itemInsertPosition);
        new AiTask(MainActivity.this, aiDataService, itemInsertPosition).execute(msg, "", "", sessionId);
//        aiService.startListening();
    }

    @Override
    public void onResult(AIResponse result) {
        Result result1 = result.getResult();
        Toast.makeText(this, "Query: " + result1.getResolvedQuery() + "\nAction: " + result1.getAction(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, result1.getFulfillment().getSpeech(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(AIError error) {
    }

    @Override
    public void onAudioLevel(float level) {
    }

    @Override
    public void onListeningStarted() {
    }

    @Override
    public void onListeningCanceled() {
    }

    @Override
    public void onListeningFinished() {
    }

    @Override
    protected void onResume() {
        super.onResume();

        String question_content = preferences.getString("question_content", null);
        String response = preferences.getString("response", null);
        String response_type = preferences.getString("response_type", null);
        int video_start = preferences.getInt("video_start", -1);

        if (response_type != null && response != null && !response.equals("")) {

            ChatViewData msg = new ChatViewData(ChatViewData.MSG_TYPE_SENT, question_content);
//            chatViewData.add(msg);
//            int newMsgPosition = chatViewData.size() - 1;
//            chatViewAdapter.notifyItemInserted(newMsgPosition);
//            recyclerView.scrollToPosition(newMsgPosition);
            recyclerView.smoothScrollToPosition(chatViewAdapter.addChatData(msg));

            if (response_type.equals("youtube")) {
                final int itemInsertPosition = this.chatViewAdapter.addChatData(new ChatViewData(ChatViewData.MSG_TYPE_PACEHOLDER, ""));
                recyclerView.smoothScrollToPosition(itemInsertPosition);
                final ChatViewData msgTemp = new ChatViewData(ChatViewData.MSG_TYPE_VIDEO, response);
                msgTemp.setStartSeconds(video_start);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        chatViewAdapter.updateItemAtPos(msgTemp, itemInsertPosition);
                        recyclerView.smoothScrollToPosition(itemInsertPosition);
                    }
                }, 3000);

//                msg = new ChatViewData(ChatViewData.MSG_TYPE_VIDEO, response);
//                msg.setStartSeconds(video_start);
//                chatViewData.add(msg);
//                newMsgPosition = chatViewData.size() - 1;
//                chatViewAdapter.notifyItemInserted(newMsgPosition);
//                recyclerView.scrollToPosition(newMsgPosition);
//                recyclerView.smoothScrollToPosition(chatViewAdapter.addChatData(msg));
            } else if (response_type.equals("text")) {
                final int itemInsertPosition = this.chatViewAdapter.addChatData(new ChatViewData(ChatViewData.MSG_TYPE_PACEHOLDER, ""));
                recyclerView.smoothScrollToPosition(itemInsertPosition);
                final ChatViewData msgTemp = new ChatViewData(ChatViewData.MSG_TYPE_RECEIVED, response);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        chatViewAdapter.updateItemAtPos(msgTemp, itemInsertPosition);
                        recyclerView.smoothScrollToPosition(itemInsertPosition);
                    }
                }, 3000);

//                msg = new ChatViewData(ChatViewData.MSG_TYPE_RECEIVED, response);
//                chatViewData.add(msg);
//                newMsgPosition = chatViewData.size() - 1;
//                chatViewAdapter.notifyItemInserted(newMsgPosition);
//                recyclerView.scrollToPosition(newMsgPosition);
//                recyclerView.smoothScrollToPosition(chatViewAdapter.addChatData(msg));
            }

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("question_content", null);
            editor.putString("response_type", null);
            editor.putString("response", null);
            editor.putInt("video_start", -1);
            editor.apply();
        }
    }

    public void openBucket(View view) {
        View view2 = this.getCurrentFocus();

        if (view2 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
        }
    }
}

class AiTask extends AsyncTask<String, Void, AIResponse> {

    MainActivity mainActivity;
    private final AIDataService aiService;
    private final int itemInsertPosition;

    AiTask(MainActivity mainActivity, AIDataService aiService, int itemInsertPosition) {
        this.mainActivity = mainActivity;
        this.aiService = aiService;
        this.itemInsertPosition = itemInsertPosition;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected AIResponse doInBackground(final String... params) {
        AIRequest request = new AIRequest();
        String query = params[0];
        String event = params[1];
        String context = params[2];
        String sessionId = params[3];

        if (!TextUtils.isEmpty(query)) {
            request.setQuery(query);
            Log.i("query", query);
            request.setSessionId(sessionId);
            Log.i("sessionId", sessionId);
        }

        if (!TextUtils.isEmpty(event)) {
            request.setEvent(new AIEvent(event));
        }

        RequestExtras requestExtras = null;
        if (!TextUtils.isEmpty(context)) {
            final List<AIContext> contexts = Collections.singletonList(new AIContext(context));
            requestExtras = new RequestExtras(contexts, null);
        }

        try {
            return aiService.request(request, requestExtras);
        } catch (final AIServiceException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(final AIResponse response) {
        if (response != null) {
            Log.i("sessionId", response.getSessionId());
            final Result result = response.getResult();
            final String speech = result.getFulfillment().getSpeech();
            //Toast.makeText(context.get(), speech, Toast.LENGTH_LONG).show();
            //result.getResolvedQuery()

//            text -- text{}content separated by {|}
//            image -- image{}content  separated by {|}
//            bubble separation {|}
//            String ex = "text{}content1{|}content2{|}content3";
//            youtube -- youtube{}video_id(|)start(|)end
//            String ex = "youtube{}TqUbiOgEb0w(|)88(|)200";

            Log.i("RESULT", speech);
            String speech2 = speech.replace("{}", "|");
            String[] splitSpeech = speech2.split("\\|");

            String type = null;
            String url = null;
            int startTime = 0, stopTime = 0;
            int count = 0;

            for (String s : splitSpeech) {
                Log.i("RESULT", s);
                s = s.replace("{", "");
                s = s.replace("}", "");
                s = s.replace("(", "");
                s = s.replace(")", "");

                if (s.equals("text") || s.equals("image") || s.equals("youtube")) {
                    type = s;
                    continue;
                }

                try {
                    if (type.equals("text")) {
                        showText(s);
                        type = null;
                    } else if (type.equals("image")) {
                        showImage(s);
                        type = null;
                    } else if (type.equals("youtube")) {
                        if (count == 0) {
                            url = s;
                            count++;
                        } else if (count == 1) {
                            startTime = Integer.parseInt(s);
                            count++;
                        } else if (count == 2) {
                            stopTime = Integer.parseInt(s);
                            count = 0;
                            showVideo(url, startTime, stopTime);
                            type = null;
                        }
                    }
                } catch (Exception e) {
                    showText(s);
                }
            }
        }
    }

    private void showText(String text) {
        ChatViewData msg = new ChatViewData(ChatViewData.MSG_TYPE_RECEIVED, text);
//        mainActivity.chatViewData.add(msg);
//        int newMsgPosition = mainActivity.chatViewData.size() - 1;
//        mainActivity.chatViewAdapter.notifyItemInserted(newMsgPosition);
//        mainActivity.recyclerView.scrollToPosition(newMsgPosition);
        updateRVAdapter(msg);
    }

    private void showImage(String url) {
        ChatViewData msg = new ChatViewData(ChatViewData.MSG_TYPE_IMAGE, url);
//        mainActivity.chatViewData.add(msg);
//        int newMsgPosition = mainActivity.chatViewData.size() - 1;
//        mainActivity.chatViewAdapter.notifyItemInserted(newMsgPosition);
//        mainActivity.recyclerView.scrollToPosition(newMsgPosition);
        updateRVAdapter(msg);
    }

    private void showVideo(String url, int startTime, int stopTime) {
        ChatViewData msg = new ChatViewData(ChatViewData.MSG_TYPE_VIDEO, url);
        msg.setStartSeconds(startTime);
//        mainActivity.chatViewData.add(msg);
//        int newMsgPosition = mainActivity.chatViewData.size() - 1;
//        mainActivity.chatViewAdapter.notifyItemInserted(newMsgPosition);
//        mainActivity.recyclerView.scrollToPosition(newMsgPosition);
        updateRVAdapter(msg);
    }

    private void updateRVAdapter(ChatViewData msg) {
        mainActivity.chatViewAdapter.updateItemAtPos(msg, itemInsertPosition);
        mainActivity.recyclerView.smoothScrollToPosition(itemInsertPosition);
    }
}