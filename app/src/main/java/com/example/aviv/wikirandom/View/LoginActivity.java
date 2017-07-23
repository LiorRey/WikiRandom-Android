package com.example.aviv.wikirandom.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.daimajia.androidanimations.library.BaseViewAnimator;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.aviv.wikirandom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends Activity
{
    private FirebaseAuth mAuth;
    private EditText emailET;
    private EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Checks for user session. If the user is already logged in, skip login screen
        if (mAuth.getCurrentUser() != null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(), LangActivity.class));
        }

        emailET = (EditText) findViewById(R.id.emailField_Log);
        passwordET = (EditText) findViewById(R.id.passwordField_Log);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        emailET.setText("");
        passwordET.setText("");
    }
        // this method is responsible for logging in the user to fireBase
    public void loginAction(final View view)
    {
        final String email = emailET.getText().toString();
        final String password = passwordET.getText().toString();

        // checks if the login fields aren't empty
        if (email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(LoginActivity.this, "Please enter valid email and password", Toast.LENGTH_LONG).show();
        }

        else
        {
            // This method sign in the user using FireBase SignIn method that gets email and password as it's arguemnts
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                // Log in success

                                // Animate the login button
                                YoYo.with(Techniques.Flash)
                                        .duration(300)
                                        .playOn(findViewById(view.getId()));

                                Thread myThread = new Thread()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            sleep(200);
                                            finish();
                                            startActivity(new Intent(LoginActivity.this, LangActivity.class));
                                        }
                                        catch (InterruptedException e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                myThread.start();
                            }

                            else
                            {
                                Toast.makeText(LoginActivity.this, "User does not exist!\nPlease enter valid email and password", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public void registerAction(View view)
    {
        // Animate the register button
        YoYo.with(Techniques.Flash)
                .duration(300)
                .playOn(findViewById(view.getId()));

        Thread myThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    sleep(200);
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}
