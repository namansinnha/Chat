package com.developer.gkweb.knowlocation.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.developer.gkweb.knowlocation.Util.AsteriskPasswordTransformationMethod;
import com.developer.gkweb.knowlocation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    //vars
    private TextInputEditText username, password;
    private TextView wants_to_register, forgot_password;
    private Button loginButton;
    private Toolbar loginToolbar;

    //Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mFirebaseUser;

    //widget
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance ();
        mFirebaseUser = mFirebaseAuth.getCurrentUser ();
        mProgressDialog = new ProgressDialog (this);

        initializeVars ();
        settingToolbar ();

        password.setTransformationMethod (new AsteriskPasswordTransformationMethod ());

        wants_to_register.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                startActivity (new Intent (LoginActivity.this, RegisterActivity.class));

            }
        });

        forgot_password.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                startActivity (new Intent (LoginActivity.this, ForgotPasswordActivity.class));

            }
        });

        loginButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                String email = username.getText ().toString ().trim ();
                String passcode = password.getText ().toString ().trim ();

                if (validateEntries ()) {

                    mProgressDialog.setMessage ("Please wait verifying your identity");
                    mProgressDialog.show ();

                    mFirebaseAuth.signInWithEmailAndPassword (email, passcode).addOnCompleteListener (new OnCompleteListener<AuthResult> () {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful ()) {

                                mProgressDialog.dismiss ();
                                startActivity (new Intent (LoginActivity.this, MainActivity.class));
                                finish ();

                            } else {

                                mProgressDialog.dismiss ();
                                Toasty.error (getApplicationContext (), task.getException ().getMessage (), Toasty.LENGTH_SHORT).show ();

                            }
                        }
                    });
                } else {


                }
            }
        });

    }

    private void initializeVars() {

        loginToolbar = findViewById (R.id.toolbar_login);
        username = findViewById (R.id.username);
        password = findViewById (R.id.password);
        loginButton = findViewById (R.id.loginButton);
        wants_to_register = findViewById (R.id.wants_to_register);
        forgot_password = findViewById (R.id.forgot_password);
    }

    private void settingToolbar() {

        loginToolbar.setTitle ("Login");
        loginToolbar.setSubtitle ("to begin with this application");
    }

    private boolean validateEntries() {

        boolean result;

        if ((!validateUsername ()) || (!validatePassword ())) {

            result = false;

        } else {

            result = true;
        }

        return result;

    }

    private boolean validateUsername() {

        boolean result;

        if (username.length () <= 2) {

            username.setError ("Atleast 3 characters expected");

            if (username.length () == 0) {

                username.setError ("Username is mandatory");

            }

            result = false;

        } else {

            result = true;

        }

        return result;
    }

    private boolean validatePassword() {

        boolean result;

        if (password.length () <= 5) {

            password.setError ("Atleast 6 characters expected");

            if (password.length () == 0) {

                password.setError ("Password is mandatory");

            }

            result = false;

        } else {

            result = true;
        }

        return result;

    }

    @Override
    protected void onStart() {
        super.onStart ();

        if (mFirebaseUser != null) {

            startActivity (new Intent (LoginActivity.this, MainActivity.class));
            finish ();
        }

    }
}
