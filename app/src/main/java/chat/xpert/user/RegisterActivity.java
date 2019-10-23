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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pabitrarista.chatdialog.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    AppCompatTextView termsTextView;
    EditText editTextFirstName, editTextLastName;
    Spinner dropdownGender, dropdownAge;

    String firstName, lastName, gender, age, Uid;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    private SharedPreferences preferences;

    private static final String USER_MASTER_KEY = "user_master";
    private static final String FIRST_NAME_KEY = "first_name";
    private static final String LAST_NAME_KEY = "last_name";
    private static final String GENDER_KEY = "gender";
    private static final String AGE_KEY = "age";
    private static final String TIMESTAMP_KEY = "timestamp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateName() || !validGender() || !validAge())
                    return;

                Map<String, Object> docData = new HashMap<>();
                docData.put(TIMESTAMP_KEY, FieldValue.serverTimestamp());
                docData.put(FIRST_NAME_KEY, firstName);
                docData.put(LAST_NAME_KEY, lastName);
                docData.put(GENDER_KEY, gender);
                docData.put(AGE_KEY, Integer.parseInt(age));

                db.collection(USER_MASTER_KEY)
                        .document(Uid)
                        .set(docData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("userFirstName", firstName);
                                editor.putString("userLastName", lastName);
                                editor.apply();
                                Intent in = new Intent(RegisterActivity.this, XpertListActivity.class);
                                startActivity(in);
                                finish();
                            }
                        });

            }
        });
    }

    private void init() {
        termsTextView = findViewById(R.id.register_terms);
        setTermsPolicyText(termsTextView);

        editTextFirstName = findViewById(R.id.register_first_name);
        editTextLastName = findViewById(R.id.register_last_name);

        dropdownGender = findViewById(R.id.register_spinner_gender);
        String[] itemsGender = new String[]{"GENDER", "MALE", "FEMALE"};
        ArrayAdapter<String> adapterGender = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsGender);
        dropdownGender.setAdapter(adapterGender);
        dropdownGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        dropdownAge = findViewById(R.id.register_spinner_age);
        String[] itemsAge = new String[91];
        itemsAge[0] = "AGE";
        int num = 10;
        int i = 1;
        while (num <= 99) {
            itemsAge[i++] = Integer.toString(num);
            num++;
        }
        ArrayAdapter<String> adapterAge = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsAge);
        dropdownAge.setAdapter(adapterAge);
        dropdownAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                age = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getUid();
        db = FirebaseFirestore.getInstance();

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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

    private boolean validateName() {
        firstName = editTextFirstName.getText().toString().trim();
        lastName = editTextLastName.getText().toString().trim();
        if (firstName.isEmpty() || firstName.length() < 3 || lastName.isEmpty() || lastName.length() < 2) {
            Toast.makeText(this, "Enter your full name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validGender() {
        if (gender.equals("GENDER")) {
            Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validAge() {
        if (age.equals("AGE")) {
            Toast.makeText(this, "Select age", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
