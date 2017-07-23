package com.example.aviv.wikirandom.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.aviv.wikirandom.Model.User;
import com.example.aviv.wikirandom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends Activity
{
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference leaderboardRef;
    private EditText emailET;
    private EditText nicknameET;
    private EditText passwordET;
    private EditText confirmPasswordET;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        leaderboardRef = database.getReference("leaderboard");

        emailET = (EditText) findViewById(R.id.emailField_Reg);
        nicknameET = (EditText) findViewById(R.id.nicknameField_Reg);
        passwordET = (EditText) findViewById(R.id.passwordField_Reg);
        confirmPasswordET = (EditText) findViewById(R.id.confirmPasswordField_Reg);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    // The method that registers a new user
    public void registerAction(final View view)
    {
        final String email = emailET.getText().toString();
        final String nickname = nicknameET.getText().toString();
        final String password = passwordET.getText().toString();
        final String confirmPassword = confirmPasswordET.getText().toString();

        // Checks that all fields are valid
        if (email.isEmpty() || nickname.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())
        {
            // All or some of the fields are not valid - displays suitable message
            new AlertDialog.Builder(RegisterActivity.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Oops!")
                    .setMessage("One or more of your fields are empty.\nplease try again!")
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    }).show();
            return;
        }

        // Checks if the user typed password that contains at least 6 characters long
        if (password.length() < 6)
        {
            new AlertDialog.Builder(RegisterActivity.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Oops!")
                    .setMessage("Your password contains less than a 6 characters!\nplease try again!")
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    }).show();
                return;
        }

        else
        {
            // Checks if the passwords match
            if (confirmPassword.equals(password))
            {   // create user using fireBase method that takes email and passowrd as its arguments
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful())
                                {
                                    // Success

                                    // Animate the register button
                                    YoYo.with(Techniques.Flash)
                                            .duration(300)
                                            .playOn(findViewById(view.getId()));

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String userID = user.getUid();
                                    writeNewUser(nickname, userID);

                                    Thread myThread = new Thread()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            try
                                            {
                                                sleep(200);
                                                finish();
                                                startActivity(new Intent(getApplicationContext(), LangActivity.class));
                                            }
                                            catch (InterruptedException e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }
                                    };
                                    myThread.start();

                                } else
                                {
                                    // If operation fails, display a message to the user.
                                    new AlertDialog.Builder(RegisterActivity.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Oops!")
                                            .setMessage("Authentication failed!\nplease try again!")
                                            .setNeutralButton("Ok", new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which)
                                                {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                    return;
                                }
                            }
                        });
            }

            else
            {
                // Passwords do not match - displays suitable message
                new AlertDialog.Builder(RegisterActivity.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Oops!")
                        .setMessage("Your passwords do not match!\nplease try again!")
                        .setNeutralButton("Ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        }).show();
                return;
            }
        }
    }
    // This method writes new user details to the fireBase dataBase
    private void writeNewUser(String nickname, String userID)
    {
        User user = new User();
        user.setNickname(nickname);
        user.setScore(0);
        user.setUserID(userID);

        try
        {
            leaderboardRef.child(userID).setValue(user);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // Animate the cancel button
    public void cancelAction(View view)
    {
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