package com.pabitrarista.chatdialog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
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

    AIService aiService;
    AIDataService aiDataService;

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

        final AIConfiguration config = new AIConfiguration("5a28a24679e6411699636dc44558724c",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        aiDataService = new AIDataService(this, config);
    }

    public void funFacts(View view) {
        Intent in = new Intent(MainActivity.this, AskScreen2Activity.class);
        startActivity(in);
        finish();
    }

    public void sendMsg(View view) {
        String msg = editText.getText().toString().toString();
        if (!msg.equals("")) {
            textView.append("\n\n" + msg);
            new AiTask(this, aiDataService, textView).execute(msg, "", "");
            editText.setText("");
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
}

class AiTask extends AsyncTask<String, Void, AIResponse> {

    private final WeakReference<Context> context;
    private final AIDataService aiService;
    TextView textView;

    public AiTask(Context context, AIDataService aiService, TextView textView) {
        this.context = new WeakReference<>(context);
        this.aiService = aiService;
        this.textView = textView;
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
        }
    }
}