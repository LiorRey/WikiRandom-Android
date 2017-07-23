package com.example.aviv.wikirandom.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.aviv.wikirandom.Model.FetchArticleTask;
import com.example.aviv.wikirandom.Model.Language;
import com.example.aviv.wikirandom.Model.SoundPlayer;
import com.example.aviv.wikirandom.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

// The main game screen
public class GameActivity extends Activity
{
    private static GameActivity instance;
    public static GameActivity get() { return instance; }
    private static final int NUM_OF_ANSWERS = 4;

    private static String randomArticleURL; // The URL of a random Wikipedia article, at JSON format
    private Language langAbb; // Chosen language abbreviation

    //~~~~FireBase:~~~~
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference leaderboardRef;
    private DatabaseReference userRef;
    private DatabaseReference scoreRef;
    private String userID;

    private JSONObject randomJSON; // Random article JSON object
    private String randArt_ID; // Random article page id
    private String randArt_Category; // Random article category
    private String randArt_URL; // Random article full URL (The web address of an article in Wikipedia)
    private ArrayList<String> answers; // The answers (made from "Article ID|Random selected category of article")
    private WebView rndmArticleWV; // The web view of the random article
    private ImageButton muteBTN; // The button that mutes or unmutes music and sounds
    private Button[] answerButtons; // The answer buttons
    private Button nextArtBTN; // The "next random article" button
    private TextView scoreBar; // The bar that shows the user's score (points collected)
    private TextView correctBar; // The bar that appears after a correct answer
    private Random rand; // Random factor
    private int score; // The user's score (points collected)
    private SoundPlayer sound; // The sounds in the game (Correct / Wrong)
    private MediaPlayer bgMusic; // The background music in the game
    public static Boolean volumeIsMuted; // // The flag of the volume status

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        instance = this;
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_game);

        Intent intent = getIntent();

        // get the chosen language's 2 letters aberration
        langAbb = (Language) intent.getSerializableExtra("language");

        //the base url with ~~ has a placeholder for the 2 letters aberration which describe the chosen Language
        randomArticleURL = "https://~~.wikipedia.org/w/api.php?action=query&generator=random&grnnamespace=0&indexpageids&prop=pageimages|categories|info&inprop=url&pithumbsize=1000&format=json";

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        leaderboardRef = database.getReference("leaderboard");
        userID = mAuth.getCurrentUser().getUid();

        // if the user is not null , return his a reference to his 'score' key
        if (userID != null)
        {
            userRef = leaderboardRef.child(userID);
            scoreRef = userRef.child("score");
        }

        else
        {
            // if user is null , return him to login screen
            finish();
            startActivity(new Intent(GameActivity.this, LoginActivity.class));
        }

        rndmArticleWV = (WebView) findViewById(R.id.rndmArticleWV);
        rndmArticleWV.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return true;
            }
        });
        answers = new ArrayList<>(NUM_OF_ANSWERS);

        answerButtons = new Button[NUM_OF_ANSWERS];

        // Create outlet/reference for each answer button in the answerButtons array
        for (int i = 0; i < NUM_OF_ANSWERS; i++)
        {
            String buttonID = "answerBtn" + (i+1);
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            answerButtons[i] = (Button) findViewById(resID);
            answerButtons[i].setBackground(ContextCompat.getDrawable(GameActivity.this, R.drawable.bordered_button  ));
        }

        muteBTN = (ImageButton) findViewById(R.id.muteBtn);
        nextArtBTN = (Button) findViewById(R.id.nextArtBtn);
        scoreBar = (TextView) findViewById(R.id.scoreBar);
        correctBar = (TextView) findViewById(R.id.correctBar);

        score = 0;

        sound = SoundPlayer.get();
        volumeIsMuted = false;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // Add a listener for the points incrementation
        scoreRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                score = dataSnapshot.getValue(Integer.class);
                scoreBar.setText("" + score + " " + setScoreBarText());
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });

        // Set the Wikipedia language according to what the user chose at LangActivity
        randomArticleURL = randomArticleURL.replace("~~", langAbb.toString());
        // Set the text for the next-random-article button according to the language
        nextArtBTN.setText(setNextRandomArticleText());
        // Do the main things in GameActivity
        showRandomArticle(null);
        // Initialize background music
        bgMusic = MediaPlayer.create(this, R.raw.music_background);
        bgMusic.setLooping(true);

        // If the volume is not muted
        if (!volumeIsMuted)
        {
            // Play (that funky) background music (white boy) from the start, and set the volume icon accordingly
            bgMusic.start();
            muteBTN.setImageResource(R.drawable.unmute);
        }
    }

    // The main method of GameActivity, that handles 4 random articles as JSON objects, does some important manipulation
    // and make all the needed displays on the screen
    public void showRandomArticle(View view)
    {
        // This one's purpose is - that when that game starts at first, and this methods get called, it wont animate the button
        if (view != null)
        {
            YoYo.with(Techniques.Bounce)
                    .duration(500)
                    .playOn(findViewById(view.getId()));
        }

        // Make the correct bar invisible
        correctBar.setVisibility(View.INVISIBLE);
        // Give the answer buttons their original color enable them
        for (Button button : answerButtons)
        {
            button.setBackground(ContextCompat.getDrawable(GameActivity.this, R.drawable.bordered_button));
            button.setEnabled(true);
        }
        // Clear the last answers array
        answers.clear();

        // A web-data-fetching algorithm that needs to run in a different thread than the main one
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    // Handle 4 random Wikipedia articles
                    for (int i = 0; i < NUM_OF_ANSWERS; i++)
                    {
                        // Make a JSON object from the random article using the link above
                        FetchArticleTask fetchArticleTasker = new FetchArticleTask(GameActivity.this, randomArticleURL);
                        randomJSON = fetchArticleTasker.execute().get();
                        // Get the article ID
                        randArt_ID = randomJSON.getJSONObject("query").getJSONArray("pageids").getString(0);
                        // Get the article categories
                        JSONArray categoriesArr = randomJSON.getJSONObject("query").getJSONObject("pages").getJSONObject(randArt_ID).getJSONArray("categories");
                        // Get a random category from the categories array
                        rand = new Random();
                        randArt_Category = categoriesArr.getJSONObject(rand.nextInt(categoriesArr.length())).getString("title");
                        // Get rid of the unnecessary prefix of the categories (for example, the English prefix is: "Category:")
                        randArt_Category = randArt_Category.substring(randArt_Category.indexOf(':') + 1);
                        // Make a unique string for each answer, using the article's ID and random category
                        // (used for checking answers later)
                        answers.add((randArt_ID + '~').concat(randArt_Category));
                    }

                    // If there's a thumbnail in the random article - try to load it in the web view
                    try
                    {
                        String thumbnail_URL = randomJSON.getJSONObject("query").getJSONObject("pages").getJSONObject(randArt_ID).getJSONObject("thumbnail").getString("source");
                        rndmArticleWV.loadUrl(thumbnail_URL);
                    }

                    // else - display an image of question marks
                    catch (JSONException je)
                    {
                        rndmArticleWV.loadUrl("http://www.s-safety.co.uk/wp-content/uploads/2011/08/question-mark-300x278.jpg");
                    }

                    // Get the URL of the random article (for possible later display in the webview)
                    randArt_URL = randomJSON.getJSONObject("query").getJSONObject("pages").getJSONObject(randArt_ID).getString("fullurl");
                    // Shuffle the answers (to make the game more difficult & interesting)
                    Collections.shuffle(answers);

                    // Set a unique tag for each answer button (article ID)
                    // and set the selected random category as the button's text
                    for (int i = 0; i < NUM_OF_ANSWERS; i++)
                    {
                        String[] randArt_Details = answers.get(i).split("~");
                        answerButtons[i].setTag(randArt_Details[0]);
                        answerButtons[i].setText(randArt_Details[1]);
                    }
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).run();
    }

    // Selects the text for the score bar according to the language
    public String setScoreBarText()
    {
        switch (langAbb)
        {
            case HEBREW: return "נקודות";
            case ENGLISH: return "Points";
            case RUSSIAN: return "Очки";
            default: return "Points";
        }
    }

    // Selects the text for the correct bar according to the language
    public String setCorrectBarText()
    {
        switch (langAbb)
        {
            case HEBREW: return "נכון! 1+";
            case ENGLISH: return "Correct! +1";
            case RUSSIAN: return "Правильно! +1";
            default: return "Correct! +1";
        }
    }

    // Selects the text for the next-random-article button according to the language
    public String setNextRandomArticleText()
    {
        switch (langAbb)
        {
            case HEBREW: return ">>> הערך האקראי הבא >>>";
            case ENGLISH: return ">>> NEXT RANDOM ARTICLE >>>";
            case RUSSIAN: return ">>> СЛЕДУЮЩАЯ СЛУЧАЙНАЯ СТАТЬЯ >>>";
            default: return ">>> NEXT RANDOM ARTICLE >>>";
        }
    }

    public void checkAnswer(View view)
    {
        // If the pressed button has the CORRECT answer
        if (view.getTag().equals(randArt_ID))
        {
            // Add 1 point, display the correct bar and paint green the correct answer button
            scoreRef.setValue(score + 1);
            correctBar.setText(setCorrectBarText());
            correctBar.setVisibility(View.VISIBLE);

            // Animate the "Correct +1!" bar, as soon as it appears (when the user's answer is correct)
            YoYo.with(Techniques.Tada)
                    .duration(300)
                    .playOn(findViewById(R.id.correctBar));

            view.setBackground(ContextCompat.getDrawable(GameActivity.this, R.drawable.correct_button));

            // Animate the button that has the correct answer
            YoYo.with(Techniques.Pulse)
                    .duration(500)
                    .playOn(findViewById(view.getId()));

            // If the volume is not muted
            if (!volumeIsMuted)
            {
                // Play the "correct" sound
                sound.playCorrectSound();
            }
        }

        // If the pressed button has a WRONG answer
        else
        {
            // Paint red the pressed wrong answer button
            view.setBackground(ContextCompat.getDrawable(GameActivity.this, R.drawable.wrong_button));

            // Animate the button that has the wrong answer
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(findViewById(view.getId()));

            // If the volume is not muted
            if (!volumeIsMuted)
            {
                // Play the "wrong" sound
                sound.playWrongSound();
            }

            // Look for the correct answer button and paint it green
            for (int i = 0; i < NUM_OF_ANSWERS; i++)
            {
                if (answerButtons[i].getTag().equals(randArt_ID))
                {
                    answerButtons[i].setBackground(ContextCompat.getDrawable(GameActivity.this, R.drawable.correct_button));

                    // Animate the button that has the correct answer
                    YoYo.with(Techniques.Pulse)
                            .duration(500)
                            .playOn(findViewById(answerButtons[i].getId()));

                }
            }
        }

        // Disable all the answer buttons
        for (Button button : answerButtons)
        {
            button.setEnabled(false);
        }

        // Load the correct random article in the webview
        rndmArticleWV.loadUrl(randArt_URL);
    }

    public void switchVolumeStatus(View view)
    {
        // Animate the mute button
        YoYo.with(Techniques.Swing)
                .duration(300)
                .playOn(findViewById(view.getId()));

        // If the volume is NOT muted
        if (!volumeIsMuted)
        {
            bgMusic.pause();
            muteBTN.setImageResource(R.drawable.mute);
        }

        // If the volume IS muted
        else
        {
            bgMusic.start();
            muteBTN.setImageResource(R.drawable.unmute);
        }

        // Switch the volume status
        volumeIsMuted = !volumeIsMuted;
    }

    public void keyboardDown(View view)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        bgMusic.release();
    }
}