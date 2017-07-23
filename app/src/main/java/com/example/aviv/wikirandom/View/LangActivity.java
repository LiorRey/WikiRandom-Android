package com.example.aviv.wikirandom.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.aviv.wikirandom.Model.Language;
import com.example.aviv.wikirandom.R;
import com.google.firebase.auth.FirebaseAuth;

public class LangActivity extends Activity
{
    private FirebaseAuth mAuth;
    private Button[] langButtons; // The answer buttons

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_lang);

        mAuth = FirebaseAuth.getInstance();

        // Don't let unauthenticated users to get to the language selection screen. if they made it somehow, throw them to the login screen
        if (mAuth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(LangActivity.this, LoginActivity.class));
        }

        // Array of buttons
        langButtons = new Button[Language.values().length];

        // Give each button in the array, an outlet/reference
        for (int i = 0; i < langButtons.length; i++)
        {
            String buttonID = Language.values()[i] + "Button";
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            langButtons[i] = (Button) findViewById(resID);
        }
    }

    public void languageSelect(View view)
    {
        final Language langAbb; // Chosen language abbreviation

        YoYo.with(Techniques.Flash)
                .duration(300)
                .playOn(findViewById(view.getId()));

        // This switch statement is used to differentiate between languages, when selected, using the buttons tag
        switch (Integer.parseInt(view.getTag().toString()))
        {
            case 1: langAbb = Language.HEBREW;
                break;
            case 2: langAbb = Language.ENGLISH;
                break;
            case 3: langAbb = Language.RUSSIAN;
                break;
            default: langAbb = Language.ENGLISH;
                break;
        }

        Thread myThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    sleep(200);
                    Intent intent = new Intent(LangActivity.this, GameActivity.class);
                    intent.putExtra("language", langAbb);
                    startActivity(intent);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }

    // This method uses the FireBase's signOut method in order to logout the user
    public void logoutAction(View view)
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
                    mAuth.signOut();
                    finish();
                    startActivity(new Intent(LangActivity.this, LoginActivity.class));
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }

    public void goToLeaderboard(View view)
    {
        YoYo.with(Techniques.Bounce)
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
                    startActivity(new Intent(LangActivity.this, LeaderboardActivity.class));
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }

    public void goToAbout(View view)
    {
        YoYo.with(Techniques.Bounce)
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
                    startActivity(new Intent(LangActivity.this, AboutActivity.class));
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }

    @Override
    public void onBackPressed()
    {
        // Display alert message when back button has been pressed
        new AlertDialog.Builder(LangActivity.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure you want to exit WikiRandom ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish();
                    }
                }).setNegativeButton("No", null).show();
    }
}