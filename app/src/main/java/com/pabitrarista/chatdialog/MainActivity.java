package com.pabitrarista.chatdialog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;
import com.pabitrarista.chatdialog.recyclerview.ChatViewAdapter;
import com.pabitrarista.chatdialog.recyclerview.ChatViewData;

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

public class MainActivity extends AppCompatActivity implements AIListener {

    EditText editText;

    RecyclerView recyclerView;
    final List<ChatViewData> chatViewData = new ArrayList<>();
    ChatViewAdapter chatViewAdapter;

    AIService aiService;
    AIDataService aiDataService;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setElevation(0);
        View view = getSupportActionBar().getCustomView();

        TextView name = view.findViewById(R.id.name);
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "You have clicked tittle", Toast.LENGTH_LONG).show();
            }
        });

        init();
    }

    private void init() {
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
    }

    public void funFacts(View view) {
        Intent in = new Intent(MainActivity.this, AskScreen2Activity.class);
        in.putExtra("option", "personality");
        startActivity(in);
    }

    public void opinions(View view) {
        Intent in = new Intent(MainActivity.this, AskScreen2Activity.class);
        in.putExtra("option", "opinion");
        startActivity(in);
    }

    public void life(View view) {
        Intent in = new Intent(MainActivity.this, AskScreen2Activity.class);
        in.putExtra("option", "journey");
        startActivity(in);
    }

    public void cricket(View view) {
        Intent in = new Intent(MainActivity.this, AskScreen2Activity.class);
        in.putExtra("option", "pro");
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
        new AiTask(MainActivity.this, aiDataService).execute(msg, "", "");
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

            if (response_type.equals("video")) {
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

        if (!TextUtils.isEmpty(query)) {
            request.setQuery(query);
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
            final Result result = response.getResult();
            final String speech = result.getFulfillment().getSpeech();
            //Toast.makeText(context.get(), speech, Toast.LENGTH_LONG).show();
            //result.getResolvedQuery()

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
                ChatViewData msg = new ChatViewData(ChatViewData.MSG_TYPE_RECEIVED, speech);
                mainActivity.chatViewData.add(msg);
                int newMsgPosition = mainActivity.chatViewData.size() - 1;
                mainActivity.chatViewAdapter.notifyItemInserted(newMsgPosition);
                mainActivity.recyclerView.scrollToPosition(newMsgPosition);
            }
        }
    }
}