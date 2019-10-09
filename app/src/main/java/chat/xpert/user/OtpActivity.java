package chat.xpert.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.pabitrarista.chatdialog.R;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    ProgressBar progressBar;
    EditText editText;
    Button button, btResend;
    TextView tvCountDownTimer;

    private CountDownTimer countDownTimer;

    private String verificationId;
    private FirebaseAuth mAuth;

    String phone, userIdPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        init();

        phone = getIntent().getStringExtra("phone");

        sendVerificationOtp(phone);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = editText.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    editText.setError("Enter Code");
                    editText.requestFocus();
                    return;
                }
                try {
                    button.setEnabled(false);
                    btResend.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    verifyCode(code);
                } catch (Exception e) {
                    button.setEnabled(true);
                    btResend.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationOtp(phone);
                btResend.setEnabled(false);
            }
        });
    }

    private void init() {
        progressBar = findViewById(R.id.o_progressBar);
        editText = findViewById(R.id.o_otp);
        button = findViewById(R.id.o_verify);
        btResend = findViewById(R.id.o_resend);
        tvCountDownTimer = findViewById(R.id.o_resend_time);
        mAuth = FirebaseAuth.getInstance();
    }

    private void startCounter() {
        if (countDownTimer != null)
            countDownTimer.cancel();

        countDownTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvCountDownTimer.setText(millisUntilFinished / 1000 + "");
            }

            public void onFinish() {
                tvCountDownTimer.setText("");
                btResend.setEnabled(true);
            }

        };

        countDownTimer.start();
    }

    private void sendVerificationOtp(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                30,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacksPhoneAuthActivity.java

        startCounter();
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                button.setEnabled(false);
                btResend.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }
    };

    public void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            FirebaseUser mUser = mAuth.getCurrentUser();
                            try {
                                userIdPhone = mUser.getPhoneNumber();
                            } catch (Exception e) {
                            }

                            Intent in2 = new Intent(getApplicationContext(), XpertListActivity.class);
                            in2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(in2);
                        } else {
                            button.setEnabled(true);
                            btResend.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        button.setEnabled(true);
                        btResend.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
