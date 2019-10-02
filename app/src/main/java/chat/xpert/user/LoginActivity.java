package chat.xpert.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pabitrarista.chatdialog.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText nameEditText, phoneEditText;
    AppCompatTextView termsTextView;
    Button loginButton;

    String name, phone;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        name = preferences.getString("userName", "null");
        phone = preferences.getString("userPhone", "null");
        if (!name.equals("null") && !phone.equals("null")) {
            Intent in = new Intent(LoginActivity.this, XpertListActivity.class);
            startActivity(in);
            finish();
        }

        init();

        setTermsPolicyText(termsTextView);
    }

    private void init() {
        nameEditText = findViewById(R.id.login_user_name);
        phoneEditText = findViewById(R.id.login_user_phone);
        termsTextView = findViewById(R.id.login_terms);
        loginButton = findViewById(R.id.login_btn);
        loginButton.setOnClickListener(this);
    }

    private void setTermsPolicyText(AppCompatTextView textView) {
        String terms_policy = getString(R.string.accept_terms);

        SpannableString ss = new SpannableString(terms_policy);

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_terms)));
                startActivity(browserIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };

        int indexStart1 = terms_policy.indexOf("Xpert");
        int indexEnd1 = terms_policy.indexOf("and");

        ss.setSpan(clickableSpan1, indexStart1, indexEnd1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_privacy)));
                startActivity(browserIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };

        int indexStart2 = terms_policy.indexOf("Privacy");
        int indexEnd2 = terms_policy.length() - 1;

        ss.setSpan(clickableSpan2, indexStart2, indexEnd2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setLinkTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        textView.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                if (!validateName() || !validatePhone())
                    return;

                name = nameEditText.getText().toString();
                phone = phoneEditText.getText().toString();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("userName", name);
                editor.putString("userPhone", phone);
                editor.apply();
                Intent in = new Intent(LoginActivity.this, XpertListActivity.class);
                startActivity(in);
                finish();
        }
    }

    private boolean validateName() {
        name = nameEditText.getText().toString().trim();
        if (name.isEmpty() || name.length() < 3) {
            Toast.makeText(this, "Enter your full name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validatePhone() {
        phone = phoneEditText.getText().toString().trim();
        Pattern p = Pattern.compile("^[6-9][0-9]{9}$");
        Matcher m = p.matcher(phone);
        if (phone.isEmpty() || !m.find()) {
            Toast.makeText(this, "Enter valid phone no", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
