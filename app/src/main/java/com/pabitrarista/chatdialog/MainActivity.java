package com.pabitrarista.chatdialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;
import com.pabitrarista.chatdialog.recyclerview.ChatViewAdapter;
import com.pabitrarista.chatdialog.recyclerview.ChatViewData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    RelativeLayout relativeLayout, relativeLayout_4thBucket;
    TextView textView, textView_4thBucket;
    HorizontalScrollView horizontalScrollView;
    EditText editText;

    RecyclerView recyclerView;
    final List<ChatViewData> chatViewData = new ArrayList<>();
    ChatViewAdapter chatViewAdapter;

    AIService aiService;
    AIDataService aiDataService;

    private SharedPreferences preferences;

    String xpertName, xpertImage, xpertId;
    String sessionId = null;
    String userPhone = "9564557783";

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
    Query content4thBucket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        set4thBucket();

        hideBucket();

        createSessionId();
    }

    private void init() {
        cardView_4thBucket = findViewById(R.id.main_4thBucket_cardView);
        cardView_4thBucket.setVisibility(View.GONE);
        relativeLayout = findViewById(R.id.main_relative_layout);
        relativeLayout_4thBucket = findViewById(R.id.main_4thBucket_relativeLayout);
        textView = findViewById(R.id.main_text_view);
        textView_4thBucket = findViewById(R.id.main_4thBucket_textView);
        horizontalScrollView = findViewById(R.id.main_horizontal_scroll_view);
        editText = findViewById(R.id.main_editText);

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

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        db = FirebaseFirestore.getInstance();
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

        db.collection(XPERT_MASTER_KEY)
                .document(xpertId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            String str = documentSnapshot.getString(PROFESSION_KEY);
                            str = str.replace('-', ' ');
                            str = str.toUpperCase();
                            textView_4thBucket.setText(str);
                            set4thBucketImage(str);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void set4thBucketImage(String str) {
        switch (str) {
            case "ACTOR":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_movies);
                break;
            case "BADMINTON PLAYER":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_badminton);
                break;
            case "BOOK WRITER":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_writers);
                break;
            case "CHEF":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_food);
                break;
            case "CHOREOGRAPHER":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_choreographers);
                break;
            case "COMEDIAN":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_comedy);
                break;
            case "CRICKETER":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_cricket);
                break;
            case "DIRECTOR":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_movies);
                break;
            case "ENTREPRENEUR":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_business);
                break;
            case "FASHION DESIGNER":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_fashion_design);
                break;
            case "FOOTBALLER":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_football);
                break;
            case "INDUSTRIALIST":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_business);
                break;
            case "INVESTOR":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_business);
                break;
            case "MAKEUP ARTIST":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_beauty);
                break;
            case "MUSIC COMPOSER":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_music_composers);
                break;
            case "NUTRITIONIST":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_nutrition);
                break;
            case "PAINTER":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_paint);
                break;
            case "PHOTOGRAPHER":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_photography);
                break;
            case "POLITICIAN":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_politics);
                break;
            case "SINGER":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_singing);
                break;
            case "SPIRITUAL GURU":
//                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_singing);
                break;
            case "TENNIS PLAYER":
                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_tennis);
                break;
            case "VENTURE CAPITALIST":
