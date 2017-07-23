package com.example.aviv.wikirandom.Model;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import com.example.aviv.wikirandom.R;
import com.example.aviv.wikirandom.View.GameActivity;

/**
 * Created by Team Hurrange on 3/30/2017.
 */

// this class in a singletone of the Android's soundPlayer - soundPool . and was made to control that soundPlayer we have only one instance.
public class SoundPlayer
{
    private static SoundPlayer instance;

    private AudioAttributes audioAttributes;
    private final int SOUND_POOL_MAX = 2;

    private static SoundPool soundPool;
    private static int soundCorrect;
    private static int soundWrong;

    public static SoundPlayer get()
    {
        if (instance == null)
        {
            instance = getSync();
        }

        return instance;
    }

    private static synchronized SoundPlayer getSync()
    {
        if (instance == null)
        {
            instance = new SoundPlayer(GameActivity.get());
        }

        return instance;
    }

    private SoundPlayer(Context context)
    {
        // SoundPool is deprecated in API level 21 (Lollipop)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(SOUND_POOL_MAX)
                    .build();
        }

        else
        {
            soundPool = new SoundPool(SOUND_POOL_MAX, AudioManager.STREAM_MUSIC, 0);
        }

        soundCorrect = soundPool.load(context, R.raw.sound_correct, 1);
        soundWrong = soundPool.load(context, R.raw.sound_wrong, 1);
    }

    public void playCorrectSound()
    {
        soundPool.play(soundCorrect, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playWrongSound()
    {
        soundPool.play(soundWrong, 1.0f, 1.0f, 1, 0, 1.0f);
    }
}
