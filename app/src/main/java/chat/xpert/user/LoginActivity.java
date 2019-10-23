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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pabitrarista.chatdialog.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText phoneEditText;
    AppCompatTextView termsTextView;
    Button loginButton;

    String phone, userIdPhone, userFirstName;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        validUser();

        setTermsPolicyText(termsTextView);
    }

    private void init() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        phoneEditText = findViewById(R.id.login_user_phone);
        termsTextView = findViewById(R.id.login_terms);
        loginButton = findViewById(R.id.login_btn);
        loginButton.setOnClickListener(this);
    }

    public void validUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        try {
            userIdPhone = mUser.getPhoneNumber();

            if (userIdPhone != null) {
                userFirstName = preferences.getString("userFirstName", "null");
                if (!userFirstName.equals("null")) {
                    Intent in = new Intent(LoginActivity.this, XpertListActivity.class);
                    startActivity(in);
                    finish();
                } else {
                    Intent in = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(in);
                    finish();
                }
            }
        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(), "Please Verify Phone no", Toast.LENGTH_SHORT).show();
        }
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
                if (!validatePhone())
                    return;

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("userPhone", phone);
                editor.apply();
                Intent in = new Intent(LoginActivity.this, OtpActivity.class);
                phone = "+91" + phone;
                in.putExtra("phone", phone);
                startActivity(in);
                //finish();
        }
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
