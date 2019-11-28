package com.developer.gkweb.knowlocation.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.developer.gkweb.knowlocation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class ForgotPasswordActivity extends AppCompatActivity {

    //vars
    TextInputEditText forgot_password_email;
    Button resetPassword;

    //Firebase
    private FirebaseAuth mFirebaseAuth;

    //widget
    private ProgressDialog mProgressDialog;

    //regex
    String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_forgot_password);

        mFirebaseAuth = FirebaseAuth.getInstance ();
        mProgressDialog = new ProgressDialog (this);

        initializeVariable ();

        resetPassword.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                if (validate ()) {

                    mProgressDialog.setMessage ("Sending reset password link");
                    mProgressDialog.show ();

                    String email = forgot_password_email.getText ().toString ().trim ();

                    mFirebaseAuth.sendPasswordResetEmail (email).addOnCompleteListener (new OnCompleteListener<Void> () {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful ()) {

                                mProgressDialog.dismiss ();
                                Toasty.success (getApplicationContext (), "Reset Link has been sent", Toasty.LENGTH_SHORT).show ();
                                finish ();

                            } else {

                                mProgressDialog.dismiss ();
                                Toasty.error (getApplicationContext (), task.getException ().getMessage (), Toasty.LENGTH_SHORT).show ();
                            }

                        }
                    });
                }
            }
        });

    }

    private void initializeVariable() {

        forgot_password_email = findViewById (R.id.forgot_password_email);
        resetPassword = findViewById (R.id.resetPassword);

    }

    private boolean validate() {

        boolean result;

        String email = forgot_password_email.getText ().toString ().trim ();

        if (email.matches (emailRegex) && email.length () > 0) {

            result = true;

        } else {
            forgot_password_email.setError ("Invalid email");
            result = false;
        }

        return result;
    }

}

