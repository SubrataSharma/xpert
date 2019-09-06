package com.pabitrarista.chatdialog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class MainActivity extends AppCompatActivity implements AIListener {

    EditText editText;
    TextView textView;

    AIService aiService;

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
                Toast.makeText(MainActivity.this, "You have clicked tittle", Toast.LENGTH_LONG).show();
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
    }

    public void funFacts(View view) {
        Intent in = new Intent(MainActivity.this, AskScreen2Activity.class);
        startActivity(in);
        finish();
    }

    public void sendMsg(View view) {
        String msg = editText.getText().toString().toString();
        if (!msg.equals("")) {
            aiService.startListening();
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
