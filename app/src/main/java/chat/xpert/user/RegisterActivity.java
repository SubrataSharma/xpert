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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pabitrarista.chatdialog.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    AppCompatTextView termsTextView;
    EditText editTextFirstName, editTextLastName;
    Spinner dropdownGender, dropdownAge, dropdownProfession;

    String firstName, lastName, userIdPhone, gender, age, profession, Uid;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore db;

    private SharedPreferences preferences;

    private static final String USER_MASTER_KEY = "user_master";
    private static final String FIRST_NAME_KEY = "first_name";
    private static final String LAST_NAME_KEY = "last_name";
    private static final String AUTH_ID_KEY = "auth_id";
    private static final String GENDER_KEY = "Gender";
    private static final String AGE_KEY = "Age";
    private static final String PROFESSION_KEY = "Profession";
    private static final String PHONE_NO_KEY = "phone_no";
    private static final String REGISTER_DATE_KEY = "register_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateName() || !validGender() || !validAge() || !validProfession())
                    return;

                Map<String, Object> docData = new HashMap<>();
                docData.put(REGISTER_DATE_KEY, FieldValue.serverTimestamp());
                docData.put(FIRST_NAME_KEY, firstName);
                docData.put(LAST_NAME_KEY, lastName);
                docData.put(PHONE_NO_KEY, userIdPhone);
                docData.put(AUTH_ID_KEY, Uid);
                docData.put(GENDER_KEY, gender);
                docData.put(AGE_KEY, Integer.parseInt(age));
                docData.put(PROFESSION_KEY, profession);

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
        String[] itemsGender = new String[]{"Gender", "Male", "Female"};
        ArrayAdapter<String> adapterGender = new ArrayAdapter<>(this, R.layout.layout_spinner_item_view, itemsGender);
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
        itemsAge[0] = "Age";
        int num = 10;
        int i = 1;
        while (num <= 99) {
            itemsAge[i++] = Integer.toString(num);
            num++;
        }
        ArrayAdapter<String> adapterAge = new ArrayAdapter<>(this, R.layout.layout_spinner_item_view, itemsAge);
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

        dropdownProfession = findViewById(R.id.register_spinner_profession);
        String[] itemsProfession = new String[]{"I am currently", "Studying", "Working", "Retired", "Other"};
        ArrayAdapter<String> adapterProfession = new ArrayAdapter<>(this, R.layout.layout_spinner_item_view, itemsProfession);
        dropdownProfession.setAdapter(adapterProfession);
        dropdownProfession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                profession = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getUid();
        mUser = mAuth.getCurrentUser();
        try {
            userIdPhone = mUser.getPhoneNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (gender.equalsIgnoreCase("Gender")) {
            Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validAge() {
        if (age.equalsIgnoreCase("Age")) {
            Toast.makeText(this, "Select age", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validProfession() {
        if (profession.equalsIgnoreCase("I am currently")) {
            Toast.makeText(this, "Select profession", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