//                relativeLayout_4thBucket.setBackgroundResource(R.drawable.image_bucket_singing);
                break;
        }
        cardView_4thBucket.setVisibility(View.VISIBLE);
    }

    private void hideBucket() {
        relativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = relativeLayout.getRootView().getHeight() - relativeLayout.getHeight();
                //Log.i(TAG, "onGlobalLayout: " + relativeLayout.getRootView().getHeight() + " " + relativeLayout.getHeight() + " " + heightDiff);
                if (heightDiff > 400) {
                    textView.setVisibility(View.GONE);
                    horizontalScrollView.setVisibility(View.GONE);
                    //Log.i(TAG, "keyboard opened");
                } else {
                    textView.setVisibility(View.VISIBLE);
                    horizontalScrollView.setVisibility(View.VISIBLE);
                    //Log.i(TAG, "keyboard closed");
                }
            }
        });
    }

    private void createSessionId() {
        sessionId = xpertId + "|" + userPhone;

        db.collection(XPERT_MASTER_KEY)
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
                });
    }

    public void funFacts(View view) {
        Intent in = new Intent(MainActivity.this, AskScreen2Activity.class);
        in.putExtra("option", "personality");
        in.putExtra("xpertId", xpertId);
        in.putExtra("bucket", BUCKET_3_KEY);
        startActivity(in);
    }

    public void opinions(View view) {
        Intent in = new Intent(MainActivity.this, AskScreen2Activity.class);
        in.putExtra("option", "opinion");
        in.putExtra("xpertId", xpertId);
        in.putExtra("bucket", BUCKET_3_KEY);
        startActivity(in);
    }

    public void life(View view) {
        Intent in = new Intent(MainActivity.this, AskScreen2Activity.class);
        in.putExtra("option", "journey");
        in.putExtra("xpertId", xpertId);
        in.putExtra("bucket", BUCKET_4_KEY);
        startActivity(in);
    }

    public void bucket4(View view) {
        Intent in = new Intent(MainActivity.this, AskScreen2Activity.class);
        in.putExtra("option", "pro");
        in.putExtra("xpertId", xpertId);
        in.putExtra("bucket", BUCKET_4_KEY);
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
            chatViewData.add(msg);
            int newMsgPosition = chatViewData.size() - 1;
            // Notify recycler view insert one new data.
            chatViewAdapter.notifyItemInserted(newMsgPosition);
            // Scroll RecyclerView to the last message.
            recyclerView.scrollToPosition(newMsgPosition);

            editText.setText("");
        }
    }

    public void callDialogFlow(String msg) {
        new AiTask(MainActivity.this, aiDataService).execute(msg, "", "", sessionId);
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
            chatViewData.add(msg);
            int newMsgPosition = chatViewData.size() - 1;
            chatViewAdapter.notifyItemInserted(newMsgPosition);
            recyclerView.scrollToPosition(newMsgPosition);

            if (response_type.equals("youtube")) {
                msg = new ChatViewData(ChatViewData.MSG_TYPE_VIDEO, response);
                msg.setStartSeconds(video_start);
                chatViewData.add(msg);
                newMsgPosition = chatViewData.size() - 1;
                chatViewAdapter.notifyItemInserted(newMsgPosition);
                recyclerView.scrollToPosition(newMsgPosition);
            } else if (response_type.equals("text")) {
                msg = new ChatViewData(ChatViewData.MSG_TYPE_RECEIVED, response);
                chatViewData.add(msg);
                newMsgPosition = chatViewData.size() - 1;
                chatViewAdapter.notifyItemInserted(newMsgPosition);
                recyclerView.scrollToPosition(newMsgPosition);
            }

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("question_content", null);
            editor.putString("response_type", null);
            editor.putString("response", null);
            editor.putInt("video_start", -1);
            editor.apply();
        }
    }
}

class AiTask extends AsyncTask<String, Void, AIResponse> {

    MainActivity mainActivity;
    private final AIDataService aiService;

    AiTask(MainActivity mainActivity, AIDataService aiService) {
        this.mainActivity = mainActivity;
        this.aiService = aiService;
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

            //String test = "ABC DEF|UJJWAL|MAITY";
            String[] splitSpeech = speech.split("\\|");
            for (String s : splitSpeech) {
                Log.i("RESULT", s);

                if (result.getResolvedQuery().equals("Image")) {
                    ChatViewData msg = new ChatViewData(ChatViewData.MSG_TYPE_IMAGE, "http://rma-upload.s3.amazonaws.com/2019_08_22_11_11_59banner.png");
                    mainActivity.chatViewData.add(msg);
                    int newMsgPosition = mainActivity.chatViewData.size() - 1;
                    mainActivity.chatViewAdapter.notifyItemInserted(newMsgPosition);
                    mainActivity.recyclerView.scrollToPosition(newMsgPosition);
                } /*else if (result.getResolvedQuery().equals("Video")) {
                    ChatViewData msg = new ChatViewData(ChatViewData.MSG_TYPE_VIDEO, "TqUbiOgEb0w");
                    msg.setStartSeconds(88);
                    mainActivity.chatViewData.add(msg);
                    int newMsgPosition = mainActivity.chatViewData.size() - 1;
                    mainActivity.chatViewAdapter.notifyItemInserted(newMsgPosition);
                    mainActivity.recyclerView.scrollToPosition(newMsgPosition);
                }*/ else {
                    ChatViewData msg = new ChatViewData(ChatViewData.MSG_TYPE_RECEIVED, s);
                    mainActivity.chatViewData.add(msg);
                    int newMsgPosition = mainActivity.chatViewData.size() - 1;
                    mainActivity.chatViewAdapter.notifyItemInserted(newMsgPosition);
                    mainActivity.recyclerView.scrollToPosition(newMsgPosition);
                }
            }
        }
    }
}