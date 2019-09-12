package com.pabitrarista.chatdialog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
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
    TextView textView;
    ScrollView scrollView;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    // Create the initial data list.
    final List<MsgSendReceive> msgDtoList = new ArrayList<>();
    MsgAdapter msgAdapter;

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
        textView = findViewById(R.id.main_textView);
        scrollView = findViewById(R.id.main_scroll_view);

        recyclerView = findViewById(R.id.main_recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        MsgSendReceive msgDto = new MsgSendReceive(MsgSendReceive.MSG_TYPE_RECEIVED, "");
        msgDtoList.add(msgDto);
        // Create the data adapter with above data list.
        msgAdapter = new MsgAdapter(msgDtoList);
        // Set data adapter to RecyclerView.
        recyclerView.setAdapter(msgAdapter);

        final AIConfiguration config = new AIConfiguration("5a28a24679e6411699636dc44558724c",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        aiDataService = new AIDataService(this, config);
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
        String msg = editText.getText().toString().trim();
        if (!msg.equals("")) {
            // Add a new sent message to the list.
            MsgSendReceive msgDto1 = new MsgSendReceive(MsgSendReceive.MSG_TYPE_SENT, msg);
            msgDtoList.add(msgDto1);
            int newMsgPosition = msgDtoList.size() - 1;
            // Notify recycler view insert one new data.
            msgAdapter.notifyItemInserted(newMsgPosition);
            // Scroll RecyclerView to the last message.
            recyclerView.scrollToPosition(newMsgPosition);

            textView.append("\n\n" + msg);
            new AiTask(this, aiDataService, textView, scrollView, msgAdapter, msgDtoList, recyclerView).execute(msg, "", "");
            editText.setText("");
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//            aiService.startListening();
//            textView.append(msg + "\n\n");
        }
    }

    @Override
    public void onResult(AIResponse result) {
        Result result1 = result.getResult();
//        textView.setText("Query: " + result1.getResolvedQuery() + "\nAction: " + result1.getAction());
        textView.append(result1.getResolvedQuery() + "\n" + result1.getFulfillment().getSpeech() + "\n\n");
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
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String ans = preferences.getString("ans", null);
        String ans_format = preferences.getString("ans_format", null);
        int ans_start = preferences.getInt("ans_start", -1);

        if (ans_format != null && ans != null) {
//            Toast.makeText(this, ans_format, Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, ans, Toast.LENGTH_SHORT).show();
//            if (ans_start != -1) {
//                Toast.makeText(this, ans_start + "", Toast.LENGTH_SHORT).show();
//            }

            if (ans_format.equals("video")) {
                MsgSendReceive msgDto1 = new MsgSendReceive(MsgSendReceive.MSG_TYPE_VIDEO, ans);
                msgDto1.setStartSeconds(ans_start);
                msgDtoList.add(msgDto1);
                int newMsgPosition = msgDtoList.size() - 1;
                msgAdapter.notifyItemInserted(newMsgPosition);
                recyclerView.scrollToPosition(newMsgPosition);
            } else if (ans_format.equals("text")) {
                MsgSendReceive msgDto1 = new MsgSendReceive(MsgSendReceive.MSG_TYPE_RECEIVED, ans);
                msgDtoList.add(msgDto1);
                int newMsgPosition = msgDtoList.size() - 1;
                msgAdapter.notifyItemInserted(newMsgPosition);
                recyclerView.scrollToPosition(newMsgPosition);
            }

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("ans_format", null);
            editor.putString("ans", null);
            editor.putInt("ans_start", -1);
            editor.apply();
        }
    }
}

class AiTask extends AsyncTask<String, Void, AIResponse> {

    private final WeakReference<Context> context;
    private final AIDataService aiService;
    TextView textView;
    ScrollView scrollView;
    MsgAdapter msgAdapter;
    RecyclerView recyclerView;
    List<MsgSendReceive> msgDtoList;

    public AiTask(Context context, AIDataService aiService, TextView textView, ScrollView scrollView, MsgAdapter msgAdapter, List<MsgSendReceive> msgDtoList, RecyclerView recyclerView) {
        this.context = new WeakReference<>(context);
        this.aiService = aiService;
        this.textView = textView;
        this.scrollView = scrollView;
        this.msgAdapter = msgAdapter;
        this.recyclerView = recyclerView;
        this.msgDtoList = msgDtoList;
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
            textView.append("\n" + speech);
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);

            if (result.getResolvedQuery().equals("Image")) {
                MsgSendReceive msgDto1 = new MsgSendReceive(MsgSendReceive.MSG_TYPE_IMAGE, "http://rma-upload.s3.amazonaws.com/2019_08_22_11_11_59banner.png");
                msgDtoList.add(msgDto1);
                int newMsgPosition = msgDtoList.size() - 1;
                msgAdapter.notifyItemInserted(newMsgPosition);
                recyclerView.scrollToPosition(newMsgPosition);
            } /*else if (result.getResolvedQuery().equals("Video")) {
                MsgSendReceive msgDto1 = new MsgSendReceive(MsgSendReceive.MSG_TYPE_VIDEO, "TqUbiOgEb0w");
                msgDto1.setStartSeconds(88);
                msgDtoList.add(msgDto1);
                int newMsgPosition = msgDtoList.size() - 1;
                msgAdapter.notifyItemInserted(newMsgPosition);
                recyclerView.scrollToPosition(newMsgPosition);
            }*/ else {
                // Add a new sent message to the list.
                MsgSendReceive msgDto1 = new MsgSendReceive(MsgSendReceive.MSG_TYPE_RECEIVED, speech);
                msgDtoList.add(msgDto1);
                int newMsgPosition = msgDtoList.size() - 1;
                // Notify recycler view insert one new data.
                msgAdapter.notifyItemInserted(newMsgPosition);
                // Scroll RecyclerView to the last message.
                recyclerView.scrollToPosition(newMsgPosition);
            }
        }
    }
}