package com.example.aviv.wikirandom.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.example.aviv.wikirandom.R;

// this is the launcher screen
public class LaunchScreen extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);
        // this thread's purpose, is to show the user the launcher screen for 3 seconds and then move to the Login screen
        Thread myThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    sleep(3000);
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
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
